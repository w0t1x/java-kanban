package manager;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.nullsLast(Comparator.comparing(Task::getStartTime))
    );

    public int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasIntersections(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        return getPrioritizedTasks().stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .anyMatch(task -> {
                    LocalDateTime existingStart = task.getStartTime();
                    LocalDateTime existingEnd = task.getEndTime();
                    return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
                });
    }

    @Override
    public Task createTask(Task task) {
        if (hasIntersections(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующими задачами.");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    @Override
    public void deleteTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritizedTasks(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.clear();
        historyManager.getHistory().clear();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            removeFromPrioritizedTasks(oldTask);

            if (hasIntersections(task)) {
                addToPrioritizedTasks(oldTask);
                throw new ManagerSaveException("Задача пересекается по времени с существующими задачами.");
            }

            tasks.put(task.getId(), task);
            addToPrioritizedTasks(task);
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : new ArrayList<>(epic.getSubtaskIds())) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    removeFromPrioritizedTasks(subtask);
                    historyManager.remove(subtaskId);
                }
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicTimeAndStatus(epic);
        }
    }

    protected void updateEpicTimeAndStatus(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpic(epic);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        boolean allNew = epicSubtasks.stream().allMatch(st -> st.getStatus() == Status.NEW);
        boolean allDone = epicSubtasks.stream().allMatch(st -> st.getStatus() == Status.DONE);

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        Optional<LocalDateTime> minStartOpt = epicSubtasks.stream()
                .filter(st -> st.getStartTime() != null)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> maxEndOpt = epicSubtasks.stream()
                .filter(st -> st.getStartTime() != null && st.getDuration() != null)
                .map(st -> st.getStartTime().plus(st.getDuration()))
                .max(LocalDateTime::compareTo);

        if (minStartOpt.isPresent() && maxEndOpt.isPresent()) {
            LocalDateTime minStart = minStartOpt.get();
            LocalDateTime maxEnd = maxEndOpt.get();
            epic.setStartTime(minStart);
            epic.setEndTime(maxEnd);
            epic.setDuration(Duration.between(minStart, maxEnd));
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasIntersections(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующими задачами.");
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicTimeAndStatus(epic);
        }
        addToPrioritizedTasks(subtask);
        return subtask;
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    @Override
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicTimeAndStatus(epic);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            removeFromPrioritizedTasks(oldSubtask);

            if (hasIntersections(subtask)) {
                addToPrioritizedTasks(oldSubtask);
                throw new ManagerSaveException("Подзадача пересекается по времени с существующими задачами.");
            }

            subtasks.put(subtask.getId(), subtask);
            addToPrioritizedTasks(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicTimeAndStatus(epic);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}