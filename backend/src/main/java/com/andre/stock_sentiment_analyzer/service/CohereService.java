package com.andre.stock_sentiment_analyzer.service;

import com.andre.stock_sentiment_analyzer.model.Article;
import com.cohere.api.Cohere;
import com.cohere.api.resources.v2.requests.V2ChatRequest;
import com.cohere.api.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CohereService {
    private final Cohere cohere;
    private final Logger logger = LoggerFactory.getLogger(CohereService.class);

    public CohereService(@Value("${cohere.api.key}") String cohereApiKey) {
        if (cohereApiKey == null || cohereApiKey.isBlank()) {
            logger.error("\"Cohere API key is missing\"");
            throw new IllegalArgumentException("Cohere API key is missing");
        }

        cohere = Cohere.builder()
                .token(cohereApiKey)
                .clientName("analyzer")
                .build();
    }

    private List<ChatMessageV2> buildChatMessages(String symbol, List<Article> articleList) {
        String joinedArticles = articleList.stream()
                .map(Article::toString)
                .collect(Collectors.joining("\n"));

        String prompt = "Classify each news item on " + symbol + " as positive, negative, or neutral for the stock. " +
                "List out the classification groups with their unmodified headlines. " +
                "The groups should be labelled: \"Positive:\", \"Negative:\", \"Neutral\".";

        return List.of(
                ChatMessageV2.user(
                        UserMessage.builder()
                                .content(UserMessageContent.of(prompt))
                                .build()
                ),
                ChatMessageV2.user(
                        UserMessage.builder()
                                .content(UserMessageContent.of(joinedArticles))
                                .build()
                )
        );
    }

    public Optional<TextContent> classifyArticles(String symbol, List<Article> articleList) {
        if (articleList.isEmpty()) {
            logger.warn("Article list is empty. Can not classify.");
            return Optional.empty();
        }

        // Get cohere response
        ChatResponse response = cohere.v2().chat(
                V2ChatRequest.builder()
                        .model("command-r-plus")
                        .messages(buildChatMessages(symbol, articleList))
                        .build()
        );

        // Should receive 1 message
        List<AssistantMessageResponseContentItem> message = response.getMessage()
                .getContent()
                .orElseThrow(() -> {
                    String errorMessage = "No response provided from Cohere API";
                    logger.error(errorMessage);
                    return new RuntimeException(errorMessage);
                });

        if (message.size() != 1) {
            String errorMessage = "Unexpected number of messages found from Cohere API";
            logger.error(errorMessage);
            return Optional.empty();
        }

        return message.get(0).getText();
    }
}
