package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.andre.stock_sentiment_analyzer.utility.Constants.POPULAR_STOCKS_STREAM_TOPIC;

@Service
public class ScheduledNewsAnalysisService {

//    @Autowired
//    private NewsProducer newsProducer;

   @Autowired
   private StockService stockService;

   private final int numThreads = Runtime.getRuntime().availableProcessors();

   private final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
   private final Logger logger = LoggerFactory.getLogger(ScheduledNewsAnalysisService.class);

   // Sends popular stocks to producer for analysis
   // 300_000 - 5 mins
   // 10_000 - 10 secs
   @Scheduled(fixedRate = 30_000) // 30 secs
   public void analyzePopularStocks() {
       logger.info("Running scheduled anaylsis on popular stocks");
       List<String> stockSymbols = List.of("NVDA", "AAPL", "META", "AMZN", "GOOG", "TSLA", "MSFT");
       stockSymbols.forEach(symbol ->
               executorService.execute(() -> {
                   stockService.processArticleSentiments(symbol, POPULAR_STOCKS_STREAM_TOPIC);
                   Stock stock = stockService.findBySymbol(symbol)
                           .orElseGet(() -> stockService.saveStock(Stock.builder()
                                   .symbol(symbol)
                                   .build())
                           );
                   stockService.updateScoreByAge(stock);
               }));
   }
}
