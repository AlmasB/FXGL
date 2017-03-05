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

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

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

//    @Override
//    protected void initGame() {
//        try {
//            HttpClient httpClient = HttpClientBuilder.create().build();
//
//            HttpGet getRequest = new HttpGet("http://192.168.0.2:8080/getscore?name=FXGLTest");
//            getRequest.addHeader("accept", "application/json");
//
//            HttpResponse response = httpClient.execute(getRequest);
//
//            int successCode = 200;
//
//            if (response.getStatusLine().getStatusCode() != successCode) {
//                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
//            }
//
//            ObjectMapper mapper = new ObjectMapper();
//            ScoreData data = mapper.readValue(response.getEntity().getContent(), ScoreData.class);
//
//            System.out.println(data.getName() + " " + data.getScore());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static class ScoreData {
//
//        private final String name;
//        private final int score;
//
//        @JsonCreator
//        public ScoreData(@JsonProperty("name") String name, @JsonProperty("score") int score) {
//            this.name = name;
//            this.score = score;
//        }
//
//        public int getScore() {
//            return score;
//        }
//
//        public String getName() {
//            return name;
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
