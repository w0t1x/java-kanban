package task;

public class Subtask extends Task {
    private int epicId;

    // ������������

    public Subtask(String name, String description, Status status, int epicId) {
        super(-1, name, description, status); // id ����� �������� �����
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    // ������

    public int getEpicId() {
        return epicId;
    }

    // ���������������

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                '}';
    }
}