package manager;

import task.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodeIdMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) return;

        // Если задача уже есть — удаляем её из текущей позиции
        if (nodeIdMap.containsKey(task.getId())) {
            removeNode(nodeIdMap.get(task.getId()));
        }

        // Добавляем в конец
        Node newNode = new Node(task);
        linkLast(newNode);
        nodeIdMap.put(task.getId(), newNode);
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void unlink(Node node) {
        if (node == null) return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) {
            prev.next = next;
        } else {
            head = next; // node был первым
        }

        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev; // node был последним
        }

        node.prev = node.next = null;
        nodeIdMap.remove(node.task.getId());
    }

    private void removeNode(Node node) {
        if (node != null) {
            unlink(node);
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeIdMap.get(id);
        if (node != null) {
            unlink(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }
}