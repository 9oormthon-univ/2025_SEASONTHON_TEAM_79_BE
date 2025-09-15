package com.seasontone.repository;

import com.seasontone.dto.photo.PhotoMetaProjection;
import com.seasontone.domain.checklists.RecordPhoto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordPhotoRepository extends JpaRepository<RecordPhoto, Long> {
  List<RecordPhoto> findByItems_Id(Long itemsId);
  Optional<RecordPhoto> findByIdAndItems_Id(Long photoId, Long itemsId);
  long deleteByIdAndItems_Id(Long photoId, Long itemsId);
  Optional<RecordPhoto> findTopByItems_IdOrderByIdDesc(Long itemsId);
}

