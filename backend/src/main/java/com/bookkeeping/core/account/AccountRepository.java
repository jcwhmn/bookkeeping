package com.bookkeeping.core.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Account entity.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserIdAndDeletedFalse(Long userId);

    Optional<Account> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    boolean existsByNameAndUserIdAndDeletedFalse(String name, Long userId);
}
