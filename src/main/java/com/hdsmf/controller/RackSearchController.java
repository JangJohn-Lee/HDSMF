package com.hdsmf.controller;

import com.hdsmf.dto.IOBounds;
import com.hdsmf.service.InBoundService;
import com.hdsmf.upload.ExcelReadOption;
import com.hdsmf.upload.Upload1ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Transactional
@RequestMapping("/hdsmf")
public class RackSearchController {

    @Autowired
    private InBoundService inBoundService;

    @Autowired
    private Upload1ServiceImpl uploadService;

    @GetMapping("/racksearch")
    public String searchAll(Model model) {
        List<IOBounds> List = inBoundService.selectAllRackList();
        model.addAttribute("list",List);

        return "pages/racksearch/rackSearch";
    }

    @PostMapping("/racksearch")
    public String search(Model model
            , @RequestParam(required = true) String componentNo
            , @RequestParam(required = false) Long rackNo
            , @RequestParam(required = false, defaultValue = "") String searchName){

        //입력값 처리
        if (rackNo == null) {
            // rackNo 공란일 경우 처리
            rackNo = 0L;
        }

        List<IOBounds> List = inBoundService.selectRackList((String)componentNo, rackNo, searchName);

        model.addAttribute("list",List);

        return "pages/racksearch/rackSearch";
    }

    //componentNo 유효성 체크
    @PostMapping("/componentCheck")
    @ResponseBody
    public Map<String,Long> componentNoCheck(@RequestBody String componentNo){

        Map<String,Long> componentNoCheck = new HashMap<>();

        Long cnt = inBoundService.countByComponentNo(componentNo);

        componentNoCheck.put("componentNo", cnt);

        return componentNoCheck;
    }


    //일괄 검색 처리
    @PostMapping("/upload")
    @ResponseBody
    public HashMap<String, List<IOBounds>> excelUpload(@RequestParam("file") MultipartFile file, Model model){

        HashMap<String, List<IOBounds>> json = new HashMap<>();

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
        excelReadOption.setOutputColumns("A");
        int startRow = 2;
        excelReadOption.setStartRow(startRow);

        List<Map<String, String>> excelContent = uploadService.excelRead(excelReadOption);

        List<String> resultList = new ArrayList<>();

        List<IOBounds> exList = new ArrayList<>();

        for (int i = 0; i < excelContent.size(); i++) {
            String productNumber = excelContent.get(i).get("A");
            if (productNumber != null && !productNumber.isEmpty()) {
                resultList.add(productNumber);
            }
        }

        for(int i=0; i<resultList.size(); i++) {

            exList = inBoundService.searchExcelList(resultList.get(i));

        }

        model.addAttribute("list",exList);

        json.put("list", exList);

        return json;
    }
}