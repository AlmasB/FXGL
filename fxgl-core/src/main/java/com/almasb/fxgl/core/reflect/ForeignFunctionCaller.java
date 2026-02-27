/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.core.reflect;

import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.logging.Logger;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * TODO: WIP
 *
 * FFC is a wrapper around a native library, allowing
 * calls to native functions as if they were Java functions.
 *
 * Each FFC has its own single thread that executes all call functions.
 *
 * TODO: explore MemoryLayout for non-primitive structs
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class ForeignFunctionCaller {

    private static final Logger log = Logger.get(ForeignFunctionCaller.class);

    private static final AtomicInteger threadCount = new AtomicInteger(0);

    private List<Path> libraries;
    private List<SymbolLookup> lookups = new ArrayList<>();
    private Map<String, MemorySegment> functionsAddresses = new HashMap<>();
    private Map<String, MethodHandle> functions = new HashMap<>();

    private ForeignFunctionContext context;
    public BlockingQueue<Consumer<ForeignFunctionContext>> executionQueue = new ArrayBlockingQueue<>(1000);
    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private Thread thread;

    private Runnable onLoaded = EmptyRunnable.INSTANCE;
    private Runnable onUnloaded = EmptyRunnable.INSTANCE;
    private boolean isLoaded = false;

    /**
     * For a given library, only 1 FFC can be created.
     *
     * @param libraries the list of files to load as libraries
     */
    public ForeignFunctionCaller(List<Path> libraries) {
        this.libraries = new ArrayList<>(libraries);
    }

    public void setOnLoaded(Runnable onLoaded) {
        this.onLoaded = onLoaded;
    }

    public void setOnUnloaded(Runnable onUnloaded) {
        this.onUnloaded = onUnloaded;
    }

    public void load() {
        if (isLoaded) {
            log.warning("Already loaded: " + libraries);
            return;
        }

        isLoaded = true;

        thread = new Thread(this::threadTask, "FFCThread-" + threadCount.getAndIncrement());
        thread.setDaemon(true);
        thread.start();

        // TODO: wait until libs are loaded and loop entered
        // use CountDownLatch
    }

    private void threadTask() {
        log.debug("Starting native setup task");

        try (var arena = Arena.ofConfined()) {
            libraries.forEach(file -> {
                var lookup = SymbolLookup.libraryLookup(file, arena);
                lookups.add(lookup);
            });

            context = new ForeignFunctionContext(arena, Linker.nativeLinker(), lookups);

            log.debug("Native libs loaded and context created");
            onLoaded.run();

            while (isRunning.get()) {
                try {
                    var functionCall = executionQueue.take();
                    functionCall.accept(context);
                } catch (Exception e) {
                    log.warning("Native call failed", e);
                }
            }

        } catch (Throwable e) {
            log.warning("FFCThread task failed", e);
        }

        // the libraries loaded iva SymbolLookup are unloaded
        // when the associated arena is closed, i.e. when above try-catch completes
        // however, in practice, it appears there is a delay before the lib is fully closed
        onUnloaded.run();
    }

    private MethodHandle getFunctionImpl(String name, FunctionDescriptor fd) {
        String functionID = name + fd.toString();

        if (functions.containsKey(functionID)) {
            return functions.get(functionID);
        }

        MemorySegment functionAddress;

        if (functionsAddresses.containsKey(name)) {
            functionAddress = functionsAddresses.get(name);
        } else {
            functionAddress = lookups.stream()
                    .map(l -> l.find(name))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Failed to find function in lookup: " + name));

            functionsAddresses.put(name, functionAddress);
        }

        MethodHandle function = context.linker.downcallHandle(functionAddress, fd);

        functions.put(functionID, function);

        return function;
    }

    private Object callImpl(String name, FunctionDescriptor fd, Object... args) {
        var function = getFunctionImpl(name, fd);

        try {
            if (args.length == 0) {
                return function.invoke();
            } else {
                return function.invokeWithArguments(Arrays.asList(args));
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void execute(Consumer<ForeignFunctionContext> functionCall) {
        if (!isLoaded) {
            log.warning("Libraries are not loaded, call load() first");
            return;
        }

        try {
            executionQueue.put(functionCall);
        } catch (Throwable e) {
            log.warning("Failed to schedule a function call", e);
        }
    }

    public void unload() {
        unload(_ -> {});
    }

    /**
     * @param libExitFunctionCall the last function to call in the loaded library(-ies),
     * do not schedule any other execute() operations within the call
     */
    public void unload(Consumer<ForeignFunctionContext> libExitFunctionCall) {
        // TODO: isLoaded = false?
        // TODO: if not loaded ignore?

        execute(context -> {
            libExitFunctionCall.accept(context);
            isRunning.set(false);
        });
    }

    public final class ForeignFunctionContext {

        private Arena arena;
        private Linker linker;
        private List<SymbolLookup> lookups;

        public ForeignFunctionContext(Arena arena, Linker linker, List<SymbolLookup> lookups) {
            this.arena = arena;
            this.linker = linker;
            this.lookups = lookups;
        }

        public Arena getArena() {
            return arena;
        }

        public Linker getLinker() {
            return linker;
        }

        public List<SymbolLookup> getLookups() {
            return new ArrayList<>(lookups);
        }

        public MethodHandle getFunction(String name, FunctionDescriptor fd) {
            return getFunctionImpl(name, fd);
        }

        public Object call(String name, FunctionDescriptor fd, Object... args) {
            return callImpl(name, fd, args);
        }

        public void callVoidNoArg(String name) {
            callImpl(name, FunctionDescriptor.ofVoid());
        }

        public MemorySegment allocateIntArray(int length) {
            return arena.allocate(ValueLayout.JAVA_INT, length);
        }

        public MemorySegment allocateCharArrayFrom(String s) {
            return arena.allocateFrom(s);
        }
    }
}
