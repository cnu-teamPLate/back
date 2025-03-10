package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findTasksByProjIdAndId(String projId, String id);
    List<Task> findTasksById(String id);
    List<Task> findTasksByProjId(String projId);
    Task findTaskByTaskId(int taskId);
    boolean existsByTaskId(int taskId);
}
