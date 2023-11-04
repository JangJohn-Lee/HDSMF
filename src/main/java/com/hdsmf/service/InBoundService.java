package com.hdsmf.service;

import com.hdsmf.dto.IOBounds;
import com.hdsmf.entity.Temp;
import com.hdsmf.repository.InBoundRepository;
import com.hdsmf.repository.TempRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class InBoundService {

    private final InBoundRepository inBoundRepository;
    private final TempRepository tempRepository;


    public List<IOBounds> selectRackList(String componentNo, long rackNo, String searchName) {

        //공정 상태 입력값 처리
        if ("완제품".equals(searchName)) {
            searchName = "0"; //소재 입력시 0으로 변환
        } else if ("소재".equals(searchName)) {
            searchName = "1"; //소재 입력시 1로 변환
        } else if ("CNC1차".equals(searchName)) {
            searchName = "2"; //소재 입력시 2로 변환
        } else if ("CNC2차".equals(searchName)) {
            searchName = "3"; //소재 입력시 3로 변환
        } else if ("MCT1차".equals(searchName)) {
            searchName = "4"; //소재 입력시 4로 변환
        }
        List<IOBounds> list = inBoundRepository.getSearchList(componentNo, rackNo, searchName);

        return list;

    }

    public List<IOBounds> selectAllRackList() {
        return inBoundRepository.getAllSearchList();
    }
    public List<IOBounds> searchExcelList(String componentNo) {
        Map<String, List<String>> map = new HashMap<>();

        List<IOBounds> resultArr = new ArrayList<>();

        Long count = inBoundRepository.countByComponentNoContainingParam(componentNo);

        //iobound에 존재하는 componentNo 개수가 1이상
        if (count > 1) {

            List<IOBounds> result = selectRackList(componentNo, 0L, "");

            for (int j = 0; j < result.size(); j++) {
                resultArr.add(result.get(j));
            }
        }

        return resultArr;
    }

    //pallet번호
    public long selectPalletNo() {

        long palletNo = 0;

        //날짜 230808 형태로 나타내게 하는 부분
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = LocalDateTime.now().format(formatter);

        //0001, 0002부분
        String palletNum;

        //초기화
        long firstNum = 1;

        Integer palletMaxNum = inBoundRepository.selectPalletNo();

        if (palletMaxNum == null) {
            palletNum = String.format("%04d", firstNum);
        } else {
            palletNum = String.format("%04d", palletMaxNum + 1);
        }

        palletNo = Long.parseLong(date + palletNum);

        return palletNo;
    }

    //temp_pallet번호
    public long selectTempPalletNo() {

        long tempPalletNo = 0;

        //날짜 230808 형태로 나타내게 하는 부분
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String date = LocalDateTime.now().format(formatter);

        //0001, 0002부분
        String tempPalletNum;

        //초기화
        long firstNum = 1;

        Integer palletMaxNum = tempRepository.selectTempPalletNo();

        if (palletMaxNum == null) {
            tempPalletNum = String.format("%04d", firstNum);
        } else {
            tempPalletNum = String.format("%04d", palletMaxNum + 1);
        }

        tempPalletNo = Long.parseLong(date + tempPalletNum);

        return tempPalletNo;
    }


    //모델명 뽑기
    public String selectModelName(String componentNo, String state) {
        return inBoundRepository.selectModelName(componentNo, state);
    }

    //제품명 뽑기
    public String selectComponentNo(String componentNo, String state) {
        return inBoundRepository.selectComponentNo(componentNo, state);
    }


    //위치 선정
    public Map<String, List<String>> checkRackLocation(Map<String, List<String>> map) {

        for (int i = 0; i < map.get("componentNo").size(); i++) {
            if (map.get("componentNo").get(i) != null) {
                String componentNo = map.get("componentNo").get(i);
                long palletNo = Long.parseLong(map.get("palletNo").get(i));
                long categoryNo = inBoundRepository.selectCategoryNo(componentNo);
                long amount = Long.parseLong(map.get("inBoundAmount").get(i));
                double weight = selectCompoWeight(componentNo);

                double totalWeight = weight * amount;

                String process = map.get("process").get(i);
                String modelName = map.get("modelName").get(i);

                tempRepository.saveTemp(palletNo, categoryNo, amount, componentNo, totalWeight, process, modelName);
            }

        }

        List<String> componentNoList = map.get("componentNo");

        insertRackLocation(componentNoList);

        Map<String, List<String>> rackLocationMap = new HashMap<>();

        List<String> rackLocation = new ArrayList<>();
        List<String> failReason = new ArrayList<>();

        for (int i = 0; i < map.get("palletNo").size(); i++) {
            long palletNo = Long.parseLong(map.get("palletNo").get(i));
            rackLocation.add(selectRackLocation(palletNo));
            failReason.add(tempRepository.selectFailReason(palletNo));
        }

        rackLocationMap.put("rackLocation", rackLocation);
        rackLocationMap.put("failReason", failReason);

        return rackLocationMap;
    }


    //위치 설정
    public void insertRackLocation(List<String> componentNoList){
        //카테고리별 필요한 자리 개수
        Map<Integer, Integer> categoryList = selectCategoryList(componentNoList);
        //들어갈 수 있는 rack list
        Map<Integer, List<String>> rackList = selectRackId(categoryList);
        //들어갈 수 있는 rack list의 개수
        Map<Integer, Integer> rackNumMap = new HashMap<>();

        for (Integer rackNum : rackList.keySet()) {
            if (rackNum != null) {
                rackNumMap.put(rackNum, rackList.get(rackNum).size());
            }
        }

        List<String> orderByPalletNo = new ArrayList<>();

        for (Integer categoryNum : categoryList.keySet()) {
            if (categoryNum != null) {
                orderByPalletNo = tempRepository.selectPalletNo(categoryNum);

                for (int i = 0; i < orderByPalletNo.size(); i++) {

                    long palletNo = Long.parseLong(orderByPalletNo.get(i));

                    if (rackList.get(categoryNum).size() > i) {

                        tempRepository.insertRackLocation(rackList.get(categoryNum).get(i), palletNo);
                    } else if (rackList.get(categoryNum).size() <= i) {

                        tempRepository.insertRackLocation("적재 불가", palletNo);
                        tempRepository.insertFailReason("A", palletNo);
                    }
                }
            }
        }

        //1500kg 이상일 경우 처리
        for (Integer categoryNum : categoryList.keySet()) {
            if (categoryNum != null) {

                //1. 1500kg 이상인 temp_pallet_no 중 rack_id의 마지막이 1이 아닌 것 출력
                List<String> maxPalletNo = tempRepository.selectMaxWeight(categoryNum);
                //2. 잔여 Rack list 개수에서 categoryList의 value값 만큼 빼기

                //잔여 1층 Rack
                List<String> firstFloor = tempRepository.selectFirstFloor(categoryNum);
                int num = rackNumMap.get(categoryNum) - categoryList.get(categoryNum);

                for (int j = 0; j < maxPalletNo.size(); j++) {
                    long palletNo = Long.parseLong(maxPalletNo.get(j));
                    if (j < firstFloor.size()) {
                        tempRepository.insertRackLocation(firstFloor.get(j), palletNo);
                    } else {
                        tempRepository.insertRackLocation("적재 불가", palletNo);
                        tempRepository.insertFailReason("B", palletNo);
                    }
                }
            }
        }
    }


    //카테고리 출력
    public Map<Integer, Integer> selectCategoryList(List<String> componentNo) {

        List<Integer> categoryNoList = new ArrayList<>();

        List<String> componentNoList = componentNo;

        Map<Integer, Integer> categoryListMap = new HashMap<>();

        for (int i = 0; i < componentNoList.size(); i++) {
            if (componentNoList.get(i) != null) {
                long categoryNo = inBoundRepository.selectCategoryNo(componentNoList.get(i));
                categoryNoList.add((int) categoryNo);
            } else {
                categoryNoList.add(null);
            }
        }

        Set<Integer> set = new HashSet<>(categoryNoList);
        for (Integer str : set) {
            categoryListMap.put(str, Collections.frequency(categoryNoList, str));
        }

        return categoryListMap;
    }


    //렉 분류
    public Map<Integer, List<String>> selectRackId(Map<Integer, Integer> categoryList) {

        List<Integer> categoryNoList = new ArrayList<>();
        Map<Integer, List<String>> rackList = new HashMap<>();

        for (Integer key : categoryList.keySet()) {
            categoryNoList.add(key);
        }

        for (int i = 0; i < categoryNoList.size(); i++) {
            Integer categoryNo = categoryNoList.get(i);
            if (categoryNo != null) {
                List<String> rackIdList = inBoundRepository.selectRackId((long)categoryNo);
                rackList.put(categoryNo, rackIdList);
            } else {
                rackList.put(null, null);
            }

        }
        return rackList;
    }

    //완제품 여부에 따른 제품 무게 출력
    public double selectCompoWeight(@Param("componentNo") String coponentNo) {
        long finCompoYn = inBoundRepository.selectFinCompo(coponentNo);
        double weight = 0;
        if (finCompoYn == 0) {
            //완제품 component_state = 0
            weight = inBoundRepository.selectFWeight(coponentNo);
        } else {
            //완제품 아닐 경우 1,2,3,4 등등
            weight = inBoundRepository.selectSWeight(coponentNo);
        }
        return weight;
    }

    public String selectRackLocation(long palletNo) {
        return tempRepository.selectRackLocation(palletNo);
    }

    public void insertInBound(String inBoundNo, long palletNo, long tempPalletNo) {
        inBoundRepository.insertIoBound(inBoundNo, palletNo, tempPalletNo);
    }

    public void deleteTemp() {
        tempRepository.deleteTemp();
    }

    public void deleteComponentNo(long tempPalletNo) {
        tempRepository.deleteByComponentNo(tempPalletNo);
    }

    public List<Temp> selectInbound(){
        return tempRepository.selectAll();
    }

    public long countByComponentNo(String componentno) {
        return inBoundRepository.countByComponentNoContainingParam(componentno);
    }

    //실시간 검색 - 원래 사용
    public List<String> findSelctComponentList(String searchNum){
        return inBoundRepository.findSelectComponentNoList(searchNum);
    }


    public void rackLocationNull(long palletNo){
        tempRepository.insertRackLocation(null, palletNo);
        tempRepository.insertFailReason(null, palletNo);
    }

    public void selectTempList(){
        List<String> componentList = new ArrayList<>();

        List<Temp> tempArrayList = selectInbound();

        for(int i = 0; i < tempArrayList.size(); i++){
            componentList.add(tempArrayList.get(i).getComponentNo());
            long palletNo = tempArrayList.get(i).getTempPalletNo();
            rackLocationNull(palletNo);
        }

        insertRackLocation(componentList);
    }
}