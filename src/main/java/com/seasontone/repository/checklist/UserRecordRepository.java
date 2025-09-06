package com.seasontone.repository.checklist;

import com.seasontone.entity.checklist.UserRecord;
import com.seasontone.entity.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecordRepository extends JpaRepository<UserRecord, Long> {
	List<UserRecordRepository> findByUser(User user);
}
