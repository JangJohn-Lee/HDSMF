package com.hdsmf.service;

import com.hdsmf.entity.IOBound;
import com.hdsmf.repository.OutBoundRepository;
import com.hdsmf.repository.RackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@RequiredArgsConstructor
@Service
public class OutBoundService {

    private final OutBoundRepository outRepository;

    //출고 시 part_no 검색 → 포함된 리스트 출력
    public List<IOBound> selectIOBoundList(String partNo){                                  //출고 시 part_no 검색 → 포함된 리스트 출력
        return outRepository.selectIOBoundList(partNo);
    }

    //rack detail no으로 rack 위치 출력
    public String selectRackLocation(long rackNo){
        return outRepository.selectRackLocation(rackNo);
    }

    //출고 처리
    public void deleteRack(long rackNo){
        outRepository.deleteRack(rackNo);
    }

    public Map<String, List> searchIOBoundList(List<String> searchList){
        Map<String, List> map = new HashMap<String, List>();
        List<IOBound> resultMap = new ArrayList<IOBound>();

        for(int i = 0; i< searchList.size(); i++){
            resultMap.addAll(selectIOBoundList(String.valueOf(searchList.get(i))));
        }

        List<String> resultArr = new ArrayList<String>();

        for(int i =0; i < resultMap.size(); i++){
            long rackNo = resultMap.get(i).getRackDetailNo();
            resultArr.add(selectRackLocation(rackNo));
        }

        map.put("resultMap", resultMap);
        map.put("resultArr", resultArr);

        return map;
    }

    public List<String> OutComponentList(String componentNo){
        return outRepository.OutComponentList(componentNo);
    }
}