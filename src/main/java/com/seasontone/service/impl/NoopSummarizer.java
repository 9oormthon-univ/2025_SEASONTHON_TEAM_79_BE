package com.seasontone.service.impl;

import com.seasontone.service.TextSummarizer;
import org.springframework.stereotype.Component;

@Component
public class NoopSummarizer implements TextSummarizer {
  @Override public String summarize(String text) {
    return "(미리보기) 요약 결과는 추후 GPT 연동 시 채워집니다.";
  }
}