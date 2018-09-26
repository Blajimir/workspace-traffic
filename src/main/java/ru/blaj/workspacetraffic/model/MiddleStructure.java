package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

/**
 * Класс - модель промежуточная структура для облегчения обмена
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Data
@NoArgsConstructor
public class MiddleStructure {
    private HashMap<AbsoluteZone, AbsoluteZone> betweenZones;
    private BufferedImage source;
    private BufferedImage dest;

    public MiddleStructure(BufferedImage source, BufferedImage dest,
                           List<AbsoluteZone> srcZones, List<AbsoluteZone> dstZones){
        this.source = source;
        this.dest = dest;
        betweenZones = new HashMap<>();
        for(AbsoluteZone srcZone: srcZones) {
           betweenZones.put(srcZone, dstZones.get(srcZones.indexOf(srcZone)));
        }
    }
}
