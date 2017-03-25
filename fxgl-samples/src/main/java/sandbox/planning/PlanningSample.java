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

package sandbox.planning;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlanningSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PlanningSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(500, 100)
                .viewFromNode(new Rectangle(40, 40, Color.GOLD))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(400, 400)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .buildAndAttach(getGameWorld());












        State currentState = new WorldState(Ternary.TRUE, Ternary.TRUE, Ternary.FALSE);

        State goalState = new WorldState(Ternary.UNKNOWN, Ternary.FALSE, Ternary.UNKNOWN);

        PlanningSystem system = new PlanningSystem();

        Op killPlayer = new Op("Kill Player") {
            @NotNull
            @Override
            public State preCondition() {
                return new WorldState(Ternary.FALSE, Ternary.TRUE, Ternary.TRUE);
            }

            @NotNull
            @Override
            public State postCondition() {
                return new WorldState(Ternary.FALSE, Ternary.FALSE, Ternary.TRUE);
            }
        };

        Op collectCoin = new Op("Collect Coin") {
            @NotNull
            @Override
            public State preCondition() {
                return new WorldState(Ternary.TRUE, Ternary.TRUE, Ternary.UNKNOWN);
            }

            @NotNull
            @Override
            public State postCondition() {
                return new WorldState(Ternary.FALSE, Ternary.TRUE, Ternary.UNKNOWN);
            }
        };

        Op collectPowerup = new Op("Collect Powerup") {
            @NotNull
            @Override
            public State preCondition() {
                return new WorldState(Ternary.FALSE, Ternary.TRUE, Ternary.UNKNOWN);
            }

            @NotNull
            @Override
            public State postCondition() {
                return new WorldState(Ternary.FALSE, Ternary.TRUE, Ternary.TRUE);
            }
        };

        /////////////////////////////////////////////

        system.addOperation(killPlayer);
        system.addOperation(collectPowerup);
        system.addOperation(collectCoin);

        Plan plan = system.createPlan(currentState, goalState);
        plan.getActions().forEach(op -> System.out.println(op.getName()));
    }

    private static class WorldState extends State {

        private Ternary playerInvincible;
        private Ternary playerAlive;
        private Ternary enemyCanKillPlayer;

        public WorldState(Ternary playerInvincible, Ternary playerAlive, Ternary enemyCanKillPlayer) {
            this.playerInvincible = playerInvincible;
            this.playerAlive = playerAlive;
            this.enemyCanKillPlayer = enemyCanKillPlayer;
        }

        @Override
        public boolean matches(@NotNull State otherState) {
            WorldState other = (WorldState) otherState;

//            System.out.println("MATCHES:");
//            System.out.println(playerAlive + " " + playerInvincible + " " + enemyCanKillPlayer);
//            System.out.println(other.playerAlive + " " + other.playerInvincible + " " + other.enemyCanKillPlayer);
//
//            System.out.println(playerAlive.weakEquals(other.playerAlive)
//                    && playerInvincible.weakEquals(other.playerInvincible)
//                    && enemyCanKillPlayer.weakEquals(other.enemyCanKillPlayer));

            return playerAlive.weakEquals(other.playerAlive)
                    && playerInvincible.weakEquals(other.playerInvincible)
                    && enemyCanKillPlayer.weakEquals(other.enemyCanKillPlayer);
        }

        @Override
        public int hash() {
            return Objects.hash(playerAlive, playerInvincible, enemyCanKillPlayer);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
