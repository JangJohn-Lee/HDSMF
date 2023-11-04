package com.hdsmf.entity;

import com.hdsmf.dto.ComponentsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "components")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Components {

    @Id
    private String componentNo;

    private Long plantNo;

    private String categoryName;

    private String model;

    private String product;

    private String sNo; //소재품번

    private double sWeight; //소재 무게

    private double fWeight; //완제품 무게

    private String componentState; //1-소재, 2-CNC1차

    @Builder
    Components(String componentNo, Long plantNo, String categoryName, String model, String product, String sNo, double sWeight, double fWeight, String componentState){
        this.componentNo = componentNo;
        this.plantNo = plantNo;
        this.categoryName = categoryName;
        this.model = model;
        this.product = product;
        this.sNo = sNo;
        this.sWeight = sWeight;
        this.fWeight = fWeight;
        this.componentState = componentState;
    }

    public static Components createComponents(ComponentsDto componentsDto){
        return Components.builder()
                .componentNo(componentsDto.getComponentNo())
                .plantNo(componentsDto.getPlantNo())
                .categoryName(componentsDto.getCategoryName())
                .model(componentsDto.getModel())
                .product(componentsDto.getProduct())
                .sNo(componentsDto.getSNo())
                .sWeight(componentsDto.getSWeight())
                .fWeight(componentsDto.getFWeight())
                .componentState(componentsDto.getComponentState())
                .build();
    }
}
