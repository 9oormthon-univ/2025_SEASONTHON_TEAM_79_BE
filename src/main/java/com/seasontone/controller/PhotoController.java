package com.seasontone.controller;

import com.seasontone.Entity.RecordPhoto;
import com.seasontone.Entity.User;
import com.seasontone.dto.PhotoDto;
import com.seasontone.service.ChecklistPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checklists")
public class PhotoController {

  private final ChecklistPhotoService photoService;

  @PostMapping(value="/{checkId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public PhotoDto upload(@PathVariable Long checkId,
      @RequestPart("file") MultipartFile file,
      @RequestPart(value="caption", required=false) String caption,
      @AuthenticationPrincipal User me){
    return photoService.upload(checkId, me.getId(), file, caption);
  }

  @GetMapping("/{checkId}/photos")
  public List<PhotoDto> list(@PathVariable Long checkId){
    return photoService.list(checkId);
  }

  // (편의) photoId로 바로 보기 → <img src="/api/photos/{id}">
  @GetMapping("/photos/{photoId}")
  public ResponseEntity<ByteArrayResource> rawByPhotoId(@PathVariable Long photoId){
    RecordPhoto p = photoService.loadEntityForRawByPhotoId(photoId);
    var body = new ByteArrayResource(p.getData());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(p.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (p.getFilename()!=null?p.getFilename():"photo") + "\"")
        .body(body);
  }

  // (검증) checkId와 photoId 매칭해서 보기
  @GetMapping("/{checkId}/photos/{photoId}/raw")
  public ResponseEntity<ByteArrayResource> raw(@PathVariable Long checkId, @PathVariable Long photoId){
    RecordPhoto p = photoService.loadEntityForRawByCheck(checkId, photoId);
    var body = new ByteArrayResource(p.getData());
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(p.getContentType()))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + (p.getFilename()!=null?p.getFilename():"photo") + "\"")
        .body(body);
  }

  @DeleteMapping("/{checkId}/photos/{photoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @PathVariable Long photoId, @AuthenticationPrincipal User me){
    photoService.delete(checkId, photoId, me.getId());
  }
}