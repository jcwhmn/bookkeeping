package com.bookkeeping.core.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPictureRepository extends JpaRepository<TransactionPicture, Long> {
    long countByUserId(Long userId);
    long countByTransactionId(Long transactionId);
    void deleteByTransactionId(Long transactionId);
    void deleteByUserId(Long userId);
}