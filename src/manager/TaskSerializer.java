package manager;

import util.DataTimeFormat;

import resource.Epic;
import resource.SubTask;
import resource.Task;
import resource.TaskType;

import java.time.format.DateTimeFormatter;

public class TaskSerializer {

    private static final DateTimeFormatter formatter = DataTimeFormat.getDataTimeFormat();

    public static String serializeTask(Task task) {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s\n",
                task.getId(),
                TaskType.TASK,
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getDuration().toMinutes(),
                task.getStartTime().format(formatter),
                task.getEndTime().format(formatter)
        );
    }

    public static String serializeEpic(Epic epic) {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                epic.getId(),
                TaskType.EPIC,
                epic.getName(),
                epic.getStatus(),
                epic.getDescription(),
                epic.getDuration().toMinutes(),
                epic.getStartTime().format(formatter),
                epic.getEndTime().format(formatter),
                epic.getSubTasksList()
        );
    }

    public static String serializeSubTask(SubTask subTask) {
        return String.format(
                "%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                subTask.getId(),
                TaskType.SUBTASK,
                subTask.getName(),
                subTask.getStatus(),
                subTask.getDescription(),
                subTask.getDuration().toMinutes(),
                subTask.getStartTime().format(formatter),
                subTask.getEndTime().format(formatter),
                subTask.getIdEpic()
        );
    }
}