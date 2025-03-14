package com.andre.stock_sentiment_analyzer.controller;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.andre.stock_sentiment_analyzer.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @GetMapping
    public List<Article> getArticlesBySymbol() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{symbol}")
    public List<Article> getArticlesBySymbol(@PathVariable String symbol) {
        return articleService.getAllArticles(symbol);
    }

    @DeleteMapping("/all")
    public void delete() {
        articleService.delete();
    }
}
