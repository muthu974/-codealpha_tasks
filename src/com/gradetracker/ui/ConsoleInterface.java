package com.gradetracker.ui;

import com.gradetracker.model.Student;
import com.gradetracker.service.GradeTrackerService;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive text-based console user interface for Student Grade Tracker.
 */
public class ConsoleInterface {
    private final GradeTrackerService service;
    private final Scanner scanner;

    public ConsoleInterface(GradeTrackerService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            String choice = prompt("Enter your choice (0-9): ");
            System.out.println();
            switch (choice) {
                case "1" -> handleAddStudent();
                case "2" -> handleViewAllStudents();
                case "3" -> handleViewSummaryReport();
                case "4" -> handleSearchStudents();
                case "5" -> handleUpdateStudent();
                case "6" -> handleDeleteStudent();
                case "7" -> handleLoadSampleData();
                case "8" -> handleClearAll();
                case "9" -> {
                    System.out.println("Launching Swing GUI window...");
                    GradeTrackerGUI.launch(service);
                }
                case "0" -> {
                    System.out.println("Thank you for using Student Grade Tracker. Goodbye!");
                    running = false;
                }
                default -> System.out.println("[Error] Invalid choice! Please select an option from 0 to 9.");
            }
            if (running) {
                System.out.println();
                pause();
            }
        }
    }

    private void printBanner() {
        System.out.println("=================================================================");
        System.out.println("            STUDENT GRADE TRACKER SYSTEM (CLI)                   ");
        System.out.println("=================================================================");
        System.out.println(" Manage student scores, calculate averages, highs, and lows.");
        System.out.println(" Built with Java | Console & Modern Swing GUI Support");
        System.out.println("-----------------------------------------------------------------");
    }

    private void printMenu() {
        System.out.println("\n----------------------- MAIN MENU -------------------------------");
        System.out.println(" [1] Add New Student");
        System.out.println(" [2] View All Students List");
        System.out.println(" [3] View Statistical Summary Report (Avg, Max, Min, Distribution)");
        System.out.println(" [4] Search Student by Name / ID");
        System.out.println(" [5] Update Student Score / Name");
        System.out.println(" [6] Delete Student Record");
        System.out.println(" [7] Load Sample Demo Students");
        System.out.println(" [8] Clear All Records");
        System.out.println(" [9] Open Modern Swing GUI Window");
        System.out.println(" [0] Exit Application");
        System.out.println("-----------------------------------------------------------------");
    }

    private void handleAddStudent() {
        System.out.println("--- ADD NEW STUDENT ---");
        String name = promptNonEmpty("Enter student full name: ");
        double score = promptScore("Enter score (0.0 - 100.0): ");

        Student student = service.addStudent(name, score);
        System.out.println("\n[Success] Student added successfully!");
        System.out.printf("  ID: %d | Name: %s | Score: %.2f | Grade: %s (%s)\n",
                student.getId(), student.getName(), student.getScore(),
                student.getLetterGrade(), student.getStatus());
    }

    private void handleViewAllStudents() {
        System.out.println("--- STUDENT DIRECTORY ---");
        List<Student> students = service.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students enrolled yet. Use option [1] to add or [7] to load sample data.");
            return;
        }

        printStudentTable(students);
    }

    private void handleViewSummaryReport() {
        System.out.println(service.generateSummaryReport());
    }

    private void handleSearchStudents() {
        System.out.println("--- SEARCH STUDENTS ---");
        String query = prompt("Enter search keyword (name or ID): ");
        List<Student> matches = service.searchStudents(query);
        if (matches.isEmpty()) {
            System.out.println("[Notice] No students found matching: " + query);
        } else {
            System.out.printf("Found %d matching student(s):\n", matches.size());
            printStudentTable(matches);
        }
    }

    private void handleUpdateStudent() {
        System.out.println("--- UPDATE STUDENT ---");
        if (service.isEmpty()) {
            System.out.println("No student records available to update.");
            return;
        }

        int id = promptInt("Enter Student ID to update: ");
        List<Student> list = service.getAllStudents();
        Student target = list.stream().filter(s -> s.getId() == id).findFirst().orElse(null);

        if (target == null) {
            System.out.println("[Error] Student with ID " + id + " not found.");
            return;
        }

        System.out.printf("Current Record -> ID: %d | Name: %s | Score: %.2f\n", target.getId(), target.getName(), target.getScore());
        String newName = prompt("Enter new name (leave blank to keep current): ");
        if (newName.trim().isEmpty()) {
            newName = target.getName();
        }

        String scoreInput = prompt("Enter new score 0-100 (leave blank to keep current): ");
        double newScore = target.getScore();
        if (!scoreInput.trim().isEmpty()) {
            try {
                double parsed = Double.parseDouble(scoreInput.trim());
                if (parsed < 0 || parsed > 100) {
                    System.out.println("[Error] Score must be between 0 and 100. Update cancelled.");
                    return;
                }
                newScore = parsed;
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid numerical score. Update cancelled.");
                return;
            }
        }

        boolean updated = service.updateStudent(id, newName, newScore);
        if (updated) {
            System.out.println("[Success] Student record updated successfully!");
        } else {
            System.out.println("[Error] Failed to update record.");
        }
    }

    private void handleDeleteStudent() {
        System.out.println("--- DELETE STUDENT ---");
        if (service.isEmpty()) {
            System.out.println("No student records available to delete.");
            return;
        }

        int id = promptInt("Enter Student ID to delete: ");
        boolean removed = service.removeStudent(id);
        if (removed) {
            System.out.println("[Success] Student with ID " + id + " has been removed.");
        } else {
            System.out.println("[Error] Student with ID " + id + " not found.");
        }
    }

    private void handleLoadSampleData() {
        int added = service.populateSampleData();
        System.out.printf("[Success] Added %d demo student(s)! All existing records are preserved.\n", added);
        System.out.println("Select option [2] to view them or [3] for the statistical breakdown.");
    }

    private void handleClearAll() {
        String confirm = prompt("Are you sure you want to delete ALL records? (yes/no): ");
        if ("yes".equalsIgnoreCase(confirm.trim()) || "y".equalsIgnoreCase(confirm.trim())) {
            service.clearAll();
            System.out.println("[Success] All student records have been cleared.");
        } else {
            System.out.println("[Notice] Clear operation cancelled.");
        }
    }

    private void printStudentTable(List<Student> students) {
        String header = String.format("%-6s | %-24s | %-7s | %-6s | %-8s | %-16s",
                "ID", "STUDENT NAME", "SCORE", "GRADE", "STATUS", "REMARKS");
        String border = "-".repeat(header.length());

        System.out.println(border);
        System.out.println(header);
        System.out.println(border);
        for (Student s : students) {
            System.out.printf("%-6d | %-24s | %7.2f | %-6s | %-8s | %-16s\n",
                    s.getId(), s.getName(), s.getScore(), s.getLetterGrade(), s.getStatus(), s.getPerformanceRemarks());
        }
        System.out.println(border);
        System.out.printf("Total Count: %d student(s)\n", students.size());
    }

    private String prompt(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private String promptNonEmpty(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[Error] Name cannot be empty. Please enter a valid name.");
        }
    }

    private double promptScore(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= 0.0 && val <= 100.0) {
                    return val;
                }
                System.out.println("[Error] Score must be between 0.0 and 100.0.");
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid number. Please enter a valid decimal number (e.g. 85.5).");
            }
        }
    }

    private int promptInt(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid integer ID.");
            }
        }
    }

    private void pause() {
        System.out.print("Press [Enter] to continue...");
        scanner.nextLine();
    }
}
