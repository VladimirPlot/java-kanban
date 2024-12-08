public class SubTask extends Task {

    private int epicId;

    public SubTask(Epic epic, String name, String description, Status status) {
        super(name, description, status);
        this.epicId = epic.getId();
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() + '\'' +
                ", idEpic=" + epicId +
                '}';
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getIdEpic () {
        return epicId;
    }
}