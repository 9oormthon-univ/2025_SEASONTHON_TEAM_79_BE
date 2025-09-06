package com.seasontone.entity;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
@Table(name = "record_photos")
public class RecordPhoto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)   // ← DB가 키 생성
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "items_id", nullable = false)
  private ChecklistItems items;      // 체크리스트 항목 (1:N)

  @Column(length = 255)
  private String filename;

  @Column(length = 255)
  private String contentType;

  private Long size;

  @Lob @Basic(fetch = FetchType.LAZY)
  @Column(name = "data", nullable = false, columnDefinition = "LONGBLOB")
  private byte[] data;               // ★ 변환 없이 바이트 그대로

  @Column(length = 255)
  private String caption;

  private Instant createdAt;
}
