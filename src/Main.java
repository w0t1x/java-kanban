import manager.*;
import task.*;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        File file = new File("tasks.csv");
        TaskManager manager = new FileBackedTaskManager(file);

        // Create tasks with duration and start time
        RealizationTask task1 = new RealizationTask("Купить хлеб", "Свежий бородинский");
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2023, 10, 26, 9, 0));
        manager.createTask(task1);

        RealizationTask task2 = new RealizationTask("Сходить в магазин", "Купить продукты");
        task2.setDuration(Duration.ofMinutes(90));
        task2.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        manager.createTask(task2);

        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        manager.createEpic(epic1);

        Subtask sub1 = new Subtask("Упаковать книги", "В коробки", Status.NEW, epic1.getId());
        sub1.setDuration(Duration.ofMinutes(120));
        sub1.setStartTime(LocalDateTime.of(2023, 10, 27, 14, 0));
        manager.createSubtask(sub1);

        Subtask sub2 = new Subtask("Упаковать одежду", "В чемоданы", Status.NEW, epic1.getId());
        sub2.setDuration(Duration.ofMinutes(180));
        sub2.setStartTime(LocalDateTime.of(2023, 10, 27, 16, 0));
        manager.createSubtask(sub2);

        System.out.println("Задачи до перезагрузки:");
        manager.getAllTasks().forEach(System.out::println);
        manager.getAllEpics().forEach(epic -> {
            System.out.println(epic);
            System.out.println("  Epic Start: " + epic.getStartTime());
            System.out.println("  Epic End: " + epic.getEndTime());
            System.out.println("  Epic Duration: " + epic.getDuration());
        });
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\nЗадачи по приоритету:");
        manager.getPrioritizedTasks().forEach(System.out::println);

        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\nЗадачи после загрузки из файла:");
        loadedManager.getAllTasks().forEach(System.out::println);
        loadedManager.getAllEpics().forEach(epic -> {
            System.out.println(epic);
            System.out.println("  Loaded Epic Start: " + epic.getStartTime());
            System.out.println("  Loaded Epic End: " + epic.getEndTime());
            System.out.println("  Loaded Epic Duration: " + epic.getDuration());
        });
        loadedManager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\nЗагруженные задачи по приоритету:");
        loadedManager.getPrioritizedTasks().forEach(System.out::println);
    }
}