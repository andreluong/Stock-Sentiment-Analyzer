package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.andre.stock_sentiment_analyzer.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getAllArticles(String symbol) {
        return articleRepository.findBySymbol(symbol);
    }

    public List<Article> get20LatestArticles(String symbol) {
        return articleRepository.findTop20BySymbolOrderByTimelineDesc(symbol);
    }

    public boolean findArticle(Article article) {
        return articleRepository.existsByHeadlineAndDescription(article.getHeadline(), article.getDescription());
    }

    // TODO: If an article exists, it should update the timeline
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }

    public void delete() {
        articleRepository.deleteAll();
    }
}
