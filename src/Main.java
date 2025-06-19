import manager.*;
import task.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task t1 = new Task("������ ����", "������ �����������");
        Task t2 = new Task("��������� �����", "���������� �� ����");

        taskManager.createTask(t1);
        taskManager.createTask(t2);

        Epic epic = new Epic("�������", "������� ����");
        taskManager.createEpic(epic);

        Subtask s1 = new Subtask("��������� �����", "� �������", Status.NEW, epic.getId());
        Subtask s2 = new Subtask("����� ���������", "�������� �����", Status.NEW, epic.getId());
        taskManager.createSubtask(s1);
        taskManager.createSubtask(s2);

        taskManager.getTask(t1.getId());
        taskManager.getTask(t2.getId());
        taskManager.getTask(t1.getId());

        System.out.println("������� ����� ����������:");
        taskManager.getHistory().forEach(System.out::println);

        taskManager.deleteTask(t1.getId());

        System.out.println("\n������� ����� �������� t1:");
        taskManager.getHistory().forEach(System.out::println);

        taskManager.deleteEpic(epic.getId());

        System.out.println("\n������� ����� �������� �����:");
        taskManager.getHistory().forEach(System.out::println);
    }
}