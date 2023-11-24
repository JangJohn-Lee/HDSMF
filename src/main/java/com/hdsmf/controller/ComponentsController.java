package com.hdsmf.controller;

import com.hdsmf.entity.Components;
import com.hdsmf.service.ComponentsService;
import com.hdsmf.upload.ExcelReadOption;
import com.hdsmf.upload.Upload2ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Transactional
@RequestMapping("hdsmf")
public class ComponentsController {

    private final ComponentsService componentsService;

    private final Upload2ServiceImpl uploadService;

    // 전체 컴포넌트 조회
    @GetMapping("/components")
    public String getAllComponents(Model model) {
        List<Components> componentsList = componentsService.getAllComponents();
        model.addAttribute("componentsList", componentsList);
        return "pages/components/components";
    }

    //품번 or 품명 검색
    @PostMapping("/components/")
    public String searchComponents(@RequestParam(required = false) String searchField
            , @RequestParam(required = false) String searchValue
            , Model model) {

        List<Components> searchedComponents = componentsService.searchComponentsByField(searchField, searchValue);
        model.addAttribute("componentsList", searchedComponents);

        return "pages/components/components"; // 검색 결과 페이지로 이동
    }

    //등록
    @PostMapping("/components/register")
    public String registerComponent(@ModelAttribute Components components, Model model) {

        // 카테고리명과 완제품명을 대문자로 변환
        String categoryName = components.getCategoryName().toUpperCase();
        String product = components.getProduct().toUpperCase();

        components.setCategoryName(categoryName);
        components.setProduct(product);

        componentsService.saveComponent(components);

        List<Components> componentsList = componentsService.getAllComponents();
        model.addAttribute("componentsList", componentsList);

        /*return "pages/components/components";*/
        return "redirect:/hdsmf/components";
    }

    //수정 시 조회
    @PostMapping("/components/edit")
    @ResponseBody
    public Components editComponent(@RequestBody Map<String, String> requestData, Model model) {
        String componentNo = requestData.get("componentNo");

        // 품번에 따른 조회(품번 입력)
        Components component = componentsService.findAllByComponentNo(String.valueOf(componentNo));

        return component;

    }

    //수정
    @PostMapping("/components/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateComponent(@RequestBody Map<String, String> data, Model model) {

        String componentNo = data.get("componentNo");
        String categoryName = data.get("categoryName").toUpperCase();
        String modelData = data.get("model");
        String componentState = data.get("componentState");
        Long plantNo = Long.valueOf(data.get("plantNo"));
        String product = data.get("product").toUpperCase();
        String sNo = data.get("sno");
        Double sWeight = data.get("sWeight").isEmpty() ? 0.0 : Double.valueOf(data.get("sWeight"));
        Double fWeight = data.get("fWeight").isEmpty() ? 0.0 : Double.valueOf(data.get("fWeight"));

//        String componentState = "";

//        //componentNo에서 componentState 추출
//        if (componentNo.length() > 9) {
//            System.out.println(componentNo.length() + "자릿수");
//            componentState = componentNo.substring(componentNo.length() - 1);
//        } else {
//            componentState = "0";
//        }
        Components componentinfo = new Components();

        componentinfo.setComponentNo(componentNo);
        componentinfo.setCategoryName(categoryName);
        componentinfo.setModel(modelData);
        componentinfo.setComponentState(componentState);
        componentinfo.setPlantNo(plantNo);
        componentinfo.setProduct(product);
        componentinfo.setSNo(sNo);
        componentinfo.setSWeight(sWeight);
        componentinfo.setFWeight(fWeight);

        //수정
        componentsService.updateComponent(componentinfo);

        List<Components> AllList = componentsService.getAllComponents();
        model.addAttribute("componentsList", AllList);

//        return "pages/components/components";
    }

    //삭제
    @PostMapping("/components/deleteComponents")
    @ResponseBody
    public ResponseEntity<String> deleteComponent(@RequestBody Map<String, List<String>> mapComponentNo) {

        List<String> componentNoList = mapComponentNo.get("componentNo");

        for (String componentNo : componentNoList) {
            Integer checkNum = componentsService.selectIoBoundComponentNoCheck(componentNo);

            if(checkNum > 0){ //중복됨
                return ResponseEntity.badRequest().body("이미 사용중인 컴포넌트 번호입니다.");

            }else{ //중복되어있지 x
                componentsService.deleteComponent(componentNo);
            }
        }

        return ResponseEntity.ok("등록이 완료되었습니다.");
    }

    //삭제 - 덮어씌움
    @PostMapping("/components/deleteComponentsCheck")
    @ResponseBody
    public String deleteComponentsCheck(@RequestBody Map<String, List<String>> mapComponentNo) {
        List<String> componentNoList = mapComponentNo.get("componentNo");
        for (String componentNo : componentNoList) {
            componentsService.deleteComponent(componentNo);

        }

        return "pages/components/components";
    }

    //일괄 등록 처리
    @PostMapping("/components/uploadSave")
    @ResponseBody
    public ResponseEntity<String> excelUpload(@RequestParam("file") MultipartFile file) {

        String uploadPath = System.getProperty("user.dir");

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
        excelReadOption.setOutputColumns("A,B,C,D,E,F,G,H,I");
        int startRow = 2;
        excelReadOption.setStartRow(startRow);

        List<Map<String, String>> excelContent = uploadService.excelRead(excelReadOption);
        List<Components> savedComponents = new ArrayList<>();

        for (int i = 0; i < excelContent.size(); i++) {
            Map<String, String> excelRow = excelContent.get(i);
            Components components = new Components();

            double value = 0.0;
            DecimalFormat decimalFormat = new DecimalFormat();
            String formattedValue = "";

            String componentNoScientific = excelRow.get("A");
            if(componentNoScientific.contains("-")) { //이미 문자열 형태

            }else{ //지수형태
                value = Double.parseDouble(componentNoScientific);
                decimalFormat = new DecimalFormat("0.######"); // 지수 형식을 일반 형식으로 변환
                componentNoScientific = decimalFormat.format(value);
            }
            Components componentCheck = componentsService.findAllByComponentNo(componentNoScientific);

            if (componentCheck == null) { //null - componentNo이 중복되지 않음

//                components.setComponentNo(excelRow.get("A"));
                components.setComponentNo(componentNoScientific);
                components.setCategoryName(excelRow.get("B").toUpperCase());
                if(!excelRow.get("C").isEmpty()){
                    components.setFWeight(Double.parseDouble(excelRow.get("C")));
                }else if(excelRow.get("C").isEmpty()) {
                    components.setFWeight(Double.parseDouble("0.0"));
                }

                if(excelRow.get("D") != null){
                    components.setModel(excelRow.get("D"));
                }else if(excelRow.get("D") == null) {
                    components.setModel("");
                }

                if(excelRow.get("E") != null){
                    components.setComponentState(excelRow.get("E").split("\\.")[0]);
                }else if(excelRow.get("E") == null) {
                    components.setComponentState("");
                }

                components.setPlantNo(Long.valueOf(excelRow.get("F").split("\\.")[0])); // 문자열 파싱

                if(excelRow.get("G") != null){
                    components.setProduct(excelRow.get("G").toUpperCase());
                }else if(excelRow.get("G") == null) {
                    components.setProduct("");
                }

                if(excelRow.get("H") != null){
                    components.setSNo(excelRow.get("H"));
                }else if(excelRow.get("H") == null) {
                    components.setSNo("");
                }

                if(excelRow.get("I") != null){
                    components.setSWeight(Double.parseDouble(excelRow.get("I")));
                }else if(excelRow.get("I") == null) {
                    components.setFWeight(Double.parseDouble("0.0"));
                }


                Components saved = componentsService.saveComponent(components);
                savedComponents.add(saved);


            } else if (componentCheck != null) { //null이 아님 = componentNo이 중복됨 // 중복된 애 빼고 진행
                String componentVaild = componentCheck.getComponentNo();

                return ResponseEntity.badRequest().body(componentVaild + "중복된 컴포넌트 번호가 존재합니다.");

            }
        }

        return ResponseEntity.ok("등록이 완료되었습니다.");
    }

    //일괄 등록 처리
    @PostMapping("/components/uploadSaveCheck")
    @ResponseBody
    public List<Components> excelUploadCheck(@RequestParam("file") MultipartFile file){

        String uploadPath = System.getProperty("user.dir");

        File destFile = new File(uploadPath + File.separator + "excel" + File.separator + file.getOriginalFilename());

        try{
            if(!destFile.exists()){
                destFile.mkdirs();
            }file.transferTo(destFile);
        } catch (IOException e) {

        }

        ExcelReadOption excelReadOption = new ExcelReadOption();
        excelReadOption.setFilePath(destFile.getAbsolutePath());
        excelReadOption.setOutputColumns("A,B,C,D,E,F,G,H,I");
        int startRow = 2;
        excelReadOption.setStartRow(startRow);

        List<Map<String, String>> excelContent = uploadService.excelRead(excelReadOption);

        List<Components> savedComponents = new ArrayList<>();

        for (int i = 0; i < excelContent.size(); i++) {

            double value = 0.0;
            DecimalFormat decimalFormat = new DecimalFormat();
            String componentNoScientific = "";

            Map<String, String> excelRow = excelContent.get(i);

            Components components = new Components();
            componentNoScientific = excelRow.get("A");

            if(componentNoScientific.contains("-")) { //이미 문자열 형태

            }else{ //지수형태

                value = Double.parseDouble(componentNoScientific);
                decimalFormat = new DecimalFormat("0.######"); // 지수 형식을 일반 형식으로 변환
                componentNoScientific = decimalFormat.format(value);
            }

            components.setComponentNo(componentNoScientific);
            components.setCategoryName(excelRow.get("B").toUpperCase());
            if(!excelRow.get("C").isEmpty()){
                components.setFWeight(Double.parseDouble(excelRow.get("C")));
            }else if(excelRow.get("C").isEmpty()) {
                components.setFWeight(Double.parseDouble("0.0"));
            }
            if(excelRow.get("D") != null){
                components.setModel(excelRow.get("D"));
            }else if(excelRow.get("D") == null) {
                components.setModel("");
            }

            if(excelRow.get("E") != null){
                components.setComponentState(excelRow.get("E").split("\\.")[0]);
            }else if(excelRow.get("E") == null) {
                components.setComponentState("");
            }

            components.setPlantNo(Long.valueOf(excelRow.get("F").split("\\.")[0])); // 문자열 파싱

            if(excelRow.get("G") != null){
                components.setProduct(excelRow.get("G").toUpperCase());
            }else if(excelRow.get("G") == null) {
                components.setProduct("");
            }

            if(excelRow.get("H") != null){
                components.setSNo(excelRow.get("H"));
            }else if(excelRow.get("H") == null) {
                components.setSNo("");
            }

            if(excelRow.get("I") != null){
                components.setSWeight(Double.parseDouble(excelRow.get("I")));
            }else if(excelRow.get("I") == null) {
                components.setFWeight(Double.parseDouble("0.0"));
            }

            //component table insert
            Components saved = componentsService.saveComponent(components);
            savedComponents.add(saved);
        }

        return savedComponents;
    }

    @PostMapping("/componentNumNow")
    @ResponseBody
    public Integer searchList(@RequestBody String keyword) {
        Integer componentList = componentsService.selectComponentNoCheck(keyword);

        return componentList;
    }
}