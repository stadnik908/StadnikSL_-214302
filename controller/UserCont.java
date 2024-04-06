package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserCont extends Main {

    @GetMapping
    public String users(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("roles", Role.values());
        model.addAttribute("departments", departmentRepo.findAll());
        return "users";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, @RequestParam Role role, @RequestParam Long departmentId) {
        if (departmentId == 0) return "redirect:/users";
        AppUser user = userRepo.getReferenceById(id);
        user.setRole(role);
        user.setDepartment(departmentRepo.getReferenceById(departmentId));
        userRepo.save(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return "redirect:/users";
    }

    @GetMapping("/{id}/enable")
    public String enableUser(@PathVariable Long id) {
        AppUser user = userRepo.getReferenceById(id);
        user.setEnabled(true);
        userRepo.save(user);
        return "redirect:/users";
    }
}