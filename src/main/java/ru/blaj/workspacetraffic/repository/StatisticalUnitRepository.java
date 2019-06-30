package ru.blaj.workspacetraffic.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.blaj.workspacetraffic.model.StatisticalUnit;

import java.util.Collection;

public interface StatisticalUnitRepository extends JpaRepository<StatisticalUnit, Long> {
    Collection<StatisticalUnit> findAllByCameraId(Long id);
    Page<StatisticalUnit> findAllByCameraId(Long id, Pageable pageable);
    void deleteAllByCameraId(Long id);
}
