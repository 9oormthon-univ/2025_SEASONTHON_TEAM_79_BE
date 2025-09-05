package com.seasontone.Entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userid") //칼럼명 user_id 아닌..id
  private Long id; //회원id pk

  @Column(nullable = false, length = 10)
  private String name; //회원 이름

  @Column(nullable = false, length = 100, unique = true)
  private String email; //회원 이메일, 유니크 제약조건

  @Column(nullable = false, length = 100)
  private String password; //회원 비밀번호, 암호화 저장

  private Boolean emailVerified; //이메일 인증 여부, 기본값은 false

}
