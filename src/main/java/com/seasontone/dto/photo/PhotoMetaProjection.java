package com.seasontone.dto.photo;

import java.time.Instant;

public interface PhotoMetaProjection {
  Long getId();
  String getFilename();
  String getContentType();
  Long getSize();
  String getCaption();
  Instant getCreatedAt();
}
