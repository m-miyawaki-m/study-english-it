package com.studyenglish.controller;

import com.studyenglish.model.GrammarTopic;
import com.studyenglish.service.GrammarService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar")
public class GrammarController {

    private final GrammarService grammarService;

    public GrammarController(GrammarService grammarService) {
        this.grammarService = grammarService;
    }

    @GetMapping
    public List<GrammarTopic> getAll() {
        return grammarService.findAll();
    }

    @PutMapping("/{id}/completed")
    public ResponseEntity<Void> updateCompleted(@PathVariable int id, @RequestBody Map<String, Object> body) {
        Object completedValue = body.get("completed");
        if (completedValue == null) {
            return ResponseEntity.badRequest().build();
        }
        int completed;
        try {
            completed = ((Number) completedValue).intValue();
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().build();
        }
        if (completed != 0 && completed != 1) {
            return ResponseEntity.badRequest().build();
        }
        grammarService.updateCompleted(id, completed);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/content", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getContent(@PathVariable int id) {
        String html = grammarService.getContentAsHtml(id);
        if (html == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(html);
    }
}
