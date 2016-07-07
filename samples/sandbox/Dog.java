/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.math.MathUtils;

/** @author implicit-invocation
 * @author davebaol */
public class Dog {

    public String name;
    public String brainLog;
    private BehaviorTree<Dog> behaviorTree;

    public Dog (String name) {
        this(name, null);
    }

    public Dog (String name, BehaviorTree<Dog> btree) {
        this.name = name;
        this.brainLog = name + " brain";
        this.behaviorTree = btree;

        if (btree != null)
            btree.setObject(this);
    }

    public BehaviorTree<Dog> getBehaviorTree () {
        return behaviorTree;
    }

    public void setBehaviorTree (BehaviorTree<Dog> behaviorTree) {
        this.behaviorTree = behaviorTree;
    }

    public void bark () {
        log("bark");
    }

    public void startWalking () {
        log("startWalking");
    }

    public void randomlyWalk () {
        log("walking");
    }

    public void stopWalking () {
        log("stopWalking");
    }

    public Boolean markATree (int i) {
        if (i == 0) {
            log("Swoosh....");
            return null;
        }
        if (MathUtils.randomBoolean()) {
            log("MUMBLE MUMBLE - Still leaking out");
            return Boolean.FALSE;
        }
        log("I'm ok now :)");
        return Boolean.TRUE;
    }

//	private boolean urgent = false;
//
//	public boolean isUrgent () {
//		return urgent;
//	}
//
//	public void setUrgent (boolean urgent) {
//		this.urgent = urgent;
//	}

    public void log (String msg) {
        GdxAI.getLogger().info(name, msg);
        System.out.println(name + ": " + msg);
    }

    public void brainLog (String msg) {
        GdxAI.getLogger().info(brainLog, msg);
        System.out.println(brainLog + ": " + msg);
    }

}
