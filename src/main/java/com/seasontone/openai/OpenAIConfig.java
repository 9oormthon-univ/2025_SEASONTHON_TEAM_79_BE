package com.seasontone.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
@RequiredArgsConstructor
public class OpenAIConfig {
  private final OpenAiProperties props;

  @Bean
  RestClient openAiRestClient() {
    return RestClient.builder()
        .baseUrl(props.baseUrl() == null ? "https://api.openai.com" : props.baseUrl())
        .requestFactory(new JdkClientHttpRequestFactory())
        .defaultHeaders(h -> {
          h.setBearerAuth(props.apiKey());
          h.set("Accept", "application/json");
        })
        .build();
  }
}
