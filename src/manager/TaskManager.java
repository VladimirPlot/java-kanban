package manager;

import resource.Epic;
import resource.SubTask;
import resource.Task;

import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    Task getTaskById(int id);

    List<Task> getAllTasks();

    Epic getEpicById(int id);

    List<Epic> getAllEpics();

    SubTask getSubTaskById(int id);

    List<SubTask> getAllSubTasks();

    void removeTaskById(int id);

    void removeAllTasks();

    void removeEpicById(int id);

    void removeAllEpics();

    void removeSubTaskById(int id);

    void removeAllSubTasks();

    Task updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    List<Task> getHistory();
}
