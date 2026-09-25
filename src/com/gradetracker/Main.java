package com.gradetracker;

import com.gradetracker.service.GradeTrackerService;
import com.gradetracker.ui.ConsoleInterface;
import com.gradetracker.ui.GradeTrackerGUI;

import java.awt.GraphicsEnvironment;

/**
 * Main application launcher for Student Grade Tracker.
 * Supports both Modern Swing GUI and Interactive Console CLI modes.
 */
public class Main {
    public static void main(String[] args) {
        GradeTrackerService service = new GradeTrackerService();

        boolean forceConsole = false;
        boolean forceGui = false;

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceConsole = true;
            } else if ("--gui".equalsIgnoreCase(arg) || "-g".equalsIgnoreCase(arg)) {
                forceGui = true;
            }
        }

        // Check if desktop GUI environment is available
        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (forceConsole || isHeadless) {
            System.out.println("Starting Student Grade Tracker in Interactive Console Mode...");
            ConsoleInterface cli = new ConsoleInterface(service);
            cli.start();
        } else {
            System.out.println("===============================================================");
            System.out.println(" 🎓 Starting Student Grade Tracker in Modern GUI Mode...       ");
            System.out.println(" (Note: Run with '--cli' argument to run in terminal mode)     ");
            System.out.println("===============================================================");
            GradeTrackerGUI.launch(service);
        }
    }
}
