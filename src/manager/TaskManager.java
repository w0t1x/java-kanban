package manager;

import task.*;

import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task getTask(int id);

    Collection<Task> getAllTasks();

    void deleteTask(int id);

    void deleteAllTasks();

    Epic createEpic(Epic epic);

    Collection<Epic> getAllEpics();

    void deleteEpic(int id);

    Subtask createSubtask(Subtask subtask);

    Collection<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksByEpic(Epic epic);

    List<Task> getHistory();
}