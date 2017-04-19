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
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FractalGenerationsSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("FractalGenerationsSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    private Line line;

    @Override
    protected void initUI() {
        setSeedAndReinitialize();

        line = new Line(400, getHeight(), 400, getHeight() - 20);
        branch(120, line, -90, true);
    }

    private float t = 0;

    private List<Node> nodes = new ArrayList<>();

    @Override
    protected void onUpdate(double tpf) {
        t += tpf;


        nodes.forEach(getGameScene()::removeUINode);
        nodes.clear();

        branch(120, line, -90, true);



        
//        getGameScene().getUINodes()
//                .stream()
//                .skip(1)
//                .map(n -> (Line) n)
//                .forEach(l -> l.setEndX(l.getStartX() + noise1D(t) * 50));
//                .map(n -> (Rotate) n.getTransforms().get(0))
//                .forEach(r -> {
//                    r.setAngle(noise1D(t) * 10);
//                });
    }

    // adapted from Nature of Code
    private void branch(float len, Line parent, double angle, boolean left) {

        Vec2 v = Vec2.fromAngle(angle);
        v.normalizeLocal().mulLocal(len);

        Line line = new Line(parent.getEndX(), parent.getEndY(), parent.getEndX() + v.x, parent.getEndY() + v.y);
        line.startXProperty().bind(parent.endXProperty());
        line.startYProperty().bind(parent.endYProperty());

        Rotate r = new Rotate();
        r.pivotXProperty().bind(parent.endXProperty());
        r.pivotYProperty().bind(parent.endYProperty());

        //line.getTransforms().add(r);

        line.setStrokeWidth(len / 120.0 * 6.5);

        getGameScene().addUINode(line);
        nodes.add(line);

        len *= 0.66;

        if (len > 2) {

            // Here the angle is controlled by perlin noise
            // This is a totally arbitrary way to do it, try others!
            float theta = noise1D(t) * 3 * FXGLMath.radiansToDegrees;

            //if (n%2==0) theta *= -1;

            if (left) {
                branch(len, line, theta, true);
                branch(len, line, -theta, false);
            } else {
                branch(len, line, theta, true);
            }

            if (FXGLMath.randomBoolean()) {
                branch(len, line, theta, true);
            }
        }
    }

    // from CRYtek
    public static final int NOISE_TABLE_SIZE = 256;
    public static final int NOISE_MASK = 255;

    private float[] gx = new float[NOISE_TABLE_SIZE];
    private float[] gy = new float[NOISE_TABLE_SIZE];

    void setSeedAndReinitialize() {
        // Generate the gradient lookup tables
        for (int i = 0; i < NOISE_TABLE_SIZE; i++) {
            // Ken Perlin proposes that the gradients are taken from the unit
            // circle/sphere for 2D/3D.
            // So lets generate a good pseudo-random vector and normalize it

            Vec2 v = new Vec2();
            // cry_frand is in the 0..1 range
            v.x = -0.5f + FXGLMath.random();
            v.y = -0.5f + FXGLMath.random();
            v.normalizeLocal();

            gx[i] = v.x;
            gy[i] = v.y;
        }
    }

    // (-0.5, 0.5) t > 0
    private float noise1D(float x) {
        // Compute what gradients to use
        int qx0 = (int)Math.floor(x);
        int qx1 = qx0 + 1;
        float tx0 = x - (float)qx0;
        float tx1 = tx0 - 1;

        // Make sure we don't come outside the lookup table
        qx0 = qx0 & NOISE_MASK;
        qx1 = qx1 & NOISE_MASK;

        // Compute the dotproduct between the vectors and the gradients
        float v0 = gx[qx0] * tx0;
        float v1 = gx[qx1] * tx1;

        // Modulate with the weight function
        float wx = (3 - 2 * tx0) * tx0 * tx0;
        float v = v0 - wx * (v0 - v1);

        return v;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
