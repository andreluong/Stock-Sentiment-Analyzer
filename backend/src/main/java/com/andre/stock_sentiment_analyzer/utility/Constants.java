package com.andre.stock_sentiment_analyzer.utility;

public final class Constants {
    private Constants() {}

    public static final String NEWS_STREAM_TOPIC = "news-stream";
    public static final int NEWS_STREAM_TOPIC_NUM_PARTITIONS = 1;

    public static final String POPULAR_STOCKS_STREAM_TOPIC = "popular-stocks-stream";
    public static final int POPULAR_STOCKS_STREAM_NUM_PARTITIONS = 1;

}
