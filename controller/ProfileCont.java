package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.AppUser;
import com.certification.model.Passing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileCont extends Main {
    @GetMapping
    public String profile(Model model) {
        getCurrentUserAndRole(model);

        List<Passing> passings = getUser().getPassings();

        int countTrue = passings.stream().reduce(0, (i, passing) -> {
            if (passing.isStatus()) return i + 1;
            return i;
        }, Integer::sum);

        int countFalse = passings.stream().reduce(0, (i, passing) -> {
            if (!passing.isStatus()) return i + 1;
            return i;
        }, Integer::sum);

        model.addAttribute("countTrue", countTrue);
        model.addAttribute("countFalse", countFalse);

        if (getUser().isCertification()) {
            model.addAttribute("message", "Вам нужно пройти аттестацию!");
        }

        return "profile";
    }

    @PostMapping("/photo")
    public String profileFio(Model model, @RequestParam MultipartFile photo) {
        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String result = "user/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + result));

                AppUser user = getUser();
                user.setPhoto(result);
                userRepo.save(user);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            return "profile";
        }

        return "redirect:/profile";
    }

    @PostMapping("/fio")
    public String profileFio(@RequestParam String fio) {
        AppUser user = getUser();
        user.setFio(fio);
        userRepo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/department")
    public String departmentFio(@RequestParam Long departmentId) {
        AppUser user = getUser();
        user.setDepartment(departmentRepo.getReferenceById(departmentId));
        userRepo.save(user);
        return "redirect:/profile";
    }
}
