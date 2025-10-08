package com.skynetauth.auth_service.models;

import com.skynetauth.auth_service.config.Auditable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ship_tos")
@NoArgsConstructor
public class ShipTo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "sold_tos_id")
    private SoldTo soldTos;

    public ShipTo(String name, SoldTo soldTos) {
        this.name = name;
        this.soldTos = soldTos;
    }

}