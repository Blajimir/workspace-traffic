package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
