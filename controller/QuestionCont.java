package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.Question;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/materials/{materialId}/tests/{testId}/questions")
public class QuestionCont extends Main {

    @PostMapping("/add")
    public String addTest(@PathVariable Long materialId, @PathVariable Long testId, @RequestParam String name) {
        Question question = questionRepo.save(new Question(name, testRepo.getReferenceById(testId)));
        return "redirect:/materials/{materialId}/tests/{testId}/questions/" + question.getId();
    }

    @GetMapping("/{questionId}")
    public String question(Model model, @PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId) {
        getCurrentUserAndRole(model);
        model.addAttribute("question", questionRepo.getReferenceById(questionId));
        return "question";
    }

    @GetMapping("/{questionId}/delete")
    public String deleteQuestion(@PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId) {
        questionRepo.deleteById(questionId);
        return "redirect:/materials/{materialId}/tests/{testId}";
    }

    @PostMapping("/{questionId}/edit")
    public String deleteQuestion(@PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId, @RequestParam String name) {
        Question question = questionRepo.getReferenceById(questionId);
        question.setName(name);
        questionRepo.save(question);
        return "redirect:/materials/{materialId}/tests/{testId}/questions/{questionId}";
    }


}
