import manager.FileBackedTaskManager;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager manager = new FileBackedTaskManager(file);

        Task task1 = new Task("Купить хлеб", "Свежий бородинский");
        manager.createTask(task1);

        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        manager.createEpic(epic1);

        Subtask sub1 = new Subtask("Упаковать книги", "В коробки", Status.NEW, epic1.getId());
        manager.createSubtask(sub1);

        System.out.println("Задачи до перезагрузки:");
        manager.getAllTasks().forEach(System.out::println);
        manager.getAllEpics().forEach(System.out::println);
        manager.getAllSubtasks().forEach(System.out::println);

        // Пересоздаем менеджер из файла
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println("\nЗадачи после загрузки из файла:");
        loadedManager.getAllTasks().forEach(System.out::println);
        loadedManager.getAllEpics().forEach(System.out::println);
        loadedManager.getAllSubtasks().forEach(System.out::println);
    }
}