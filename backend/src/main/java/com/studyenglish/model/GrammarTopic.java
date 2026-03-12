package com.studyenglish.model;

public class GrammarTopic {
    private Integer id;
    private Integer sortOrder;
    private String title;
    private String docPath;
    private Integer completed;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDocPath() { return docPath; }
    public void setDocPath(String docPath) { this.docPath = docPath; }

    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }
}
