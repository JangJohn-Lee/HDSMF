package com.hdsmf.service;

import com.hdsmf.dto.ComponentsDto;
import com.hdsmf.dto.IOBoundDto;
import com.hdsmf.entity.Components;
import com.hdsmf.entity.IOBound;
import com.hdsmf.repository.ComponentsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ComponentsService {

    private final ComponentsRepository componentsRepository;

    // 생성자를 통한 의존성 주입
    public ComponentsService(ComponentsRepository componentsRepository){
        this.componentsRepository = componentsRepository;
    }

    public void saveComponents(List<ComponentsDto> componentsDtos) {
        List<Components> components = new ArrayList<>();

        for (ComponentsDto componentsDto : componentsDtos) {
            components.add(Components.createComponents(componentsDto));
        }

        componentsRepository.saveAll(components); // 새로운 데이터 저장
    }

    //조회
    public List<Components> getAllComponents() {
        return componentsRepository.findAll();
    }

    //등록
    public Components saveComponent(Components components) {

        String componentState = components.getComponentState();
        String componentNo = components.getComponentNo();

        return componentsRepository.save(components);
    }

    //componentNo별 조회
    public Components findAllByComponentNo(String componentNo) {
        return  componentsRepository.findAllByComponentNo(componentNo);
    }

    //수정
    public Components updateComponent(Components components) {
        return componentsRepository.saveAndFlush(components);
    }

    //삭제
    public void deleteComponent(String componentNo) {
        componentsRepository.deleteByComponentNo(componentNo);
    }

    public List<Components> searchComponentsByField(String searchField, String searchValue) {
        return componentsRepository.searchComponentsByField(searchField, searchValue);
    }

    // ComponentsDto로 Components 전체 리스트 받아오기
    public List<ComponentsDto> getComponentsList() {
        List<ComponentsDto> componentsDtoDtos =  new ArrayList<>();

        for(Components components : componentsRepository.findAll()){
            componentsDtoDtos.add(ComponentsDto.of(components));
        }
        return componentsDtoDtos;
    }

    public Integer selectComponentNoCheck(String componentNo) {
        return componentsRepository.selectComponentNoCheck(componentNo);
    }

    public Integer selectIoBoundComponentNoCheck(String componentNo) {
        return componentsRepository.selectIoBoundComponentNoCheck(componentNo);
    }
}
