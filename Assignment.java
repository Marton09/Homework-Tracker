import java.time.LocalDate;

public class Assignment extends Task {
    private String course;
    private int estimatedMinutes;
    private int priority;

    public Assignment(int id, String title, LocalDate dueDate,
                      String course, int estimatedMinutes, int priority) {
        super(id, title, dueDate);
        setCourse(course);
        setEstimatedMinutes(estimatedMinutes);
        setPriority(priority);
    }

    @Override
    public String getType() {
        return "ASSIGNMENT";
    }

    public String getCourse() { return course; }
    public int getEstimatedMinutes() { return estimatedMinutes; }
    public int getPriority() { return priority; }

    public void setCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            throw new IllegalArgumentException("Course cannot be empty.");
        }
        this.course = course.trim();
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        if (estimatedMinutes < 0) {
            throw new IllegalArgumentException("Estimated minutes must be 0 or more.");
        }
        this.estimatedMinutes = estimatedMinutes;
    }

    public void setPriority(int priority) {
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be 1 to 5.");
        }
        this.priority = priority;
    }

    @Override
    public String getSummaryLine() {
        return "Course: " + course + " | Est: " + estimatedMinutes + " min | Priority: " + priority;
    }

    @Override
    public String toFileString() {
        return String.join(",",
                getType(),
                String.valueOf(getId()),
                escape(getTitle()),
                getDueDate().toString(),
                getStatus().name(),
                escape(course),
                String.valueOf(estimatedMinutes),
                String.valueOf(priority)
        );
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }
}
