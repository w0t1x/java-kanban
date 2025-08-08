package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    public void updateTimesAndDuration(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
                LocalDateTime subEnd = subtask.getEndTime();
                if (latestEnd == null || (subEnd != null && subEnd.isAfter(latestEnd))) {
                    latestEnd = subEnd;
                }
            }
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Epic epic)) return false;
        return super.equals(object) &&
                Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}
