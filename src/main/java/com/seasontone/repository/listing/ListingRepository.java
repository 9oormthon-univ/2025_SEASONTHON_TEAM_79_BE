package com.seasontone.repository.listing;

import com.seasontone.domain.listing.Listing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
  @Query(value = """
    WITH ranked AS (
        SELECT l.listing_id,
               l.road_address,
               l.latitude,
               l.longitude,
               l.listing_name,
               l.updated_at,
               ROW_NUMBER() OVER (
                   PARTITION BY l.road_address
                   ORDER BY l.updated_at DESC, l.listing_id DESC
               ) AS rn
        FROM listing l
        WHERE l.latitude IS NOT NULL AND l.longitude IS NOT NULL
          AND l.road_address IS NOT NULL AND l.road_address <> ''
    ),
    agg AS (
        SELECT l.road_address, COUNT(ci.listing_id) AS checklist_count   -- <-- 여기만 교체!
        FROM listing l
        LEFT JOIN checklist_items ci ON ci.listing_id = l.listing_id
        GROUP BY l.road_address
    )
    SELECT r.listing_id         AS listingId,
           r.road_address       AS roadAddress,
           r.latitude           AS latitude,
           r.longitude          AS longitude,
           r.listing_name       AS listingName,
           COALESCE(a.checklist_count, 0) AS checklistCount
    FROM ranked r
    LEFT JOIN agg a ON a.road_address = r.road_address
    WHERE r.rn = 1
    """, nativeQuery = true)
  List<Object[]> findMapMarkersRaw();
}
