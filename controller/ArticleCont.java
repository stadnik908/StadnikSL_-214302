package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.Article;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/articles")
public class ArticleCont extends Main {
    @GetMapping
    public String articles(Model model) {
        getCurrentUserAndRole(model);
        model.addAttribute("articles", articleRepo.findAll());
        return "articles";
    }

    @GetMapping("/search")
    public String searchArticles(Model model, @RequestParam String name) {
        getCurrentUserAndRole(model);
        model.addAttribute("articles", articleRepo.findAllByNameContaining(name));
        model.addAttribute("name", name);
        return "articles";
    }

    @GetMapping("/{id}")
    public String article(Model model, @PathVariable Long id) {
        getCurrentUserAndRole(model);
        Article article = articleRepo.getReferenceById(id);
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/{id}/delete")
    public String deleteArticle(@PathVariable Long id) {
        articleRepo.deleteById(id);
        return "redirect:/articles";
    }

    @GetMapping("/add")
    public String addArticle(Model model) {
        getCurrentUserAndRole(model);
        return "article_add";
    }

    @PostMapping("/add")
    public String addArticle(Model model, @RequestParam String name, @RequestParam String author, @RequestParam String theme, @RequestParam String description, @RequestParam MultipartFile photo, @RequestParam MultipartFile file) {
        String resultPhoto = "";
        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                resultPhoto = "article/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            return "article_add";
        }

        String resultFile = "";
        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                resultFile = "article/" + uuidFile + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadImg + "/" + resultFile));
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            return "article_add";
        }

        Article article = articleRepo.save(new Article(name, author, theme, resultPhoto, resultFile, description));

        return "redirect:/articles/" + article.getId();
    }

    @GetMapping("/{id}/edit")
    public String editArticle(Model model, @PathVariable Long id) {
        getCurrentUserAndRole(model);
        model.addAttribute("article", articleRepo.getReferenceById(id));
        return "article_edit";
    }

    @PostMapping("/{id}/edit")
    public String editArticle(Model model, @PathVariable Long id, @RequestParam String name, @RequestParam String author, @RequestParam String theme, @RequestParam String description, @RequestParam MultipartFile photo, @RequestParam MultipartFile file) {
        Article article = articleRepo.getReferenceById(id);

        try {
            if (photo != null && !Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String resultPhoto = "article/" + uuidFile + "_" + photo.getOriginalFilename();
                photo.transferTo(new File(uploadImg + "/" + resultPhoto));
                article.setPhoto(resultPhoto);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("article", articleRepo.getReferenceById(id));
            return "article_edit";
        }

        try {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                String uuidFile = UUID.randomUUID().toString();
                File uploadDir = new File(uploadImg);
                if (!uploadDir.exists()) uploadDir.mkdir();
                String resultFile = "article/" + uuidFile + "_" + file.getOriginalFilename();
                file.transferTo(new File(uploadImg + "/" + resultFile));
                article.setFile(resultFile);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Некорректные данные!");
            getCurrentUserAndRole(model);
            model.addAttribute("article", articleRepo.getReferenceById(id));
            return "article_edit";
        }

        article.set(name, author, theme, description);

        articleRepo.save(article);

        return "redirect:/articles/{id}";
    }
}