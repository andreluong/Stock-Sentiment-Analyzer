package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Article;
import jakarta.annotation.PreDestroy;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.andre.stock_sentiment_analyzer.model.Sentiment.NEUTRAL;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;

@Service
public class NewsScraperService {

    @Autowired
    private TimelineService timelineService;

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final Logger logger = LoggerFactory.getLogger(NewsScraperService.class);

    public NewsScraperService(@Qualifier("firefoxDriver") WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Extracts data from a news story item based on Yahoo Finance
    private Article.ArticleBuilder scrapeNewsItem(WebElement item) {
        Article.ArticleBuilder article = Article.builder();
        try {
            String url = item.findElement(By.cssSelector("a.subtle-link")).getAttribute("href");
            String headline = item.findElement(By.tagName("h3")).getText();
            String description = item.findElement(By.tagName("p")).getText();

            WebElement articleFooter = item.findElement(By.cssSelector("div.publishing"));
            String separator = articleFooter.findElement(By.tagName("i")).getText();
            String[] parts = articleFooter.getText().split("\\s*" + separator + "\\s*");
            String publisher = parts.length > 0 ? parts[0] : "";
            String timeline = parts.length > 1 ? parts[1] : "";

            article.headline(headline)
                    .description(description)
                    .publisher(publisher)
                    .timeline(timelineService.convertToDateTime(timeline).orElse(null))
                    .sentiment(NEUTRAL.getValue()) // Default
                    .url(url);
        } catch (NoSuchElementException noSuchElementException) {
            logger.warn("Error processing news article: {}", noSuchElementException.getMessage());
        }
        return article;
    }

    // Returns a list of the latest news articles of a given stock symbol
    public List<Article> scrape(String symbol) {
        List<Article> articleList = new ArrayList<>();

        String latestNewsUrl = "https://ca.finance.yahoo.com/quote/" + symbol + "/latest-news/";
        logger.info("Start scraping: {}", latestNewsUrl);

        try {
            driver.get(latestNewsUrl);
            wait.until(presenceOfAllElementsLocatedBy(By.cssSelector("li.story-item")));
            List<WebElement> newsItems = driver.findElements(By.cssSelector("li.story-item"));
            newsItems.forEach(item -> articleList.add(
                    scrapeNewsItem(item)
                            .symbol(symbol)
                            .build()
            ));
        } catch (Exception e) {
            logger.error("Error scraping Yahoo Finance for {}: {}", symbol, e.getMessage());
        }

        logger.info("Finished scraping: {}", latestNewsUrl);

        return articleList;
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }
}
