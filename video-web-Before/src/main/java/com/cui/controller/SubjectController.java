package com.cui.controller;

import com.cui.pojo.Subject;
import com.cui.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subject")
public class SubjectController {
    @Autowired
    SubjectService subjectService;
    @RequestMapping("/list")
    public String selectSubjectById(Integer subjectId, Model model) {
        Subject subject = subjectService.selectSubjectById(subjectId);
        model.addAttribute("subject",subject);
        return "/before/course";
    }
}
