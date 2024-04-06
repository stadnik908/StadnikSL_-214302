package com.certification.controller;

import com.certification.controller.main.Main;
import com.certification.model.Answer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/materials/{materialId}/tests/{testId}/questions/{questionId}/answers")
public class AnswerCont extends Main {
    @PostMapping("/add")
    public String addTest(@PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId, @RequestParam String name, @RequestParam int score) {
        answerRepo.save(new Answer(name, score, questionRepo.getReferenceById(questionId)));
        return "redirect:/materials/{materialId}/tests/{testId}/questions/{questionId}";
    }

    @GetMapping("/{answerId}/delete")
    public String deleteQuestion(@PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId, @PathVariable Long answerId) {
        answerRepo.deleteById(answerId);
        return "redirect:/materials/{materialId}/tests/{testId}/questions/{questionId}";
    }

    @PostMapping("/{answerId}/edit")
    public String deleteQuestion(@PathVariable Long materialId, @PathVariable Long testId, @PathVariable Long questionId, @PathVariable Long answerId, @RequestParam String name, @RequestParam int score) {
        Answer answer = answerRepo.getReferenceById(answerId);
        answer.set(name, score);
        answerRepo.save(answer);
        return "redirect:/materials/{materialId}/tests/{testId}/questions/{questionId}";
    }
}