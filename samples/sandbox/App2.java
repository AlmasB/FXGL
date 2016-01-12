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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.InGameWindow;
import com.almasb.fxgl.ui.UIFactory;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;
import jfxtras.scene.control.window.WindowIcon;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class App2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
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
    }

    @Override
    protected void initAssets() {}

    private Entity e;

    @Override
    protected void initGame() {
        e = Entity.noType();
        e.addControl(new SomeControl());

        getGameWorld().addEntity(e);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {}

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void openWindow() {



        Window w = new Window("Character Info");
        //w.setMovable(false);
        //w.setResizableWindow(false);


        w.getLeftIcons().add(new CloseIcon(w));

        // .. or to the right
        w.getRightIcons().add(new MinimizeIcon(w));

//        // you can also add custom icons
//        WindowIcon customIcon = new WindowIcon();
//        customIcon.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent t) {
//                // we add a nice scale transition
//                // (it doesn't do anything useful but it is cool!)
//                ScaleTransition st = new ScaleTransition(Duration.seconds(2), w);
//                st.setFromX(w.getScaleX());
//                st.setFromY(w.getScaleY());
//                st.setToX(0.1);
//                st.setToY(0.1);
//                st.setAutoReverse(true);
//                st.setCycleCount(2);
//                st.play();
//            }
//        });
//
//        // finally, we add our custom icon
//        w.getRightIcons().add(customIcon);

        // note: we actually could style the icon via css
        //       see the javafx documentation on how to do that

        // set the window position to 10,10 (coordinates inside canvas)
        w.setTranslateX(10);
        w.setTranslateY(10);

        // define the initial window size
        w.setPrefSize(300, 200);









//        Text text = UIFactory.newText("Are you sure?", 24);
//
//        Button btnYes = UIFactory.newButton("YES");
//        Button btnNo = UIFactory.newButton("NO");
//
//        HBox hbox = new HBox(20, btnYes, btnNo);
//        hbox.setAlignment(Pos.CENTER);
//
//        VBox vbox = new VBox(10, text, hbox);
//        vbox.setAlignment(Pos.CENTER);
//
//        window.setContentPane(vbox);
//
        InGameWindow w2 = new InGameWindow("Skills");
        w2.setPrefSize(300, 200);
        w2.setTranslateX(400);
        w2.setBackgroundColor(Color.BLACK);

        getGameScene().addUINodes(w, w2);

        e.getControlUnsafe(SomeControl.class).doIt();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
