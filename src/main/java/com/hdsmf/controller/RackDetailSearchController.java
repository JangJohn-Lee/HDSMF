package com.hdsmf.controller;

import com.hdsmf.service.RackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hdsmf")
@RequiredArgsConstructor
public class RackDetailSearchController {

    private final RackService rackService;

    @GetMapping("/rackdetailsearch")
    public String rackdetailsearch(){
        return "pages/rackdetailsearch/rackDetailSearch";
    }

    @GetMapping(value = "/rackdetailsearch/sendData")
    public String sendData(@RequestParam("data") long data, Model model) {
        model.addAttribute("RackDetailSearchDtos", rackService.findRackNo(data));
        model.addAttribute("NonNullInboundDateCount", rackService.countRemain(data));
        model.addAttribute("barColor", rackService.viewColor(data));
        model.addAttribute("RackNo", data);
        model.addAttribute("RackRow", rackService.selectRowNo(data));
        model.addAttribute("RackCol", rackService.selectColNo(data));
        model.addAttribute("CategoryName", rackService.findDistinctCategoryNamesByRackNo(data));
        return "pages/rackdetailsearch/tableCard";
    }
}
