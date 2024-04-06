package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.Passing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/passings")
public class PassingCont extends Main {
    @GetMapping
    public String passings(Model model) {
        getCurrentUserAndRole(model);
        return "passings";
    }

    @GetMapping("/{passingId}")
    public String passing(Model model, @PathVariable Long passingId) {
        Passing passing = passingRepo.getReferenceById(passingId);
        if (passing.isStatus()) return "redirect:/passings";
        getCurrentUserAndRole(model);
        model.addAttribute("passing", passing);
        return "passing";
    }

    @PostMapping("/{passingId}/finish")
    public String finishPassing(@PathVariable Long passingId, @RequestParam int[] answer) {
        Passing passing = passingRepo.getReferenceById(passingId);
        passing.setScore(Arrays.stream(answer).sum());
        passing.setStatus(true);
        passingRepo.save(passing);
        AppUser user = getUser();
        user.setCertification(false);
        user.setCertificationDate(getDateNowYearMonthDay());
        user.setCertificationScore(passing.getScore());
        user.setCertificationScoreMax(passing.getTest().getMaxScore());
        userRepo.save(user);
        return "redirect:/passings";
    }
}
