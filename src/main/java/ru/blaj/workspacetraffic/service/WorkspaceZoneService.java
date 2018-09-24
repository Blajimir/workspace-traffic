package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.repository.WorkspaceZoneRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class WorkspaceZoneService {
    @Autowired
    private WorkspaceZoneRepository zoneRepository;

    public Collection<WorkspaceZone> getAllZones(){
        return Collections.unmodifiableCollection(this.zoneRepository.findAll());
    }

}
