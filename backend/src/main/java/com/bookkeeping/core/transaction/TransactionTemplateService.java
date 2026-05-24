package com.bookkeeping.core.transaction;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionTemplateService {

    private final TransactionTemplateRepository templateRepository;
    private final TransactionTemplateMapper templateMapper;
    private final SecurityUtils securityUtils;

    public TransactionTemplateService(TransactionTemplateRepository templateRepository,
                                      TransactionTemplateMapper templateMapper,
                                      SecurityUtils securityUtils) {
        this.templateRepository = templateRepository;
        this.templateMapper = templateMapper;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<TransactionTemplateDto> listTemplates(Integer templateType) {
        Long userId = securityUtils.requireCurrentUser().getId();
        if (templateType != null) {
            return templateRepository.findByUserIdAndTemplateTypeOrderByDisplayOrderAsc(userId, templateType)
                    .stream().map(templateMapper::toDto).toList();
        }
        return templateRepository.findByUserIdOrderByDisplayOrderAsc(userId)
                .stream().map(templateMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public TransactionTemplateDto getTemplate(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        return templateRepository.findByIdAndUserId(id, userId)
                .map(templateMapper::toDto)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Template not found"));
    }

    @Transactional
    public TransactionTemplateDto createTemplate(TransactionTemplateCreateRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TransactionTemplate template = TransactionTemplate.builder()
                .userId(userId)
                .templateType(request.templateType() != null ? request.templateType() : 1)
                .name(request.name())
                .transactionType(request.transactionType())
                .categoryId(request.categoryId())
                .sourceAccountId(request.sourceAccountId())
                .destinationAccountId(request.destinationAccountId())
                .sourceAmount(request.sourceAmount())
                .destinationAmount(request.destinationAmount())
                .hideAmount(request.hideAmount())
                .description(request.description())
                .tagIds(request.tagIds())
                .displayOrder(request.displayOrder() != null ? request.displayOrder() : 0)
                .hidden(false)
                .build();
        return templateMapper.toDto(templateRepository.save(template));
    }

    @Transactional
    public TransactionTemplateDto modifyTemplate(Long id, TransactionTemplateModifyRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TransactionTemplate template = templateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Template not found"));
        TransactionTemplate updated = template.toBuilder()
                .name(request.name())
                .templateType(request.templateType())
                .transactionType(request.transactionType())
                .categoryId(request.categoryId())
                .sourceAccountId(request.sourceAccountId())
                .destinationAccountId(request.destinationAccountId())
                .sourceAmount(request.sourceAmount())
                .destinationAmount(request.destinationAmount())
                .hideAmount(request.hideAmount())
                .description(request.description())
                .tagIds(request.tagIds())
                .displayOrder(request.displayOrder())
                .build();
        return templateMapper.toDto(templateRepository.save(updated));
    }

    @Transactional
    public void hideTemplate(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TransactionTemplate template = templateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Template not found"));
        templateRepository.save(template.toBuilder().hidden(hidden).build());
    }

    @Transactional
    public void reorderTemplates(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            final int order = i;
            templateRepository.findByIdAndUserId(orderedIds.get(i), userId)
                    .ifPresent(t -> templateRepository.save(t.toBuilder().displayOrder(order).build()));
        }
    }

    @Transactional
    public void deleteTemplate(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TransactionTemplate template = templateRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Template not found"));
        templateRepository.delete(template);
    }

    public record TransactionTemplateCreateRequest(
            Integer templateType, String name, Integer transactionType, Long categoryId,
            Long sourceAccountId, Long destinationAccountId, Long sourceAmount, Long destinationAmount,
            Boolean hideAmount, String description, String tagIds, Integer displayOrder
    ) {}

    public record TransactionTemplateModifyRequest(
            Long id, Integer templateType, String name, Integer transactionType, Long categoryId,
            Long sourceAccountId, Long destinationAccountId, Long sourceAmount, Long destinationAmount,
            Boolean hideAmount, String description, String tagIds, Integer displayOrder
    ) {}
}