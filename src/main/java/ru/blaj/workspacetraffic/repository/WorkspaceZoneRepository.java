package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.WorkspaceZone;

public interface WorkspaceZoneRepository extends JpaRepository<WorkspaceZone, Long> {
}
