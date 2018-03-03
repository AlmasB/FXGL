/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.almasb.fxgl.ai.btree.utils;

import com.almasb.fxgl.ai.btree.BehaviorTree;
import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.ai.btree.annotation.TaskAttribute;
import com.almasb.fxgl.ai.btree.annotation.TaskConstraint;
import com.almasb.fxgl.ai.btree.branch.*;
import com.almasb.fxgl.ai.btree.decorator.*;
import com.almasb.fxgl.ai.btree.leaf.Failure;
import com.almasb.fxgl.ai.btree.leaf.Success;
import com.almasb.fxgl.ai.btree.leaf.Wait;
import com.almasb.fxgl.ai.utils.random.Distribution;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.collection.ObjectSet;
import com.almasb.fxgl.core.reflect.Annotation;
import com.almasb.fxgl.core.reflect.ClassReflection;
import com.almasb.fxgl.core.reflect.Field;
import com.almasb.fxgl.core.reflect.ReflectionException;

import java.io.InputStream;
import java.io.Reader;

/** A {@link BehaviorTree} parser.
 * 
 * @author davebaol */
public class BehaviorTreeParser<E> {

	public static final int DEBUG_NONE = 0;
	public static final int DEBUG_LOW = 1;
	public static final int DEBUG_HIGH = 2;

	private static final String TAG = "BehaviorTreeParser";

	public int debugLevel;
	public DistributionAdapters distributionAdapters;

	private DefaultBehaviorTreeReader<E> btReader;

	public BehaviorTreeParser () {
		this(DEBUG_NONE);
	}

	public BehaviorTreeParser (DistributionAdapters distributionAdapters) {
		this(distributionAdapters, DEBUG_NONE);
	}

	public BehaviorTreeParser (int debugLevel) {
		this(new DistributionAdapters(), debugLevel);
	}

	public BehaviorTreeParser (DistributionAdapters distributionAdapters, int debugLevel) {
		this(distributionAdapters, debugLevel, null);
	}

	public BehaviorTreeParser (DistributionAdapters distributionAdapters, int debugLevel, DefaultBehaviorTreeReader<E> reader) {
		this.distributionAdapters = distributionAdapters;
		this.debugLevel = debugLevel;
		btReader = reader == null ? new DefaultBehaviorTreeReader<E>() : reader;
		btReader.setParser(this);
	}

	/** Parses the given string.
	 * @param string the string to parse
	 * @param object the blackboard object. It can be {@code null}.
	 * @return the behavior tree
	 * @throws RuntimeException if the string cannot be successfully parsed. */
	public BehaviorTree<E> parse (String string, E object) {
		btReader.parse(string);
		return createBehaviorTree(btReader.root, object);
	}

	/** Parses the given input stream.
	 * @param input the input stream to parse
	 * @param object the blackboard object. It can be {@code null}.
	 * @return the behavior tree
	 * @throws RuntimeException if the input stream cannot be successfully parsed. */
	public BehaviorTree<E> parse (InputStream input, E object) {
		btReader.parse(input);
		return createBehaviorTree(btReader.root, object);
	}

//	/** Parses the given file.
//	 * @param file the file to parse
//	 * @param object the blackboard object. It can be {@code null}.
//	 * @return the behavior tree
//	 * @throws RuntimeException if the file cannot be successfully parsed. */
//	public BehaviorTree<E> parse (FileHandle file, E object) {
//		btReader.parse(file);
//		return createBehaviorTree(btReader.root, object);
//	}

	/** Parses the given reader.
	 * @param reader the reader to parse
	 * @param object the blackboard object. It can be {@code null}.
	 * @return the behavior tree
	 * @throws RuntimeException if the reader cannot be successfully parsed. */
	public BehaviorTree<E> parse (Reader reader, E object) {
		btReader.parse(reader);
		return createBehaviorTree(btReader.root, object);
	}

	protected BehaviorTree<E> createBehaviorTree (Task<E> root, E object) {
		if (debugLevel > BehaviorTreeParser.DEBUG_LOW) printTree(root, 0);
		return new BehaviorTree<E>(root, object);
	}

	protected static <E> void printTree (Task<E> task, int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print(' ');
		if (task.getGuard() != null) {
			System.out.println("Guard");
			indent = indent + 2;
			printTree(task.getGuard(), indent);
			for (int i = 0; i < indent; i++)
				System.out.print(' ');
		}
		System.out.println(task.getClass().getSimpleName());
		for (int i = 0; i < task.getChildCount(); i++) {
			printTree(task.getChild(i), indent + 2);
		}
	}

	public static class DefaultBehaviorTreeReader<E> extends BehaviorTreeReader {

		private static final ObjectMap<String, String> DEFAULT_IMPORTS = new ObjectMap<String, String>();
		static {
			Class<?>[] classes = new Class<?>[] {// @off - disable libgdx formatter
				AlwaysFail.class,
				AlwaysSucceed.class,
				DynamicGuardSelector.class,
				Failure.class,
				Include.class,
				Invert.class,
				Parallel.class,
				Random.class,
				RandomSelector.class,
				RandomSequence.class,
				Repeat.class,
				Selector.class,
				SemaphoreGuard.class,
				Sequence.class,
				Success.class,
				UntilFail.class,
				UntilSuccess.class,
				Wait.class
			}; // @on - enable libgdx formatter
			for (Class<?> c : classes) {
				String fqcn = c.getName();
				String cn = c.getSimpleName();
				String alias = Character.toLowerCase(cn.charAt(0)) + (cn.length() > 1 ? cn.substring(1) : "");
				DEFAULT_IMPORTS.put(alias, fqcn);
			}
		}

		enum Statement {
			Import("import") {
				@Override
				protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
				}

				@Override
				protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
					if (!(value instanceof String)) reader.throwAttributeTypeException(this.name, name, "String");
					reader.addImport(name, (String)value);
					return true;
				}

				@Override
				protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
					return;
				}
			},
			Subtree("subtree") {
				@Override
				protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
				}

				@Override
				protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
					if (!name.equals("name")) reader.throwAttributeNameException(this.name, name, "name");
					if (!(value instanceof String)) reader.throwAttributeTypeException(this.name, name, "String");
					if ("".equals(value)) throw new RuntimeException(this.name + ": the name connot be empty");
					if (reader.subtreeName != null)
						throw new RuntimeException(this.name + ": the name has been already specified");
					reader.subtreeName = (String)value;
					return true;
				}

				@Override
				protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
					if (reader.subtreeName == null)
						throw new RuntimeException(this.name + ": the name has not been specified");
					reader.switchToNewTree(reader.subtreeName);
					reader.subtreeName = null;
				}
			},
			Root("root") {
				@Override
				protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
					reader.subtreeName = ""; // the root tree has empty name
				}

				@Override
				protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
					reader.throwAttributeTypeException(this.name, name, null);
					return true;
				}

				@Override
				protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
					reader.switchToNewTree(reader.subtreeName);
					reader.subtreeName = null;
				}
			},
			TreeTask(null) {
				@Override
				protected <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard) {
					// Root tree is the default one
					if (reader.currentTree == null) {
						reader.switchToNewTree("");
						reader.subtreeName = null;
					}

					reader.openTask(name, isGuard);
				}

				@Override
				protected <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value) {
					StackedTask<E> stackedTask = reader.getCurrentTask();
					AttrInfo ai = stackedTask.metadata.attributes.get(name);
					if (ai == null) return false;
					boolean isNew = reader.encounteredAttributes.add(name);
					if (!isNew) throw reader.stackedTaskException(stackedTask, "attribute '" + name + "' specified more than once");
					Field attributeField = reader.getField(stackedTask.task.getClass(), ai.fieldName);
					reader.setField(attributeField, stackedTask.task, value);
					return true;
				}

				@Override
				protected <E> void exit (DefaultBehaviorTreeReader<E> reader) {
					if (!reader.isSubtreeRef) {
						reader.checkRequiredAttributes(reader.getCurrentTask());
						reader.encounteredAttributes.clear();
					}
				}
			};
			
			String name;
			
			Statement(String name) {
				this.name = name;
			}
			
			protected abstract <E> void enter (DefaultBehaviorTreeReader<E> reader, String name, boolean isGuard);
			protected abstract <E> boolean attribute (DefaultBehaviorTreeReader<E> reader, String name, Object value);
			protected abstract <E> void exit (DefaultBehaviorTreeReader<E> reader);

		} 

		protected BehaviorTreeParser<E> btParser;

		ObjectMap<Class<?>, Metadata> metadataCache = new ObjectMap<Class<?>, Metadata>();

		Task<E> root;
		String subtreeName;
		Statement statement;
		private int indent;

		public DefaultBehaviorTreeReader () {
			this(false);
		}

		public DefaultBehaviorTreeReader (boolean reportsComments) {
			super(reportsComments);
		}

		public BehaviorTreeParser<E> getParser () {
			return btParser;
		}

		public void setParser (BehaviorTreeParser<E> parser) {
			this.btParser = parser;
		}

		@Override
		public void parse (char[] data, int offset, int length) {
			root = null;
			clear();
			super.parse(data, offset, length);

			// Pop all task from the stack and check their minimum number of children
			popAndCheckMinChildren(0);

			Subtree<E> rootTree = subtrees.get("");
			if (rootTree == null) throw new RuntimeException("Missing root tree");
			root = rootTree.rootTask;
			if (root == null) throw new RuntimeException("The tree must have at least the root task");

			clear();
		}
		
		@Override
		protected void startLine (int indent) {
			this.indent = indent;
		}

		private Statement checkStatement (String name) {
			if (name.equals(Statement.Import.name)) return Statement.Import;
			if (name.equals(Statement.Subtree.name)) return Statement.Subtree;
			if (name.equals(Statement.Root.name)) return Statement.Root;
			return Statement.TreeTask;
		}
		
		@Override
		protected void startStatement (String name, boolean isSubtreeReference, boolean isGuard) {
			this.isSubtreeRef = isSubtreeReference;
			
			this.statement = isSubtreeReference ? Statement.TreeTask : checkStatement(name);
			if (isGuard) {
				if (statement != Statement.TreeTask)
					throw new RuntimeException(name + ": only tree's tasks can be guarded");
			}

			statement.enter(this, name, isGuard);
		}

		@Override
		protected void attribute (String name, Object value) {
			boolean validAttribute = statement.attribute(this, name, value);
			if (!validAttribute) {
				if (statement == Statement.TreeTask) {
					throw stackedTaskException(getCurrentTask(), "unknown attribute '" + name + "'");
				} else {
					throw new RuntimeException(statement.name + ": unknown attribute '" + name + "'");
				}
			}
		}

		private Field getField (Class<?> clazz, String name) {
			try {
				return ClassReflection.getField(clazz, name);
			} catch (ReflectionException e) {
				throw new RuntimeException(e);
			}
		}

		private void setField (Field field, Task<E> task, Object value) {
			field.setAccessible(true);
			Object valueObject = castValue(field, value);
			try {
				field.set(task, valueObject);
			} catch (ReflectionException e) {
				throw new RuntimeException(e);
			}
		}

		private Object castValue (Field field, Object value) {
			Class<?> type = field.getType();
			Object ret = null;
			if (value instanceof Number) {
				Number numberValue = (Number)value;
				if (type == int.class || type == Integer.class)
					ret = numberValue.intValue();
				else if (type == float.class || type == Float.class)
					ret = numberValue.floatValue();
				else if (type == long.class || type == Long.class)
					ret = numberValue.longValue();
				else if (type == double.class || type == Double.class)
					ret = numberValue.doubleValue();
				else if (type == short.class || type == Short.class)
					ret = numberValue.shortValue();
				else if (type == byte.class || type == Byte.class)
					ret = numberValue.byteValue();
				else if (ClassReflection.isAssignableFrom(Distribution.class, type)) {
					@SuppressWarnings("unchecked")
					Class<Distribution> distributionType = (Class<Distribution>)type;
					ret = btParser.distributionAdapters.toDistribution("constant," + numberValue, distributionType);
				}
			} else if (value instanceof Boolean) {
				if (type == boolean.class || type == Boolean.class) ret = value;
			} else if (value instanceof String) {
				String stringValue = (String)value;
				if (type == String.class)
					ret = value;
				else if (type == char.class || type == Character.class) {
					if (stringValue.length() != 1) throw new RuntimeException("Invalid character '" + value + "'");
					ret = Character.valueOf(stringValue.charAt(0));
				} else if (ClassReflection.isAssignableFrom(Distribution.class, type)) {
					@SuppressWarnings("unchecked")
					Class<Distribution> distributionType = (Class<Distribution>)type;
					ret = btParser.distributionAdapters.toDistribution(stringValue, distributionType);
				} else if (ClassReflection.isAssignableFrom(Enum.class, type)) {
					Enum<?>[] constants = (Enum<?>[])type.getEnumConstants();
					for (int i = 0, n = constants.length; i < n; i++) {
						Enum<?> e = constants[i];
						if (e.name().equalsIgnoreCase(stringValue)) {
							ret = e;
							break;
						}
					}
				}
			}
			if (ret == null) throwAttributeTypeException(getCurrentTask().name, field.getName(), type.getSimpleName());
			return ret;
		}

		private void throwAttributeNameException (String statement, String name, String expectedName) {
			String expected = " no attribute expected";
			if (expectedName != null)
				expected = "expected '" + expectedName + "' instead";
			throw new RuntimeException(statement + ": attribute '" + name + "' unknown; " + expected);
		}

		private void throwAttributeTypeException (String statement, String name, String expectedType) {
			throw new RuntimeException(statement + ": attribute '" + name + "' must be of type " + expectedType);
		}

		@Override
		protected void endLine () {
		}

		@Override
		protected void endStatement () {
			statement.exit(this);
		}

		private void openTask (String name, boolean isGuard) {
			try {
				Task<E> task;
				if (isSubtreeRef) {
					task = subtreeRootTaskInstance(name);
				}
				else {
					String className = getImport(name);
					if (className == null) className = name;
					@SuppressWarnings("unchecked")
                    Task<E> tmpTask = (Task<E>)ClassReflection.newInstance(ClassReflection.forName(className));
					task = tmpTask;
				}
				
				if (!currentTree.inited()) {
					initCurrentTree(task, indent);
					indent = 0;
				} else if (!isGuard) {
					StackedTask<E> stackedTask = getPrevTask();

					indent -= currentTreeStartIndent;
					if (stackedTask.task == currentTree.rootTask) {
						step = indent;
					}
					if (indent > currentDepth) {
						stack.add(stackedTask); // push
					} else if (indent <= currentDepth) {
						// Pop tasks from the stack based on indentation
						// and check their minimum number of children
						int i = (currentDepth - indent) / step;
						popAndCheckMinChildren(stack.size() - i);
					}

					// Check the max number of children of the parent
					StackedTask<E> stackedParent = stack.last();
					int maxChildren = stackedParent.metadata.maxChildren;
					if (stackedParent.task.getChildCount() >= maxChildren)
						throw stackedTaskException(stackedParent, "max number of children exceeded ("
							+ (stackedParent.task.getChildCount() + 1) + " > " + maxChildren + ")");

					// Add child task to the parent
					stackedParent.task.addChild(task);
				}
				updateCurrentTask(createStackedTask(name, task), indent, isGuard);
			} catch (ReflectionException e) {
				throw new RuntimeException("Cannot parse behavior tree!!!", e);
			}
		}
		
		private StackedTask<E> createStackedTask (String name, Task<E> task) {
			Metadata metadata = findMetadata(task.getClass());
			if (metadata == null)
				throw new RuntimeException(name + ": @TaskConstraint annotation not found in '" + task.getClass().getSimpleName()
					+ "' class hierarchy");
			return new StackedTask<E>(lineNumber, name, task, metadata);
		}

		private Metadata findMetadata (Class<?> clazz) {
			Metadata metadata = metadataCache.get(clazz);
			if (metadata == null) {
				Annotation tca = ClassReflection.getAnnotation(clazz, TaskConstraint.class);
				if (tca != null) {
					TaskConstraint taskConstraint = tca.getAnnotation(TaskConstraint.class);
					ObjectMap<String, AttrInfo> taskAttributes = new ObjectMap<String, AttrInfo>();
					Field[] fields = ClassReflection.getFields(clazz);
					for (Field f : fields) {
						Annotation a = f.getDeclaredAnnotation(TaskAttribute.class);
						if (a != null) {
							AttrInfo ai = new AttrInfo(f.getName(), a.getAnnotation(TaskAttribute.class));
							taskAttributes.put(ai.name, ai);
						}
					}
					metadata = new Metadata(taskConstraint.minChildren(), taskConstraint.maxChildren(), taskAttributes);
					metadataCache.put(clazz, metadata);
				}
			}
			return metadata;
		}

		protected static class StackedTask<E> {
			public int lineNumber;
			public String name;
			public Task<E> task;
			public Metadata metadata;

			StackedTask (int lineNumber, String name, Task<E> task, Metadata metadata) {
				this.lineNumber = lineNumber;
				this.name = name;
				this.task = task;
				this.metadata = metadata;
			}
		}

		private static class Metadata {
			int minChildren;
			int maxChildren;
			ObjectMap<String, AttrInfo> attributes;

			/** Creates a {@code Metadata} for a task accepting from {@code minChildren} to {@code maxChildren} children and the given
			 * attributes.
			 * @param minChildren the minimum number of children (defaults to 0 if negative)
			 * @param maxChildren the maximum number of children (defaults to {@link Integer#MAX_VALUE} if negative)
			 * @param attributes the attributes */
			Metadata (int minChildren, int maxChildren, ObjectMap<String, AttrInfo> attributes) {
				this.minChildren = minChildren < 0 ? 0 : minChildren;
				this.maxChildren = maxChildren < 0 ? Integer.MAX_VALUE : maxChildren;
				this.attributes = attributes;
			}
		}

		private static class AttrInfo {
			String name;
			String fieldName;
			boolean required;

			AttrInfo (String fieldName, TaskAttribute annotation) {
				this(annotation.name(), fieldName, annotation.required());
			}

			AttrInfo (String name, String fieldName, boolean required) {
				this.name = name == null || name.length() == 0 ? fieldName : name;
				this.fieldName = fieldName;
				this.required = required;
			}
		}

		protected static class Subtree<E> {
			String name;  // root tree must have no name
			Task<E> rootTask;
			int referenceCount;

			Subtree() {
				this(null);
			}

			Subtree(String name) {
				this.name = name;
				this.rootTask = null;
				this.referenceCount = 0;
			}

			public void init(Task<E> rootTask) {
				this.rootTask = rootTask;
			}

			public boolean inited() {
				return rootTask != null;
			}

			public boolean isRootTree() {
				return name == null || "".equals(name);
			}

			public Task<E> rootTaskInstance () {
				if (referenceCount++ == 0) {
					return rootTask;
				}
				return rootTask.cloneTask();
			}
		}

		ObjectMap<String, String> userImports = new ObjectMap<String, String>();

		ObjectMap<String, Subtree<E>> subtrees = new ObjectMap<String, Subtree<E>>();
		Subtree<E> currentTree;

		int currentTreeStartIndent;
		int currentDepth;
		int step;
		boolean isSubtreeRef;
		protected StackedTask<E> prevTask;
		protected StackedTask<E> guardChain;
		protected Array<StackedTask<E>> stack = new Array<StackedTask<E>>();
		ObjectSet<String> encounteredAttributes = new ObjectSet<String>();
		boolean isGuard;
		
		StackedTask<E> getLastStackedTask() {
			return stack.last();
		}
		
		StackedTask<E> getPrevTask() {
			return prevTask;
		}
		
		StackedTask<E> getCurrentTask() {
			return isGuard? guardChain : prevTask;
		}
		
		void updateCurrentTask(StackedTask<E> stackedTask, int indent, boolean isGuard) {
			this.isGuard = isGuard;
			stackedTask.task.setGuard(guardChain == null ? null : guardChain.task);
			if (isGuard) {
				guardChain = stackedTask;
			}
			else {
				prevTask = stackedTask;
				guardChain = null;
				currentDepth = indent;
			}
		}
		
		void clear() {
			prevTask = null;
			guardChain = null;
			currentTree = null;
			userImports.clear();
			subtrees.clear();
			stack.clear();
			encounteredAttributes.clear();
		}
		
		//
		// Subtree
		//
		
		void switchToNewTree(String name) {
			// Pop all task from the stack and check their minimum number of children
			popAndCheckMinChildren(0);

			this.currentTree = new Subtree<E>(name);
			Subtree<E> oldTree = subtrees.put(name, currentTree);
			if (oldTree != null)
				throw new RuntimeException("A subtree named '" + name + "' is already defined");
		}
		
		void initCurrentTree(Task<E> rootTask, int startIndent) {
			currentDepth = -1;
			step = 1;
			currentTreeStartIndent = startIndent;
			this.currentTree.init(rootTask);
			prevTask = null;
		}
		
		Task<E> subtreeRootTaskInstance(String name) {
			Subtree<E> tree = subtrees.get(name);
			if (tree == null)
				throw new RuntimeException("Undefined subtree with name '" + name + "'");
			return tree.rootTaskInstance();
		}
		
		//
		// Import
		//

		void addImport (String alias, String task) {
			if (task == null) throw new RuntimeException("import: missing task class name.");
			if (alias == null) {
				Class<?> clazz = null;
				try {
					clazz = ClassReflection.forName(task);
				} catch (ReflectionException e) {
					throw new RuntimeException("import: class not found '" + task + "'");
				}
				alias = clazz.getSimpleName();
			}
			String className = getImport(alias);
			if (className != null) throw new RuntimeException("import: alias '" + alias + "' previously defined already.");
			userImports.put(alias, task);
		}

		String getImport (String as) {
			String className = DEFAULT_IMPORTS.get(as);
			return className != null ? className : userImports.get(as);
		}
		
		//
		// Integrity checks
		//

		private void popAndCheckMinChildren (int upToFloor) {
			// Check the minimum number of children in prevTask
			if (prevTask != null) checkMinChildren(prevTask);

			// Check the minimum number of children while popping up to the specified floor
			while (stack.size() > upToFloor) {
				StackedTask<E> stackedTask = stack.pop();
				checkMinChildren(stackedTask);
			}
		}

		private void checkMinChildren (StackedTask<E> stackedTask) {
			// Check the minimum number of children
			int minChildren = stackedTask.metadata.minChildren;
			if (stackedTask.task.getChildCount() < minChildren)
				throw stackedTaskException(stackedTask, "not enough children (" + stackedTask.task.getChildCount() + " < " + minChildren
					+ ")");
		}

		private void checkRequiredAttributes (StackedTask<E> stackedTask) {
			// Check the minimum number of children
			ObjectMap.Entries<String, AttrInfo> entries = stackedTask.metadata.attributes.iterator();
			while (entries.hasNext()) {
				ObjectMap.Entry<String, AttrInfo> entry = entries.next();
				if (entry.value.required && !encounteredAttributes.contains(entry.key))
					throw stackedTaskException(stackedTask, "missing required attribute '" + entry.key + "'");
			}
		}

		private RuntimeException stackedTaskException(StackedTask<E> stackedTask, String message) {
			return new RuntimeException(stackedTask.name + " at line " + stackedTask.lineNumber + ": " + message);
		}

	}
}
