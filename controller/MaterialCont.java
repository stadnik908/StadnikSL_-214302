package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.Material;
import com.certification.model.Test;
import com.certification.model.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/materials")
public class MaterialCont extends Main {
    @GetMapping
    public String materials(Model model) {
        getCurrentUserAndRole(model);
        AppUser user = getUser();
        List<Material> materials = new ArrayList<>();
        if (user.getRole() == Role.ADMIN) {
            materials = materialRepo.findAll();
        } else {
            if (user.getDepartment() != null) {
                materials = user.getDepartment().getMaterials();
            }
        }

        model.addAttribute("materials", materials);
        return "materials";
    }

    @GetMapping("/search")
    public String searchMaterials(Model model, @RequestParam String name) {
        getCurrentUserAndRole(model);
        model.addAttribute("name", name);

        AppUser user = getUser();
        List<Material> materials = new ArrayList<>();
        if (user.getRole() == Role.ADMIN) {
            materials = materialRepo.findAllByNameContaining(name);
        } else {
            if (user.getDepartment() != null) {
                materials = materialRepo.findAllByNameContainingAndDepartment_Id(name, user.getDepartment().getId());
            }
        }

        model.addAttribute("materials", materials);

        return "materials";
    }

    @GetMapping("/{id}")
    public String material(Model model, @PathVariable Long id) {
        getCurrentUserAndRole(model);
        Material material = materialRepo.getReferenceById(id);
        model.addAttribute("material", material);

        if (getUser().getRole() == Role.USER) {
            List<Test> tests = material.getTests();

            if (tests.isEmpty()) {
                model.addAttribute("passing", 0);
            }

            AppUser user = getUser();

            int count = 0;

            for (Test i : tests) {
                if (passingRepo.findByOwner_IdAndTest_IdAndStatus(user.getId(), i.getId(), true) != null) {
                    count++;
                }
            }

            if (count == 0) {
                model.addAttribute("passing", 0);
            }

            int size = tests.stream().reduce(0, (i, test) -> {
                if (test.isStatus()) {
                    return i + 1;
                } else return i;
            }, Integer::sum);

            float res = (float) count / size * 100;
            res = round(res);

            model.addAttribute("passing", res);
        }

        return "material";
    }

    @GetMapping("/{id}/delete")
    public String deleteMaterial(@PathVariable Long id) {
        materialRepo.deleteById(id);
        return "redirect:/materials";
    }

    @GetMapping("/add")
    public String addMaterial(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("departments", departmentRepo.findAll());
        return "material_add";
    }

    @PostMapping("/add")
    public String addMaterial(Model model, @RequestParam String name, @RequestParam Long departmentId, @RequestParam MultipartFile photo, @RequestParam MultipartFile file) {
        String resultPhoto = "";
        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                resultPhoto = "material/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("departments", departmentRepo.findAll());
            return "material_add";
        }

        String resultFile = "";
        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                resultFile = "material/" + uuidFile + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadImg + "/" + resultFile));
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("departments", departmentRepo.findAll());
            return "material_add";
        }

        Material material = materialRepo.save(new Material(name, resultPhoto, resultFile, departmentRepo.getReferenceById(departmentId)));

        return "redirect:/materials/" + material.getId();
    }

    @GetMapping("/{id}/edit")
    public String editMaterial(Model model, @PathVariable Long id) {
        getCurrentUserAndRole(model);
        model.addAttribute("departments", departmentRepo.findAll());
        model.addAttribute("material", materialRepo.getReferenceById(id));
        return "material_edit";
    }

    @PostMapping("/{id}/edit")
    public String editMaterial(Model model, @PathVariable Long id, @RequestParam String name, @RequestParam Long departmentId, @RequestParam MultipartFile photo, @RequestParam MultipartFile file) {
        Material material = materialRepo.getReferenceById(id);

        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String resultPhoto = "material/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
                material.setPhoto(resultPhoto);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("departments", departmentRepo.findAll());
            model.addAttribute("material", materialRepo.getReferenceById(id));
            return "material_edit";
        }

        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String resultFile = "material/" + uuidFile + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadImg + "/" + resultFile));
                material.setFile(resultFile);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("departments", departmentRepo.findAll());
            model.addAttribute("material", materialRepo.getReferenceById(id));
            return "material_edit";
        }

        material.set(name, departmentRepo.getReferenceById(departmentId));

        materialRepo.save(material);

        return "redirect:/materials/{id}";
    }
}