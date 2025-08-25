package com.wlu.examscheduler.controller;

import com.wlu.examscheduler.exammodel.Exam;
import com.wlu.examscheduler.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private ExamService examService;

    @GetMapping("/exams")
    public List<Exam> all() {
        return examService.getAllExams();
    }

    @GetMapping("/search")
    public List<Exam> search(@RequestParam String courseCode) {
        return examService.searchByCourseCode(courseCode);
    }
}
