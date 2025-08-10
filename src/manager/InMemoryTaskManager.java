package manager;

import task.*;

import java.util.*;

public abstract class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    private boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getEndTime() == null ||
                t2.getStartTime() == null || t2.getEndTime() == null) return false;
        return !t1.getEndTime().isBefore(t2.getStartTime()) &&
                !t1.getStartTime().isAfter(t2.getEndTime());
    }

    private void checkForOverlaps(Task newTask) {
        for (Task existing : prioritizedTasks) {
            if (isOverlapping(existing, newTask)) {
                throw new IllegalArgumentException("Время задачи пересекается с другой");
            }
        }
    }

    @Override
    public Task createTask(Task task) {
        checkForOverlaps(task);
        task.setId(generateId());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) prioritizedTasks.add(task);
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
        if (task != null) prioritizedTasks.remove(task);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
        historyManager.getHistory().clear();
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
            for (int subtaskId : epic.getSubtaskIds()) {
                prioritizedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            historyManager.remove(id);
        }
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        checkForOverlaps(subtask);
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            epic.updateTimesAndDuration(getSubtasksByEpic(epic));
        }
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
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public abstract void deleteAllSubtasks();

    public abstract void deleteAllEpics();
}
