package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    Collection<Task> getAllTasks();

    void deleteTask(int id);

    void deleteAllTasks();

    void updateTask(Task task);

    Epic createEpic(Epic epic);

    Collection<Epic> getAllEpics();

    void deleteEpic(int id);

    void updateEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Collection<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksByEpic(Epic epic);

    void deleteSubtask(int id);

    void updateSubtask(Subtask subtask);

    List<Task> getPrioritizedTasks(); // Новый метод

    List<Task> getHistory();
}