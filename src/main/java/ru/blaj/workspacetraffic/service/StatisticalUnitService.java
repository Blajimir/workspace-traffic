package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.repository.StatisticalUnitRepository;

@Service
public class StatisticalUnitService {
    private StatisticalUnitRepository statisticalUnitRepository;

    @Autowired
    public StatisticalUnitService(StatisticalUnitRepository statisticalUnitRepository) {
        this.statisticalUnitRepository = statisticalUnitRepository;
    }

    
}
