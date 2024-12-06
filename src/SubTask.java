public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, int id, int epicId) {
        super(name, description, id);
        this.epicId = epicId;
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

    public int getIdEpic () {
        return epicId;
    }
}