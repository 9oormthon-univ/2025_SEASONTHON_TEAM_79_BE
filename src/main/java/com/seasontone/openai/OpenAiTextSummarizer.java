package com.seasontone.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seasontone.service.TextSummarizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenAiTextSummarizer implements TextSummarizer {

  private final RestClient openAiRestClient;
  private final OpenAiProperties props;
  private final ObjectMapper om = new ObjectMapper();

  @Override
  public String summarize(String transcript) {
    if (transcript == null || transcript.isBlank()) return null;

    try {
      String model = (props.summarizeModel() == null || props.summarizeModel().isBlank())
          ? "gpt-4o-mini" : props.summarizeModel();

      Map<String, Object> payload = new HashMap<>();
      payload.put("model", model);
      payload.put("temperature", 0.2);
      payload.put("messages", List.of(
          Map.of("role","system","content",
              // 핵심: 인삿말/서론/결론/형식 외 금지
              "당신은 부동산 답사 기록 요약기입니다. 지시가 없으면 일절 말하지 마세요. " +
                  "출력 형식: 불릿 목록(각 줄은 '- '로 시작), 최대 500자. " +
                  "인삿말·서론·결론·설명 금지. 코드블록·마크다운 굵게·번호 매기기 금지. " +
                  "한국어로 간결하게."),
          Map.of("role","user","content",
              "다음 전사 텍스트를 요약하세요. " +
                  "\n\n전사:\n" + transcript)
      ));

      String json = openAiRestClient.post()
          .uri("/v1/chat/completions")
          .contentType(MediaType.APPLICATION_JSON)
          .body(payload)
          .retrieve()
          .body(String.class);

      JsonNode root = om.readTree(json);
      return root.path("choices").path(0).path("message").path("content").asText(null);

    } catch (Exception e) {
      throw new IllegalStateException("OpenAI summarize 실패", e);
    }
  }
}