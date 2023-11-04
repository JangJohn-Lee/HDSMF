package com.hdsmf.dto;

import com.hdsmf.entity.Components;
import com.hdsmf.entity.IOBound;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ComponentsDto {

    private String componentNo;

    private Long plantNo;

    private String categoryName;

    private String model;

    private String product;

    private String sNo; //소재품번

    private double sWeight; //소재 무게

    private double fWeight; //완제품 무게

    private String componentState; //1-소재, 2-CNC1차



    private static ModelMapper modelMapper = new ModelMapper();

    public static ComponentsDto of(Components components){
        return  modelMapper.map(components, ComponentsDto.class);
    }
}
