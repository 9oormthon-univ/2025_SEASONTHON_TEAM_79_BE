package com.seasontone.dto.checklists;

import com.seasontone.domain.enums.RentType;
import com.seasontone.domain.enums.RoomType;


public record RecordCreateRequest(
		/* 유령 리퀘스트
		// 매물 기본
		String aptNm,
		String address,
		String detailAddress,        // 상세주소
		RentType rentType,           // 전세/월세
		RoomType roomType,            // 원룸/투룸/3룸+
		Integer area,
		Integer deposit,
		Integer monthlyRent,
		Integer maintenanceFee,
		Integer floorAreaSqm,

		// 사진/음성은 추후 multipart로 분리. 지금은 생략/널 허용
		// char 타입은 사용하지 않음

		// 점수/옵션
		Integer mining,
		Integer water,
		Integer cleanliness,
		Integer options,
		Integer security,
		Integer noise,
		Integer surroundings,
		Integer recycling,
		Boolean isElevatorAvailable,
		Boolean isBalconyAvailable,
		Boolean isPetAllowed,

		String notes

		 */
) {}
