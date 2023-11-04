package com.hdsmf.entity;

import com.hdsmf.dto.RackInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rackInfo")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class RackInfo {

    @Id
    private Long rackNo;

    private String rackName;

    private Long columnNum;

    private Long rowNum;

    @Builder
    public RackInfo(Long rackNo, String rackName, Long columnNum, Long rowNum) {
        this.rackNo = rackNo;
        this.rackName = rackName;
        this.columnNum = columnNum;
        this.rowNum = rowNum;
    }

    public static RackInfo createRackInfo(RackInfoDto rackInfoDto) {
        return RackInfo.builder()
                .rackNo(rackInfoDto.getRackNo())
                .rackName(rackInfoDto.getRackName())
                .columnNum(rackInfoDto.getColumnNum())
                .rowNum(rackInfoDto.getRowNum())
                .build();
    }
}
