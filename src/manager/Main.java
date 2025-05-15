package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // 1. Создаем обычные задачи

        Task task1 = new Task("Купить хлеб", "Свежий бородинский");
        Task task2 = new Task("Позвонить врачу", "Записаться на прием");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // 2. Создаем эпик с двумя подзадачами

        Epic epic1 = new Epic("Переезд", "Собрать вещи, найти грузчиков");
        taskManager.createEpic(epic1);

        Subtask sub1 = new Subtask("Упаковать книги", "В коробки", Status.NEW, epic1.getId());
        Subtask sub2 = new Subtask("Найти грузчиков", "Оформить заказ", Status.NEW, epic1.getId());

        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);

        // 3. Выводим всё

        System.out.println("Все задачи:");
        taskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        taskManager.getAllEpics().forEach(System.out::println);

        System.out.println("\nПодзадачи первого эпика:");
        taskManager.getSubtasksByEpic(epic1).forEach(System.out::println);

        // 4. Обновляем статусы подзадач

        sub1.setStatus(Status.DONE);
        taskManager.createSubtask(sub1);

        sub2.setStatus(Status.DONE);
        taskManager.createSubtask(sub2);

        System.out.println("\nСтатус эпика после завершения подзадач:");
        System.out.println(taskManager.getTask(epic1.getId()));

        // 5. Удаляем эпик
        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("Оставшиеся задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // 7. Получить все подзадачи

        System.out.println(taskManager.getAllSubtasks());

        // 6. Полное удаление

        System.out.println("Удаляем все задачи:");
        taskManager.deleteAllTasks();
    }
}