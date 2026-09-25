package com.gradetracker.model;

/**
 * Model class representing a student and their academic score.
 */
public class Student {
    private final int id;
    private String name;
    private double score;

    public Student(int id, String name, double score) {
        this.id = id;
        this.name = name.trim();
        this.score = Math.round(score * 100.0) / 100.0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = Math.round(score * 100.0) / 100.0;
    }

    /**
     * Determines letter grade based on standard scale:
     * 90 - 100: A
     * 80 - 89.9: B
     * 70 - 79.9: C
     * 60 - 69.9: D
     * < 60: F
     */
    public String getLetterGrade() {
        if (score >= 90.0) return "A";
        if (score >= 80.0) return "B";
        if (score >= 70.0) return "C";
        if (score >= 60.0) return "D";
        return "F";
    }

    /**
     * Standard pass threshold is 60.0.
     */
    public boolean isPassing() {
        return score >= 60.0;
    }

    public String getStatus() {
        return isPassing() ? "Pass" : "Fail";
    }

    public String getPerformanceRemarks() {
        if (score >= 90.0) return "Outstanding";
        if (score >= 80.0) return "Very Good";
        if (score >= 70.0) return "Good";
        if (score >= 60.0) return "Satisfactory";
        return "Needs Improvement";
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | %-20s | Score: %6.2f | Grade: %-2s | %s",
                id, name, score, getLetterGrade(), getPerformanceRemarks());
    }
}
