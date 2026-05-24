package com.bookkeeping.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Account entity.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserIdAndDeletedFalseOrderBySortOrderAsc(Long userId);

    List<Account> findByUserIdAndDeletedFalseAndHiddenFalseOrderBySortOrderAsc(Long userId);

    List<Account> findByUserIdAndDeletedFalseAndParentIdIsNullOrderBySortOrderAsc(Long userId);

    Optional<Account> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long userId);

    @Modifying
    @Query("UPDATE Account a SET a.sortOrder = :sortOrder WHERE a.id = :id AND a.userId = :userId")
    int updateSortOrder(@Param("id") Long id, @Param("userId") Long userId, @Param("sortOrder") Integer sortOrder);

    @Modifying
    @Query("UPDATE Account a SET a.hidden = :hidden WHERE a.id = :id AND a.userId = :userId")
    int updateHidden(@Param("id") Long id, @Param("userId") Long userId, @Param("hidden") Boolean hidden);

    long countByUserId(Long userId);
}