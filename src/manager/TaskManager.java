package manager;

import resource.Epic;
import resource.SubTask;
import resource.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    Optional<Task> createTask(Task task);

    Optional<Task> getTaskById(int id);

    List<Task> getAllTasks();

    Optional<Task> updateTask(Task task);

    Optional<Boolean> removeTaskById(int id);

    Optional<Void> removeAllTasks();

    Optional<Epic> createEpic(Epic epic);

    Optional<Epic> getEpicById(int id);

    List<Epic> getAllEpics();

    Optional<Epic> updateEpic(Epic epic);

    Optional<Boolean> removeEpicById(int id);

    Optional<Void> removeAllEpics();

    Optional<SubTask> createSubTask(SubTask subTask);

    Optional<SubTask> getSubTaskById(int id);

    List<SubTask> getAllSubTasks();

    Optional<SubTask> updateSubTask(SubTask subTask);

    Optional<Boolean> removeSubTaskById(int id);

    Optional<Void> removeAllSubTasks();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}