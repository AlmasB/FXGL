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

package s06gameplay.levelparsing;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.parser.OldEntityFactory;
import com.almasb.fxgl.parser.EntityProducer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MyGameFactoryOld extends OldEntityFactory {

    private static final int BLOCK_SIZE = 200;

    public MyGameFactoryOld() {
        super('0');
    }

    @EntityProducer('1')
    public Entity newEnemy(int x, int y) {
        return Entities.builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
                .build();
    }

    @EntityProducer('2')
    public Entity newCoin(int x, int y) {
        return Entities.builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.YELLOW))
                .build();
    }
}
