package com.studyenglish.mapper;

import com.studyenglish.model.Word;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WordMapperTest {

    @Autowired
    private WordMapper wordMapper;

    @Test
    void findAll_noFilter_returnsWords() {
        List<Word> words = wordMapper.findAll(null, null, null, 0, 10);
        assertNotNull(words);
        assertFalse(words.isEmpty());
        assertEquals(10, words.size());
    }

    @Test
    void findAll_filterByLevel_returnsOnlyMatchingLevel() {
        List<Word> words = wordMapper.findAll(1, null, null, 0, 1000);
        assertFalse(words.isEmpty());
        for (Word w : words) {
            assertEquals(1, w.getLevel());
        }
    }

    @Test
    void findAll_filterBySourceNGSL_returnsOnlyNGSL() {
        List<Word> words = wordMapper.findAll(null, "NGSL", null, 0, 1100);
        assertFalse(words.isEmpty());
        for (Word w : words) {
            assertEquals("NGSL", w.getSource());
        }
        assertEquals(1000, words.size());
    }

    @Test
    void findAll_filterBySourceIT_returnsNonNGSL() {
        List<Word> words = wordMapper.findAll(null, "IT", null, 0, 100);
        assertFalse(words.isEmpty());
        for (Word w : words) {
            assertNotEquals("NGSL", w.getSource());
        }
    }

    @Test
    void findAll_pagination_returnsCorrectPage() {
        List<Word> page0 = wordMapper.findAll(null, null, null, 0, 5);
        List<Word> page1 = wordMapper.findAll(null, null, null, 5, 5);
        assertEquals(5, page0.size());
        assertEquals(5, page1.size());
        assertNotEquals(page0.get(0).getId(), page1.get(0).getId());
    }

    @Test
    void findById_existingWord_returnsWord() {
        // Get a known word first
        List<Word> words = wordMapper.findAll(null, null, null, 0, 1);
        assertFalse(words.isEmpty());
        int id = words.get(0).getId();

        Word word = wordMapper.findById(id);
        assertNotNull(word);
        assertEquals(id, word.getId());
        assertNotNull(word.getWord());
    }

    @Test
    void updateKnown_changesState() {
        List<Word> words = wordMapper.findAll(null, null, null, 0, 1);
        int id = words.get(0).getId();

        wordMapper.updateKnown(id, 1, "2026-03-13 10:00:00");
        Word updated = wordMapper.findById(id);
        assertEquals(1, updated.getKnown());
        assertEquals("2026-03-13 10:00:00", updated.getLastReviewed());
    }

    @Test
    void getStats_returnsCounts() {
        Map<String, Object> stats = wordMapper.getStats();
        assertNotNull(stats);
        assertNotNull(stats.get("total"));
        int total = ((Number) stats.get("total")).intValue();
        assertTrue(total > 0);
        assertEquals(1025, total);
    }

    @Test
    void countAll_noFilter_returnsTotal() {
        int count = wordMapper.countAll(null, null, null);
        assertEquals(1025, count);
    }
}
