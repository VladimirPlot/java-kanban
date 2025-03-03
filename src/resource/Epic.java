package resource;

import manager.TaskSerializer;
import util.DataTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, Status.NEW, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public Epic(String name, String description, Duration taskDuration, LocalDateTime startTime) {
        super(name, description, Status.NEW, taskDuration, startTime);
    }

    public Epic(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, Status.NEW, duration, startTime);
    }


    @Override
    public String toString() {
        return "resource.Epic{" +
                "id=" + getId() +
                ", name=" + getName() +
                ", subTasksIdList=" + getSubTasksList() +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(DataTimeFormat.getDataTimeFormat()) +
                ", endTime=" + getEndTime().format(DataTimeFormat.getDataTimeFormat()) +
                '}';
    }

    @Override
    public String serializeToCsv() {
        return TaskSerializer.serializeEpic(this);
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