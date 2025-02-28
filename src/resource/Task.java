package resource;

import util.DataTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(
            String name,
            String description,
            Status status,
            Duration duration,
            LocalDateTime startTime
    ) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public Task(
            int id,
            String taskName,
            String description,
            Status status,
            Duration duration,
            LocalDateTime startTime
    ) {
        this.id = id;
        this.name = taskName;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public String serializeToCsv() {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s\n",
                id,
                TaskType.TASK,
                name,
                status,
                description,
                duration.toMinutes(),
                startTime.format(DataTimeFormat.getDataTimeFormat()),
                endTime.format(DataTimeFormat.getDataTimeFormat()));
    }

    @Override
    public String toString() {
        return "resource.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(DataTimeFormat.getDataTimeFormat()) +
                ", endTime=" + endTime.format(DataTimeFormat.getDataTimeFormat()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) {
            return false;
        }
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}