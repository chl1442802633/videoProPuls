package com.cui.controller;

import com.cui.pojo.Admin;
import com.cui.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    @RequestMapping("showLogin")
    public String showLogin() {
        return "/behind/login";
    }

    @RequestMapping("/login")
    @ResponseBody
    public String login(Admin admin) {
        int login = adminService.login(admin);
        return login > 0 ? "success" : "fail";

    }
}
