package me.alphamode.mcbig.world.phys;

import net.minecraft.world.level.tile.Tile;
import net.minecraft.world.phys.AABB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// So far this doesn't have good coverage or cover numbers with different scale values
class BigAABBTest {

    @Test
    @DisplayName("Test big AABB collision")
    void clipXCollide() {
        Tile stoneTile = Tile.STONE;

        AABB hitbox = AABB.create(0, 1, 0, .5, 2, .5);

        AABB box = stoneTile.getAABB(null, 2, 1, 0);
        BigAABB bigBox = BigAABB.from(box);

        assertEquals(box.clipXCollide(hitbox, 2), bigBox.clipXCollide(BigAABB.from(hitbox), 2));
    }

    @Test
    void clipYCollide() {
        Tile stoneTile = Tile.STONE;

        AABB hitbox = AABB.create(0, 1, 0, .5, 2, .5);

        AABB box = stoneTile.getAABB(null, 0, 2, 0);
        BigAABB bigBox = BigAABB.from(box);

        assertEquals(box.clipYCollide(hitbox, 2), bigBox.clipYCollide(BigAABB.from(hitbox), 2));
    }

    @Test
    void clipZCollide() {
        Tile stoneTile = Tile.STONE;

        AABB hitbox = AABB.create(0, 1, 0, .5, 2, .5);

        AABB box = stoneTile.getAABB(null, 0, 1, 2);
        BigAABB bigBox = BigAABB.from(box);

        assertEquals(box.clipZCollide(hitbox, 2), bigBox.clipZCollide(BigAABB.from(hitbox), 2));
    }
}