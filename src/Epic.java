import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public void addSubTaskId (SubTask subTask) {
        subTasks.add(subTask.getId());
    }

    public void removeSubTaskInList(int id) {
        subTasks.remove((Integer) id);
    }

    public void clearSubTasksList() {
        subTasks.clear();
    }

    public List<Integer> getSubTasksList() {
        return subTasks;
    }
}