package com.andre.stock_sentiment_analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.andre.stock_sentiment_analyzer.model.Sentiment.NEUTRAL;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(columnDefinition = "TEXT(65535)")
    private String headline;

    @NonNull
    @Column(columnDefinition = "TEXT(65535)")
    private String description;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime timeline;

    private String publisher;

    @Column(columnDefinition = "TEXT(65535)")
    private String url;

    @Builder.Default
    @Column(columnDefinition = "int")
    private int sentiment = NEUTRAL.getValue();

    @NonNull
    @Column(columnDefinition = "VARCHAR(5)")
    private String symbol;

    @Override
    public String toString() {
        String dateTime = timeline != null ? timeline.format(DateTimeFormatter.ISO_DATE_TIME) : "N/A";

        return "Headline: " + headline +
                "\nDescription: " + description +
                "\nPublisher: " + publisher +
                "\nTimeline: " + dateTime +
                "\nUrl: " + url +
                "\n";
    }
}
