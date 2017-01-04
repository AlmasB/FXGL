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

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.gameutils.math.GameMath;
import com.almasb.gameutils.math.Vec2;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.gameutils.math.GameMath.clamp;
import static java.lang.Math.*;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FilterSample extends GameApplication {

    private static final int W = 800;
    private static final int H = 600;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(W);
        settings.setHeight(H);
        settings.setTitle("FilterSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up Hue") {
            @Override
            protected void onAction() {
                hue += 5;
                System.out.println(hue);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down Hue") {
            @Override
            protected void onAction() {
                hue -= 5;
                System.out.println(hue);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Up Sat") {
            @Override
            protected void onAction() {
                sat += 0.1;
                System.out.println(sat);
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Down Sat") {
            @Override
            protected void onAction() {
                sat -= 0.1;
                System.out.println(sat);
            }
        }, KeyCode.Q);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {

        double spread = 0.1;

        getGameWorld().addEntity(Entities.builder().viewFromNode(new Rectangle(W, H)).build());




        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        //emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x + t * 10 * spread, y + GameMath.sin(t * 10) * spread));
        emitter.setSpawnPointFunction((i, x, y) -> curveFunction().add(x, y));
        emitter.setColorFunction(() -> Color.color(abs(sin(t)), abs(cos(t)), abs(cos(t))));
        //emitter.setGravityFunction(() -> new Point2D(spread * cos(t), spread * pow(t, 0.55)));

        ParticleEmitter emitter2 = ParticleEmitters.newSparkEmitter();
        emitter2.setSpawnPointFunction((i, x, y) -> new Point2D(x + GameMath.cos(t * 10) * spread, y + pow(t, 0.55)));
        emitter2.setColorFunction(() -> Color.color(abs(sin(t)), 0.75, 1.0));
        emitter2.setEmissionRate(0.33);



        Entities.builder()
                .at(400, 300)
                .with(new ParticleControl(emitter))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(200, 300)
                //.with(new ParticleControl(emitter2))
                .buildAndAttach(getGameWorld());
    }

    private Point2D curveFunction() {
        double x = sin(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));
        double y = cos(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));

        return new Point2D(x, -y).multiply(85);
    }

    private Point2D curveFunction2() {
        double x = 14 * pow(1.5, GameMath.cos(t));
        double y = 7 * sin(cos(1.14 * t)) + pow(sin(t), 3);

        return new Point2D(x, -y).multiply(15);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {
//        Rectangle bg = new Rectangle(800, 600);
//        getGameScene().addUINode(bg);
//
//        Rectangle rect = new Rectangle(800, 30);
//        rect.setTranslateY(260);
//
//        LinearGradient gradient = new LinearGradient(0, 0, 0, 30, false, CycleMethod.REFLECT,
//                new Stop(0.1, Color.BLUE),
//                new Stop(0.2, Color.WHITESMOKE),
//                new Stop(0.8, Color.WHITESMOKE),
//                new Stop(0.9, Color.BLUE.deriveColor(0, 1, 1, 0.7)));
//
//        rect.setFill(gradient);
//        getGameScene().addUINode(rect);
    }

    @Override
    protected void onUpdate(double tpf) {
        t += 0.016f;

//        if (t > 3)
//            t = 0;
    }

    float t = 0;

    double hue = 250;
    double sat = 1.0;

    double position = 0;
    double scale = 0.15;
    double intensity = 1.0;
    double lim = 2;

    private double band(Vec2 pos, double amplitude, double frequency) {
        double wave = scale * amplitude * GameMath.sin((float)(2.0 * PI * frequency * pos.x + t));
        return clamp(amplitude * 0.002, 0.001 / scale, 1.0) * scale / abs(wave - pos.y);
    }

    private void sine() {
        GraphicsContext g = getGameScene().getGraphicsContext();

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, W, H);

        t += 0.016;
        //t *= 0.037;

//
//        varying vec2 surfacePosition;

//                vec2 surfacePosition = vec2(surfacePosition.x * 2., surfacePosition.y);


//        vec3 color = vec3(0.5, 0.5, 1.0);
//        color = color == vec3(0.0)? vec3(0.5, 0.5, 1.0) : color;
//        vec2 pos = surfacePosition;

//        float spectrum = 0.0;

//	#define time time*0.037 + pos.x*10.

//        for(float i = 0.; i < lim; i++){
//            spectrum += band(pos, 1.0*sin(time*0.1), 1.0*sin(time*i/lim))/pow(lim, 0.420 + 0.133*cos(time*3e3+time));
//        }
//        gl_FragColor = vec4(color * spectrum, spectrum);


        for (int y = 1; y < H; y++) {
            for (int x = 1; x < W; x++) {

                Vec2 p = new Vec2(1.0f * x / W - 0.5f, 1.0f * y / H - 0.5f).mulLocal(2);


                double spectrum = 0;

                for (int i = 0; i < lim; i++) {

                    spectrum += band(p, 1.0, i) / pow(1.0, GameMath.cos((float)t));

                    // sin wave
                    //spectrum = -sin(toRadians(t + x)) / (p.y);
                }

                double howFar = abs(1 - spectrum);

                //System.out.println(howFar);

                spectrum = 0.05 / howFar;

                spectrum = clamp(spectrum, 0, 1);

                Color color = Color.color(0.5 * spectrum, 0.5* spectrum, 1.0* spectrum, 1.0);

                g.setFill(color);
                g.fillOval(x, y, 1.5, 1.5);
            }
        }
    }

    private void wave() {
        GraphicsContext g = getGameScene().getGraphicsContext();

        //            "vec2 p = ( gl_FragCoord.xy / resolution.xy ) - 0.5;",
        //
        //            "float sx = 0.3 * (p.x + 0.8) * sin( 900.0 * p.x - 1. * pow(time, 0.55)*5.);",
        //
        //            "float dy = 4./ ( 500.0 * abs(p.y - sx));",
        //
        //            "dy += 1./ (25. * length(p - vec2(p.x, 0.)));",
        //
        //            "gl_FragColor = vec4( (p.x + 0.1) * dy, 0.3 * dy, dy, 1.1 );",

        for (int y = 1; y < 400; y++) {
            for (int x = 1; x < 800; x++) {
                Vec2 p = new Vec2(x / 800.0f - 0.5f, y / 600.0f - 0.5f);

                double sx = (float)(0.3f * (p.x + 0.8) * sin(900 * p.x - pow(t, 0.55) * 5));

                double dy =  4.0f / (500 * abs(p.y - sx));

                dy += 1.0f / (25 * p.sub(new Vec2(p.x, 0)).length());

                if (dy > 1) {
                    dy = 1.0f;
                }

                double red = (p.x + 0.1) * dy;

                if (red < 0) {
                    red = 0;
                }

                g.setFill(Color.color(red, 0.3 * dy, dy, 1.0));
                g.fillOval(x, y, 1.5, 1.5);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
