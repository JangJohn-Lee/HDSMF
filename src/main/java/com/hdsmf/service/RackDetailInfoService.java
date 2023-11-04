package com.hdsmf.service;

import com.hdsmf.dto.CategoryDto;
import com.hdsmf.dto.IOBoundDto;
import com.hdsmf.dto.RackDetailInfoDto;
import com.hdsmf.entity.Category;
import com.hdsmf.entity.IOBound;
import com.hdsmf.entity.RackDetailInfo;
import com.hdsmf.repository.IOBoundRepository;
import com.hdsmf.repository.RackDetailInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RackDetailInfoService {

    private final RackDetailInfoRepository rackDetailInfoRepository;

    // 생성자를 통한 의존성 주입
    public RackDetailInfoService(RackDetailInfoRepository rackDetailInfoRepository){
        this.rackDetailInfoRepository = rackDetailInfoRepository;
    }

    public List<RackDetailInfoDto> getRackDetailInfoList() {
        List<RackDetailInfoDto> rackDetailInfoDtos =  new ArrayList<>();

        for(RackDetailInfo rackDetailInfo : rackDetailInfoRepository.findAll()){
            rackDetailInfoDtos.add(RackDetailInfoDto.of(rackDetailInfo));
        }
        return rackDetailInfoDtos;
    }

    public void saveRackDetailInfo(List<RackDetailInfoDto> rackDetailInfoDtos) {
        List<RackDetailInfo> rackDetailInfos = new ArrayList<>();

        for (RackDetailInfoDto rackDetailInfoDto : rackDetailInfoDtos) {
            rackDetailInfos.add(RackDetailInfo.createRackDetailInfo(rackDetailInfoDto));
        }

        rackDetailInfoRepository.saveAll(rackDetailInfos); // 새로운 데이터 저장
    }


    public void updateRackDetailInfos(List<RackDetailInfoDto> rackDetailInfoDtos) {
        List<RackDetailInfo> rackDetailInfosToUpdateOrInsert = new ArrayList<>();

        // 1. RackDetailInfoDto 목록에서 rackId만을 추출
        List<String> rackIdListFromDto = rackDetailInfoDtos.stream()
                .map(RackDetailInfoDto::getRackId)
                .collect(Collectors.toList());

        // 2. 데이터베이스에서 모든 RackDetailInfo 목록을 가져옴
        List<RackDetailInfo> allExistingRackDetailInfos = rackDetailInfoRepository.findAll();

        for (RackDetailInfoDto rackDetailInfoDto : rackDetailInfoDtos) {
            RackDetailInfo existingRackDetailInfo = allExistingRackDetailInfos.stream()
                    .filter(rackDetailInfo -> rackDetailInfo.getRackId().equals(rackDetailInfoDto.getRackId()))
                    .findFirst()
                    .orElse(null);

            if (existingRackDetailInfo != null) {
                // 존재하는 RackDetailInfo 업데이트 로직
                existingRackDetailInfo.updateRackDetailInfoDto(rackDetailInfoDto);  // 이름 변경
                rackDetailInfosToUpdateOrInsert.add(existingRackDetailInfo);
            } else {
                // 새로운 RackDetailInfo 추가 로직
                rackDetailInfosToUpdateOrInsert.add(RackDetailInfo.createRackDetailInfo(rackDetailInfoDto));
            }
        }

        // 3. 변경된 RackDetailInfo 목록 저장
        rackDetailInfoRepository.saveAll(rackDetailInfosToUpdateOrInsert);
    }

}
