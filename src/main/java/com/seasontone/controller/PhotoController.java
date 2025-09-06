package com.seasontone.controller;

import com.seasontone.entity.ChecklistItems;
import com.seasontone.entity.RecordPhoto;
import com.seasontone.entity.UserRecord;
import com.seasontone.repository.UserRecordRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class PhotoController {

  private final UserRecordRepository recordRepo;

  // 업로드
  @Transactional
  @PostMapping(path = "/{checkId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String, Object> uploadPhoto(@PathVariable Long checkId,
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "caption", required = false) String caption) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file이 비어 있습니다");
    }

    UserRecord record = recordRepo.findById(checkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "checklist not found: " + checkId));

    // items 없으면 즉시 생성 (@MapsId 구조)
    record.attachBlankItems();
    ChecklistItems items = record.getItems();

    // 요청 스레드 안에서 즉시 바이트 복사 (임시파일 선삭제 방지)
    byte[] data;
    try (var in = file.getInputStream()) {
      data = in.readAllBytes();
    }

    RecordPhoto p = new RecordPhoto();
    p.setFilename(Optional.ofNullable(file.getOriginalFilename()).orElse("upload"));
    p.setContentType(Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    p.setSize((long) data.length);
    p.setCaption(caption);
    p.setData(data);

    // 양방향 연결 (ChecklistItems에 helper가 있다고 했음)
    items.addPhoto(p);

    // 부모만 저장하면 Cascade로 items → photos까지 전파
    recordRepo.saveAndFlush(record); // flush 해서 photo id 확보

    Map<String, Object> res = new LinkedHashMap<>();
    res.put("id", p.getId());
    res.put("filename", p.getFilename());
    res.put("contentType", p.getContentType());
    res.put("size", p.getSize());
    res.put("caption", p.getCaption());
    // createdAt 필드가 있으면 사용하고, 없으면 생략해도 됨
    try {
      var createdAt = RecordPhoto.class.getMethod("getCreatedAt").invoke(p);
      res.put("createdAt", createdAt);
    } catch (Exception ignore) {}
    res.put("rawUrl", "/api/checklists/" + checkId + "/photos/" + p.getId() + "/raw");
    return res;
  }

  // 목록
  @GetMapping("/{checkId}/photos")
  public List<Map<String, Object>> listPhotos(@PathVariable Long checkId) {
    UserRecord record = recordRepo.findById(checkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "checklist not found: " + checkId));

    ChecklistItems items = record.getItems();
    if (items == null || items.getPhotos() == null) return List.of();

    List<Map<String, Object>> out = new ArrayList<>();
    for (RecordPhoto p : items.getPhotos()) {
      Map<String, Object> m = new LinkedHashMap<>();
      m.put("id", p.getId());
      m.put("filename", p.getFilename());
      m.put("contentType", p.getContentType());
      m.put("size", p.getSize());
      m.put("caption", p.getCaption());
      try {
        var createdAt = RecordPhoto.class.getMethod("getCreatedAt").invoke(p);
        m.put("createdAt", createdAt);
      } catch (Exception ignore) {}
      m.put("rawUrl", "/api/checklists/" + checkId + "/photos/" + p.getId() + "/raw");
      out.add(m);
    }
    return out;
  }

  // 원본
  @GetMapping("/{checkId}/photos/{photoId}/raw")
  public ResponseEntity<byte[]> raw(@PathVariable Long checkId, @PathVariable Long photoId) {
    UserRecord record = recordRepo.findById(checkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "checklist not found: " + checkId));

    ChecklistItems items = record.getItems();
    if (items == null || items.getPhotos() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no photos");
    }

    RecordPhoto p = items.getPhotos().stream()
        .filter(ph -> Objects.equals(ph.getId(), photoId))
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "photo not found: " + photoId));

    String ct = Optional.ofNullable(p.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    String name = Optional.ofNullable(p.getFilename()).orElse("photo-" + photoId);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + name.replace("\"", "") + "\"")
        .body(p.getData());
  }

  // 삭제
  @Transactional
  @DeleteMapping("/{checkId}/photos/{photoId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long checkId, @PathVariable Long photoId) {
    UserRecord record = recordRepo.findById(checkId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "checklist not found: " + checkId));

    ChecklistItems items = record.getItems();
    if (items == null || items.getPhotos() == null) return;

    // orphanRemoval=true 이면 관계만 끊어도 DB에서 삭제됨
    items.getPhotos().removeIf(ph -> Objects.equals(ph.getId(), photoId));

    recordRepo.save(record);
  }
}
