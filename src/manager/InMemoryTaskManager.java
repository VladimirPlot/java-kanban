package manager;

import resource.Epic;
import resource.Status;
import resource.SubTask;
import resource.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private int id = 1;

    @Override
    public void createTask(Task task) {
        task.setId(nextId());
        taskMap.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    @Override
    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.replace(task.getId(), task);
        }
        return taskMap.get(task.getId());
    }

    @Override
    public void removeTaskById(int id) {
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        for (Map.Entry<Integer, Task> set : taskMap.entrySet()) {
            historyManager.remove(set.getKey());
        }
        taskMap.clear();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(nextId());
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.replace(epic.getId(), epic);
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epicMap.get(id);
        removeListSubTasks(epic.getSubTasksList());
        epicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllEpics() {
        for (Map.Entry<Integer, SubTask> set : subTaskMap.entrySet()) {
            historyManager.remove(set.getKey());
        }
        for (Map.Entry<Integer, Epic> set : epicMap.entrySet()) {
            historyManager.remove(set.getKey());
        }
        subTaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(nextId());
        Epic epic = epicMap.get(subTask.getIdEpic());
        epic.addSubTaskId(subTask);

        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTaskMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.replace(subTask.getId(), subTask);
        }
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
    }

    @Override
    public void removeSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getIdEpic());
        epic.removeSubTaskInList(id);
        subTaskMap.remove(id);
        updateEpicStatus(epic);
        historyManager.remove(id);
    }

    @Override
    public void removeAllSubTasks() {
        for (Map.Entry<Integer, SubTask> set : subTaskMap.entrySet()) {
            historyManager.remove(set.getKey());
        }
        clearEpicSubTasks();
        subTaskMap.clear();
        epicMap.values().forEach(this::updateEpicStatus);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int nextId() {
        return id++;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubTasksList().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allTasksIsNew = true;
        boolean allTasksIsDone = true;

        List<SubTask> epicSubTasks = getListSubTasksByEpicId(epic.getSubTasksList());

        for (SubTask subTask : epicSubTasks) {
            if (subTask.getStatus() != Status.NEW) {
                allTasksIsNew = false;
            }

            if (subTask.getStatus() != Status.DONE) {
                allTasksIsDone = false;
            }
        }

        if (allTasksIsDone) {
            epic.setStatus(Status.DONE);
        } else if (allTasksIsNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void removeListSubTasks(List<Integer> subTaskIds) {
        for (Integer id : subTaskIds) {
            subTaskMap.remove(id);
            historyManager.remove(id);
        }
    }

    private List<SubTask> getListSubTasksByEpicId(List<Integer> subTaskIds) {
        List<SubTask> subTasks = new ArrayList<>();
        for (Integer id : subTaskIds) {
            subTasks.add(subTaskMap.get(id));
        }
        return subTasks;
    }

    private void clearEpicSubTasks() {
        epicMap.values().forEach(Epic::clearSubTasksList);
    }
}