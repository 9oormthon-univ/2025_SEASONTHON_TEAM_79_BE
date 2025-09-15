package com.seasontone.domain.checklists;

import com.seasontone.domain.BaseEntity;
import com.seasontone.domain.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checklist_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItems /* extends BaseEntity (원하면 제거 가능) */ {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "check_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // ===== 폼 필드만 유지 =====
  @Column(length = 255) private String name;
  @Column(length = 255) private String address;
  private Integer monthly;
  private Integer deposit;
  private Integer maintenanceFee;
  private Integer floorAreaSqm;
  private Integer mining, water, cleanliness, options, security, noise, surroundings, recycling;
  private Boolean elevator, veranda, pet;
  @Lob @Column(columnDefinition = "TEXT")
  @jakarta.validation.constraints.Size(max = 500)
  private String memo;

  // photos: 1:N
  @Builder.Default
  @OneToMany(mappedBy = "items", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecordPhoto> photos = new ArrayList<>();

  // voice: 1:1 (비식별)
  @OneToOne(mappedBy = "items", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private RecordVoiceNote voiceNote;

  public List<Integer> scores() {
    return Stream.of(mining, water, cleanliness, options, security, noise, surroundings, recycling)
        .filter(Objects::nonNull).toList();
  }
  @Transient
  public double averageScore() {
    var s = scores();
    return s.isEmpty() ? 0.0 : s.stream().mapToInt(Integer::intValue).average().orElse(0.0);
  }

  public void addPhoto(RecordPhoto p){ photos.add(p); p.setItems(this); }
  public void removePhoto(RecordPhoto p){ photos.remove(p); p.setItems(null); }
  public void setVoiceNote(RecordVoiceNote v){ this.voiceNote = v; if (v!=null) v.setItems(this); }
}