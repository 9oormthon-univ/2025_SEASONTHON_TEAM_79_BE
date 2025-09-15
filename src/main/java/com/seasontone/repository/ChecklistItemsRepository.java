package com.seasontone.repository;

import com.seasontone.domain.checklists.ChecklistItems;
import com.seasontone.domain.users.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistItemsRepository extends JpaRepository<ChecklistItems, Long> {
	@EntityGraph(attributePaths = {"photos", "voiceNote"})
	Page<ChecklistItems> findByUser_Id(Long userId, Pageable pageable);

	@EntityGraph(attributePaths = {"photos", "voiceNote"})
	Optional<ChecklistItems> findByIdAndUser_Id(Long id, Long userId);

	List<ChecklistItems> findByUser(User user);
	List<ChecklistItems> findByAddressContaining(String region);
}
