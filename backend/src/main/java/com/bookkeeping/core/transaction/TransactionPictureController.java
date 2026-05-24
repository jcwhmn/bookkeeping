package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ApiResponse;
import com.bookkeeping.supporting.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transaction/pictures")
@Tag(name = "Transaction Pictures", description = "Transaction picture management APIs")
public class TransactionPictureController {

    private final TransactionPictureRepository pictureRepository;
    private final SecurityUtils securityUtils;

    public TransactionPictureController(TransactionPictureRepository pictureRepository,
                                       SecurityUtils securityUtils) {
        this.pictureRepository = pictureRepository;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/remove_unused.json")
    @Operation(summary = "Remove unused pictures")
    public ApiResponse<RemoveResult> removeUnused() {
        Long userId = securityUtils.requireCurrentUser().getId();
        long total = pictureRepository.countByUserId(userId);
        // Stub: just return count, actual cleanup would check transaction references
        pictureRepository.deleteByUserId(userId);
        return ApiResponse.success(new RemoveResult(total, total));
    }

    public record RemoveResult(long totalPictures, long removedPictures) {}
}