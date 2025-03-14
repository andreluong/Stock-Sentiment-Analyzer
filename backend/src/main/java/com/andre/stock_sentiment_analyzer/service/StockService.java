package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.andre.stock_sentiment_analyzer.model.Sentiment;
import com.andre.stock_sentiment_analyzer.model.Stock;
import com.andre.stock_sentiment_analyzer.repository.StockRepository;
import com.cohere.api.types.TextContent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.andre.stock_sentiment_analyzer.model.Sentiment.*;
import static com.andre.stock_sentiment_analyzer.utility.Constants.NEWS_STREAM_TOPIC;
import static com.andre.stock_sentiment_analyzer.utility.Constants.POPULAR_STOCKS_STREAM_TOPIC;

@Service
public class StockService {

    @Autowired
    private NewsScraperService newsScraperService;

    @Autowired
    private CohereService cohereService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private SentimentService sentimentService;

    private final Logger logger = LoggerFactory.getLogger(StockService.class);

    // Builds a map of sentiments and their respective headlines
    private Map<Sentiment, List<String>> buildSentimentMap(TextContent content) {
        // Split groups based on sentiment categories
        String groupRegex = Arrays.stream(Sentiment.values())
                .map(Sentiment::getName)
                .collect(Collectors.joining("|", "(?=", ":)"));

        // Separate each sentiment category
        List<String> groups = Arrays.stream(content.getText()
                        .split(groupRegex))
                .filter(group -> !group.isBlank())
                .map(String::trim)
                .toList();

        Map<Sentiment, List<String>> classificationMap = new HashMap<>();
        groups.forEach(group -> {
            String[] lines = group.split("\n");
            Optional<Sentiment> optSentiment = fromString(lines[0].replace(":", ""));
            if (optSentiment.isEmpty()) {
                System.out.println("Found invalid sentiment: " + lines[0]);
                return;
            }

            List<String> headlines = Arrays.stream(lines)
                    .filter(line -> line.startsWith("-"))
                    .map(line -> line.replaceAll("(?m)^-\\s+", ""))
                    .toList();

            classificationMap.put(optSentiment.get(), headlines);
        });
        return classificationMap;
    }

    // Builds a list of classified articles from comparing the map's headlines list to the articles list
    private List<Article> setSentimentToArticles(Map<Sentiment, List<String>> sentimentMap, List<Article> articleList) {
        List<Article> newArticleList = new ArrayList<>();

        // Traverse each classification group's articles and sets their sentiment to an article list copy
        sentimentMap.forEach((sentiment, headlines) -> {
            for (String headline : headlines) {
                // Find article with matching headline and set sentiment
                articleList.stream()
                        .filter(item -> item.getHeadline().equals(headline))
                        .findFirst()
                        .ifPresent(article -> {
                            article.setSentiment(sentiment.getValue());
                            newArticleList.add(article);
                        });
            }
        });
        return newArticleList;
    }

    // Process the sentiment of a stock's latest news and return as map
    public List<Article> processArticleSentiments(String symbol) {
        logger.info("Start processing symbol {}", symbol);

        // Filter duplicate articles
        List<Article> articleList = newsScraperService
                .scrape(symbol)
                .stream()
                .filter(article -> !articleService.findArticle(article))
                .toList();

        logger.info("Found {} new articles for {}", articleList.size(), symbol);

        Optional<TextContent> content = cohereService.classifyArticles(symbol, articleList);
        if (content.isEmpty()) {
            logger.error("No text content found from Cohere response: {}", articleList);
            return Collections.emptyList();
        }

        Map<Sentiment, List<String>> classificationMap = buildSentimentMap(content.get());

        // Set sentiment to each article and update it in the database
        List<Article> sentimentalArticleList = setSentimentToArticles(classificationMap, articleList);
        logger.warn("new sentimental list: {}", sentimentalArticleList.size());
        sentimentalArticleList.forEach(articleService::saveArticle);

        logger.info("Successfully analyzed {} articles on {}", sentimentalArticleList.size(), symbol);

        return sentimentalArticleList;
    }

    // Sends data as JSON object to client WebSocket
    public void processArticleSentiments(String symbol, String topic) {
        List<Article> articleList = processArticleSentiments(symbol);

        JSONObject json = new JSONObject();
        json.put("symbol", symbol);
        json.put("articleList", articleList);

        switch (topic) {
            case NEWS_STREAM_TOPIC -> {
                messagingTemplate.convertAndSend("/topic/news", json.toString());
                logger.info("Finished sending data to news topic");
            }
            case POPULAR_STOCKS_STREAM_TOPIC -> {
                messagingTemplate.convertAndSend("/topic/popular", json.toString());
                logger.info("Finished sending data to popular topic");
            }
            default -> logger.error("Unknown topic found: {}", topic);
        }
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Optional<Stock> findBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }

    public void incrementSentimentCounts(Stock stock, List<Article> articleList) {
        if (articleList.isEmpty()) return;

        int positives = 0, neutrals = 0, negatives = 0;
        for (Article article : articleList) {
            Sentiment sentiment = fromValue(article.getSentiment()).orElse(NEUTRAL);
            switch (sentiment) {
                case POSITIVE -> positives++;
                case NEUTRAL -> neutrals++;
                case NEGATIVE -> negatives++;
            }
        }
        stock.incrementCounts(positives, neutrals, negatives);
    }

    // Update score with latest articles
    public void updateStockScore(Stock stock, List<Article> articleList) {
        double updatedScore = sentimentService.getSentimentScore(stock, articleList);
        logger.warn("updated score: {}", updatedScore);
        stock.setScore(updatedScore);
    }

    // Update all scores based on age
    public void updateScoreByAge(Stock stock) {
        List<Article> articleList = articleService.getAllArticles(stock.getSymbol());
        double updatedScore = sentimentService.getSentimentScore(stock, articleList);
        logger.info("Updated score by age: {}", updatedScore);
        stock.setScore(updatedScore);
    }
}
