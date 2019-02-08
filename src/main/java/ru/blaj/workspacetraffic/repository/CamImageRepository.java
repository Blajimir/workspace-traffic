package ru.blaj.workspacetraffic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.CamImage;

import java.util.List;

public interface CamImageRepository extends JpaRepository<CamImage, Long> {
    void deleteAllByCameraId(Long id);
    Page<CamImage> findAllByCameraId(Long id, Pageable pageable);
}
