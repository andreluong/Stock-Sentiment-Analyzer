package com.andre.stock_sentiment_analyzer.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {

    @Bean(name="firefoxDriver", destroyMethod = "quit")
    public WebDriver firefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        return new FirefoxDriver(options);
    }
}
