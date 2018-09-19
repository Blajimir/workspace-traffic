package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс - модель Зон, зоны нужны для того чтобы отметить активные зоны на изображениях получаемых с камер
 * @author Alesandr Kovalev aka blajimir
 * */

@Entity
@Table(name = "workspace_zones")
@Data
@NoArgsConstructor
public class WorkspaceZone extends Zone{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_zones_seq")
    @SequenceGenerator(name = "workspace_zones_seq", sequenceName = "workspace_zones_id_seq", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private String name;
}
