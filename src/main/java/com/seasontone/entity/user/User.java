package com.seasontone.entity.user;

import com.seasontone.entity.BaseEntity;
import com.seasontone.entity.checklist.Checklist;
import com.seasontone.entity.checklist.UserRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; //회원id pk

    @Column(nullable = false, length = 10)
    private String name; //회원 이름

    @Column(nullable = false, length = 100, unique = true)
    private String email; //회원 이메일, 유니크 제약조건

    @Column(nullable = false, length = 100)
    private String password; //회원 비밀번호, 암호화 저장

    private Boolean emailVerified; //이메일 인증 여부, 기본값은 false

    private String region;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRecord> userRecords = new ArrayList<>();

    public void updateRegion(String region) {
        this.region = region;
    }
}
