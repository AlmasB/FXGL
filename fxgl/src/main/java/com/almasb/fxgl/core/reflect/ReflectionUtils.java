/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.util.Function;
import com.almasb.fxgl.util.Optional;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * A collection of convenience methods to isolate reflection code.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ReflectionUtils {

    private ReflectionUtils() {}









    /**
     * Private helper method
     *
     * @param directory
     *            The directory to start with
     * @param pckgname
     *            The package name to search for. Will be needed for getting the
     *            Class object.
     * @param classes
     *            if a file isn't loaded but still is in the directory
     * @throws ClassNotFoundException
     */
    private static void checkDirectory(File directory, String pckgname,
                                       ArrayList<Class<?>> classes) throws ClassNotFoundException {
        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {
            final String[] files = directory.list();

            for (final String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(pckgname + '.'
                                + file.substring(0, file.length() - 6)));
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ((tmpDirectory = new File(directory, file))
                        .isDirectory()) {
                    checkDirectory(tmpDirectory, pckgname + "." + file, classes);
                }
            }
        }
    }

    /**
     * Private helper method.
     *
     * @param connection
     *            the connection to the jar
     * @param pckgname
     *            the package name to search for
     * @param classes
     *            the current ArrayList of all classes. This method will simply
     *            add new classes.
     * @throws ClassNotFoundException
     *             if a file isn't loaded but still is in the jar file
     * @throws IOException
     *             if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<Class<?>> classes)
            throws ClassNotFoundException, IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry = null; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);) {
            name = jarEntry.getName();

            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    classes.add(Class.forName(name));
                }
            }
        }
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException
     *             if something went wrong
     */
    public static ArrayList<Class<?>> getClassesForPackage(String pckgname)
            throws ClassNotFoundException {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            final ClassLoader cld = Thread.currentThread()
                    .getContextClassLoader();

            if (cld == null)
                throw new ClassNotFoundException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url = null; resources.hasMoreElements()
                    && ((url = resources.nextElement()) != null);) {
                try {
                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes);
                    } else {
                        try {
                            checkDirectory(
                                    new File(URLDecoder.decode(url.getPath(),
                                            "UTF-8")), pckgname, classes);
                        } catch (final UnsupportedEncodingException ex) {
                            throw new ClassNotFoundException(
                                    pckgname
                                            + " does not appear to be a valid package (Unsupported encoding)",
                                    ex);
                        }
                    }
                } catch (final IOException ioex) {
                    throw new ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new ClassNotFoundException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }

        return classes;
    }





    public static Map<Class<?>, List<Class<?>>> findClasses(String packageName, Class<? extends java.lang.annotation.Annotation>... annotations) throws Exception {
        Map<Class<?>, List<Class<?>>> map = new HashMap<>();

        for (Class<? extends Annotation> annotationClass : annotations) {
            List<Class<?>> classes = new ArrayList<>();

            map.put(annotationClass, classes);
        }

        List<Class<?>> classes = getClassesForPackage(packageName);


        System.out.println("CLASSNAMES:BEGIN");
        System.out.println(classes);
        System.out.println("CLASSNAMES:END");



        for (Class<?> cl : classes) {
            for (Class<? extends Annotation> annotationClass : annotations) {
                if (cl.getAnnotation(annotationClass) != null) {
                    map.get(annotationClass).add(cl);
                }
            }
        }

        System.out.println(map);

        return map;
    }

    /**
     * @return mapping from annotation class to list of classes with that annotation (on the classpath)
     */
    public static Map<Class<?>, List<Class<?>>> findClasses(Class<?> rootClass, Class<? extends java.lang.annotation.Annotation>... annotations) {
        Map<Class<?>, List<Class<?>>> map = new HashMap<>();

        for (Class<? extends Annotation> annotationClass : annotations) {
            List<Class<?>> classes = new ArrayList<>();

            map.put(annotationClass, classes);
        }

        Package pack = rootClass.getPackage();

        String packageName = pack.getName();

        System.out.println(packageName);

        List<File> classNames = getClasspathClasses(rootClass);

        System.out.println("CLASSNAMES:BEGIN");
        System.out.println(classNames);
        System.out.println("CLASSNAMES:END");

        for (File file : classNames) {
            String name = file.toString().replace(File.separator, ".");

            int index = name.indexOf(packageName);
            name = name.substring(index, name.length() - 6);

            try {
                Class<?> cl = Class.forName(name);

                System.out.println(cl);

                for (Class<? extends Annotation> annotationClass : annotations) {
                    if (cl.getAnnotation(annotationClass) != null) {
                        map.get(annotationClass).add(cl);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace(System.out);
            }
        }

        System.out.println(map);

        return map;
    }

    private static List<File> getClasspathClasses(Class<?> rootClass) {
        try {
            List<File> result = new ArrayList<>();

            Enumeration<URL> roots = rootClass.getClassLoader().getResources("");

            while (roots.hasMoreElements()) {
                URL root = roots.nextElement();

                File file = new File(root.getPath());

                System.out.println("ROOT FILE: " + file);

                if (file.isDirectory()) {
                    classes(result, file, rootClass.getPackage().getName());
                }
            }

            return result;
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        return Collections.emptyList();
    }

    private static void classes(List<File> result, File dir, String rootName) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classes(result, f, rootName);
            } else {
                String name = f.toString().replace(File.separator, ".");

                //System.out.println(name + " -" + rootName);

                if (name.endsWith(".class") && !name.contains("$") && name.contains(rootName)) {
                    result.add(f);
                }
            }
        }
    }

    public static <A extends java.lang.annotation.Annotation> Map<A, Method>
        findMethods(Object instance, Class<A> annotationClass) {

        Map<A, Method> map = new HashMap<>();

        for (Method method : instance.getClass().getDeclaredMethods()) {
            A annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                map.put(annotation, method);
            }
        }

        return map;
    }

    public static <T, R, A extends java.lang.annotation.Annotation> Map<A, Function<T, R>>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass) {

        Map<A, Function<T, R>> map = new HashMap<>();

        forEach(
                findMethods(instance, annotationClass),
                (annotation, method) -> map.put(annotation, mapToFunction(instance, method))
        );

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T, R, F extends Function<T, R>, A extends java.lang.annotation.Annotation> Map<A, F>
        findMethodsMapToFunctions(Object instance, Class<A> annotationClass, Class<F> functionClass) {

        Map<A, F> map = new HashMap<>();

        forEach(
                findMethods(instance, annotationClass),
                (annotation, method) -> {
                    // we create an instance implementing F on the fly
                    // so that high-level calling code stays clean
                    F function = (F) Proxy.newProxyInstance(functionClass.getClassLoader(),
                            new Class[] { functionClass },
                            (proxy, proxyMethod, args) -> method.invoke(instance, args));

                    map.put(annotation, function);
                }
        );

        return map;
    }

    @SuppressWarnings("unchecked")
    public static <T> T call(Object instance, Method method, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (Exception e) {
            throw new ReflectionException("Cannot call " + method.getName() + " Error: " + e);
        }
    }

    public static <T, R> Function<T, R> mapToFunction(Object instance, Method method) {
        return input -> call(instance, method, input);
    }

    public static <A extends java.lang.annotation.Annotation> Array<Field>
        findFieldsByAnnotation(Object instance, Class<A> annotationClass) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Find declared fields of "instance" that have type / subtype of given "type" parameter.
     *
     * @param instance object whose fields to search
     * @param type super type
     * @return declared fields that meet criteria
     */
    public static Array<Field> findDeclaredFieldsByType(Object instance, Class<?> type) {

        Array<Field> fields = new Array<>();

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Find all fields of "instance" that have type / subtype of given "type" parameter.
     * Note: this will recursively search all matching fields in supertypes of "instance".
     *
     * @param instance object whose fields to search
     * @param type super type
     * @return all fields that meet criteria
     */
    public static Array<Field> findFieldsByTypeRecursive(Object instance, Class<?> type) {

        Array<Field> fields = new Array<>();

        for (Field field : getAllFieldsRecursive(instance)) {
            if (type.isAssignableFrom(field.getType())) {
                fields.add(field);
            }
        }

        return fields;
    }

    private static Array<Field> getAllFieldsRecursive(Object instance) {
        Array<Field> result = new Array<>();

        Class<?> typeClass = instance.getClass();
        while (typeClass != null && typeClass != Object.class) {
            result.addAll(typeClass.getDeclaredFields());
            typeClass = typeClass.getSuperclass();
        }

        return result;
    }

    public static Optional<Field> getDeclaredField(String fieldName, Object instance) {
        try {
            return Optional.of(instance.getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ReflectionException("Cannot get declared field: " + fieldName + " of " + instance + " Error: " + e);
        }
    }

    /**
     * Injects field of an instance to injectionInstance.
     *
     * @param field the field object
     * @param instance field's object
     * @param injectionInstance the target value to inject
     */
    public static void inject(Field field, Object instance, Object injectionInstance) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(instance, injectionInstance);
        } catch (Exception e) {
            throw new ReflectionException("Cannot inject " + injectionInstance + " into " + field.getName() + " Error: " + e);
        }
    }

    /**
     * @param type class
     * @return instance of given class using its no-arg ctor
     * @throws ReflectionException if cannot be instantiated
     */
    public static <T> T newInstance(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }
}
