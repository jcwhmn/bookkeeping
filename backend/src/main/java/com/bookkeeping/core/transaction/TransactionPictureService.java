package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionPictureService {

    private static final String UPLOAD_DIR = System.getenv("PICTURE_UPLOAD_DIR") != null
            ? System.getenv("PICTURE_UPLOAD_DIR")
            : "/tmp/bookkeeping-pictures";

    private final TransactionPictureRepository pictureRepository;
    private final TransactionRepository transactionRepository;
    private final SecurityUtils securityUtils;

    public TransactionPictureService(TransactionPictureRepository pictureRepository,
                                      TransactionRepository transactionRepository,
                                      SecurityUtils securityUtils) {
        this.pictureRepository = pictureRepository;
        this.transactionRepository = transactionRepository;
        this.securityUtils = securityUtils;
        initStorageDir();
    }

    private void initStorageDir() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            // Ignore - will fail at upload time if directory doesn't exist
        }
    }

    /**
     * Upload a picture and attach to a transaction.
     */
    @Transactional
    public TransactionPictureDto uploadPicture(Long transactionId, String fileName, byte[] data, String mimeType) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Verify transaction belongs to user
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.TRANSACTION_NOT_FOUND, "Transaction not found"));

        // Validate file size (max 5MB)
        if (data.length > 5 * 1024 * 1024) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "File size exceeds 5MB limit");
        }

        // Save file
        String ext = getExtension(fileName);
        String storedName = UUID.randomUUID() + ext;
        Path filePath = Paths.get(UPLOAD_DIR, String.valueOf(userId), storedName);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, data);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "Failed to save picture");
        }

        // Save record
        TransactionPicture picture = TransactionPicture.builder()
                .userId(userId)
                .transactionId(transactionId)
                .fileName(fileName)
                .filePath(filePath.toString())
                .fileSize((long) data.length)
                .mimeType(mimeType)
                .build();

        picture = pictureRepository.save(picture);
        return toDto(picture);
    }

    /**
     * List pictures for a transaction.
     */
    @Transactional(readOnly = true)
    public List<TransactionPictureDto> listByTransaction(Long transactionId) {
        Long userId = securityUtils.requireCurrentUser().getId();

        // Verify transaction belongs to user
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.TRANSACTION_NOT_FOUND, "Transaction not found"));

        return pictureRepository.findByTransactionIdAndDeletedFalse(transactionId)
                .stream().map(this::toDto).toList();
    }

    /**
     * Delete a picture (soft delete).
     */
    @Transactional
    public void deletePicture(Long pictureId) {
        Long userId = securityUtils.requireCurrentUser().getId();

        TransactionPicture picture = pictureRepository.findByIdAndUserIdAndDeletedFalse(pictureId, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Picture not found"));

        // Soft delete
        picture.setDeleted(true);
        picture.setDeletedAt(System.currentTimeMillis() / 1000);
        pictureRepository.save(picture);

        // Optionally delete file
        try {
            Files.deleteIfExists(Paths.get(picture.getFilePath()));
        } catch (IOException ignored) {}
    }

    /**
     * Get picture file for serving.
     */
    @Transactional(readOnly = true)
    public Optional<TransactionPicture> getFile(Long pictureId) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return pictureRepository.findByIdAndUserIdAndDeletedFalse(pictureId, userId)
                .filter(p -> Files.exists(Paths.get(p.getFilePath())));
    }

    /**
     * Get all pictures for a user (for data export).
     */
    @Transactional(readOnly = true)
    public List<TransactionPictureDto> listByUser(Long userId) {
        return pictureRepository.findByUserIdAndDeletedFalse(userId)
                .stream().map(this::toDto).toList();
    }

    /**
     * Cleanup unused pictures (no transaction reference).
     */
    @Transactional
    public long cleanupUnused() {
        Long userId = securityUtils.requireCurrentUser().getId();
        List<TransactionPicture> all = pictureRepository.findByUserIdAndDeletedFalse(userId);
        long count = 0;
        Long now = System.currentTimeMillis() / 1000;

        for (TransactionPicture pic : all) {
            boolean hasTx = transactionRepository.existsById(pic.getTransactionId());
            if (!hasTx) {
                pic.setDeleted(true);
                pic.setDeletedAt(now);
                pictureRepository.save(pic);
                try {
                    Files.deleteIfExists(Paths.get(pic.getFilePath()));
                } catch (IOException ignored) {}
                count++;
            }
        }
        return count;
    }

    private TransactionPictureDto toDto(TransactionPicture pic) {
        return new TransactionPictureDto(
                pic.getId(),
                pic.getTransactionId(),
                pic.getFileName(),
                pic.getFileSize(),
                pic.getMimeType(),
                pic.getCreatedAt()
        );
    }

    private String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return i > 0 ? fileName.substring(i) : "";
    }
}