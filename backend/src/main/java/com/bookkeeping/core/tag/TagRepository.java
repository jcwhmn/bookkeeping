package com.bookkeeping.core.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserIdAndDeletedFalseOrderByNameAsc(Long userId);
    Optional<Tag> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    boolean existsByUserIdAndNameAndDeletedFalse(Long userId, String name);
}