# 🎓 Student Grade Tracker (Java)

A complete, production-grade Java application for inputting and managing student grades, computing statistical metrics (average, highest, lowest, passing percentage, letter grade distributions), and generating comprehensive summary reports.

Supports both a **Modern Swing Desktop GUI** and an **Interactive Console (CLI)** interface.

---

## 📌 Project Objectives & Requirements Addressed

| Requirement | Implementation Detail |
|---|---|
| **Input and manage student grades** | Full CRUD operations: Add new students, update scores, delete records, search by name/ID, and prevent invalid input (0.0 to 100.0 bounds checking). |
| **Calculate average, highest, and lowest scores** | Automated calculation of class average, identifying highest and lowest scoring students with IDs and exact percentages, passing/failing rates, and grade distribution (A, B, C, D, F). |
| **Use arrays or ArrayLists** | Uses `ArrayList<Student>` for dynamic record management and provides native array utility methods (`double[] getAllScores()`, `Student[] toStudentArray()`) to demonstrate both approaches. |
| **Display a summary report of all students** | Formatted tabular reporting with student IDs, names, numerical scores, letter grades (A-F), status (Pass/Fail), and performance remarks. Can also be exported to `.txt`. |
| **Console-based or GUI-based** | **Dual-mode support**: Modern, styled Java Swing GUI with live KPI cards, search filter, and badge renderers + interactive Console CLI menu with full validation. |

---

## 📁 Project Structure

```
StudentGradeTracker/
├── src/
│   └── com/
│       └── gradetracker/
│           ├── Main.java                          # Entry point (handles CLI vs GUI arguments)
│           ├── model/
│           │   └── Student.java                   # Student entity with grading logic
│           ├── service/
│           │   └── GradeTrackerService.java       # Core business logic & statistics (ArrayList & Arrays)
│           └── ui/
│               ├── ConsoleInterface.java          # Interactive terminal CLI with menus
│               └── GradeTrackerGUI.java           # Modern Swing GUI with live cards & table
├── bin/                                           # Compiled .class bytecode
├── StudentGradeTracker.jar                        # Standalone executable JAR
├── run-gui.bat                                    # Quick launcher for Modern GUI mode
├── run-console.bat                                # Quick launcher for Console CLI mode
├── build.bat                                      # Compilation & packaging script
└── README.md                                      # Documentation & usage guide
```

---

## 🚀 How to Run the Application

### Option 1: Quick Launch via Windows Batch Scripts
- **Modern GUI Mode**: Double-click `run-gui.bat`
- **Interactive Console Mode**: Double-click `run-console.bat`

---

### Option 2: Running via Command Line (JAR)

Make sure you are in the project folder:
```powershell
cd C:\Users\PC\.gemini\antigravity-ide\scratch\StudentGradeTracker
```

#### Launch Modern Swing GUI Mode:
```powershell
java -jar StudentGradeTracker.jar
```

#### Launch Interactive Console (CLI) Mode:
```powershell
java -jar StudentGradeTracker.jar --cli
```

---

### Option 3: Compiling from Source

If you edit any Java files, recompile with:
```powershell
javac -d bin src/com/gradetracker/model/*.java src/com/gradetracker/service/*.java src/com/gradetracker/ui/*.java src/com/gradetracker/*.java
```

Then run:
```powershell
# Run GUI
java -cp bin com.gradetracker.Main

# Run Console CLI
java -cp bin com.gradetracker.Main --cli
```

---

## 🖥️ Feature Walkthrough

### 1. Modern Swing GUI
- **Live KPI Dashboard Cards**:
  - **Total Students** count
  - **Class Average** score
  - **Highest Score** (with student name badge)
  - **Lowest Score** (with student name badge)
  - **Passing Rate %**
- **Animated Action Buttons**:
  - **➕ Add Student**: Spins 90° smoothly on hover with a dark slate background, tactile click depression, and glowing halo.
  - **✏️ Update**: Dynamic wiggle/tilt animation on hover.
  - **🗑️ Delete**: Warning vibration/shake animation on hover.
  - **✨ Load Demo Data**: Shimmering/pulsing scale animation on hover.
  - **📄 Export Report**: Subtle upward elevation lift on hover with dark styling.
  - **🧹 Clear Form**: Sweeping tilt animation on hover.
  - **Click Ripple & Tactile Feedback**: Expanding ripple effect and 1px tactile click depression on all buttons.
- **Animated Toast Feedback Bar**:
  - Floating status pill that smoothly fades in and out with color-coded status badges.
- **Non-Destructive Demo Data**:
  - Clicking `✨ Load Demo Data` appends sample students without deleting or overwriting any data you entered.
- **Student Data Table**:
  - Displays ID, Student Name, Score, Grade, Status, and Remarks.
  - Grade badges color-coded (Green for A, Blue for B, Red for F).
  - Clicking any row loads the student info into the edit fields.
- **Instant Search / Live Filter**:
  - Filter by typing any student name or ID into the search bar.
- **Report Export**:
  - Click `📄 Export Report` to save the comprehensive statistical summary to a `.txt` file.
- **One-Click Demo Data**:
  - Click `✨ Load Demo Data` to quickly test calculations with diverse grades.

### 2. Interactive Console (CLI)
- Menu-driven text interface with options:
  - `[1]` Add New Student
  - `[2]` View All Students List
  - `[3]` View Statistical Summary Report
  - `[4]` Search Student by Name / ID
  - `[5]` Update Student Score / Name
  - `[6]` Delete Student Record
  - `[7]` Load Sample Demo Students
  - `[8]` Clear All Records
  - `[9]` Open Modern Swing GUI Window
  - `[0]` Exit Application
- Robust input validation (prevents crashes from invalid characters, strings entered for scores, or scores outside 0–100).

---

## 📊 Grading Scale Standard

| Score Range | Letter Grade | Status | Performance Remarks |
|---|---|---|---|
| **90.0 – 100.0** | **A** | Pass | Outstanding |
| **80.0 – 89.9**  | **B** | Pass | Very Good |
| **70.0 – 79.9**  | **C** | Pass | Good |
| **60.0 – 69.9**  | **D** | Pass | Satisfactory |
| **< 60.0**       | **F** | Fail | Needs Improvement |

---

## 📄 Sample Report Output

```text
=================================================================
                  STUDENT GRADE TRACKER REPORT                  
=================================================================
ID     | STUDENT NAME             | SCORE   | GRADE  | STATUS      
-----------------------------------------------------------------
1001   | Alex Johnson             |   94.50 | A      | Pass        
1002   | Sophia Martinez          |   88.00 | B      | Pass        
1003   | Liam Smith               |   76.50 | C      | Pass        
1004   | Emma Brown               |   92.00 | A      | Pass        
1005   | Noah Davis               |   58.00 | F      | Fail        
1006   | Olivia Wilson            |   82.50 | B      | Pass        
1007   | Mason Taylor             |   67.00 | D      | Pass        
1008   | Ava Anderson             |   45.00 | F      | Fail        
=================================================================
                      STATISTICAL SUMMARY                      
-----------------------------------------------------------------
 Total Students Enrolled : 8
 Class Average Score     : 75.44 / 100
 Highest Score           : 94.50 (Alex Johnson, ID: 1001)
 Lowest Score            : 45.00 (Ava Anderson, ID: 1008)
 Passed Students (>=60)  : 6 (75.0%)
 Failed Students (<60)   : 2
-----------------------------------------------------------------
 Grade Distribution:
   Grade A : 2    [#####               ]
   Grade B : 2    [#####               ]
   Grade C : 1    [##                  ]
   Grade D : 1    [##                  ]
   Grade F : 2    [#####               ]
=================================================================
```
#   - c o d e a l p h a _ t a s k s  
 