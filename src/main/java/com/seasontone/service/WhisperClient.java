package com.seasontone.service;

public interface WhisperClient {
  String transcribeBytes(byte[] data, String contentType)throws Exception;
}
