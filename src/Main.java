import manager.*;
import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task t1 = new Task("Купить хлеб", "Свежий бородинский");
        Task t2 = new Task("Позвонить врачу", "Записаться на приём");

        taskManager.createTask(t1);
        taskManager.createTask(t2);

        Epic epic = new Epic("Переезд", "Собрать вещи");
        taskManager.createEpic(epic);

        Subtask s1 = new Subtask("Упаковать книги", "В коробки", Status.NEW, epic.getId());
        Subtask s2 = new Subtask("Найти грузчиков", "Оформить заказ", Status.NEW, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);

        taskManager.getTask(t1.getId());
        taskManager.getTask(t2.getId());
        taskManager.getTask(t1.getId());

        System.out.println("История после просмотров:");
        taskManager.getHistory().forEach(System.out::println);

        taskManager.deleteTask(t1.getId());

        System.out.println("\nИстория после удаления t1:");
        taskManager.getHistory().forEach(System.out::println);

        taskManager.deleteEpic(epic.getId());

        System.out.println("\nИстория после удаления эпика:");
        taskManager.getHistory().forEach(System.out::println);
    }
}