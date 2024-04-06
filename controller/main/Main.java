package com.certification.controller.main;

import com.certification.model.AppUser;
import com.certification.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

public class Main {
    @Autowired
    protected UserRepo userRepo;
    @Autowired
    protected MaterialRepo materialRepo;
    @Autowired
    protected TestRepo testRepo;
    @Autowired
    protected QuestionRepo questionRepo;
    @Autowired
    protected AnswerRepo answerRepo;
    @Autowired
    protected PassingRepo passingRepo;
    @Autowired
    protected DepartmentRepo departmentRepo;
    @Autowired
    protected ArticleRepo articleRepo;

    @Value("${upload.img}")
    protected String uploadImg;

    protected void getCurrentUserAndRole(Model model) {
        model.addAttribute("role", getRole());
        model.addAttribute("user", getUser());
    }

    protected AppUser getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((!(auth instanceof AnonymousAuthenticationToken)) && auth != null) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            return userRepo.findByUsername(userDetail.getUsername());
        }
        return null;
    }

    protected String getRole() {
        AppUser appUser = getUser();
        if (appUser == null) return "NOT";
        return appUser.getRole().name();
    }

    protected String getDateNowYearMonthDay() {
        return LocalDateTime.now().toString().substring(0, 10);
    }

    protected String getDateNowYearMonth() {
        return LocalDateTime.now().toString().substring(0, 7);
    }

    public static float round(float value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (float) tmp / factor;
    }
}
