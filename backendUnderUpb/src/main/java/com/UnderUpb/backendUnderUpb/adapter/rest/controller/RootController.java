package com.UnderUpb.backendUnderUpb.adapter.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
}
