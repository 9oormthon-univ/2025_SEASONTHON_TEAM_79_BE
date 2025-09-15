package com.seasontone.controller;

import com.seasontone.dto.photo.PhotoDto;
import com.seasontone.service.checklist.ChecklistPhotoService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class PhotoController {

  private final ChecklistPhotoService photoService;

  // 업로드 (소유자 검증)
  @PostMapping(path = "/{checkId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public PhotoDto upload(@PathVariable Long checkId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value="caption", required=false) String caption,
      @AuthenticationPrincipal(expression="id") Long meId) {
    if (meId == null) throw new AccessDeniedException("Login required");
    return photoService.upload(checkId, meId, file, caption);
  }

  // 목록 (1:N)
  @GetMapping("/{checkId}/photos")
  public List<PhotoDto> list(@PathVariable Long checkId) {
    return photoService.list(checkId);
  }

  // 원본
  @GetMapping("/{checkId}/photos/{photoId}/raw")
  public ResponseEntity<ByteArrayResource> raw(@PathVariable Long checkId,
      @PathVariable Long photoId) {
    var p = photoService.loadEntityForRaw(checkId, photoId);
    var body = new ByteArrayResource(p.getData());
    var ct = (p.getContentType() != null) ? p.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    var name = (p.getFilename() != null) ? p.getFilename() : ("photo-" + photoId);
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name.replace("\"","") + "\"")
        .body(body);
  }

  // 삭제 (특정 사진)
  @DeleteMapping("/{checkId}/photos/{photoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId,
      @PathVariable Long photoId,
      @AuthenticationPrincipal(expression="id") Long meId) {
    if (meId == null) throw new AccessDeniedException("Login required");
    photoService.delete(checkId, photoId, meId);
  }
}