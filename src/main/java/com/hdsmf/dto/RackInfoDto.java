package com.hdsmf.dto;

import com.hdsmf.entity.Category;
import com.hdsmf.entity.RackInfo;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RackInfoDto {

    private Long rackNo;

    private String rackName;

    private Long columnNum;

    private Long rowNum;

    private static ModelMapper modelMapper = new ModelMapper();

    public static RackInfoDto of(RackInfo rackInfo){
        return  modelMapper.map(rackInfo, RackInfoDto.class);
    }

}
