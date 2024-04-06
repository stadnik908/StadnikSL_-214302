package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.Department;
import com.certification.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/certification")
public class CertificationCont extends Main {

    @GetMapping
    public String certification(Model model) {
        getCurrentUserAndRole(model);

        List<AppUser> users = userRepo.findAllByRole(Role.USER);

        model.addAttribute("users", users);

        model.addAttribute("countTrue", users.stream().reduce(0, (i, user) -> {
            if (user.isCertification()) return i + 1;
            return i;
        }, Integer::sum));
        model.addAttribute("countFalse", users.stream().reduce(0, (i, user) -> {
            if (user.isCertification()) return i;
            return i + 1;
        }, Integer::sum));

        List<Department> departments = departmentRepo.findAll();

        String[] departmentString = new String[departments.size()];
        float[] departmentFloat = new float[departments.size()];

        for (int i = 0; i < departments.size(); i++) {
            departmentString[i] = departments.get(i).getName();
            List<AppUser> temp = departments.get(i).getUsers();
            departmentFloat[i] = Main.round(temp.stream().reduce(0f, (aFloat, user) -> aFloat + user.getCertificationPercent(), Float::sum) / temp.size());
        }

        model.addAttribute("departmentString", departmentString);
        model.addAttribute("departmentFloat", departmentFloat);

        return "certification";
    }

    @GetMapping("/pdf")
    public String certificationPdf(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("users", userRepo.findAllByRole(Role.USER));
        return "certificationPdf";
    }

    @GetMapping("/{id}/certification")
    public String certificationUser(@PathVariable Long id) {
        AppUser user = userRepo.getReferenceById(id);
        user.setCertification(true);
        userRepo.save(user);
        return "redirect:/certification";
    }
}