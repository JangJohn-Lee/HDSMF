package com.hdsmf.controller;

import com.hdsmf.dto.CategoryDto;
import com.hdsmf.dto.RackDetailInfoDto;
import com.hdsmf.entity.RackDetailInfo;
import com.hdsmf.repository.CategoryRepository;
import com.hdsmf.repository.RackInfoRepository;
import com.hdsmf.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hdsmf")
public class RackInfoController {

    private final CategoryService categoryService;
    private final RackDetailInfoService rackDetailInfoService;
    private final IOBoundService iOBoundService;
    private final RackInfoService rackInfoService;
    private final ComponentsService componentsService;

    public RackInfoController(CategoryService categoryService, RackDetailInfoService rackDetailInfoService, IOBoundService iOBoundService, RackInfoService rackInfoService, ComponentsService componentsService){
        this.categoryService = categoryService;
        this.rackDetailInfoService = rackDetailInfoService;
        this.iOBoundService = iOBoundService;
        this.rackInfoService = rackInfoService;
        this.componentsService = componentsService;
    }


    @GetMapping("/rackinfo")
    public String rackinfo(Model model){
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("rackDetailInfoList", rackDetailInfoService.getRackDetailInfoList());
        model.addAttribute("iOBoundList", iOBoundService.getIOBoundList());
        model.addAttribute("rackInfoList", rackInfoService.getRackInfoList());
        model.addAttribute("componentsList", componentsService.getComponentsList());

        return "pages/rackinfo/rackInfo";
    }



    @PutMapping("/rackinfo/updateCategories")
    @ResponseBody
    public ResponseEntity<?> updateCategories(@RequestBody List<CategoryDto> categoryDtos) {
        try {
            categoryService.updateCategories(categoryDtos);
            return ResponseEntity.ok("카테고리가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("카테고리 업데이트 중 오류 발생: " + e.getMessage());
        }
    }


    @PutMapping("/rackinfo/updateRackDetailInfo")
    @ResponseBody
    public ResponseEntity<?> updateRackDetailInfos(@RequestBody List<RackDetailInfoDto> rackDetailInfoDtos) {
        try {
            rackDetailInfoService.updateRackDetailInfos(rackDetailInfoDtos);
            return ResponseEntity.ok("RackDetailInfo가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("RackDetailInfo 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
}
