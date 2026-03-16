package com.studyenglish.mapper;

import com.studyenglish.model.GrammarTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GrammarMapper {
    List<GrammarTopic> findAll();
    GrammarTopic findById(@Param("id") int id);
    void updateCompleted(@Param("id") int id, @Param("completed") int completed);
}
