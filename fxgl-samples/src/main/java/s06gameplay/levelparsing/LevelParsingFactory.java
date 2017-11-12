/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.levelparsing;

import com.almasb.fxgl.entity.SpawnSymbol;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.TextEntityFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LevelParsingFactory implements TextEntityFactory {

    private static final int BLOCK_SIZE = 200;

    @Spawns("Enemy")
    @SpawnSymbol('1')
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
                .build();
    }

    @Spawns("Coin")
    @SpawnSymbol('2')
    public Entity newCoin(SpawnData data) {
        return Entities.builder()
                .at(data.getX(), data.getY())
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.YELLOW))
                .build();
    }

    @Override
    public char emptyChar() {
        return '0';
    }

    @Override
    public int blockWidth() {
        return BLOCK_SIZE;
    }

    @Override
    public int blockHeight() {
        return BLOCK_SIZE;
    }
}
