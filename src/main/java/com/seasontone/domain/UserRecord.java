package com.seasontone.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Entity
@Table(name = "user_records")
public class UserRecord {

  // ---------- getters / setters ----------
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "check_id")
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "userid", nullable = false)
  private User user;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pro_id")
  private Listing listing;

  @Setter
  @Column(length = 200)
  private String title;

  @Setter
  @Lob
  private String notes;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Setter
  @OneToOne(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private ChecklistItems items;

  public UserRecord() {}

  //빈 폼 생성 + 양방향 연결
  public void attachBlankItems() {
    if (this.items == null) {
      ChecklistItems i = new ChecklistItems();
      i.setChecklist(this);   // @MapsId 관계 세팅
      this.items = i;
    }
  }
}