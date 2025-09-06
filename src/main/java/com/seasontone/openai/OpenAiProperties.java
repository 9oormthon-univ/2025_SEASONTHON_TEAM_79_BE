package com.seasontone.openai;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
    String apiKey,
    String baseUrl,
    String transcribeModel,
    String summarizeModel,
    String language
) {}