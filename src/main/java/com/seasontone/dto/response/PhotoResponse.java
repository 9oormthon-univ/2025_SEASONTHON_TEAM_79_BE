package com.seasontone.dto.response;


import com.seasontone.Entity.RecordPhoto;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoResponse {
  private Long id;
  private String filename;
  private String contentType;
  private long size;
  private String caption;
  private Instant createdAt;
  private String rawUrl;

  public static PhotoResponse from(RecordPhoto p) {
    return PhotoResponse.builder()
        .id(p.getId())
        .filename(p.getFilename())
        .contentType(p.getContentType())
        .size(p.getSize())
        .caption(p.getCaption())
        .createdAt(p.getCreatedAt())           // 엔티티에 createdAt 필드가 있어야 함
        .rawUrl("/api/photos/" + p.getId())
        .build();
  }
}