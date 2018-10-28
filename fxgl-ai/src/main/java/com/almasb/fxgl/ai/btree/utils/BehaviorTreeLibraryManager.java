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
import com.almasb.fxgl.ai.btree.TaskCloneException;

/** The {@code BehaviorTreeLibraryManager} is a singleton in charge of the creation of behavior trees using the underlying library.
 * If no library is explicitly set (see the method {@link #setLibrary(BehaviorTreeLibrary)}), a default library instantiated by
 * the constructor {@link BehaviorTreeLibrary#BehaviorTreeLibrary() BehaviorTreeLibrary()} is used instead.
 * 
 * @author davebaol */
public final class BehaviorTreeLibraryManager {

	private static BehaviorTreeLibraryManager instance = new BehaviorTreeLibraryManager();

	protected BehaviorTreeLibrary library;

	private BehaviorTreeLibraryManager () {
		setLibrary(new BehaviorTreeLibrary());
	}

	/** Returns the singleton instance of the {@code BehaviorTreeLibraryManager}. */
	public static BehaviorTreeLibraryManager getInstance () {
		return instance;
	}

	/** Gets the the behavior tree library
	 * @return the behavior tree library */
	public BehaviorTreeLibrary getLibrary () {
		return library;
	}

	/** Sets the the behavior tree library
	 * @param library the behavior tree library to set */
	public void setLibrary (BehaviorTreeLibrary library) {
		this.library = library;
	}

	/** Creates the root task of {@link BehaviorTree} for the specified reference.
	 * @param treeReference the tree identifier, typically a path
	 * @return the root task of the tree cloned from the archetype.
	 * @throws RuntimeException if the reference cannot be successfully parsed.
	 * @throws TaskCloneException if the archetype cannot be successfully parsed. */
	public <T> Task<T> createRootTask (String treeReference) {
		return library.createRootTask(treeReference);
	}

	/** Creates the {@link BehaviorTree} for the specified reference.
	 * @param treeReference the tree identifier, typically a path
	 * @return the tree cloned from the archetype.
	 * @throws RuntimeException if the reference cannot be successfully parsed.
	 * @throws TaskCloneException if the archetype cannot be successfully parsed. */
	public <T> BehaviorTree<T> createBehaviorTree (String treeReference) {
		return library.createBehaviorTree(treeReference);
	}

	/** Creates the {@link BehaviorTree} for the specified reference and blackboard object.
	 * @param treeReference the tree identifier, typically a path
	 * @param blackboard the blackboard object (it can be {@code null}).
	 * @return the tree cloned from the archetype.
	 * @throws RuntimeException if the reference cannot be successfully parsed.
	 * @throws TaskCloneException if the archetype cannot be successfully parsed. */
	public <T> BehaviorTree<T> createBehaviorTree (String treeReference, T blackboard) {
		return library.createBehaviorTree(treeReference, blackboard);
	}

}
