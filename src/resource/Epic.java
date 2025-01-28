package resource;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(int id, String taskName, String description, Status status) {
        super(id, taskName, description, status);
    }


    @Override
    public String toString() {
        return "resource.Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", subTasksIdList=" + getSubTasksList() +
                ", status=" + getStatus() +
                '}';
    }

    public void addSubTaskId(SubTask subTask) {
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