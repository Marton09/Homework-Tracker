import java.time.LocalDate;

public abstract class Task {
    private final int id;
    private String title;
    private LocalDate dueDate;
    private Status status;

    public Task(int id, String title, LocalDate dueDate) {
        this.id = id;
        setTitle(title);
        setDueDate(dueDate);
        this.status = Status.NOT_STARTED;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public LocalDate getDueDate() { return dueDate; }
    public Status getStatus() { return status; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = title.trim();
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null.");
        }
        this.dueDate = dueDate;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        this.status = status;
    }

    // Polymorphism methods
    public abstract String getType();
    public abstract String getSummaryLine();
    public abstract String toFileString();
}
