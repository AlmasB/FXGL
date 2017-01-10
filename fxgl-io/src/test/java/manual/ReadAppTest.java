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

package manual;

import com.almasb.fxgl.io.FS;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ReadAppTest extends Application {

    private Parent createContent() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Reading from ...");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Text text = new Text();

        Button btn = new Button("Say Hi");
        btn.setOnAction(e -> {
            System.out.println("Hi");
        });

        Button btn2 = new Button("READ");
        btn2.setOnAction(e -> {

            FS.loadDirectoryNamesTask("./", false)
                    .onSuccess(names -> System.out.println(names))
                    .onFailure(error -> System.out.println(error))
                    .executeAsync(executor);

//            new IOTask<String>() {
//
//                @Override
//                protected String onExecute() throws Exception {
//                    System.out.println(Thread.currentThread().getName());
//
//                    return Files.readAllLines(Paths.get("reading.txt")).get(0);
//                }
//            }
//                    .onSuccess(line -> {
//                        text.setText(line);
//                        executor.shutdownNow();
//                    })
//                    .onFailure(System.out::println)
//                    //.execute();
//                    //.executeAsync(executor);
//                    .executeAsyncWithDialog(executor, new UIDialogHandler() {
//
//                        @Override
//                        public void show() {
//                            Platform.runLater(alert::show);
//                        }
//
//                        @Override
//                        public void dismiss() {
//                            Platform.runLater(alert::close);
//                        }
//                    });
        });

        VBox vbox = new VBox(btn, btn2, text);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(400, 400);
        return vbox;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }
}
