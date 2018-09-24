package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.StatisticalUnit;

public interface StatisticalUnitRepository extends JpaRepository<StatisticalUnit, Long> {
}
