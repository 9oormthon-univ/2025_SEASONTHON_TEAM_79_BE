package com.seasontone.repository;

import com.seasontone.Entity.ChecklistItems;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistItemsRepository extends JpaRepository<ChecklistItems, Long> {
	List<ChecklistItems> findByAddressContaining(String region);
}
