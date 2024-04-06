package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.Passing;
import com.certification.model.Test;
import com.certification.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/materials/{materialId}/tests")
public class TestCont extends Main {

    @PostMapping("/add")
    public String addTest(@PathVariable Long materialId, @RequestParam String name) {
        Test test = testRepo.save(new Test(name, materialRepo.getReferenceById(materialId)));
        return "redirect:/materials/{materialId}/tests/" + test.getId();
    }

    @GetMapping("/{testId}")
    public String test(Model model, @PathVariable Long materialId, @PathVariable Long testId) {
        getCurrentUserAndRole(model);
        model.addAttribute("test", testRepo.getReferenceById(testId));
        if (getUser().getRole() == Role.USER) {
            if (passingRepo.findByOwner_IdAndTest_Id(getUser().getId(), testId) != null) {
                model.addAttribute("passing", "true");
            }
        }
        return "test";
    }

    @GetMapping("/{testId}/delete")
    public String deleteTest(@PathVariable Long materialId, @PathVariable Long testId) {
        testRepo.deleteById(testId);
        return "redirect:/materials/{materialId}";
    }

    @PostMapping("/{testId}/edit")
    public String deleteTest(@PathVariable Long materialId, @PathVariable Long testId, @RequestParam String name) {
        Test test = testRepo.getReferenceById(testId);
        test.setName(name);
        testRepo.save(test);
        return "redirect:/materials/{materialId}/tests/{testId}";
    }

    @GetMapping("/{testId}/status")
    public String statusTest(@PathVariable Long materialId, @PathVariable Long testId) {
        Test test = testRepo.getReferenceById(testId);
        test.setStatus(!test.isStatus());
        testRepo.save(test);
        return "redirect:/materials/{materialId}/tests/{testId}";
    }

    @GetMapping("/{testId}/passing")
    public String passingTest(@PathVariable Long materialId, @PathVariable Long testId) {
        Passing passing = passingRepo.save(new Passing(testRepo.getReferenceById(testId), getUser()));
        return "redirect:/passings/" + passing.getId();
    }
}
