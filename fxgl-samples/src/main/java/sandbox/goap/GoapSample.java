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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;

/**
 *
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
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    private static GameEntity player, coin, weapon, agent;
    private GoapAgent goapAgent;

    @Override
    protected void initGame() {

        initObjects();
        HashSet<GoapAction> actions = initActions();


        AIAgent aiAgent = new AIAgent(agent);

        goapAgent = new GoapAgent(agent, aiAgent, new ArrayList<>(actions));

//        GoapPlanner planner = new GoapPlanner();
//        Queue<GoapAction> result = planner.plan(agent, actions, aiAgent.getWorldState(), aiAgent.createGoalState());
//
    }

    @Override
    protected void onUpdate(double tpf) {
        goapAgent.update();
    }

    private void initObjects() {
        player = Entities.builder()
                .at(300, 300)
                .viewFromNode(new Text("PLAYER"))
                .buildAndAttach(getGameWorld());

        coin = Entities.builder()
                .at(500, 100)
                .viewFromNode(new Text("COIN"))
                .buildAndAttach(getGameWorld());

        weapon = Entities.builder()
                .at(30, 500)
                .viewFromNode(new Text("WEAPON"))
                .buildAndAttach(getGameWorld());

        agent = Entities.builder()
                .at(400, 400)
                .viewFromNode(new Text("AGENT"))
                .buildAndAttach(getGameWorld());
    }

    private HashSet<GoapAction> initActions() {
        HashSet<GoapAction> actions = new HashSet<>();
        actions.add(new PickupWeapon());
        actions.add(new KillPlayer());
        actions.add(new PickupCoin());
        actions.add(new WanderAction());
        actions.add(new BlowUpAction());
        actions.add(new WaitAction());
        return actions;
    }

    static State worldState = null;

    private static class AIAgent implements Goap {

        private GameEntity entity;

        public AIAgent(GameEntity entity) {
            this.entity = entity;
        }

        @Override
        public State getWorldState() {

            // TODO: this should be retrieved from the world itself

            if (worldState == null) {
                worldState = new State();
                worldState.add("playerInvincible", true);
                worldState.add("playerAlive", true);
                worldState.add("hasCoin", false);
                worldState.add("hasWeapon", false);
            } else {
                worldState.add("playerAlive", false);
                worldState.add("playerInvincible", false);
                worldState.add("hasCoin", true);
                worldState.add("hasWeapon", true);
            }

            return worldState;
        }

        @Override
        public State createGoalState() {
            State goal = new State();
            goal.add("playerAlive", false);
            return goal;
        }

        @Override
        public void planFailed(State failedGoal) {

        }

        @Override
        public void planFound(State goal, Queue<GoapAction> actions) {
            System.out.println("Plan found!");
            for (GoapAction action : actions) {
                System.out.println(action);
            }
        }

        @Override
        public void actionsFinished() {
            System.out.println("Actions finished");
        }

        @Override
        public void planAborted(GoapAction aborter) {

        }

        @Override
        public boolean moveAgent(GoapAction nextAction) {

            PositionComponent targetPosition = nextAction.target.getComponentUnsafe(PositionComponent.class);

            if (entity.getPositionComponent().distance(targetPosition) > 5) {
                entity.translate(targetPosition.getValue().subtract(entity.getPosition()).normalize().multiply(0.016 * 100));
                return false;
            }

            nextAction.setInRange(true);
            return true;
        }
    }

    private static class BaseGoapAction extends GoapAction {

        private boolean done = false;

        public BaseGoapAction(String name) {
            super(name);
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
            // pickup / kill instantly
            return true;
        }

        @Override
        public boolean requiresInRange() {
            return true;
        }
    }

    private static class KillPlayer extends BaseGoapAction {
        public KillPlayer() {
            super("KillPlayer");
            addPrecondition("playerInvincible", false);
            addPrecondition("playerAlive", true);
            addPrecondition("hasWeapon", true);
            addEffect("playerAlive", false);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            target = player;
            return true;
        }
    }

    private static class PickupWeapon extends BaseGoapAction {
        public PickupWeapon() {
            super("PickupWeapon");
            addPrecondition("hasWeapon", false);
            addEffect("hasWeapon", true);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            target = weapon;
            return true;
        }
    }

    private static class PickupCoin extends BaseGoapAction {
        public PickupCoin() {
            super("PickupCoin");
            addPrecondition("hasCoin", false);
            addEffect("hasCoin", true);
            addEffect("playerInvincible", false);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            target = coin;
            return true;
        }
    }

    private static class WanderAction extends BaseGoapAction {
        public WanderAction() {
            super("WanderAction");
            addPrecondition("hasCoin", true);
            addEffect("hasCoin", false);
        }
    }

    private static class BlowUpAction extends BaseGoapAction {
        public BlowUpAction() {
            super("BlowUpAction");
            addPrecondition("playerInvincible", true);
            addPrecondition("playerAlive", true);
            addEffect("playerAlive", false);
            cost = 10.0f;
        }
    }

    private static class WaitAction extends BaseGoapAction {
        public WaitAction() {
            super("WaitAction");
            addPrecondition("playerAlive", false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
