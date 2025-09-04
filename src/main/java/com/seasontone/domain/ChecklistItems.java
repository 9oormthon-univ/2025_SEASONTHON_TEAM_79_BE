package com.seasontone.domain;


import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.*;

@Entity
@Table(name = "checklist_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChecklistItems {

  @Id @Column(name = "check_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId                               // check_id = 부모 PK 공유
  @JoinColumn(name = "check_id")
  private UserRecord checklist;

  @Lob @Column(name = "pic")
  private byte[] pic; // 사진: 추후 multipart로 교체 권장

  @Column(name = "name", length = 255)    private String name;
  @Column(name = "address", length = 255) private String address;

  @Column(name = "monthly") private Boolean monthly;
  @Column(name = "mining") private Integer mining;     // 채광
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

  @Lob @Column(name = "memo")    private String memo;

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
}

