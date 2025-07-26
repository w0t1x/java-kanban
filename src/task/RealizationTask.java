package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class RealizationTask extends Task {
    public RealizationTask(String name, String description) {
        super(-1, name, description, Status.NEW);
    }

    public RealizationTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    // Конструктор с новыми полями
    public RealizationTask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    @Override
    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public String toString() {
        return String.join(",",
                String.valueOf(getId()),
                getType().name(),
                getName(),
                getStatus().name(),
                getDescription(),
                "",
                getDuration() != null ? String.valueOf(getDuration().toMinutes()) : "", // duration в минутах
                getStartTime() != null ? getStartTime().toString() : "" // startTime
        );
    }
}