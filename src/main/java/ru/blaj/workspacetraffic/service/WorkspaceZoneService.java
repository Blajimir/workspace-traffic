package ru.blaj.workspacetraffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.blaj.workspacetraffic.model.WorkspaceZone;
import ru.blaj.workspacetraffic.repository.WorkspaceZoneRepository;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class WorkspaceZoneService {
    @Autowired
    private WorkspaceZoneRepository zoneRepository;

    public Collection<WorkspaceZone> getAllZones() {
        return Collections.unmodifiableCollection(this.zoneRepository.findAll());
    }

    public WorkspaceZone getZone(@NotNull Long id) {
        return Optional.of(id)
                .filter(il -> il != 0)
                .map(il -> this.zoneRepository.findById(il).orElse(null))
                .orElse(null);
    }

    public WorkspaceZone addZone(@NotNull WorkspaceZone zone){
        if(zone.getId()!=null){
            zone.setId(null);
        }
        return this.zoneRepository.save(zone);
    }

    public WorkspaceZone saveZone(@NotNull WorkspaceZone zone){
        return Optional.of(zone)
                .filter(z -> z.getId()!=null && z.getId()!=0)
                .map(z -> this.zoneRepository.save(z))
                .orElse(null);
    }

    public void deleteZone(@NotNull WorkspaceZone zone){
            this.zoneRepository.delete(zone);
    }

    public void deleteZone(@NotNull Long id){
        if(this.zoneRepository.existsById(id)){
            this.zoneRepository.deleteById(id);
        }
    }

}
