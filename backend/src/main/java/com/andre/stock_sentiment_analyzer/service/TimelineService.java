package com.andre.stock_sentiment_analyzer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TimelineService {
    private final Logger logger = LoggerFactory.getLogger(TimelineService.class);

    /*
        <1 min ago>
        <# mins ago>
        <1 hour ago>
        <# hours ago>
        <yesterday>
        <# days ago>
        <last month>
        <# months ago>  - Max "11 months ago"
     */
    public Optional<LocalDateTime> convertToDateTime(String timeline) {
        if(timeline == null) return Optional.empty();

        timeline = timeline.toLowerCase();
        LocalDateTime now = LocalDateTime.now();

        // Check fixed patterns
        Map<String, LocalDateTime> fixedPatterns = new HashMap<>();
        fixedPatterns.put("yesterday", now.minusDays(1));
        fixedPatterns.put("last month", now.minusMonths(1));
        if (fixedPatterns.containsKey(timeline)) {
            return Optional.of(fixedPatterns.get(timeline));
        }

        // Check dynamic patterns
        Map<Pattern, Function<Matcher, LocalDateTime>> dynamicPatterns = new HashMap<>();
        dynamicPatterns.put(Pattern.compile("^(\\d+) minutes? ago$"), (Matcher m) -> now.minusMinutes(Integer.parseInt(m.group(1))));
        dynamicPatterns.put(Pattern.compile("^(\\d+) hours? ago$"), (Matcher m) -> now.minusHours(Integer.parseInt(m.group(1))));
        dynamicPatterns.put(Pattern.compile("^(\\d+) days? ago$"), (Matcher m) -> now.minusDays(Integer.parseInt(m.group(1))));
        dynamicPatterns.put(Pattern.compile("^(\\d+) months? ago$"), (Matcher m) -> now.minusMonths(Integer.parseInt(m.group(1))));
        for (var entry : dynamicPatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(timeline);
            if (matcher.matches()) {
                LocalDateTime result = entry.getValue().apply(matcher);
                if (result != null) {
                    return Optional.of(result);
                }
            }
        }

        logger.error("Error converting timeline to LocalDateTime: {}", timeline);
        return Optional.empty();
    }

    public long convertToDays(LocalDateTime dateTime) {
        if (dateTime == null) return -1;

        return Duration.between(LocalDateTime.now(), dateTime).toDays();
    }
}
