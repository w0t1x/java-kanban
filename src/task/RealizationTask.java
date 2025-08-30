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

    public RealizationTask(String name, String description, Status status) {
        super(name, description, status);
    }

    @Override
    public TaskType getType() {
        return TaskType.TASK;
    }
}