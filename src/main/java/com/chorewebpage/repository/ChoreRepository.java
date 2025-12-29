package com.chorewebpage.repository;

import com.chorewebpage.model.Chore;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoreRepository extends JpaRepository<Chore, Long> {
    List<Chore> findByNextDueDateLessThanEqual(LocalDateTime dueBefore);
}
