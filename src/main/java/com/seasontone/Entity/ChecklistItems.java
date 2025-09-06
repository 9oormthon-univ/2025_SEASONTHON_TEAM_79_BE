package com.seasontone.Entity;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.*;

@Entity
@Table(name = "checklist_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItems extends BaseEntity{

  @Id @Column(name = "check_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId                               // check_id = 부모 PK 공유
  @JoinColumn(name = "check_id")
  private UserRecord checklist;

  // 사진: 1:N
  @Builder.Default
  @OneToMany(mappedBy = "items", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RecordPhoto> photos = new ArrayList<>();

  // 음성: 1:1 (체크리스트당 하나)
  @OneToOne(mappedBy = "items", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private RecordVoiceNote voiceNote;

  @Column(name = "name", length = 255)    private String name;
  @Column(name = "address", length = 255) private String address;

  @Column(name = "monthly")     private int monthly; //월세
  @Column(name = "deposit")     private int deposit; //보증금
  @Column(name = "maintenanceFee")    private int  maintenanceFee; //관리비
  @Column(name = "floorAreaSqm")  private Integer floorAreaSqm; //평수
  @Column(name = "mining")      private Integer mining;     // 채광
  @Column(name = "water")        private Integer water;        // 수압
  @Column(name = "cleanliness")  private Integer cleanliness;  // 청결
  @Column(name = "options")      private Integer options;      // 옵션
  @Column(name = "security")     private Integer security;     // 보안
  @Column(name = "noise")        private Integer noise;        // 소음
  @Column(name = "surroundings") private Integer surroundings; // 주변환경
  @Column(name = "recycling")    private Integer recycling;    // 분리수거

  @Column(name = "elevator")     private Boolean elevator;
  @Column(name = "veranda")      private Boolean veranda;
  @Column(name = "pet")          private Boolean pet;

  @Lob
  @Column(columnDefinition = "TEXT")   // 저장은 TEXT로 넉넉하게
  @jakarta.validation.constraints.Size(max = 500) // ★ 최대 1000자 검증
  private String memo;

  // 점수 계산.. 이거 들어가나?
  public List<Integer> scores() {
    return Stream.of(mining, water, cleanliness, options, security, noise, surroundings, recycling)
        .filter(Objects::nonNull).toList();
  }

  @Transient
  public double averageScore() {
    var s = scores(); // 이미 있는 메서드
    return s.isEmpty() ? 0.0 : s.stream().mapToInt(Integer::intValue).average().orElse(0.0);
  }

  // helpers
  public void addPhoto(RecordPhoto p){ photos.add(p); p.setItems(this); }
  public void removePhoto(RecordPhoto p){ photos.remove(p); p.setItems(null); }
  public void setVoiceNote(RecordVoiceNote v){ this.voiceNote = v; if (v!=null) v.setItems(this); }
}

