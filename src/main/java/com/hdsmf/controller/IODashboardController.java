package com.hdsmf.controller;

import com.hdsmf.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class IODashboardController {
    private final CategoryService categoryService;
    private final RackDetailInfoService rackDetailInfoService;
    private final IOBoundService iOBoundService;
    private final RackInfoService rackInfoService;
    private final ComponentsService componentsService;

    public IODashboardController(CategoryService categoryService, RackDetailInfoService rackDetailInfoService, IOBoundService iOBoundService, RackInfoService rackInfoService, ComponentsService componentsService){
        this.categoryService = categoryService;
        this.rackDetailInfoService = rackDetailInfoService;
        this.iOBoundService = iOBoundService;
        this.rackInfoService = rackInfoService;
        this.componentsService = componentsService;
    }


    @GetMapping("/hdsmf/iodashboard")
    public String iodashboard(Model model){
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("rackDetailInfoList", rackDetailInfoService.getRackDetailInfoList());
        model.addAttribute("iOBoundList", iOBoundService.getIOBoundList());
        model.addAttribute("rackInfoList", rackInfoService.getRackInfoList());
        model.addAttribute("componentsList", componentsService.getComponentsList());

        return "pages/iodashboard/iodashboard";
    }

}
