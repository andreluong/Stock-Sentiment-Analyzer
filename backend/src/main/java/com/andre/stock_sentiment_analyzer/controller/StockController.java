package com.andre.stock_sentiment_analyzer.controller;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.andre.stock_sentiment_analyzer.model.RedditPost;
import com.andre.stock_sentiment_analyzer.model.Stock;
import com.andre.stock_sentiment_analyzer.model.StockAnalysis;
import com.andre.stock_sentiment_analyzer.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final CSVReaderService csvReaderService = new CSVReaderService("data/listing_status.csv");

    @Autowired
    private StockService stockService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedditService redditService;

    @PostMapping("/validate")
    public Boolean validateSymbol(@RequestParam String symbol) {
        return csvReaderService.findValueFromCSV(symbol, 0); // 0 = Symbol
    }

    @PostMapping("/analyze")
    public StockAnalysis getAnalysis(@RequestParam String symbol) {
        if (!csvReaderService.findValueFromCSV(symbol, 0)) return null;

        List<Article> articleList = stockService.processArticleSentiments(symbol);

        Stock stock = stockService.findBySymbol(symbol)
                .orElseGet(() -> stockService.saveStock(Stock.builder()
                        .symbol(symbol)
                        .build())
                );
        stockService.incrementSentimentCounts(stock, articleList);
        stockService.updateScoreByAge(stock);

        // No new articles found, get the newest 20 from database
        if (articleList.isEmpty()) {
            articleList = articleService.get20LatestArticles(symbol);
        }

        return new StockAnalysis(stock, articleList);
    }

    @GetMapping("/analyze/reddit")
    public String asd() {
        List<RedditPost> posts = redditService.fetch();

        return posts.toString();
    }
}
