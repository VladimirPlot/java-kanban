package manager;

import resource.Epic;
import resource.SubTask;
import resource.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    void createTask(Task task);

    Optional<Task> getTaskById(int id);

    List<Task> getAllTasks();

    Task updateTask(Task task);

    void removeTaskById(int id);

    void removeAllTasks();

    void createEpic(Epic epic);

    Optional<Epic> getEpicById(int id);

    List<Epic> getAllEpics();

    void updateEpic(Epic epic);

    void removeEpicById(int id);

    void removeAllEpics();

    void createSubTask(SubTask subTask);

    Optional<SubTask> getSubTaskById(int id);

    List<SubTask> getAllSubTasks();

    void updateSubTask(SubTask subTask);

    void removeSubTaskById(int id);

    void removeAllSubTasks();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}