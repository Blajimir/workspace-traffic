package ru.blaj.workspacetraffic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.blaj.workspacetraffic.model.CamImage;

import java.util.Date;
import java.util.List;

public interface CamImageRepository extends JpaRepository<CamImage, Long> {
    void deleteAllByCameraId(Long id);
    @Query("select img from CamImage as img where img.cameraId = :id and img.timestamp >= :start and img.timestamp <= :end")
    Page<CamImage> findAllByCameraId(@Param("id") Long id, @Param("start") Date startDate,
                                     @Param("end") Date endDate, Pageable pageable);
}
