package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.andre.stock_sentiment_analyzer.model.Stock;
import com.andre.stock_sentiment_analyzer.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentimentService {
    private static final double DECAY_RATE = 0.3;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TimelineService timelineService;

    private final Logger logger = LoggerFactory.getLogger(SentimentService.class);

    // TODO: Latest news have more influence on score than older news
    public double getSentimentScore(Stock stock, List<Article> newArticles) {
        logger.info("Old score: {}", stock.getScore());

        double weightedSum = 0.0;
        double totalWeight = 0.0;

        for (Article a : newArticles) {
            long age = timelineService.convertToDays(a.getTimeline());
            if (age >= 0) {
                double decayFactor = Math.exp(-DECAY_RATE * age);
                weightedSum += a.getSentiment() * decayFactor;
                totalWeight += decayFactor;
            }
        }

        return totalWeight != 0
                ? weightedSum / totalWeight
                : stock.getScore();
    }
}
