package com.seasontone.controller;

import com.seasontone.dto.VoiceNoteDto;
import com.seasontone.service.VoiceNoteService;
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

  private final VoiceNoteService audioService; // ← 실제 서비스 타입과 맞추기

  @PostMapping(value="/{checkId}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public VoiceNoteDto upload(
      @PathVariable Long checkId,
      @RequestParam("file") MultipartFile file,                // ← file
      @RequestParam(value="durationSec", required=false) Integer durationSec,
      @AuthenticationPrincipal(expression="id") Long meId
  ) throws IOException {
    if (meId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
    return audioService.uploadReplace(checkId, meId, file, durationSec);
  }

  @GetMapping("/{checkId}/audio")
  public VoiceNoteDto meta(@PathVariable Long checkId){
    return audioService.getMeta(checkId);
  }

  @GetMapping("/{checkId}/audio/raw")
  public ResponseEntity<ByteArrayResource> raw(@PathVariable Long checkId){
    var v = audioService.loadEntityForRaw(checkId);
    var body = new ByteArrayResource(v.getData());
    var ct = (v.getContentType()!=null)? v.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    var name = (v.getFilename()!=null)? v.getFilename() : ("voice-"+checkId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name.replace("\"","") + "\"")
        .body(body);
  }

  @DeleteMapping("/{checkId}/audio")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @AuthenticationPrincipal(expression="id") Long meId){
    if (meId == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다");
    }
    audioService.delete(checkId, meId);
  }
}