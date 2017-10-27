package net.jakubpas.log_parser.repository;

import net.jakubpas.log_parser.model.entity.CommentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLogRepository extends JpaRepository<CommentLog, Long> {
}