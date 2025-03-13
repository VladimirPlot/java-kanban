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
    public Optional<Task> createTask(Task task) {
        task.setId(nextId());
        validateAndAddTask(task);
        taskMap.put(task.getId(), task);
        return Optional.of(task);
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
    public Optional<Task> updateTask(Task task) {
        if (!taskMap.containsKey(task.getId())) {
            return Optional.empty();
        }

        validateAndAddTask(task);
        prioritizedTasks.remove(task);
        taskMap.replace(task.getId(), task);
        prioritizedTasks.add(task);

        return Optional.of(taskMap.get(task.getId()));
    }

    @Override
    public Optional<Boolean> removeTaskById(int id) {
        if (taskMap.containsKey(id)) {
            prioritizedTasks.remove(taskMap.get(id));
            taskMap.remove(id);
            historyManager.remove(id);
            return Optional.of(true);
        } else {
            return Optional.of(false);
        }
    }

    @Override
    public Optional<Void> removeAllTasks() {
        taskMap.keySet().forEach(historyManager::remove);
        taskMap.values().forEach(prioritizedTasks::remove);
        taskMap.clear();
        return Optional.empty();
    }

    @Override
    public Optional<Epic> createEpic(Epic epic) {
        epic.setId(nextId());
        epicMap.put(epic.getId(), epic);
        return Optional.of(epic);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return Optional.of(epic);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicMap.values().stream().toList();
    }

    @Override
    public Optional<Epic> updateEpic(Epic epic) {
        if (!epicMap.containsKey(epic.getId())) {
            return Optional.empty();
        }

        epicMap.replace(epic.getId(), epic);
        return Optional.of(epic);
    }

    @Override
    public Optional<Boolean> removeEpicById(int id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            removeListSubTasks(epic.getSubTasksList());
            epicMap.remove(id);
            historyManager.remove(id);
            return Optional.of(true);
        } else {
            return Optional.of(false);
        }

    }

    @Override
    public Optional<Void> removeAllEpics() {
        epicMap.keySet().forEach(historyManager::remove);
        subTaskMap.values().forEach(prioritizedTasks::remove);
        subTaskMap.clear();
        epicMap.clear();
        return Optional.empty();
    }

    @Override
    public Optional<SubTask> createSubTask(SubTask subTask) {
        subTask.setId(nextId());
        validateAndAddTask(subTask);

        Epic epic = epicMap.get(subTask.getIdEpic());
        epic.addSubTaskId(subTask);

        subTaskMap.put(subTask.getId(), subTask);
        historyManager.add(subTask);
        updateEpicStatus(epic);
        updateEpicTime(epic);

        return Optional.of(subTask);
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        SubTask subTask = subTaskMap.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
            return Optional.of(subTask);
        }
        return Optional.empty();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return subTaskMap.values().stream().toList();
    }

    @Override
    public Optional<SubTask> updateSubTask(SubTask subTask) {
        if (!subTaskMap.containsKey(subTask.getId())) { // Если подзадача не найдена
            return Optional.empty();
        }

        validateAndAddTask(subTask);
        prioritizedTasks.remove(subTask);
        subTaskMap.replace(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);

        updateEpicStatus(epicMap.get(subTask.getIdEpic()));
        updateEpicTime(epicMap.get(subTask.getIdEpic()));

        return Optional.of(subTask);
    }

    @Override
    public Optional<Boolean> removeSubTaskById(int id) {
        if (subTaskMap.containsKey(id)) {
            Epic epic = epicMap.get(subTaskMap.get(id).getIdEpic());
            epic.removeSubTaskInList(id);
            prioritizedTasks.remove(subTaskMap.get(id));
            subTaskMap.remove(id);

            updateEpicStatus(epic);
            updateEpicTime(epic);
            historyManager.remove(id);

            return Optional.of(true);
        } else {
            return Optional.of(false);
        }
    }

    @Override
    public Optional<Void> removeAllSubTasks() {
        subTaskMap.keySet().forEach(historyManager::remove);

        clearEpicSubTasks();
        subTaskMap.clear();
        epicMap.values().forEach(epic -> {
            updateEpicStatus(epic);
            epic.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setDuration(Duration.ofMinutes(0));
            prioritizedTasks.remove(epic);
        });
        return Optional.empty();
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

    private void validateAndAddTask(Task task) {
        validateTask(task);
        prioritizedTasks.add(task);
    }

    private void validateTask(Task task) {
        List<Integer> collected = prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> isIntersected(t.getStartTime(), t.getEndTime(), task.getStartTime(), task.getEndTime()))
                .map(Task::getId)
                .toList();

        if (!collected.isEmpty()) {
            throw new InvalidTaskTimeException("Задача с id=" + task.getId() + " пересекается с задачами id=" + collected);
        }
    }

    private boolean isIntersected(LocalDateTime x1, LocalDateTime x2, LocalDateTime y1, LocalDateTime y2) {
        return !x1.isAfter(y2) && !y1.isAfter(x2);
    }

    private LocalDateTime getMinimalDateTime(Epic epic) {
        return epic.getSubTasksList().stream()
                .map(subTaskMap::get)
                .filter(Objects::nonNull)
                .map(Task::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private long calculateEpicDuration(List<Integer> subTaskIds) {
        return subTaskIds.stream()
                .map(subTaskMap::get)
                .map(subTask -> subTask.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    private void updateEpicTime(Epic epic) {
        LocalDateTime localDateTime = getMinimalDateTime(epic);

        if (localDateTime == null) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        long duration = calculateEpicDuration(epic.getSubTasksList());
        epic.setStartTime(localDateTime);
        epic.setDuration(Duration.ofMinutes(duration));
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
    }
}