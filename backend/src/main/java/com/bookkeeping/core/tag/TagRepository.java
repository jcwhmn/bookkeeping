package com.bookkeeping.core.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUserIdAndDeletedFalseOrderBySortOrderAsc(Long userId);
    Optional<Tag> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
    boolean existsByUserIdAndNameAndDeletedFalse(Long userId, String name);

    @Modifying
    @Query("UPDATE Tag t SET t.sortOrder = :sortOrder WHERE t.id = :id AND t.userId = :userId")
    int updateSortOrder(@Param("id") Long id, @Param("userId") Long userId, @Param("sortOrder") Integer sortOrder);

    @Modifying
    @Query("UPDATE Tag t SET t.hidden = :hidden WHERE t.id = :id AND t.userId = :userId")
    int updateHidden(@Param("id") Long id, @Param("userId") Long userId, @Param("hidden") Boolean hidden);

    List<Tag> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Tag t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}