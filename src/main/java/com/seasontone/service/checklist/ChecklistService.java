package com.seasontone.service.checklist;

import com.seasontone.dto.checklist.ChecklistDetailsResponse;
import com.seasontone.dto.checklist.ChecklistGroupResponse;
import com.seasontone.dto.checklist.MyChecklistResponse;
import com.seasontone.entity.checklist.Checklist;
import com.seasontone.entity.checklist.UserRecord;
import com.seasontone.entity.user.User;
import com.seasontone.repository.checklist.ChecklistRepository;
import com.seasontone.repository.checklist.UserRecordRepository;
import com.seasontone.repository.user.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChecklistService {
	private final ChecklistRepository checklistRepository;
	private final UserRecordRepository userRecordRepository;
	private final UserRepository userRepository;

	//나의 기록 목록 조회
	public List<MyChecklistResponse> getMyChecklists(User user){
		User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new NullPointerException("존재하지 않는 회원입니다."));

		return findUser.getUserRecords().stream()
				.map(userRecord -> {
					Checklist checklist = userRecord.getChecklist();
					return MyChecklistResponse.builder()
							.id(checklist.getId())
							.aptNm(checklist.getName())
							.address(checklist.getAddress())
							.deposit(checklist.getDeposit())
							.monthly(checklist.getMonthly())
							.maintenanceFee(checklist.getMaintenanceFee())
							.avgScore(userRecord.getScores())
							.build();
				})
				.toList();
	}

	//주소 + 가장 최근의 월세, 보증금, 관리비, 점수로 묶어서 리스트
	//둘러보기 목록 조회
	public List<ChecklistGroupResponse> getGroupedByAddress() {
		List<Checklist> checklists = checklistRepository.findAll();

		Map<String, List<Checklist>> grouped = checklists.stream()
				.collect(Collectors.groupingBy(Checklist::getAddress));

		return grouped.entrySet().stream()
				.map(entry -> {
					List<Checklist> groupList = entry.getValue();

					// 각 그룹에서 가장 최근 생성된 체크리스트 가져오기
					Checklist latest = groupList.stream()
							.max(Comparator.comparing(Checklist::getCreatedAt))
							.orElseThrow();


					return ChecklistGroupResponse.builder()
							.address(entry.getKey())
							.latestName(latest.getName())
							.latestMonthly(latest.getMonthly())
							.latestDeposit(latest.getDeposit())
							.latestScore(latest.getUserRecord().getScores())
							.latestMaintenanceFee(latest.getMaintenanceFee())
							.checklists(groupList.stream()
									.map(checklist -> ChecklistGroupResponse.ChecklistDetailsResponse.builder()
											.id(checklist.getId())
											.name(checklist.getName())
											.monthly(checklist.getMonthly())
											.deposit(checklist.getDeposit())
											.maintenanceFee(checklist.getMaintenanceFee())
											.score(checklist.getUserRecord().getScores())
											.build())
									.toList())
							.build();
				})
				.toList();
	}

	//나의 기록, 둘러보기 상세 조회
	//TODO: 음성녹음, 이미 추가해야됨
	public ChecklistDetailsResponse getChecklistDetails(Long userRecordId) {
		UserRecord findUserRecord = userRecordRepository.findById(userRecordId).orElseThrow(()->new NullPointerException("존재하지 않는 유저 체크리스트입니다."));
		Checklist findChecklist = findUserRecord.getChecklist();

		return ChecklistDetailsResponse.builder()
				.id(findUserRecord.getId())
				.house(ChecklistDetailsResponse.House.builder()
						.address(findChecklist.getAddress())
						.name(findChecklist.getName())
						.monthly(findChecklist.getMonthly())
						.deposit(findChecklist.getDeposit())
						.maintenanceFee(findChecklist.getMaintenanceFee())
						.scores(findUserRecord.getScores())
						.build())
				.checklist(ChecklistDetailsResponse.Checklist.builder()
						.mining(findChecklist.getMining())
						.water(findChecklist.getWater())
						.cleanliness(findChecklist.getCleanliness())
						.options(findChecklist.getOptions())
						.security(findChecklist.getSecurity())
						.noise(findChecklist.getNoise())
						.surroundings(findChecklist.getSurroundings())
						.recycling(findChecklist.getRecycling())
						.elevator(findChecklist.isElevator())
						.veranda(findChecklist.isVeranda())
						.pet(findChecklist.isPet())
						.build())
				.memo(findChecklist.getMemo())
				.build();
	}
}
