package com.seasontone.controller;

import com.seasontone.dto.voice.VoiceNoteDto;
import com.seasontone.service.checklist.VoiceNoteService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checklists")
public class AudioController {

  private final VoiceNoteService audioService;

  // 음성 업로드(+ 교체)
  @PostMapping(
      value = "/{checkId}/audio",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ResponseStatus(HttpStatus.CREATED)
  public VoiceNoteDto upload(
      @PathVariable Long checkId,
      @RequestPart("file") MultipartFile file,                     // ← 멀티파트 파일
      @RequestParam(value = "durationSec", required = false) Integer durationSec,
      @AuthenticationPrincipal(expression = "id") Long meId
  ) throws IOException {
    if (meId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
    return audioService.uploadReplace(checkId, meId, file, durationSec);
  }

  //음성 메타 조회
  @GetMapping(value = "/{checkId}/audio", produces = MediaType.APPLICATION_JSON_VALUE)
  public VoiceNoteDto meta(@PathVariable Long checkId) {
    return audioService.getMeta(checkId);
  }

  // 원본 스트리밍
  @GetMapping("/{checkId}/audio/raw")
  public ResponseEntity<ByteArrayResource> raw(@PathVariable Long checkId){
    var v = audioService.loadForRaw(checkId);              // ← 서비스에 추가한 메서드 사용
    var body = new ByteArrayResource(v.getData());
    var ct = (v.getContentType()!=null)? v.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    var name = (v.getFilename()!=null)? v.getFilename() : ("voice-"+checkId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name.replace("\"","") + "\"")
        .body(body);
  }

  //음성 삭제
  @DeleteMapping("/{checkId}/audio")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable Long checkId,
      @AuthenticationPrincipal(expression = "id") Long meId
  ) {
    if (meId == null)
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
    audioService.delete(checkId, meId);
  }
}