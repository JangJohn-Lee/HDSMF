package com.hdsmf.controller;

import com.hdsmf.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/")
public class EasySearchController {
    private final CategoryService categoryService;
    private final RackDetailInfoService rackDetailInfoService;
    private final IOBoundService iOBoundService;
    private final RackInfoService rackInfoService;
    private final ComponentsService componentsService;

    private final RackService rackService;

    public EasySearchController(CategoryService categoryService, RackDetailInfoService rackDetailInfoService, IOBoundService iOBoundService, RackInfoService rackInfoService, ComponentsService componentsService, RackService rackService){
        this.categoryService = categoryService;
        this.rackDetailInfoService = rackDetailInfoService;
        this.iOBoundService = iOBoundService;
        this.rackInfoService = rackInfoService;
        this.componentsService = componentsService;
        this.rackService = rackService;
    }


    @GetMapping("/hdsmf/easysearch")
    public String rackInfo(Model model){
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("rackDetailInfoList", rackDetailInfoService.getRackDetailInfoList());
        model.addAttribute("iOBoundList", iOBoundService.getIOBoundList());
        model.addAttribute("rackInfoList", rackInfoService.getRackInfoList());
        model.addAttribute("componentsList", componentsService.getComponentsList());

        return "pages/easysearch/easysearch";
    }

    @PostMapping("/hdsmf/easysearch/move")
    public ResponseEntity<String> handleDataTransfer(@RequestParam String moveCellValue, @RequestParam String targetCellValue){
        String toMoveRackId = rackService.parseCellValue(moveCellValue);
        String targetRackId = rackService.parseCellValue(targetCellValue);
        String message = "";
        try{
            message = rackService.moveToRack(toMoveRackId, targetRackId);
        }catch (EntityNotFoundException e1) {
            message = e1.getMessage();
        }catch (RuntimeException e2) {
            message = e2.getMessage();
        }

        // 작업이 완료되면 클라이언트에 응답을 반환
        return new ResponseEntity<String>(message, HttpStatus.OK);
    }
}
