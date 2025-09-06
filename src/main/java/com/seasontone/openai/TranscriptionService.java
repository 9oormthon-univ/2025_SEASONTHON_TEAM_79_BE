package com.seasontone.openai;

import com.openai.client.OpenAIClient;
import com.openai.models.audio.AudioModel;
import com.openai.models.audio.AudioResponseFormat;
import com.openai.models.audio.transcriptions.Transcription;
import com.openai.models.audio.transcriptions.TranscriptionCreateParams;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/*
@Service
@RequiredArgsConstructor
public class TranscriptionService {

  private final OpenAIClient openAIClient;


  public String transcribeFile(Path audioPath, boolean prefer4o) {
    if (audioPath == null || !Files.exists(audioPath)) {
      throw new IllegalArgumentException("오디오 파일을 찾을 수 없습니다: " + audioPath);
    }

    // 모델 선택
    final AudioModel model = prefer4o
        ? AudioModel.GPT_4O_TRANSCRIBE   // 고품질/최신
        : AudioModel.WHISPER_1;          // 저렴/안정

    // 필요한 경우 한국어 고정: .language("ko")
    // 세그먼트/타임스탬프가 필요하면 responseFormat을 VERBOSE_JSON/VTT로 변경
    TranscriptionCreateParams params = TranscriptionCreateParams.builder()
        .model(model)
        .file(audioPath)                                  // 서버에 저장된 파일 Path
        .responseFormat(AudioResponseFormat.TEXT)         // TEXT만 원문 문자열
        .temperature(0.2)                                 // (선택) 안정적 출력
        //.language("ko")                                  // (선택) 입력 언어 힌트
        //.prompt("부동산/임대 관련 용어는 한국어 표기로 전사") // (선택) 도메인 프롬프트
        .build();

    Transcription res = openAIClient.audio().transcriptions().create(params);
    return res.text(); // TEXT 형식이면 여기로 텍스트가 옴
  }


}

 */
