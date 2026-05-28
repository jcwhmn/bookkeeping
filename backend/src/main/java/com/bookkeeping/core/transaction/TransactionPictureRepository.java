package com.bookkeeping.core.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionPictureRepository extends JpaRepository<TransactionPicture, Long> {

    List<TransactionPicture> findByTransactionIdAndDeletedFalse(Long transactionId);

    List<TransactionPicture> findByUserIdAndDeletedFalse(Long userId);

    long countByUserId(Long userId);

    long countByTransactionId(Long transactionId);

    @Modifying
    @Query("UPDATE TransactionPicture p SET p.deleted = true, p.deletedAt = :now WHERE p.transactionId = :transactionId")
    void softDeleteByTransactionId(@Param("transactionId") Long transactionId, @Param("now") Long now);

    @Modifying
    @Query("UPDATE TransactionPicture p SET p.deleted = true, p.deletedAt = :now WHERE p.userId = :userId")
    void softDeleteByUserId(@Param("userId") Long userId, @Param("now") Long now);

    Optional<TransactionPicture> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    @Query("SELECT p FROM TransactionPicture p WHERE p.transactionId IN :transactionIds AND p.deleted = false")
    List<TransactionPicture> findByTransactionIds(@Param("transactionIds") List<Long> transactionIds);
}