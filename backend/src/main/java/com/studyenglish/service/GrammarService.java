package com.studyenglish.service;

import com.studyenglish.mapper.GrammarMapper;
import com.studyenglish.model.GrammarTopic;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class GrammarService {

    private final GrammarMapper grammarMapper;
    private final String docsBasePath;

    public GrammarService(GrammarMapper grammarMapper,
                          @Value("${app.docs-base-path}") String docsBasePath) {
        this.grammarMapper = grammarMapper;
        this.docsBasePath = docsBasePath;
    }

    public List<GrammarTopic> findAll() {
        return grammarMapper.findAll();
    }

    public GrammarTopic findById(int id) {
        return grammarMapper.findById(id);
    }

    public void updateCompleted(int id, int completed) {
        grammarMapper.updateCompleted(id, completed);
    }

    public String getContentAsHtml(int id) {
        GrammarTopic topic = grammarMapper.findById(id);
        if (topic == null) {
            return null;
        }

        Path filePath = Path.of(docsBasePath, topic.getDocPath());
        try {
            String markdown = Files.readString(filePath);
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            Node document = parser.parse(markdown);
            return renderer.render(document);
        } catch (IOException e) {
            return null;
        }
    }
}
