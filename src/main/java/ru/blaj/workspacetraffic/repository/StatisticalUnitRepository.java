package ru.blaj.workspacetraffic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.blaj.workspacetraffic.model.StatisticalUnit;

import java.util.Collection;

public interface StatisticalUnitRepository extends JpaRepository<StatisticalUnit, Long> {
    Collection<StatisticalUnit> findAllByCamera_Id(Long id);
    void deleteAllByCamera_Id(Long id);
}
