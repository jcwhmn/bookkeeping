package com.bookkeeping.core.insights;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsightsExplorerService {

    private final InsightsExplorerRepository explorerRepository;
    private final SecurityUtils securityUtils;

    public InsightsExplorerService(InsightsExplorerRepository explorerRepository, SecurityUtils securityUtils) {
        this.explorerRepository = explorerRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<InsightsExplorerDto> listExplorers() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return explorerRepository.findByUserIdOrderByDisplayOrderAsc(userId).stream()
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public InsightsExplorerDto getExplorer(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return explorerRepository.findByIdAndUserId(id, userId)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Explorer not found"));
    }

    @Transactional
    public InsightsExplorerDto createExplorer(CreateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        InsightsExplorer explorer = InsightsExplorer.builder()
                .userId(userId)
                .name(request.name())
                .data(request.data())
                .displayOrder(0)
                .hidden(false)
                .build();
        return toDto(explorerRepository.save(explorer));
    }

    @Transactional
    public InsightsExplorerDto modifyExplorer(ModifyRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        InsightsExplorer explorer = explorerRepository.findByIdAndUserId(request.id(), userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Explorer not found"));
        InsightsExplorer updated = explorer.toBuilder()
                .name(request.name())
                .data(request.data())
                .build();
        return toDto(explorerRepository.save(updated));
    }

    @Transactional
    public void hideExplorer(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        InsightsExplorer explorer = explorerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Explorer not found"));
        explorerRepository.save(explorer.toBuilder().hidden(hidden).build());
    }

    @Transactional
    public void reorderExplorers(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            final int order = i;
            explorerRepository.findByIdAndUserId(orderedIds.get(i), userId)
                    .ifPresent(e -> explorerRepository.save(e.toBuilder().displayOrder(order).build()));
        }
    }

    @Transactional
    public void deleteExplorer(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        InsightsExplorer explorer = explorerRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Explorer not found"));
        explorerRepository.delete(explorer);
    }

    private InsightsExplorerDto toDto(InsightsExplorer e) {
        return new InsightsExplorerDto(e.getId(), e.getName(), e.getData(), e.getDisplayOrder(), e.getHidden(), e.getCreatedAt());
    }

    public record InsightsExplorerDto(Long id, String name, String data, Integer displayOrder, Boolean hidden, Long createdAt) {}
    public record CreateRequest(String name, String data) {}
    public record ModifyRequest(Long id, String name, String data) {}
}