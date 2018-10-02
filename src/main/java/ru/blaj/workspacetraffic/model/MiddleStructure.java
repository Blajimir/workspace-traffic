package ru.blaj.workspacetraffic.model;

import lombok.*;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс - модель промежуточная структура для облегчения обмена
 *
 * @author Alesandr Kovalev aka blajimir
 * */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class MiddleStructure {
    @Data
    @NoArgsConstructor
    public class MiddleStructureUnit{
        private Zone sourceZone;
        private AbsoluteZone sourceAbsoluteZone;
        private AbsoluteZone destAbsoluteZone;

        public MiddleStructureUnit withSourceZone(Zone sourceZone){
            this.sourceZone = sourceZone;
            return this;
        }

        public MiddleStructureUnit withSourceAbsoluteZone(AbsoluteZone sourceAbsoluteZone){
            this.sourceAbsoluteZone = sourceAbsoluteZone;
            return this;
        }

        public MiddleStructureUnit withDestAbsoluteZone(AbsoluteZone destAbsoluteZone){
            this.destAbsoluteZone = destAbsoluteZone;
            return this;
        }
    }
    private Set<MiddleStructureUnit> unionZones;
    private BufferedImage source;
    private BufferedImage dest;

    public MiddleStructure(BufferedImage source, BufferedImage dest, List<Zone> zones,
                           List<AbsoluteZone> srcZones, List<AbsoluteZone> dstZones){
        this.source = source;
        this.dest = dest;
        unionZones = zones.stream().map(z -> new MiddleStructureUnit().withSourceZone(z)
                .withSourceAbsoluteZone(srcZones.get(zones.indexOf(z)))
                .withDestAbsoluteZone(dstZones.get(zones.indexOf(z))))
                .collect(Collectors.toSet());
        }

    public MiddleStructureUnit getByZone(Zone zone){
        return this.unionZones.stream().filter(unit -> unit.getSourceZone() == zone).findFirst().orElse(null);
    }
}
