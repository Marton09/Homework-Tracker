import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class TaskStore {
    private final Path path;

    public TaskStore(String filename) {
        this.path = Paths.get(filename);
    }

    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(path)) return tasks;

        for (String line : Files.readAllLines(path)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\|", -1);
            String type = parts[0];

            int id = Integer.parseInt(parts[1]);
            String title = parts[2];
            LocalDate dueDate = LocalDate.parse(parts[3]);
            Status status = Status.valueOf(parts[4]);

            if ("ASSIGNMENT".equals(type)) {
                String course = parts[5];
                int est = Integer.parseInt(parts[6]);
                int priority = Integer.parseInt(parts[7]);
                tasks.add(new Assignment(id, title, dueDate, status, course, est, priority));
            } else if ("EVENT".equals(type)) {
                String location = parts[5];
                LocalTime startTime = LocalTime.parse(parts[6]);
                tasks.add(new Event(id, title, dueDate, status, location, startTime));
            } else {
                throw new IOException("Unknown task type: " + type);
            }
        }
        return tasks;
    }

    public void save(List<Task> tasks) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            if (t instanceof Assignment a) {
                lines.add(String.join("|",
                        "ASSIGNMENT",
                        String.valueOf(a.getId()),
                        escape(a.getTitle()),
                        a.getDueDate().toString(),
                        a.getStatus().name(),
                        escape(a.getCourse()),
                        String.valueOf(a.getEstimatedMinutes()),
                        String.valueOf(a.getPriority())
                ));
            } else if (t instanceof Event e) {
                lines.add(String.join("|",
                        "EVENT",
                        String.valueOf(e.getId()),
                        escape(e.getTitle()),
                        e.getDueDate().toString(),
                        e.getStatus().name(),
                        escape(e.getLocation()),
                        e.getStartTime().toString()
                ));
            }
        }
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    private String escape(String s) { return s.replace("|", "/"); }
}
