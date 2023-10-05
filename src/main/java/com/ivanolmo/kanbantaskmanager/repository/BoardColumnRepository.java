package com.ivanolmo.kanbantaskmanager.repository;

import com.ivanolmo.kanbantaskmanager.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, Long> {
}
