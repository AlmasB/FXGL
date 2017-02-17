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
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.util.Pair;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BezierSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BezierSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {

        // (C1) q1 = q0 + pn - pn-1

        // (C2) q2 = pn-2 + 4(pn - pn-1)



        CubicCurve c1 = new CubicCurve(225, 370, 268, 371, 324, 282, 318, 252);
        c1.setFill(null);
        c1.setStroke(Color.BLUE);


        CubicCurve c2 = new CubicCurve(318, 252, 311, 223, 265, 205, 225, 205);
        c2.setFill(null);
        c2.setStroke(Color.RED);



        CubicCurve c3 = new CubicCurve(225, 205, 185, 205, 139, 223, 132, 252);
        c3.setFill(null);
        c3.setStroke(Color.BROWN);



        CubicCurve c4 = new CubicCurve(132, 252, 126, 281, 182, 370, 225, 370);
        c4.setFill(null);
        c4.setStroke(Color.DARKGREEN);

        getGameScene().addUINodes(c1, c2, c3, c4);






        Point2D[] points = new Point2D[] {
                new Point2D(132, 252),
                new Point2D(225, 370),
                new Point2D(318, 252),
                new Point2D(225, 205),

        };
        Pair<Point2D[], Point2D[]> result = ClosedBezierSpline.GetCurveControlPoints(points);

        for (int i = 0; i < 4; i++) {

            Point2D p1 = i == 3 ? points[0] : points[i+1];

            int j = i == 3 ? 0 : i+1;

            CubicCurve c = new CubicCurve(
                    points[i].getX(), points[i].getY(),
                    result.getKey()[i].getX(), result.getKey()[i].getY(),
                    result.getValue()[j].getX(), result.getValue()[j].getY(),

                    p1.getX(), p1.getY()
            );
            c.setFill(null);
            c.setStroke(Color.BLACK);
            c.setTranslateX(250);

            System.out.println(c.getControlX1() + " "  +c.getControlY1()
            + " " + c.getControlX2() + c.getControlY2());

            getGameScene().addUINodes(c);
        }








//        // q1 - q0 = pn - pn-1 (C1)
//
//        // (C2) q2 = pn-2 + 4(pn - pn-1)
//
//        // pn - pn-1 = -6, -30
//        // 4*x = -24, -120
//
//        CubicCurve c1 = new CubicCurve(318, 252, 333, 281, 326, 321, 225, 370);
//        c1.setFill(null);
//        c1.setStroke(Color.BLUE);
//
//        // pn - pn-1 = 15, 29 x4 = 60, 116
//
//        // 265, 205
//        //CubicCurve c2 = new CubicCurve(318, 252, 311, 222, 244, 250, 225, 205);
//        CubicCurve c2 = new CubicCurve(225, 205, 266, 205, 303, 223, 318, 252);
//        c2.setFill(null);
//        c2.setStroke(Color.RED);
//
//        // pn - pn-1 = 41, 0
//
//        CubicCurve c3 = new CubicCurve(132, 252, 139, 223, 184, 205, 225, 205);
//        //CubicCurve c3 = new CubicCurve(225, 205, 184, 205, 139, 223, 132, 252);
//        c3.setFill(null);
//        c3.setStroke(Color.BROWN);
//
//        // q1-q0 = 7, -29 x4 = 28, -116
//
//        // pn - pn-1 = -101, 49 x4 = -404, 196
//
//        CubicCurve c4 = new CubicCurve(225, 370, 184 - 28, 205 +116, 125, 281, 132, 252);
//        c4.setFill(null);
//        c4.setStroke(Color.BLACK);
//
//        getGameScene().addUINodes(c1, c2, c3, c4);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
