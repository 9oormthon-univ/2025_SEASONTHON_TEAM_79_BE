package com.seasontone.entity.checklist;

import com.seasontone.entity.BaseEntity;
import com.seasontone.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
public class Checklist extends BaseEntity {
	@Id
	@Column(name = "checklist_id")
	private Long id; // UserRecord PK를 공유

	@MapsId
	@OneToOne
	@JoinColumn(name = "user_record_id")
	private UserRecord userRecord;

	//사진


	private String name;

	private String address;

	private int monthly;

	private int deposit;

	private int  maintenanceFee;

	private int mining;

	private int water;

	private int cleanliness;

	private int options;

	private int security;

	private int noise;

	private int surroundings;

	private int recycling;

	private boolean elevator;

	private boolean veranda;

	private boolean pet;

	//음성녹음


	private String memo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id") // FK
	private User user;
}
