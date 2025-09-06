package com.seasontone.repository;

import com.seasontone.dto.PhotoMetaProjection;
import com.seasontone.entity.RecordPhoto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface RecordPhotoRepository extends JpaRepository<RecordPhoto, Long> {

  @Query("""
   select p.id as id, p.filename as filename, p.contentType as contentType,
          p.size as size, p.caption as caption, p.createdAt as createdAt
   from RecordPhoto p
   where p.items.id = :itemsId
   order by p.id desc
  """)
  List<PhotoMetaProjection> findMetaByItems_Id(Long itemsId);

  Optional<RecordPhoto> findByIdAndItems_Id(Long photoId, Long itemsId);

  long deleteByIdAndItems_Id(Long photoId, Long itemsId);

  Optional<RecordPhoto> findTopByItems_IdOrderByIdDesc(Long itemsId);
}
