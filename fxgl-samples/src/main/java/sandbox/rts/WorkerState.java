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

package sandbox.rts;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum WorkerState implements State<GameEntity> {

    IDLE() {
        @Override
        public void update(GameEntity entity) {
            FXGL.getApp()
                    .getGameWorld()
                    .getClosestEntity(entity, e -> {
                        return Entities.getType(e).isType(RTSSampleType.GOLD_MINE) &&
                                !e.getComponentUnsafe(GoldMineComponent.class).getValue().isFull();
                    })
                    .ifPresent(goldMine -> {
                        entity.setProperty("target", goldMine);
                        changeState(entity, WALK);
                    });
        }
    },

    WALK() {
        @Override
        public void update(GameEntity entity) {
            GameEntity target = entity.getProperty("target");

            entity.translate(target.getPosition()
                            .subtract(entity.getPosition())
                            .normalize()
                            .multiply(100 * 0.016)
            );

            if (entity.isColliding(target)) {
                if (target.isType(RTSSampleType.GOLD_MINE) && entity.getComponentUnsafe(BackpackComponent.class).getValue().getGold() < 150) {
                    changeState(entity, GATHER_GOLD);
                }

                if (target.isType(RTSSampleType.TOWN_HALL)) {
                    changeState(entity, DEPOSIT_GOLD);
                }
            }
        }
    },

    GATHER_GOLD() {
        @Override
        public void enter(GameEntity entity) {
            GameEntity target = entity.getProperty("target");
            GoldMine mine = target.getComponentUnsafe(GoldMineComponent.class).getValue();

            if (mine.isFull()) {
                System.out.println("Mine is Full");
                changeState(entity, IDLE);
            } else {
                entity.getView().setVisible(false);
                mine.onStartGathering();
            }
        }

        @Override
        public void update(GameEntity entity) {
            Backpack backpack = entity.getComponentUnsafe(BackpackComponent.class).getValue();

            backpack.addGold(1);

            if (backpack.getGold() == 150) {
                entity.getView().setVisible(true);
                GameEntity target = entity.getProperty("target");
                GoldMine mine = target.getComponentUnsafe(GoldMineComponent.class).getValue();
                mine.onEndGathering();

                entity.setProperty("target", FXGL.getApp().getGameWorld().getEntitiesByType(RTSSampleType.TOWN_HALL).get(0));
                changeState(entity, WALK);
            }
        }
    },

    DEPOSIT_GOLD() {
        @Override
        public void update(GameEntity entity) {
            Backpack backpack = entity.getComponentUnsafe(BackpackComponent.class).getValue();

            FXGL.getApp().getGameState().increment("gold", backpack.getGold());

            backpack.addGold(-backpack.getGold());

            changeState(entity, IDLE);
        }
    };

    void changeState(GameEntity entity, WorkerState state) {
        entity.getControlUnsafe(FSMControl.class).changeState(state);
    }

    @Override
    public void enter(GameEntity entity) {}

    @Override
    public void update(GameEntity entity) {}

    @Override
    public void exit(GameEntity entity) {}

    @Override
    public boolean onMessage(GameEntity entity, Telegram telegram) {
        return false;
    }
}
