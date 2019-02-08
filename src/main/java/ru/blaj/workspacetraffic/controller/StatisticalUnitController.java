package ru.blaj.workspacetraffic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.service.CamImageService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping(path="/api/statics", produces = "application/json;charset=utf-8")
public class StatisticalUnitController {
    @Autowired
    private StatisticalUnitService statisticalUnitService;
    @Autowired
    private CamImageService camImageService;

    @RequestMapping(path = "/{id}")
    public Collection<StatisticalUnit> getStaticsByCameraId(@NotNull @PathVariable("id") Long id){
        return statisticalUnitService.getUnitsByCamera(id);
    }
}
