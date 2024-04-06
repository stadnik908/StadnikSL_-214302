package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reg")
public class RegCont extends Main {
    @GetMapping
    public String reg(Model model) {
        getCurrentUserAndRole(model);
        return "reg";
    }

    @PostMapping
    public String reg(Model model, @RequestParam String username, @RequestParam String password, @RequestParam String fio) {
        if (userRepo.findByUsername(username) != null) {
            getCurrentUserAndRole(model);
            model.addAttribute("message", "Пользователь с таким логином уже существует!");
            return "reg";
        }

        AppUser user = new AppUser(username, password, fio);

        if (userRepo.findAll().isEmpty()) {
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
        }

        userRepo.save(user);

        return "redirect:/login";
    }
}
