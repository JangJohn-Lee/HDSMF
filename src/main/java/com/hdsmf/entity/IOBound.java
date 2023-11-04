package com.hdsmf.entity;

import com.hdsmf.dto.IOBoundDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ioBound")
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class IOBound {

    @Id
    private String inboundNo;

    private Long palletNo;

    private Long categoryNo;

    private String componentNo;

    private Long componentAmount;

    private Double totalWeight;

    private LocalDateTime inboundDate;

    private Long rackDetailNo; //rack위치



    @Builder
    public IOBound(String inboundNo, Long palletNo, Long categoryNo, String componentNo, Long componentAmount, Double totalWeight, LocalDateTime inboundDate, Long rackDetailNo) {
        this.inboundNo = inboundNo;
        this.palletNo = palletNo;
        this.categoryNo = categoryNo;
        this.componentNo = componentNo;
        this.componentAmount = componentAmount;
        this.totalWeight = totalWeight;
        this.inboundDate = inboundDate;
        this.rackDetailNo = rackDetailNo;
    }

    public static IOBound createIOBound(IOBoundDto ioBoundDto) {
        return IOBound.builder()
                .inboundNo(ioBoundDto.getInboundNo())
                .palletNo(ioBoundDto.getPalletNo())
                .categoryNo(ioBoundDto.getCategoryNo())
                .componentNo(ioBoundDto.getComponentNo())
                .componentAmount(ioBoundDto.getComponentAmount())
                .totalWeight(ioBoundDto.getTotalWeight())
                .inboundDate(ioBoundDto.getInboundDate())
                .rackDetailNo(ioBoundDto.getRackDetailNo())
                .build();
    }

}
