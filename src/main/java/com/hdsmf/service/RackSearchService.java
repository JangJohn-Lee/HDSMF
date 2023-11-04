package com.hdsmf.service;

import com.hdsmf.repository.RackRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class RackSearchService {

    private final RackRepository rackRepository;

    //Rack별 row의 개수
    public int selectRowNo(long rackNo){                                        //Rack별 row의 개수
        return rackRepository.selectRowNo(rackNo);
    }

    //Rack별 column의 개수
    public int selectColNo(long rackNo){                                        //Rack별 column의 개수
        return rackRepository.selectColNo(rackNo);
    }

    //Rack 총 개수
    public int selectRackNo(){                                                  //Rack 총 개수
        return rackRepository.selectRackNo();
    }
}
