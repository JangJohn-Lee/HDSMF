package com.hdsmf.controller;

import com.hdsmf.dto.IOBoundDto;
import com.hdsmf.entity.Components;
import com.hdsmf.entity.Temp;
import com.hdsmf.repository.TempRepository;
import com.hdsmf.service.InBoundService;
import com.hdsmf.upload.ExcelReadOption;
import com.hdsmf.upload.UploadServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.time.LocalTime.now;

@Controller
@RequestMapping("/hdsmf")
public class InBoundController {

    private InBoundService inBoundService;
    private UploadServiceImpl uploadService;


    public InBoundController(InBoundService inBoundService, UploadServiceImpl uploadService) {
        this.inBoundService = inBoundService;
        this.uploadService = uploadService;
    }


    @GetMapping("/inbound")
    public String inbound() {

        inBoundService.deleteTemp();

        return "pages/inbound/inbound";
    }


    @PostMapping("/addList")
    @ResponseBody
    public Map<String, List<String>> selectInboundList(@RequestBody HashMap<String, List<String>> inBoundMap) {

        /*==================================================================================================================
        * 검색, 엑셀 등록 등을 통해 화면에서 전달 받은 데이터는 전부 Map<String, List<String>> 형태
        * <"componentList", ['0822']>
        * <"tableNum" , ['1']>        => 테이블 td 개수 파악해서 palletNo 출력하기 위해서 받는 데이터 -> palletNo은 DB 넣기 전에는 바뀜
        * <"process", ['0']>          => 0: 완제품, 1: 소재, 2: CNC 1차, 3: CNC 2차, 4: MCT 1차
        * <"inBoundAmount", ['10']>   => 입고할 제품 개수
        ==================================================================================================================*/

        Map<String, List<String>> map = new HashMap<>();

        List<String> modelNameList = new ArrayList<>();
        List<String> componentNoList = new ArrayList<>();
        List<String> processList = new ArrayList<>();
        List<String> palletNoList = new ArrayList<>();

        long palletNo = inBoundService.selectTempPalletNo();


        for (int i = 0; i < inBoundMap.get("componentList").size(); i++) {

            //사용자가 입력한 품번
            String component = inBoundMap.get("componentList").get(i);
            //공정 상태
            String state = inBoundMap.get("process").get(i);
            //품번, 공정 상태에 따라 components테이블에 있는 모델명 출력
            modelNameList.add(inBoundService.selectModelName(component, state));
            //사용자가 입력한 품번, 공정 상태 비교해서 품번 출력
            componentNoList.add(inBoundService.selectComponentNo(component, state));

            palletNoList.add(String.valueOf((palletNo + i)));


            if (state.equals("0")) {
                state = "완제품";
            } else if (state.equals("1")) {
                state = "소재";
            } else if (state.equals("2")) {
                state = "CNC 1차";
            } else if (state.equals("3")) {
                state = "CNC 2차";
            } else if (state.equals("4")) {
                state = "MCT 1차";
            }
            processList.add(state);
        }
        map.put("modelName", modelNameList);
        map.put("componentNo", componentNoList);
        map.put("process", processList);
        map.put("inBoundAmount", inBoundMap.get("inBoundAmount"));
        map.put("palletNo", palletNoList);

        /*=========================================================================*/
        //Map<Integer, Integer> rackLocation =
        Map<String, List<String>> rackLocation = inBoundService.checkRackLocation(map);
        map.put("rackLocation", rackLocation.get("rackLocation"));
        map.put("failReason", rackLocation.get("failReason"));

        /*=========================================================================*/

        return map;
    }


    //엑셀 일괄 등록
    @PostMapping("/excelInboundUpload")
    @ResponseBody
    public Map<String, List<String>> excelUpload(@RequestParam("excelFile") MultipartFile file) {
        String uploadPath = System.getProperty("user.dir");

        inBoundService.deleteTemp();

        File destFile = new File(uploadPath + File.separator + "excel" + File.separator + file.getOriginalFilename());

        try {
            if (!destFile.exists()) {
                destFile.mkdirs();
            }
            file.transferTo(destFile);
        } catch (IOException e) {

        }

        ExcelReadOption excelReadOption = new ExcelReadOption();
        excelReadOption.setFilePath(destFile.getAbsolutePath());
        excelReadOption.setOutputColumns("A", "B", "C");
        int startRow = 2;
        excelReadOption.setStartRow(startRow);

        List<Map<String, String>> excelContent = uploadService.excelRead(excelReadOption);

        Map<String, List<String>> inBoundList = new HashMap<>();

        List<String> componentList = new ArrayList<>();
        List<String> processList = new ArrayList<>();
        List<String> inBoundAmount = new ArrayList<>();
        List<String> palletNoList = new ArrayList<>();

        long palletNo = inBoundService.selectTempPalletNo();

        for (int i = 0; i < excelContent.size(); i++) {
            componentList.add(excelContent.get(i).get("A"));
            processList.add(excelContent.get(i).get("B"));
            inBoundAmount.add(excelContent.get(i).get("C"));
            palletNoList.add(String.valueOf((palletNo + i)));
        }
        inBoundList.put("componentList", componentList);
        inBoundList.put("process", processList);
        inBoundList.put("inBoundAmount", inBoundAmount);
        inBoundList.put("palletNo", palletNoList);

        return inBoundList;

    }

    //입고 처리
    @PostMapping("/insertInBound")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void insertInBound(@RequestParam("inBoundList") long[] inBoundList) {

        String inBoundNo;

        for (int i = 0; i < inBoundList.length; i++) {

            long palletNo = inBoundService.selectPalletNo();
            inBoundNo = "I" + palletNo;
            long tempPalletNo = inBoundList[i];

            inBoundService.insertInBound(inBoundNo, palletNo, tempPalletNo);
        }

        inBoundService.deleteTemp();

    }

    //삭제 처리
    @PostMapping("/deleteInBound")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void deleteInBound(@RequestBody Map<String, List<String>> tempPalletNoMap) {

        List<String> palletNoList = tempPalletNoMap.get("tempPalletNo");

        for (String palletNo : palletNoList) {
            inBoundService.deleteComponentNo(Long.parseLong(palletNo));
        }

        if (inBoundService.selectInbound().size() != 0) {
            inBoundService.selectTempList();
        }
    }

    @PostMapping("/reSelect")
    @ResponseBody
    public List<Temp> reSelectLocation(){

        List<Temp> arrayList = inBoundService.selectInbound();

        return arrayList;
    }

    @PostMapping("/searchNumNow")
    @ResponseBody
    public List<String> searchList(@RequestBody String searchNum) {
        List<String> componentList = inBoundService.findSelctComponentList(searchNum);

        return componentList;
    }

}