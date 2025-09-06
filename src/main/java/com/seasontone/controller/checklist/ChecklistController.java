package com.seasontone.controller.checklist;

import com.seasontone.dto.checklist.ChecklistDetailsResponse;
import com.seasontone.dto.checklist.ChecklistGroupResponse;
import com.seasontone.dto.checklist.MyChecklistResponse;
import com.seasontone.entity.user.User;
import com.seasontone.service.checklist.ChecklistService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class ChecklistController {

	private final ChecklistService checklistService;

	@GetMapping("/mine")
	public ResponseEntity<List<MyChecklistResponse>> getMyChecklists(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(checklistService.getMyChecklists(user));
	}
	@GetMapping()
	public ResponseEntity<List<ChecklistGroupResponse>> getGroupedByAddress() {
		return ResponseEntity.ok(checklistService.getGroupedByAddress());
	}

	@GetMapping("/{userRecordId}")
	public ResponseEntity<ChecklistDetailsResponse> getChecklistDetails(@PathVariable() Long userRecordId) {
		return ResponseEntity.ok(checklistService.getChecklistDetails(userRecordId));
	}

}
