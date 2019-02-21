package ru.blaj.workspacetraffic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.blaj.workspacetraffic.model.CamImage;
import ru.blaj.workspacetraffic.model.StatisticalUnit;
import ru.blaj.workspacetraffic.service.CamImageService;
import ru.blaj.workspacetraffic.service.StatisticalUnitService;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/statics", produces = "application/json;charset=utf-8")
public class StatisticalUnitController {
    @Autowired
    private StatisticalUnitService statisticalUnitService;
    @Autowired
    private CamImageService camImageService;

    @GetMapping(path = "/{id}")
    public Collection<StatisticalUnit> getStaticsByCameraId(@NotNull @PathVariable("id") Long id){
        return statisticalUnitService.getUnitsByCamera(id);
    }

    @GetMapping(path = "/image/{id}")
    public Map<String,Object> getPaginationCamImages(@RequestParam(name = "startDate", required = false) String start,
                                                     @RequestParam(name = "endDate", required = false) String end,
                                                     @RequestParam("page") int page, @RequestParam("size") int size,
                                                     @PathVariable("id") Long id){
        Page<CamImage> camImagePage = camImageService.getAllByCameraId(id, start, end, page, size);
        HashMap<String, Object> mapBody = new HashMap<>();
        mapBody.put("logs", camImagePage.stream().collect(Collectors.toList()));
        mapBody.put("totalPages", camImagePage.getTotalPages());
        mapBody.put("totalElements", camImagePage.getTotalElements());
        return  mapBody;
    }

}
