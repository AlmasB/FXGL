/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.net.DownloadCallback;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.net.MalformedURLException;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to download a file while showing a progress dialog.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FileDownloadSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        var imageView = new ImageView();

        var btnDownload = new Button("Download");
        btnDownload.setOnAction(e -> {
            var downloadInfo = new DownloadCallback();
            var task = getNetService().downloadTask(
                    "https://raw.githubusercontent.com/AlmasB/git-server/master/storage/images/fxgl_promo.jpg",
                    "testfile.jpg",
                    downloadInfo
            )
                    .onFailure(ex -> System.out.println("Failed to download: " + ex))
                    .onSuccess(file -> {
                        try {
                            var image = getAssetLoader().loadImage(file.toUri().toURL());
                            imageView.setImage(image);

                        } catch (MalformedURLException ue) {
                            ue.printStackTrace();
                        }
                    });

            getDialogService().showProgressBox("Downloading file", downloadInfo.progressProperty(), () -> {
                System.out.println("Done!");
            });

            getTaskService().runAsync(task);
        });

        addUINode(imageView);
        addUINode(btnDownload);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
