package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.Department;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departments")
public class DepartmentCont extends Main {
    @GetMapping
    public String departments(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("departments", departmentRepo.findAll());
        return "departments";
    }

    @PostMapping
    public String addDepartment(@RequestParam String name) {
        departmentRepo.save(new Department(name));
        return "redirect:/departments";
    }

    @PostMapping("/{id}/edit")
    public String editDepartment(@PathVariable Long id, @RequestParam String name) {
        Department department = departmentRepo.getReferenceById(id);
        department.setName(name);
        departmentRepo.save(department);
        return "redirect:/departments";
    }

    @GetMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id) {
        Department department = departmentRepo.getReferenceById(id);
        for (AppUser i : department.getUsers()) {
            i.setDepartment(null);
        }
        departmentRepo.deleteById(id);
        return "redirect:/departments";
    }
}
