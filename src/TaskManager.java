import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private int id;

    public void createTask(Task task) {
        task.setId(getNewId());
        taskMap.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(getNewId());
        epicMap.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        subTask.setId(getNewId());
        Epic epic = getEpicById(subTask.getIdEpic());
        epic.addSubTaskId(subTask);

        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
    }

    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    public Epic getEpicById(int id) {
        return epicMap.get(id);
    }

    public List<Epic> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    public SubTask getSubTaskById(int id) {
        return subTaskMap.get(id);
    }

    public List<SubTask> getAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    public void removeTaskById(int id) {
        taskMap.remove(id);
    }

    public void removeAllTasks() {
        taskMap.clear();
    }

    public void removeEpicById(int id) {
        Epic epic = epicMap.get(id);
        removeListSubTasks(epic.getSubTasksList());
        epicMap.remove(id);
    }

    public void removeAllEpics() {
        subTaskMap.clear();
        epicMap.clear();
    }

    public void removeSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getIdEpic());
        epic.removeSubTaskInList(id);
        subTaskMap.remove(id);
        updateEpicStatus(epic);
    }

    public void removeAllSubTasks() {
        clearEpicSubTasks();
        subTaskMap.clear();
        epicMap.values().forEach(this::updateEpicStatus);
    }

    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.replace(task.getId(), task);
        }
        return taskMap.get(task.getId());
    }

    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.replace(epic.getId(), epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.replace(subTask.getId(), subTask);
        }
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
    }

    private int getNewId() {
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
        for (Epic epic : epicMap.values()) {
            epic.clearSubTasksList();
        }
    }
}