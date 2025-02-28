package manager;

import exception.InvalidTaskTimeException;
import resource.Epic;
import resource.Status;
import resource.SubTask;
import resource.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int id = 1;

    @Override
    public void createTask(Task task) {
        task.setId(nextId());

        try {
            validateTask(task);
        } catch (InvalidTaskTimeException e) {
            System.err.println(e.getMessage());
        }

        taskMap.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = taskMap.get(id);
        historyManager.add(task);
        return Optional.of(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskMap.values().stream().toList();
    }

    @Override
    public Task updateTask(Task task) {
        try {
            validateTask(task);
        } catch (InvalidTaskTimeException e) {
            System.err.println(e.getMessage());
        }

        if (taskMap.containsKey(task.getId())) {
            taskMap.replace(task.getId(), task);
        }
        return taskMap.get(task.getId());
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(taskMap.get(id));
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeAllTasks() {
        taskMap.values().forEach(prioritizedTasks::remove);
        taskMap.clear();
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(nextId());
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epicMap.get(id);
        historyManager.add(epic);
        return Optional.of(epic);
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
        epicMap.values().forEach(prioritizedTasks::remove);
        subTaskMap.clear();
        epicMap.clear();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(nextId());

        try {
            validateTask(subTask);
        } catch (InvalidTaskTimeException e) {
            System.err.println(e.getMessage());
        }

        Epic epic = epicMap.get(subTask.getIdEpic());
        epic.addSubTaskId(subTask);

        subTaskMap.put(subTask.getId(), subTask);
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
        updateEpicTime(epic);

        prioritizedTasks.add(subTask);
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        SubTask subTask = subTaskMap.get(id);
        historyManager.add(subTask);
        return Optional.of(subTask);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        try {
            validateTask(subTask);
        } catch (InvalidTaskTimeException e) {
            System.err.println(e.getMessage());
        }

        if (subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.replace(subTask.getId(), subTask);
        }
        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
        updateEpicTime(epicMap.get(subTask.getIdEpic()));
        prioritizedTasks.add(subTask);
    }

    @Override
    public void removeSubTaskById(int id) {
        Epic epic = epicMap.get(subTaskMap.get(id).getIdEpic());
        epic.removeSubTaskInList(id);
        prioritizedTasks.remove(subTaskMap.get(id));
        subTaskMap.remove(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.remove(id);
    }

    @Override
    public void removeAllSubTasks() {
        clearEpicSubTasks();
        subTaskMap.clear();
        epicMap.values().forEach(epic -> {
            updateEpicStatus(epic);
            epic.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setDuration(Duration.ofMinutes(0));
            prioritizedTasks.remove(epic);
        });
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
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
        }
    }

    private List<SubTask> getListSubTasksByEpicId(List<Integer> subTaskIds) {
        return subTaskIds.stream().map(subTaskMap::get).toList();
    }

    private void clearEpicSubTasks() {
        epicMap.values().forEach(Epic::clearSubTasksList);
    }

    private void validateTask(Task task) throws InvalidTaskTimeException {
        List<Integer> collected = prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> ((t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getStartTime())))) ||
                        (t.getStartTime().isBefore(task.getEndTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isAfter(task.getStartTime()) && (t.getEndTime().isBefore(task.getEndTime()))) ||
                        (t.getStartTime().equals(task.getStartTime())))
                .map(Task::getId)
                .toList();

        if (!collected.isEmpty()) {
            throw new InvalidTaskTimeException("Задача с id=" + task.getId() + " пересекается с задачами id=" + collected);
        }
    }

    private LocalDateTime getMinimalDateTime(Epic epic) {
        return epic.getSubTasksList().stream()
                .map(subTaskMap::get)
                .map(Task::getStartTime)
                .sorted(Comparator.naturalOrder())
                .limit(1)
                .toList()
                .getFirst();
    }

    private long calculateEpicDuration(List<Integer> subTaskIds) {
        return subTaskIds.stream()
                .map(subTaskMap::get)
                .map(subTask -> subTask.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    private void updateEpicTime(Epic epic) {
        LocalDateTime localDateTime = getMinimalDateTime(epic);
        long duration = calculateEpicDuration(epic.getSubTasksList());
        epic.setStartTime(localDateTime);
        epic.setDuration(Duration.ofMinutes(duration));
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
    }
}