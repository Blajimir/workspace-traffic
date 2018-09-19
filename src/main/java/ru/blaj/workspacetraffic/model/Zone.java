package ru.blaj.workspacetraffic.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@NoArgsConstructor
public class Zone {
    @Column(precision = 10, scale = 2)
    protected Float left;
    @Column(precision = 10, scale = 2)
    protected Float top;
    @Column(precision = 10, scale = 2)
    protected Float width;
    @Column(precision = 10, scale = 2)
    protected Float height;
}
