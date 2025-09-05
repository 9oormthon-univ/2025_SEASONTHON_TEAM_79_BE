package com.seasontone.service.impl;

import com.seasontone.service.WhisperClient;
import org.springframework.stereotype.Component;

@Component
public class NoopWhisperClient implements WhisperClient {
  @Override public String transcribeBytes(byte[] data, String contentType) {
    return "(미리보기) Whisper 연동 전 — 전사 결과는 추후 채워집니다.";
  }
}
