package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.Camera;

public interface CameraRepository extends JpaRepository<Camera, Long> {
}
