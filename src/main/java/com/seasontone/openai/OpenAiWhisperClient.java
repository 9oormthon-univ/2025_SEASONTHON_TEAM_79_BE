package com.seasontone.openai;

import com.seasontone.service.WhisperClient;
import io.github.classgraph.ResourceList.ByteArrayConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenAiWhisperClient implements WhisperClient {

  private final RestClient openAiRestClient;
  private final OpenAiProperties props;

  /** filename 없는 ByteArrayResource는 멀티파트에서 깨질 수 있어 반드시 override */
  static class NamedByteArrayResource extends ByteArrayResource {
    private final String filename;
    NamedByteArrayResource(byte[] byteArray, String filename) {
      super(byteArray);
      this.filename = filename;
    }
    @Override public String getFilename() { return filename; }
  }

  @Override
  public String transcribeBytes(byte[] data, String contentType) {
    final String model = empty(props.transcribeModel()) ? "gpt-4o-transcribe" : props.transcribeModel();
    final String lang  = empty(props.language()) ? null : props.language();
    final String filename = suggestFilename(contentType);

    // ---- 파일 part (headers + entity)
    Resource fileRes = new NamedByteArrayResource(data, filename);

    HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.setContentType(safeMediaType(contentType));
    // Content-Disposition: form-data; name="file"; filename="..."
    ContentDisposition cd = ContentDisposition.formData()
        .name("file")
        .filename(filename)
        .build();
    fileHeaders.setContentDisposition(cd);

    HttpEntity<Resource> filePart = new HttpEntity<>(fileRes, fileHeaders);

    // ---- 멀티파트 body
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", filePart);
    body.add("model", model);
    body.add("response_format", "text");
    body.add("temperature", "0");
    if (lang != null) body.add("language", lang);

    return openAiRestClient.post()
        .uri("/v1/audio/transcriptions")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(body)
        .retrieve()
        .body(String.class); // response_format=text ⇒ 문자열 본문 그대로
  }

  private static boolean empty(String s) { return s == null || s.isBlank(); }

  private static MediaType safeMediaType(String ct) {
    try { return (ct != null) ? MediaType.parseMediaType(ct) : MediaType.APPLICATION_OCTET_STREAM; }
    catch (Exception e) { return MediaType.APPLICATION_OCTET_STREAM; }
  }

  private static String suggestFilename(String contentType) {
    if (contentType == null) return "audio.webm";
    String ct = contentType.toLowerCase();
    if (ct.contains("mpeg") || ct.contains("mp3")) return "audio.mp3";
    if (ct.contains("mp4"))  return "audio.mp4";
    if (ct.contains("m4a"))  return "audio.m4a";
    if (ct.contains("wav"))  return "audio.wav";
    if (ct.contains("webm")) return "audio.webm";
    return "audio.bin";
  }
}
