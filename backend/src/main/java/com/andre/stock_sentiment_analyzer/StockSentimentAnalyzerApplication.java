package com.andre.stock_sentiment_analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.andre.stock_sentiment_analyzer.repository")
public class StockSentimentAnalyzerApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockSentimentAnalyzerApplication.class, args);
	}
}
