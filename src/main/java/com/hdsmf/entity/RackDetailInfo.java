package com.hdsmf.entity;

import com.hdsmf.dto.RackDetailInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rackDetailInfo")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class RackDetailInfo {

    @Id
    private Long rackDetailNo;

    private String rackId;

    private Long rackNo;

    private Long columnNo;

    private Long rowNo;

    private Long categoryNo;

    private Long maxWeight;

    private Long minWeight;

    @Builder
    public RackDetailInfo(Long rackDetailNo, String rackId, Long rackNo, Long columnNo, Long rowNo, Long categoryNo, Long maxWeight, Long minWeight) {
        this.rackDetailNo = rackDetailNo;
        this.rackId = rackId;
        this.rackNo = rackNo;
        this.columnNo = columnNo;
        this.rowNo = rowNo;
        this.categoryNo = categoryNo;
        this.maxWeight = maxWeight;
        this.minWeight = minWeight;
    }

    public static RackDetailInfo createRackDetailInfo(RackDetailInfoDto rackDetailInfoDto) {
        return RackDetailInfo.builder()
                .rackDetailNo(rackDetailInfoDto.getRackDetailNo())
                .rackId(rackDetailInfoDto.getRackId())
                .rackNo(rackDetailInfoDto.getRackNo())
                .columnNo(rackDetailInfoDto.getColumnNo())
                .rowNo(rackDetailInfoDto.getRowNo())
                .categoryNo(rackDetailInfoDto.getCategoryNo())
                .maxWeight(rackDetailInfoDto.getMaxWeight())
                .minWeight(rackDetailInfoDto.getMinWeight())
                .build();
    }

    public void updateRackDetailInfoDto(RackDetailInfoDto dto) {
        this.rackId = dto.getRackId();
        this.rackNo = dto.getRackNo();
        this.columnNo = dto.getColumnNo();
        this.rowNo = dto.getRowNo();
        this.categoryNo = dto.getCategoryNo();
        this.maxWeight = dto.getMaxWeight();
        this.minWeight = dto.getMinWeight();
    }
}
