package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.RedditPost;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

// Fetches data from Reddit JSON endpoint
@Service
public class RedditService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.reddit.com/r/stocks/.json")
            .build();

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(RedditService.class);

    public List<RedditPost> fetch() {
        logger.info("Start fetching posts from stocks subreddit...");

        var list = webClient.get()
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(jsonNode -> Flux.fromIterable(jsonNode.path("data").path("children")))
                .map(child -> objectMapper.convertValue(child.path("data"), RedditPost.class))
                .filter(RedditPost::meetsRequirements)
                .collectList()
                .block();

        logger.info("Finished fetching posts");

        return list;
    }

}
