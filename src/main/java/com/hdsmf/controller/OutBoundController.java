package com.hdsmf.controller;

import com.hdsmf.service.OutBoundService;
import com.hdsmf.service.RackService;
import com.hdsmf.upload.ExcelReadOption;
import com.hdsmf.upload.UploadServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/hdsmf")
public class OutBoundController {

    private OutBoundService outService;
    private RackService rackService;
    private UploadServiceImpl uploadService;
    public OutBoundController(OutBoundService outService, RackService rackService, UploadServiceImpl uploadService) {
        this.outService = outService;
        this.rackService = rackService;
        this.uploadService = uploadService;

    }


    //출고 관리 페이지 이동 시 Rack 테이블 생성
    @GetMapping("/outbound")
    public String outBoundRackInfo(Model model){

        long rackNum = rackService.selectRackNo();
        ArrayList<Integer> colArr = new ArrayList<>();
        ArrayList<Integer> rowArr = new ArrayList<>();

        for(long i = 0; i < rackNum; i++){
            long x = (i+5)%10+1;
            int colNum = rackService.selectColNo(x);
            int rowNum = rackService.selectRowNo(x);
            colArr.add(colNum);
            rowArr.add(rowNum);
        }
        model.addAttribute("rackNo", rackNum);
        model.addAttribute("colArr", colArr);
        model.addAttribute("rowArr", rowArr);

        return "pages/outbound/outbound";
    }

    //출고 검색 처리
    @PostMapping("/search")
    @ResponseBody
    public Map<String, List> selectIOBoundList(@RequestParam("partNoList") String[] partNoList){

        List<String> list = new ArrayList<>();
        for(int i = 0; i < partNoList.length; i++){
            list.add(partNoList[i]);
        }

        return outService.searchIOBoundList(list);
    }

    //일괄 검색 처리
    @PostMapping("/excelUpload")
    @ResponseBody
    public List<String> excelUpload(@RequestParam("excelFile") MultipartFile file){

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

        for(int i = 0; i< excelContent.size(); i++){
            resultList.add(excelContent.get(i).get("A"));
        }
        return resultList;
    }

    //출고 처리
    @PostMapping("/outBound")
    @ResponseStatus(value= HttpStatus.OK)
    @ResponseBody
    public void deleteRack(@RequestParam("locationList") long[] locationList){

        for(int i = 0; i < locationList.length; i++){
            long rackNo = locationList[i];
            outService.deleteRack(rackNo);
        }
    }

    @PostMapping("/selectRackId")
    @ResponseBody
    public List<String> selectRackId(@RequestParam("outBoundList") long[] outBoundList){

        List<String> rackId = new ArrayList<>();

        for(int i = 0; i < outBoundList.length; i++){
            String rackLocation = outService.selectRackLocation(outBoundList[i]);
            rackId.add(rackLocation);
        }
        return rackId;
    }

    @PostMapping("/searchNumNowOut")
    @ResponseBody
    public List<String> searchList(@RequestBody String searchNum) {
        //AJAX 통한 실시간 품번 검색
        List<String> componentList = outService.OutComponentList(searchNum);

        return componentList;
    }
}
