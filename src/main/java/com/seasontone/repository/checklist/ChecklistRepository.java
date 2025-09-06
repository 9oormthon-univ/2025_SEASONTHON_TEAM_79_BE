package com.seasontone.repository.checklist;

import com.seasontone.entity.checklist.Checklist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
	List<Checklist> findByAddressContaining(String region);
}
