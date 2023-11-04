package com.hdsmf.service;

import com.hdsmf.dto.IOBoundDto;
import com.hdsmf.entity.IOBound;
import com.hdsmf.repository.IOBoundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class IOBoundService {

    private final IOBoundRepository iOBoundRepository;

    public IOBoundService(IOBoundRepository iOBoundRepository) {
        this.iOBoundRepository = iOBoundRepository;
    }

    public void saveIOBound(List<IOBoundDto> ioBoundDtos) {
        List<IOBound> iOBounds = new ArrayList<>();

        for (IOBoundDto ioBoundDto : ioBoundDtos) {
            iOBounds.add(IOBound.createIOBound(ioBoundDto));
        }

        iOBoundRepository.saveAll(iOBounds); // 새로운 데이터 저장
    }

    public List<IOBoundDto> getIOBoundList() {
        List<IOBoundDto> iOBoundDtos =  new ArrayList<>();

        for(IOBound iOBound : iOBoundRepository.findAll()){
            iOBoundDtos.add(IOBoundDto.of(iOBound));

        }

        return iOBoundDtos;
    }
}
