package com.example.supermarktsimulator.model;

/**
 * Enum voor de verschillende types producten
 */
public enum ProductType {
    VLEES("🥩"),
    FRISDRANK("🥤"),
    GROENTE_FRUIT("🍎");

    private final String emoji;

    ProductType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}