package resource;

public class SubTask extends Task {

    private int epicId;

    public SubTask(Epic epic, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epic.getId();
    }

    public SubTask(int id, int epicId, String name, String description, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "resource.SubTask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name=" + getName() +
                ", status=" + getStatus() +
                '}';
    }

    @Override
    public String serializeToCsv() {
        return String.format("%s,%s,%s,%s,%s,%s\n", getId(), TaskType.SUBTASK, getName(), getStatus(), getDescription(), getIdEpic());
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getIdEpic() {
        return epicId;
    }
}