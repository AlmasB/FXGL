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

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.settings.GameSettings;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Parallel;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.AlwaysFail;
import com.badlogic.gdx.ai.btree.decorator.Include;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.ai.utils.random.TriangularIntegerDistribution;
import javafx.animation.PathTransition;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.*;
import javafx.util.Duration;
import sandbox.task.BarkTask;
import sandbox.task.WalkTask;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class App3 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("App3");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
        input.addInputMapping(new InputMapping("Test", KeyCode.O, InputModifier.CTRL));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
//        GameEntity entity = Entities.builder()
//                .at(100, 100)
//                .viewFromNode(new Rectangle(40, 40))
//                .buildAndAttach(getGameWorld());
//
//        Entities.animationBuilder()
//                .duration(Duration.seconds(3))
//                .delay(Duration.seconds(2))
//                .repeat(3)
//                .rotate(entity)
//                //.rotateFrom(30)
//                .rotateTo(315)
//                .build()
//                .play();
//
//        Path path = new Path();
//        path.getElements().addAll(new MoveTo(getWidth() - 50, 20),
//                new CubicCurveTo(380, 0, 380, 120, 200, 120),
//                new CubicCurveTo(0, 120, 0, 240, 380, 240),
//                new LineTo(getWidth() - 50, getHeight()-50));
//
//        Rectangle node = new Rectangle();
//
//        PathTransition pt = new PathTransition(Duration.seconds(4), path, node);
//        pt.play();
//
//        entity.getPositionComponent().xProperty().bind(node.translateXProperty());
//        entity.getPositionComponent().yProperty().bind(node.translateYProperty());


        BehaviorTreeLibraryManager libraryManager = BehaviorTreeLibraryManager.getInstance();
        BehaviorTreeLibrary library = new BehaviorTreeLibrary(BehaviorTreeParser.DEBUG_HIGH);

        registerDogBehavior(library);

        libraryManager.setLibrary(library);

        tree = libraryManager.createBehaviorTree("dog", new Dog("Buddy"));

        //BehaviorTreeViewer<?> treeViewer = createTreeViewer(tree.getObject().name, tree, true, skin);

        //return new ScrollPane(treeViewer, skin);

        tree.run();

        getMasterTimer().runAtInterval(tree::step, Duration.seconds(3));
    }

    BehaviorTree<Dog> tree;

    private void registerDogBehavior (BehaviorTreeLibrary library) {

        Include<Dog> include = new Include<Dog>();
        include.lazy = false;
        include.subtree = "dog.actual";

        BehaviorTree<Dog> includeBehavior = new BehaviorTree<Dog>(include);
        library.registerArchetypeTree("dog", includeBehavior);

        BehaviorTree<Dog> actualBehavior = new BehaviorTree<Dog>(createDogBehavior());
        library.registerArchetypeTree("dog.actual", actualBehavior);
    }

    private static Task<Dog> createDogBehavior () {
        Selector<Dog> selector = new Selector<Dog>();

        //Parallel<Dog> parallel = new Parallel<Dog>();
        //selector.addChild(parallel);

//        CareTask care = new CareTask();
//        care.urgentProb = 0.8f;
//        parallel.addChild(care);
//        parallel.addChild(new AlwaysFail<Dog>(new RestTask()));

        Sequence<Dog> sequence = new Sequence<Dog>();
        selector.addChild(sequence);

        BarkTask bark1 = new BarkTask();
        //bark1.times = new TriangularIntegerDistribution(1, 5, 2);

        sequence.addChild(bark1);
        sequence.addChild(new WalkTask());
        //sequence.addChild(new BarkTask());
        //sequence.addChild(new MarkTask());

        return selector;
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {
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
