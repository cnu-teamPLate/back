package com.cnu.teamProj.teamProj.task.repository;

import com.cnu.teamProj.teamProj.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByProjIdAndId(String projId, String id);

    List<Task> findTasksByProjIdAndId(String projId, String id);
    List<Task> findTasksById(String id);
    List<Task> findTasksByProjId(String projId);
    Task findTaskByTaskId(int taskId);
    boolean existsByTaskId(int taskId);

}
