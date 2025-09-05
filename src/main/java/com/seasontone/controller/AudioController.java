package com.seasontone.controller;

import com.seasontone.Entity.RecordVoiceNote;
import com.seasontone.Entity.User;
import com.seasontone.dto.VoiceNoteDto;
import com.seasontone.service.ChecklistAudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checklists")
public class AudioController {

  private final ChecklistAudioService audioService;

  @PostMapping(value="/{checkId}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public VoiceNoteDto upload(@PathVariable Long checkId,
      @RequestPart("file") MultipartFile file,
      @RequestPart(value="durationSec", required=false) Integer durationSec,
      @AuthenticationPrincipal User me){
    return audioService.uploadReplace(checkId, me.getId(), file, durationSec);
  }

  @GetMapping("/{checkId}/audio")
  public VoiceNoteDto meta(@PathVariable Long checkId){
    return audioService.getMeta(checkId);
  }

  @GetMapping("/{checkId}/audio/raw")
  public ResponseEntity<ByteArrayResource> raw(@PathVariable Long checkId){
    RecordVoiceNote v = audioService.loadRaw(checkId);
    var body = new ByteArrayResource(v.getData());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(v.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (v.getFilename()!=null?v.getFilename():"voice") + "\"")
        .body(body);
  }

  @DeleteMapping("/{checkId}/audio")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @AuthenticationPrincipal User me){
    audioService.delete(checkId, me.getId());
  }
}