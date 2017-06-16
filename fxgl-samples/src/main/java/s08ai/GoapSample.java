/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai;

import com.almasb.fxgl.ai.goap.GoapAction;
import com.almasb.fxgl.ai.goap.GoapAgent;
import com.almasb.fxgl.ai.goap.GoapControl;
import com.almasb.fxgl.ai.goap.State;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Map;
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

    private static GameEntity player, coin, weapon, agent, guard;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerInvincible", true);
        vars.put("playerAlive", true);
    }

    @Override
    protected void initGame() {

        initObjects();
        HashSet<GoapAction> actions = initActions();

        GoapControl goapControl = new GoapControl(new KillerAgent(), 100, actions);
        agent.addControl(goapControl);

        guard.addControl(new GoapControl(new GuardAgent(), 125, actions));
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

        guard = Entities.builder()
                .at(600, 50)
                .viewFromNode(new Text("GUARD"))
                .buildAndAttach(getGameWorld());

        agent.setProperty("hasWeapon", false);
        agent.setProperty("hasCoin", false);
    }

    private HashSet<GoapAction> initActions() {
        HashSet<GoapAction> actions = new HashSet<>();
        actions.add(new PickupWeapon());
        actions.add(new KillPlayer());
        actions.add(new PickupCoin());
        actions.add(new ReviveAction());
        actions.add(new BlowUpAction());
        actions.add(new WaitAction());
        return actions;
    }

    private class KillerAgent implements GoapAgent {

        @Override
        public State obtainWorldState(Entity entity) {
            State worldState = new State();
            worldState.add("playerInvincible", getGameState().getBoolean("playerInvincible"));
            worldState.add("playerAlive", getGameState().getBoolean("playerAlive"));
            worldState.add("hasWeapon", entity.getProperty("hasWeapon"));
            worldState.add("hasCoin", entity.getProperty("hasCoin"));

            return worldState;
        }

        @Override
        public State createGoalState(Entity entity) {
            State goal = new State();
            goal.add("playerAlive", false);
            return goal;
        }

        @Override
        public void planFailed(Entity entity, State failedGoal) {}

        @Override
        public void planFound(Entity entity, State goal, Queue<GoapAction> actions) {
            System.out.println("Plan found!");
            for (GoapAction action : actions) {
                System.out.println(action);
            }
        }

        @Override
        public void actionsFinished(Entity entity) {
            entity.removeControl(GoapControl.class);
        }

        @Override
        public void planAborted(Entity entity, GoapAction aborter) {}
    }

    private class GuardAgent implements GoapAgent {

        @Override
        public State obtainWorldState(Entity entity) {
            State worldState = new State();
            worldState.add("playerInvincible", getGameState().getBoolean("playerInvincible"));
            worldState.add("playerAlive", getGameState().getBoolean("playerAlive"));

            return worldState;
        }

        @Override
        public State createGoalState(Entity entity) {
            State goal = new State();

            if (!getGameState().getBoolean("playerAlive")) {
                goal.add("playerAlive", true);
            } else {
                goal.add("wait", true);
            }

            return goal;
        }

        @Override
        public void planFailed(Entity entity, State failedGoal) {}

        @Override
        public void planFound(Entity entity, State goal, Queue<GoapAction> actions) {
            System.out.println("Plan found! Guard");
            for (GoapAction action : actions) {
                System.out.println(action);
            }
        }

        @Override
        public void actionsFinished(Entity entity) {
            entity.removeControl(GoapControl.class);
        }

        @Override
        public void planAborted(Entity entity, GoapAction aborter) {}
    }

    private static class BaseGoapAction extends GoapAction {

        private boolean done = false;

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
            addPrecondition("playerInvincible", false);
            addPrecondition("playerAlive", true);
            addPrecondition("hasWeapon", true);
            addEffect("playerAlive", false);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            setTarget(player);
            return true;
        }

        @Override
        public boolean perform(Entity agent) {
            FXGL.getApp().getGameState().setValue("playerAlive", false);
            return super.perform(agent);
        }
    }

    private static class PickupWeapon extends BaseGoapAction {
        public PickupWeapon() {
            addPrecondition("hasWeapon", false);
            addEffect("hasWeapon", true);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            setTarget(weapon);
            return true;
        }

        @Override
        public boolean perform(Entity agent) {
            agent.setProperty("hasWeapon", true);
            return super.perform(agent);
        }
    }

    private static class PickupCoin extends BaseGoapAction {
        public PickupCoin() {
            addPrecondition("hasCoin", false);
            addEffect("hasCoin", true);
            addEffect("playerInvincible", false);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            setTarget(coin);
            return true;
        }

        @Override
        public boolean perform(Entity agent) {
            agent.setProperty("hasCoin", true);
            FXGL.getApp().getGameState().setValue("playerInvincible", false);
            return super.perform(agent);
        }
    }

    private static class ReviveAction extends BaseGoapAction {
        public ReviveAction() {
            addPrecondition("playerAlive", false);
            addEffect("playerAlive", true);
        }

        @Override
        public boolean checkProceduralPrecondition(Entity agent) {
            setTarget(player);
            return true;
        }
    }

    private static class BlowUpAction extends BaseGoapAction {
        public BlowUpAction() {

            setCost(10.0f);
        }
    }

    private static class WaitAction extends BaseGoapAction {
        public WaitAction() {
            addEffect("wait", true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
