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

package s32ai;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.settings.GameSettings;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class BehaviorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BehaviorSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private PlayerControl playerControl;
    GameEntity player;

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
        input.addInputMapping(new InputMapping("Test", KeyCode.O, InputModifier.CTRL));


        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        player = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new PlayerControl())
                .buildAndAttach(getGameWorld());

        playerControl = player.getControlUnsafe(PlayerControl.class);

        GameEntity enemy = Entities.builder()
                .at(400, 100)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .with(new AIControl("patrol.tree"))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(600, 100)
                .viewFromNode(new Rectangle(40, 40, Color.LIGHTGOLDENRODYELLOW))
                .with(new AIControl("patrol.tree"))
                .buildAndAttach(getGameWorld());

        //testB(enemy);
        //enemy.addControl();

//        BehaviorTreeLibraryManager libraryManager = BehaviorTreeLibraryManager.getInstance();
//
//        BehaviorTree<GameEntity> actualBehavior = new BehaviorTree<>(createDogBehavior());
//        libraryManager.getLibrary().registerArchetypeTree("guard", actualBehavior);
//
//        tree = libraryManager.createBehaviorTree("guard", enemy);
    }

    private BehaviorTree<GameEntity> tree;

    private BehaviorTree<GameEntity> testB(GameEntity enemy) {

        try {
            BehaviorTreeParser<GameEntity> parser = new BehaviorTreeParser<>(BehaviorTreeParser.DEBUG_HIGH);

            tree = parser.parse(getClass().getResourceAsStream("/assets/ai/patrol.tree"), enemy);
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // if (target close)
    //    do
    //       if (can see player)
    //          attack
    //       else
    //           patrol
    // else
    //    while (!target close)
    //         move to target
    private Task<GameEntity> createDogBehavior () {
        Selector<GameEntity> selector = new Selector<>();

        Sequence<GameEntity> seq1 = new Sequence<>();


        Selector<GameEntity> sel2 = new Selector<>();

        Sequence<GameEntity> seq2 = new Sequence<>();
        seq2.addChild(new CanSeePlayerCondition());
        seq2.addChild(new AttackTask());

        sel2.addChild(seq2);
        sel2.addChild(new PatrolTask());

        seq1.addChild(new TargetCloseCondition());
        seq1.addChild(sel2);

        selector.addChild(seq1);
        selector.addChild(new MoveTask());

        return selector;
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {
        //tree.step();
        //GdxAI.getTimepiece().update((float) tpf);
    }

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void test() {
        System.out.println("O");
    }

    @OnUserAction(name = "Test", type = ActionType.ON_ACTION_BEGIN)
    public void test2() {
        System.out.println("Ctrl + O");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
