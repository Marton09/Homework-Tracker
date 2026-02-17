import java.time.LocalDate;
import java.time.LocalTime;

public class Event extends Task {
    private String location;
    private LocalTime startTime;

    public Event(int id, String title, LocalDate dueDate,
                 String location, LocalTime startTime) {
        super(id, title, dueDate);
        setLocation(location);
        setStartTime(startTime);
    }

    @Override
    public String getType() {
        return "EVENT";
    }

    public String getLocation() { return location; }
    public LocalTime getStartTime() { return startTime; }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty.");
        }
        this.location = location.trim();
    }

    public void setStartTime(LocalTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null.");
        }
        this.startTime = startTime;
    }

    @Override
    public String getSummaryLine() {
        return "Location: " + location + " | Start: " + startTime;
    }

    @Override
    public String toFileString() { 
        return String.join(",",
                getType(),
                String.valueOf(getId()),
                escape(getTitle()),
                getDueDate().toString(),
                getStatus().name(),
                escape(location),
                startTime.toString()
        );
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }
}
