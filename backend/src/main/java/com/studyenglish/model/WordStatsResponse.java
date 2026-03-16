package com.studyenglish.model;

public class WordStatsResponse {
    private int total;
    private int unknown;
    private int learning;
    private int mastered;

    public WordStatsResponse() {}

    public WordStatsResponse(int total, int unknown, int learning, int mastered) {
        this.total = total;
        this.unknown = unknown;
        this.learning = learning;
        this.mastered = mastered;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getUnknown() { return unknown; }
    public void setUnknown(int unknown) { this.unknown = unknown; }

    public int getLearning() { return learning; }
    public void setLearning(int learning) { this.learning = learning; }

    public int getMastered() { return mastered; }
    public void setMastered(int mastered) { this.mastered = mastered; }
}
