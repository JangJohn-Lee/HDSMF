package com.hdsmf.service;

import com.hdsmf.dto.CategoryDto;
import com.hdsmf.dto.IOBoundDto;
import com.hdsmf.dto.RackDetailInfoDto;
import com.hdsmf.dto.RackInfoDto;
import com.hdsmf.entity.Category;
import com.hdsmf.entity.IOBound;
import com.hdsmf.entity.RackDetailInfo;
import com.hdsmf.entity.RackInfo;
import com.hdsmf.repository.CategoryRepository;
import com.hdsmf.repository.RackDetailInfoRepository;
import com.hdsmf.repository.RackInfoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RackInfoService {
    private final RackInfoRepository rackInfoRepository;
    private final CategoryRepository categoryRepository;
    private final RackDetailInfoRepository rackDetailInfoRepository;

    public RackInfoService(RackInfoRepository rackInfoRepository, CategoryRepository categoryRepository, RackDetailInfoRepository rackDetailInfoRepository){
        this.rackInfoRepository = rackInfoRepository;
        this.categoryRepository = categoryRepository;
        this.rackDetailInfoRepository = rackDetailInfoRepository;
    }

    public List<CategoryDto> getCategoryList() {
        List<CategoryDto> categoryDtos =  new ArrayList<>();

        for(Category category : categoryRepository.findAll()){
            categoryDtos.add(CategoryDto.of(category));
        }
        return categoryDtos;
    }


    public void saveRackInfo(List<RackInfoDto> rackInfoDtos) {
        List<RackInfo> rackInfos = new ArrayList<>();

        for (RackInfoDto rackInfoDto : rackInfoDtos) {
            rackInfos.add(RackInfo.createRackInfo(rackInfoDto));
        }

        rackInfoRepository.saveAll(rackInfos); // 새로운 데이터 저장
    }

    public List<RackInfoDto> getRackInfoList() {
        List<RackInfoDto> rackInfoDtos =  new ArrayList<>();

        for(RackInfo rackInfo : rackInfoRepository.findAll()){
            rackInfoDtos.add(RackInfoDto.of(rackInfo));
        }
        return rackInfoDtos;
    }

}
