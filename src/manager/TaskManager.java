package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.Status;

import java.util.*;

public class TaskManager {
    // ��������� ��� �������� ���� �����
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId = 1; // ������� ��� ��������� ���������� ID

    // ����� ��� ��������� ������ ID
    private int generateId() {
        return nextId++;
    }

    // ������� ������

    // ������� ����� ������� ������
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    // �������� ������ �� ID
    public Task getTask(int id) {
        return tasks.get(id);
    }

    // �������� ��� ������
    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    // ������� ������ �� ID
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    // ������� ��� ������
    public void deleteAllTasks() {
        tasks.clear();
    }

    // �����

    // ������� ����� ����
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        tasks.put(epic.getId(), epic);
        return epic;
    }

    // �������� ��� �����
    public Collection<Epic> getAllEpics() {
        return epics.values();
    }

    // ������� ���� �� ID (������ � ��� �����������)
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

    // ���������

    // ������� ����� ���������
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        tasks.put(subtask.getId(), subtask);

        // ����������� ��������� � �����
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic); // ��������� ������ �����
        }

        return subtask;
    }

    // �������� ��� ���������
    public Collection<Subtask> getAllSubtasks() {
        return subtasks.values();
    }

    // �������� ��������� ����������� �����
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (int id : epic.getSubtaskIds()) {
            result.add(subtasks.get(id));
        }
        return result;
    }

    // �������� ������ ����� �� ������ ��������
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