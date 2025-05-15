package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.Status;

import java.util.*;

public class TaskManager {
    // Коллекции для хранения всех задач
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 1; // Счётчик для генерации уникальных ID

    // Метод для генерации нового ID
    private int generateId() {
        return nextId++;
    }

    // ОБЫЧНЫЕ ЗАДАЧИ

    // Создать новую простую задачу
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    // Получить задачу по ID
    public Task getTask(int id) {
        return tasks.get(id);
    }

    // Получить все задачи
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    // Удалить задачу по ID
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // Удалить все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    // ЭПИКИ

    // Создать новый эпик
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        tasks.put(epic.getId(), epic);
        return epic;
    }

    // Получить все эпики
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    // Удалить эпик по ID (вместе с его подзадачами)
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
                tasks.remove(subtaskId);
            }
        }
        tasks.remove(id);
    }

    // ПОДЗАДАЧИ

    // Создать новую подзадачу
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        tasks.put(subtask.getId(), subtask);

        // Привязываем подзадачу к эпику
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic); // Обновляем статус эпика
        }

        return subtask;
    }

    // Получить все подзадачи
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    // Получить подзадачи конкретного эпика
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (int id : epic.getSubtaskIds()) {
            result.add(subtasks.get(id));
        }
        return result;
    }

    // Обновить статус эпика на основе подзадач
    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();

        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyNew = false;

        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() == Status.NEW) {
                anyNew = true;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}