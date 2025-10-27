package com.example.supermarktsimulator.model;

/**
 * Representeert een tile met metadata
 */
public class Tile {
    private final int index;
    private final TileType type;
    private final boolean walkable;

    public Tile(int index, TileType type, boolean walkable) {
        this.index = index;
        this.type = type;
        this.walkable = walkable;
    }

    public int getIndex() {
        return index;
    }

    public TileType getType() {
        return type;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isProduct() {
        return type == TileType.PRODUCT;
    }
}