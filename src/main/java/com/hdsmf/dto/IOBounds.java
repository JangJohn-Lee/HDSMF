package com.hdsmf.dto;

import java.time.LocalDateTime;

public interface IOBounds {

    //Long->String 변환 ('-'포함)
    String getComponentNo();

    Long getComponentAmount();

    LocalDateTime getInboundDate();

    Long getRackNo(); //1~10번 rack 조회

    String getComponentState();

    String getProduct();

    String getRackId();


}
