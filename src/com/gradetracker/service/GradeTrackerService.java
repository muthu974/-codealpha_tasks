package com.gradetracker.service;

import com.gradetracker.model.Student;

import java.util.*;

/**
 * Service managing student records and statistical calculations.
 * Employs ArrayList for dynamic list management and demonstrates
 * native array operations for score calculations.
 */
public class GradeTrackerService {
    private final List<Student> students;
    private int nextStudentId = 1001;

    public GradeTrackerService() {
        this.students = new ArrayList<>();
    }

    /**
     * Adds a new student record.
     */
    public synchronized Student addStudent(String name, double score) {
        if (score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        Student student = new Student(nextStudentId++, name, score);
        students.add(student);
        return student;
    }

    /**
     * Updates an existing student's name and score.
     */
    public synchronized boolean updateStudent(int id, String newName, double newScore) {
        if (newScore < 0.0 || newScore > 100.0) {
            throw new IllegalArgumentException("Score must be between 0.0 and 100.0");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        for (Student s : students) {
            if (s.getId() == id) {
                s.setName(newName);
                s.setScore(newScore);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a student by ID.
     */
    public synchronized boolean removeStudent(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    /**
     * Clears all student records.
     */
    public synchronized void clearAll() {
        students.clear();
        nextStudentId = 1001;
    }

    /**
     * Returns an unmodifiable view of all students.
     */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(new ArrayList<>(students));
    }

    /**
     * Demonstrates native Java array conversion and usage.
     */
    public Student[] toStudentArray() {
        return students.toArray(new Student[0]);
    }

    /**
     * Extracts an array of double scores.
     */
    public double[] getAllScores() {
        double[] scores = new double[students.size()];
        for (int i = 0; i < students.size(); i++) {
            scores[i] = students.get(i).getScore();
        }
        return scores;
    }

    public int getStudentCount() {
        return students.size();
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    /**
     * Calculates class average score.
     */
    public double calculateAverage() {
        if (students.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Student s : students) {
            sum += s.getScore();
        }
        return Math.round((sum / students.size()) * 100.0) / 100.0;
    }

    /**
     * Finds the student with the highest score.
     */
    public Optional<Student> getHighestScorer() {
        if (students.isEmpty()) {
            return Optional.empty();
        }
        Student highest = students.get(0);
        for (Student s : students) {
            if (s.getScore() > highest.getScore()) {
                highest = s;
            }
        }
        return Optional.of(highest);
    }

    /**
     * Finds the student with the lowest score.
     */
    public Optional<Student> getLowestScorer() {
        if (students.isEmpty()) {
            return Optional.empty();
        }
        Student lowest = students.get(0);
        for (Student s : students) {
            if (s.getScore() < lowest.getScore()) {
                lowest = s;
            }
        }
        return Optional.of(lowest);
    }

    public double getHighestScore() {
        return getHighestScorer().map(Student::getScore).orElse(0.0);
    }

    public double getLowestScore() {
        return getLowestScorer().map(Student::getScore).orElse(0.0);
    }

    /**
     * Computes the number of students who scored >= 60.
     */
    public int getPassingCount() {
        int count = 0;
        for (Student s : students) {
            if (s.isPassing()) count++;
        }
        return count;
    }

    public int getFailingCount() {
        return getStudentCount() - getPassingCount();
    }

    public double getPassingPercentage() {
        if (isEmpty()) return 0.0;
        return Math.round(((double) getPassingCount() / getStudentCount() * 100.0) * 10.0) / 10.0;
    }

    /**
     * Computes letter grade distribution counts (A, B, C, D, F).
     */
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> dist = new LinkedHashMap<>();
        dist.put("A", 0);
        dist.put("B", 0);
        dist.put("C", 0);
        dist.put("D", 0);
        dist.put("F", 0);

        for (Student s : students) {
            String grade = s.getLetterGrade();
            dist.put(grade, dist.getOrDefault(grade, 0) + 1);
        }
        return dist;
    }

    /**
     * Finds students whose name contains the search term (case-insensitive) or matches ID.
     */
    public List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        String q = query.trim().toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(q) || String.valueOf(s.getId()).contains(q)) {
                results.add(s);
            }
        }
        return results;
    }

    /**
     * Appends sample demonstration students WITHOUT deleting any existing user records.
     * Preserves all user data and only adds sample students not already in the roster.
     * @return Number of new demo students added.
     */
    public synchronized int populateSampleData() {
        String[][] demoList = {
            {"Alex Johnson", "94.5"},
            {"Sophia Martinez", "88.0"},
            {"Liam Smith", "76.5"},
            {"Emma Brown", "92.0"},
            {"Noah Davis", "58.0"},
            {"Olivia Wilson", "82.5"},
            {"Mason Taylor", "67.0"},
            {"Ava Anderson", "45.0"}
        };

        int addedCount = 0;
        for (String[] entry : demoList) {
            String name = entry[0];
            double score = Double.parseDouble(entry[1]);
            boolean alreadyExists = false;
            for (Student s : students) {
                if (s.getName().equalsIgnoreCase(name)) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                addStudent(name, score);
                addedCount++;
            }
        }

        // If standard demo students are already present, add an incremental sample student
        if (addedCount == 0) {
            int num = students.size() + 1;
            double randomScore = Math.round((65.0 + Math.random() * 30.0) * 10.0) / 10.0;
            addStudent("Demo Student " + num, randomScore);
            addedCount = 1;
        }

        return addedCount;
    }

    /**
     * Generates a comprehensive plain text summary report.
     */
    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(65);
        String subline = "-".repeat(65);

        sb.append(line).append("\n");
        sb.append("                  STUDENT GRADE TRACKER REPORT                  \n");
        sb.append(line).append("\n");

        if (isEmpty()) {
            sb.append("No student records found. Please add students first.\n");
            sb.append(line).append("\n");
            return sb.toString();
        }

        sb.append(String.format("%-6s | %-24s | %-7s | %-6s | %-12s\n", "ID", "STUDENT NAME", "SCORE", "GRADE", "STATUS"));
        sb.append(subline).append("\n");

        for (Student s : students) {
            sb.append(String.format("%-6d | %-24s | %7.2f | %-6s | %-12s\n",
                    s.getId(), s.getName(), s.getScore(), s.getLetterGrade(), s.getStatus()));
        }

        sb.append(line).append("\n");
        sb.append("                      STATISTICAL SUMMARY                      \n");
        sb.append(subline).append("\n");
        sb.append(String.format(" Total Students Enrolled : %d\n", getStudentCount()));
        sb.append(String.format(" Class Average Score     : %.2f / 100\n", calculateAverage()));

        getHighestScorer().ifPresent(s ->
                sb.append(String.format(" Highest Score           : %.2f (%s, ID: %d)\n", s.getScore(), s.getName(), s.getId()))
        );

        getLowestScorer().ifPresent(s ->
                sb.append(String.format(" Lowest Score            : %.2f (%s, ID: %d)\n", s.getScore(), s.getName(), s.getId()))
        );

        sb.append(String.format(" Passed Students (>=60)  : %d (%.1f%%)\n", getPassingCount(), getPassingPercentage()));
        sb.append(String.format(" Failed Students (<60)   : %d\n", getFailingCount()));

        sb.append(subline).append("\n");
        sb.append(" Grade Distribution:\n");
        Map<String, Integer> dist = getGradeDistribution();
        for (Map.Entry<String, Integer> entry : dist.entrySet()) {
            int count = entry.getValue();
            int barLength = getStudentCount() > 0 ? (count * 20 / getStudentCount()) : 0;
            String bar = "#".repeat(barLength);
            sb.append(String.format("   Grade %-2s : %-3d  [%-20s]\n", entry.getKey(), count, bar));
        }

        sb.append(line).append("\n");
        return sb.toString();
    }
}
