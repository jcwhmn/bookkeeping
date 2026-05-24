package com.bookkeeping.core.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagGroupRepository extends JpaRepository<TagGroup, Long> {
    List<TagGroup> findByUserIdAndDeletedFalseOrderBySortOrderAsc(Long userId);
    Optional<TagGroup> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    @Modifying
    @Query("UPDATE TagGroup g SET g.sortOrder = :sortOrder WHERE g.id = :id AND g.userId = :userId")
    int updateSortOrder(@Param("id") Long id, @Param("userId") Long userId, @Param("sortOrder") Integer sortOrder);
}