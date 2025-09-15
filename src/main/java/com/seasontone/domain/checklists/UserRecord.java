package com.seasontone.domain.checklists;

import com.seasontone.domain.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
/*
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
  @OneToOne(mappedBy = "checklist",
      cascade = CascadeType.ALL,       // 부모 변경/삭제가 자식에 전파
      orphanRemoval = true,            // 자식 참조 끊기면 자식 삭제
      fetch = FetchType.LAZY)
  private ChecklistItems items;

  public UserRecord() {}

  //빈 폼 생성 + 양방향 연결
  public void attachBlankItems() {
    if (this.items == null) {
      ChecklistItems i = new ChecklistItems();
      i.setChecklist(this);   // 양방향 연결(자식 -> 부모)
      this.items = i;         // 양방향 연결(부모 -> 자식)
    }
  }
}

 */
