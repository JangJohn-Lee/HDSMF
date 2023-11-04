package com.hdsmf.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RackDetailSearchDto {
    //RackDetailInfo
    private String rackId;
    private Long rackDetailNo;
    private Long columnNo;
    private Long rowNo;

    //Category
    private Long categoryNo;
    private String categoryName;
    private String categoryColor;

    //IOBound
    private Long componentAmount;
    private LocalDateTime inboundDate;

    //Components
    private String componentNo;
    private String product;
    private String model;
    private String componentState;

}
