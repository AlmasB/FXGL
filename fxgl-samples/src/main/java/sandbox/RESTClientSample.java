/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.settings.GameSettings;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.KeyCode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.List;

import static com.almasb.fxgl.app.DSLKt.onKeyDown;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RESTClientSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RESTClientSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.Q, "Get Top", () -> {
            Async.start(this::getTop);
        });

        onKeyDown(KeyCode.E, "Put New Score", () -> {
            Async.start(this::putNewScore);
        });
    }

    private void getTop() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpGet getRequest = new HttpGet("http://fxgl-top.herokuapp.com/top?gameName=test");
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            int successCode = 200;

            if (response.getStatusLine().getStatusCode() != successCode) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            Class<?> clz = ScoreData.class;
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);

            List<ScoreData> data = mapper.readValue(response.getEntity().getContent(), type);

            data.forEach(entry -> {
                System.out.println(entry.getName() + " " + entry.getScore());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putNewScore() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpPut getRequest = new HttpPut("http://fxgl-top.herokuapp.com/newscore?gameName=test&name=Almas&score=1000");
            getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            int successCode = 200;

            if (response.getStatusLine().getStatusCode() != successCode) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            ScoreData entry = mapper.readValue(response.getEntity().getContent(), ScoreData.class);

            System.out.println(entry.getName() + " " + entry.getScore());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ScoreData {

        private final String name;
        private final int score;

        @JsonCreator
        public ScoreData(@JsonProperty("name") String name, @JsonProperty("score") int score) {
            this.name = name;
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}