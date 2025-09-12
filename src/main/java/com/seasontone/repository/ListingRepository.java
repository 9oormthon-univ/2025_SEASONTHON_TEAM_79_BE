package com.seasontone.repository;

import com.seasontone.domain.checklists.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

//유령 레포
public interface ListingRepository extends JpaRepository<Listing, Long> {

}
