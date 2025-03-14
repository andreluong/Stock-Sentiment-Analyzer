package com.andre.stock_sentiment_analyzer.model;

import java.util.Arrays;
import java.util.Optional;

public enum Sentiment {
    POSITIVE("Positive", 1),
    NEGATIVE("Negative", -1),
    NEUTRAL("Neutral", 0);

    private final String name;
    private final int value;

    Sentiment(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public static Optional<Sentiment> fromString(String text) {
        return Arrays.stream(Sentiment.values())
                .filter(s -> s.name.equalsIgnoreCase(text.trim()))
                .findFirst();
    }

    public static Optional<Sentiment> fromValue(int value) {
        return Arrays.stream(Sentiment.values())
                .filter(s -> s.getValue() == value)
                .findFirst();
    }
}
