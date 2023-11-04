package com.hdsmf.dto;

import com.hdsmf.entity.IOBound;
import com.hdsmf.entity.RackDetailInfo;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IOBoundDto {

    private String inboundNo;

    private Long palletNo;

    private Long categoryNo;

    private String componentNo;

    private Long componentAmount;

    private double totalWeight;

    private LocalDateTime inboundDate;

    private String product;

    private String componentState; //1-소재, 2-CNC1차

    private String rackId; //위치

    private long rackDetailNo; //rack위치


    public IOBoundDto(LocalDateTime inboundDate, String componentNo, String componentState, Long componentAmount, String product, Long rackDetailNo,String rackId ) {
        this.inboundDate = inboundDate;
        this.componentNo = componentNo;
        this.componentState = componentState;
        this.componentAmount = componentAmount;
        this.product = product;
        this.rackDetailNo = rackDetailNo;
        this.rackId = rackId;
    }


    public IOBoundDto(String inboundNo, Long categoryNo, Long componentAmount, String componentNo, LocalDateTime inboundDate, Long palletNo, Long rackDetailNo, Double totalWeight) {
        this.inboundNo = inboundNo;
        this.palletNo = palletNo;
        this.categoryNo = categoryNo;
        this.componentNo = componentNo;
        this.componentAmount = componentAmount;
        this.totalWeight = totalWeight;
        this.inboundDate = inboundDate;
        this.rackDetailNo = rackDetailNo;
    }


    private static ModelMapper modelMapper = new ModelMapper();

    public static IOBoundDto of(IOBound ioBound){
        return  modelMapper.map(ioBound, IOBoundDto.class);
    }

}