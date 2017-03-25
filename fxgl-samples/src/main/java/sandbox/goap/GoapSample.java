/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package sandbox.goap;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Queue;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GoapSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("GoapSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {

        GameEntity agent = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());

        agent.setProperty("goto", true);



        HashSet<GoapAction> actions = new HashSet<>();
        actions.add(new TripleAction());
        actions.add(new SecondAction());
        actions.add(new TestGoapAction(true));

        State state = new State();
        state.add("prev", true);
        state.add("next", true);

        State goal = new State();
        goal.add("goal", true);

        GoapPlanner planner = new GoapPlanner();
        Queue<GoapAction> result = planner.plan(agent,
                actions,
                state, goal);

        for (GoapAction d : result) {
            System.out.println(d);
        }
    }

    private static class TestGoapAction extends GoapAction {

        private boolean done = false;

        public TestGoapAction(String name) {
            super(name);
        }

        public TestGoapAction(boolean f) {
            super("TestGoapAction");
            addPrecondition("prev", true);
            addEffect("prev", false);
        }

        @Override
        public void reset() {

        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {

            return true;
        }

        @Override
        public boolean perform(Entity agent) {

            done = true;

            return true;
        }

        @Override
        public boolean requiresInRange() {
            return false;
        }
    }

    private static class SecondAction extends TestGoapAction {
        public SecondAction() {
            super("SeconAction");
            addPrecondition("next", false);
            addEffect("goal", true);
            addEffect("next", true);
        }
    }

    private static class TripleAction extends TestGoapAction {
        public TripleAction() {
            super("TripleAction");
            addPrecondition("prev", true);
            //addEffect("goal", true);
            addEffect("next", false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
