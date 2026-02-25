import java.io.*;
import java.time.*;
import java.util.*;

public class StudyBuddyApp {

    private static final String FILE_NAME = "tasks.txt";
    private static final Scanner scanner = new Scanner(System.in);

    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static int nextId = 1;

    public static void main(String[] args) {
        loadTasks();

        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ", 1, 7);

            switch (choice) {
                case 1 -> addTask();
                case 2 -> editTask();
                case 3 -> deleteTask();
                case 4 -> markTaskStatus();
                case 5 -> viewAllTasks();
                case 6 -> viewDueSoon();
                case 7 -> {
                    saveTasks();
                    System.out.println("Saved. Goodbye!");
                    return;
                }
            }
        }
    }

    // ---------- MENU ----------
    private static void printMenu() {
        System.out.println("\n=== StudyBuddy ===");
        System.out.println("1) Add a task");
        System.out.println("2) Edit a task");
        System.out.println("3) Delete a task");
        System.out.println("4) Mark task status");
        System.out.println("5) View all tasks");
        System.out.println("6) View tasks due soon (next 3 days)");
        System.out.println("7) Save and exit");
    }

    // ---------- ADD ----------
    private static void addTask() {
        System.out.println("\nAdd Task Type:");
        System.out.println("1) Assignment");
        System.out.println("2) Event");
        int type = readInt("Choose type: ", 1, 2);

        String title = readNonEmpty("Title: ");
        LocalDate dueDate = readDate("Due date (YYYY-MM-DD): ");

        try {
            if (type == 1) {
                String course = readNonEmpty("Course: ");
                int minutes = readInt("Estimated minutes: ", 0, 100000);
                int priority = readInt("Priority (1-5): ", 1, 5);

                Assignment a = new Assignment(nextId++, title, dueDate, course, minutes, priority);
                tasks.add(a);
            } else {
                String location = readNonEmpty("Location: ");
                LocalTime time = readTime("Start time (HH:MM): ");

                Event e = new Event(nextId++, title, dueDate, location, time);
                tasks.add(e);
            }
            System.out.println("Task added.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // ---------- EDIT ----------
    private static void editTask() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to edit.");
            return;
        }

        int id = readInt("Enter task ID to edit: ", 1, Integer.MAX_VALUE);
        Task t = findById(id);

        if (t == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.println("\nEditing Task #" + t.getId() + " (" + t.getType() + ")");
        System.out.println("Current: " + t.getTitle() + " | Due: " + t.getDueDate() + " | Status: " + t.getStatus());
        System.out.println("Details: " + t.getSummaryLine());

        System.out.println("\nWhat do you want to edit?");
        System.out.println("1) Title");
        System.out.println("2) Due date");
        System.out.println("3) Type-specific fields");
        System.out.println("4) Cancel");
        int choice = readInt("Choose: ", 1, 4);

        try {
            if (choice == 1) {
                t.setTitle(readNonEmpty("New title: "));
                System.out.println("Updated.");
            } else if (choice == 2) {
                t.setDueDate(readDate("New due date (YYYY-MM-DD): "));
                System.out.println("Updated.");
            } else if (choice == 3) {
                editTypeSpecific(t);
            } else {
                System.out.println("Cancelled.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void editTypeSpecific(Task t) {
        if (t instanceof Assignment a) {
            System.out.println("\nEdit Assignment Fields:");
            System.out.println("1) Course");
            System.out.println("2) Estimated minutes");
            System.out.println("3) Priority (1-5)");
            System.out.println("4) Cancel");
            int c = readInt("Choose: ", 1, 4);

            switch (c) {
                case 1 -> a.setCourse(readNonEmpty("New course: "));
                case 2 -> a.setEstimatedMinutes(readInt("New estimated minutes: ", 0, 100000));
                case 3 -> a.setPriority(readInt("New priority (1-5): ", 1, 5));
                case 4 -> { System.out.println("Cancelled."); return; }
            }
            System.out.println("Updated.");
        } else if (t instanceof Event e) {
            System.out.println("\nEdit Event Fields:");
            System.out.println("1) Location");
            System.out.println("2) Start time (HH:MM)");
            System.out.println("3) Cancel");
            int c = readInt("Choose: ", 1, 3);

            switch (c) {
                case 1 -> e.setLocation(readNonEmpty("New location: "));
                case 2 -> e.setStartTime(readTime("New start time (HH:MM): "));
                case 3 -> { System.out.println("Cancelled."); return; }
            }
            System.out.println("Updated.");
        } else {
            System.out.println("Unknown task type.");
        }
    }

    // ---------- DELETE ----------
    private static void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to delete.");
            return;
        }

        int id = readInt("Enter task ID to delete: ", 1, Integer.MAX_VALUE);
        Task t = findById(id);

        if (t == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.println("Deleting: #" + t.getId() + " " + t.getTitle());
        String confirm = readNonEmpty("Type YES to confirm: ");
        if (confirm.equalsIgnoreCase("YES")) {
            tasks.remove(t);
            System.out.println("Deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    // ---------- STATUS ----------
    private static void markTaskStatus() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to mark.");
            return;
        }

        int id = readInt("Enter task ID: ", 1, Integer.MAX_VALUE);
        Task t = findById(id);

        if (t == null) {
            System.out.println("Task not found.");
            return;
        }

        System.out.println("Current status: " + t.getStatus());
        System.out.println("1) NOT_STARTED");
        System.out.println("2) IN_PROGRESS");
        System.out.println("3) DONE");
        int s = readInt("Choose status: ", 1, 3);

        t.setStatus(Status.values()[s - 1]);
        System.out.println("Status updated.");
    }

    // ---------- VIEW ----------
    private static void viewAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to view.");
            return;
        }

        System.out.println("\nView Options:");
        System.out.println("1) Sort by due date");
        System.out.println("2) Sort by class (assignments first)");
        int choice = readInt("Choose: ", 1, 2);

        ArrayList<Task> copy = new ArrayList<>(tasks);

        if (choice == 1) {
            copy.sort(Comparator.comparing(Task::getDueDate).thenComparing(Task::getId));
        } else {
            copy.sort((a, b) -> {
                boolean aIsAssign = a instanceof Assignment;
                boolean bIsAssign = b instanceof Assignment;

                if (aIsAssign && !bIsAssign) return -1;
                if (!aIsAssign && bIsAssign) return 1;

                if (aIsAssign) {
                    String ac = ((Assignment) a).getCourse().toLowerCase();
                    String bc = ((Assignment) b).getCourse().toLowerCase();
                    int cmp = ac.compareTo(bc);
                    if (cmp != 0) return cmp;
                }
                return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
            });
        }

        System.out.println("\n--- Tasks ---");
        for (Task t : copy) {
            System.out.println("#" + t.getId() + " | " + t.getType() + " | " + t.getTitle());
            System.out.println("Due: " + t.getDueDate() + " | Status: " + t.getStatus());
            System.out.println("Details: " + t.getSummaryLine());
            System.out.println();
        }
    }

    private static void viewDueSoon() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to view.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate cutoff = today.plusDays(3);

        ArrayList<Task> dueSoon = new ArrayList<>();
        for (Task t : tasks) {
            if (!t.getDueDate().isBefore(today) && !t.getDueDate().isAfter(cutoff)) {
                dueSoon.add(t);
            }
        }

        dueSoon.sort(Comparator.comparing(Task::getDueDate).thenComparing(Task::getId));

        System.out.println("\n--- Due Soon (next 3 days) ---");
        if (dueSoon.isEmpty()) {
            System.out.println("Nothing due soon.");
            return;
        }

        for (Task t : dueSoon) {
            System.out.println("#" + t.getId() + " | " + t.getTitle() + " | Due: " + t.getDueDate() + " | " + t.getStatus());
        }
    }

    // ---------- FILE I/O ----------
    private static void saveTasks() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task t : tasks) {
                out.println(t.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private static void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                List<String> parts = splitCsvWithEscapes(line);
                String type = parts.get(0);

                int id = Integer.parseInt(parts.get(1));
                String title = unescape(parts.get(2));
                LocalDate dueDate = LocalDate.parse(parts.get(3));
                Status status = Status.valueOf(parts.get(4));

                Task t;
                if ("ASSIGNMENT".equals(type)) {
                    String course = unescape(parts.get(5));
                    int minutes = Integer.parseInt(parts.get(6));
                    int priority = Integer.parseInt(parts.get(7));
                    Assignment a = new Assignment(id, title, dueDate, course, minutes, priority);
                    a.setStatus(status);
                    t = a;
                } else if ("EVENT".equals(type)) {
                    String location = unescape(parts.get(5));
                    LocalTime start = LocalTime.parse(parts.get(6));
                    Event e = new Event(id, title, dueDate, location, start);
                    e.setStatus(status);
                    t = e;
                } else {
                    continue;
                }

                tasks.add(t);
                nextId = Math.max(nextId, id + 1);
            }
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private static List<String> splitCsvWithEscapes(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (escaping) {
                current.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else if (ch == ',') {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result;
    }

    private static String unescape(String s) {
        StringBuilder out = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (escaping) {
                out.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }

    // ---------- HELPERS ----------
    private static Task findById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("Input cannot be empty.");
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine();
            try {
                int val = Integer.parseInt(s.trim());
                if (val < min || val > max) {
                    System.out.println("Enter a number from " + min + " to " + max + ".");
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                System.out.println("Use format YYYY-MM-DD (example: 2026-02-03).");
            }
        }
    }

    private static LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                return LocalTime.parse(s);
            } catch (Exception e) {
                System.out.println("Use format HH:MM (example: 14:30).");
            }
        }
    }
}
