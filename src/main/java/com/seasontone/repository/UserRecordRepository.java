package com.seasontone.repository;


import com.seasontone.domain.users.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
/*
public interface UserRecordRepository extends JpaRepository<UserRecord, Long> {

  @EntityGraph(attributePaths = {"items", "listing"})
  Page<UserRecord> findByUser_Id(Long userId, Pageable pageable);

  @EntityGraph(attributePaths = {"items", "listing"})
  Page<UserRecord> findByListing_Id(Long listingId, Pageable pageable);

  Optional<UserRecord> findByIdAndUser_Id(Long id, Long userId);

  long deleteByIdAndUser_Id(Long id, Long userId);

  List<UserRecord> findByUser(User user);
}

 */
