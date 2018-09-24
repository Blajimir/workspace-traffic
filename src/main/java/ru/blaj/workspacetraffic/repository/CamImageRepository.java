package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.CamImage;

public interface CamImageRepository extends JpaRepository<CamImage, Long> {
}
