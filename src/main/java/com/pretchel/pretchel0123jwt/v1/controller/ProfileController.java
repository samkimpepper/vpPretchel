package com.pretchel.pretchel0123jwt.v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile/save")
    public String save() {
        return "profile/profile-save";
    }
}
