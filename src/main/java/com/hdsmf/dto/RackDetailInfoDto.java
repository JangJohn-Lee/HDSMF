package com.hdsmf.dto;

import com.hdsmf.entity.RackDetailInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RackDetailInfoDto {

    private Long rackDetailNo;

    private String rackId;

    private Long rackNo;

    private Long columnNo;

    private Long rowNo;

    private Long categoryNo;

    private Long maxWeight;

    private Long minWeight;


    private static ModelMapper modelMapper = new ModelMapper();

    public static RackDetailInfoDto of(RackDetailInfo rackDetailInfo){
        return  modelMapper.map(rackDetailInfo, RackDetailInfoDto.class);
    }



}
