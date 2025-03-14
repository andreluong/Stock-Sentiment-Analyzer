package com.andre.stock_sentiment_analyzer.repository;

import com.andre.stock_sentiment_analyzer.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findBySymbol(String symbol);

    List<Article> findTop20BySymbolOrderByTimelineDesc(String symbol);

    boolean existsByHeadlineAndDescription(String headline, String description);
}
