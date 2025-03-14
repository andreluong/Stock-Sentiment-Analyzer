package com.andre.stock_sentiment_analyzer.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class StockAnalysis {
    @NonNull
    private Stock stock;

    @NonNull
    private List<Article> articleList;
}
