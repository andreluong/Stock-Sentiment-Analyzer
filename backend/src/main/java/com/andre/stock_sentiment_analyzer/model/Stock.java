package com.andre.stock_sentiment_analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(columnDefinition = "VARCHAR(5)")
    private String symbol;

    @Builder.Default
    private int positives = 0;

    @Builder.Default
    private int neutrals = 0;

    @Builder.Default
    private int negatives = 0;

    @Builder.Default
    private double score = 0.0;

    public void incrementCounts(int positives, int neutrals, int negatives) {
        this.positives += positives;
        this.neutrals += neutrals;
        this.negatives += negatives;
    }
}
