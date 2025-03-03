package resource;

import manager.TaskSerializer;
import util.DataTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicId;

    public SubTask(Epic epic, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epic.getId();
    }

    public SubTask(int id, int epicId, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "resource.SubTask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name=" + getName() +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(DataTimeFormat.getDataTimeFormat()) +
                ", endTime=" + getEndTime().format(DataTimeFormat.getDataTimeFormat()) +
                '}';
    }

    @Override
    public String serializeToCsv() {
        return TaskSerializer.serializeSubTask(this);
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getIdEpic() {
        return epicId;
    }
}