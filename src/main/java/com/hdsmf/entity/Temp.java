package com.hdsmf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Temp {

    @Id
    private Long tempPalletNo;

    private Long categoryNo;

    private String componentNo;

    private Long componentAmount;

    private Long totalWeight;

    private String rackLocation;

    @Column(nullable = true)
    private String reason;

    @Column(nullable = true)
    private String modelName;

    @Column(nullable = true)
    private String process;

    @Column(nullable = true)
    private String sNo;

}
