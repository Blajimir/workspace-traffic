package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.blaj.workspacetraffic.model.StatisticalUnit;

import java.util.Collection;

public interface StatisticalUnitRepository extends JpaRepository<StatisticalUnit, Long> {
    Collection<StatisticalUnit> findAllByCameraId(Long id);
    void deleteAllByCameraId(Long id);
}
