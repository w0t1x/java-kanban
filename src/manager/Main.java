package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // 1. ������� ������� ������

        Task task1 = new Task("������ ����", "������ �����������");
        Task task2 = new Task("��������� �����", "���������� �� �����");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // 2. ������� ���� � ����� �����������

        Epic epic1 = new Epic("�������", "������� ����, ����� ���������");
        taskManager.createEpic(epic1);

        Subtask sub1 = new Subtask("��������� �����", "� �������", Status.NEW, epic1.getId());
        Subtask sub2 = new Subtask("����� ���������", "�������� �����", Status.NEW, epic1.getId());

        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);

        // 3. ������� ��

        System.out.println("��� ������:");
        taskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\n��� �����:");
        taskManager.getAllEpics().forEach(System.out::println);

        System.out.println("\n��������� ������� �����:");
        taskManager.getSubtasksByEpic(epic1).forEach(System.out::println);

        // 4. ��������� ������� ��������

        sub1.setStatus(Status.DONE);
        taskManager.createSubtask(sub1);

        sub2.setStatus(Status.DONE);
        taskManager.createSubtask(sub2);

        System.out.println("\n������ ����� ����� ���������� ��������:");
        System.out.println(taskManager.getTask(epic1.getId()));

        // 5. ������� ����
        taskManager.deleteTask(task1.getId());
        taskManager.deleteEpic(epic1.getId());

        System.out.println("���������� ������:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }

        // 7. �������� ��� ���������

        System.out.println(taskManager.getAllSubtasks());

        // 6. ������ ��������

        System.out.println("������� ��� ������:");
        taskManager.deleteAllTasks();
    }
}