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

        // Если задача уже есть — удаляем её из истории
        if (nodeIdMap.containsKey(task.getId())) {
            removeNode(nodeIdMap.get(task.getId()));
        }

        // Добавляем новую в конец
        Node newNode = new Node(task);
        linkLast(newNode);
        nodeIdMap.put(task.getId(), newNode);
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
            tail = node;
        }
    }

    private void unlink(Node node) {
        Node prev = node.getPrev();
        Node next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        } else {
            head = next; // node был головой
        }

        if (next != null) {
            next.setPrev(prev);
        } else {
            tail = prev; // node был хвостом
        }

        node.setPrev(null);
        node.setNext(null);
        nodeIdMap.remove(node.getTask().getId());
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
            history.add(current.getTask());
            current = current.getNext();
        }
        return history;
    }
}