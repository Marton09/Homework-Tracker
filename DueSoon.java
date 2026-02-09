import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private final List<Task> tasks;
    private int nextId;

    public TaskManager(List<Task> existing) {
        this.tasks = existing;
        this.nextId = existing.stream().mapToInt(Task::getId).max().orElse(0) + 1;
    }

    public List<Task> getAll() { return tasks; }
    public int newId() { return nextId++; }

    public List<Task> dueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate end = today.plusDays(days);
        return tasks.stream()
                .filter(t -> !t.getStatus().equals(Status.DONE))
                .filter(t -> !t.getDueDate().isBefore(today) && !t.getDueDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public Task findById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public boolean deleteById(int id) {
        return tasks.removeIf(t -> t.getId() == id);
    }
}
