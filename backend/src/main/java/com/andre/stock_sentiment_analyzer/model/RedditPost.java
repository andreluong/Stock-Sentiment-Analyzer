package com.andre.stock_sentiment_analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditPost {

    @JsonProperty("subreddit")
    private String subreddit;

    @JsonProperty("title")
    private String title;

    @JsonProperty("selftext")
    private String description;

    @JsonProperty("upvote_ratio")

    private double upvote_ratio; // Must be >= 0.75

    @JsonProperty("score")
    private double score; // Must be >= 10

    @JsonProperty("created")
    private int created; // TODO: change type?

    @JsonProperty("url")
    private String url;

    public boolean meetsRequirements() {
        return upvote_ratio >= 0.75 && score >= 10;
    }

    @Override
    public String toString() {
        return "Title: " + title +
                "\nDescription: " + description.substring(0, 50) +
                "\nScore: " + score + " | Upvote Ratio: " + upvote_ratio +
                "\nCreated: " + created +
                "\nSubreddit: " + subreddit +
                "\n";
    }
}
