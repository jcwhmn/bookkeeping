package com.bookkeeping.core.tag;

import com.bookkeeping.common.ResultCode;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.supporting.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final SecurityUtils securityUtils;
    private final TagMapper tagMapper;

    public TagService(TagRepository tagRepository, SecurityUtils securityUtils, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.securityUtils = securityUtils;
        this.tagMapper = tagMapper;
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return tagRepository.findByUserIdAndDeletedFalseOrderByNameAsc(userId)
                .stream().map(tagMapper::toDto).toList();
    }

    @Transactional
    public TagDto createTag(CreateTagRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Long now = System.currentTimeMillis() / 1000;

        // Check for duplicate name
        if (tagRepository.existsByUserIdAndNameAndDeletedFalse(userId, request.name())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Tag with this name already exists");
        }

        Tag tag = Tag.builder()
                .userId(userId)
                .name(request.name())
                .color(request.color() != null ? request.color() : "#1976D2")
                .createdTime(now)
                .build();

        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Transactional
    public TagDto updateTag(Long id, UpdateTagRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Tag tag = tagRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Tag not found"));

        Tag.TagBuilder builder = tag.toBuilder();

        // Check for duplicate name (excluding current tag)
        if (request.name() != null && !request.name().equals(tag.getName())) {
            if (tagRepository.existsByUserIdAndNameAndDeletedFalse(userId, request.name())) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "Tag with this name already exists");
            }
            builder.name(request.name());
        }

        if (request.color() != null) {
            builder.color(request.color());
        }
        builder.updatedTime(System.currentTimeMillis() / 1000);

        return tagMapper.toDto(tagRepository.save(builder.build()));
    }

    @Transactional
    public void deleteTag(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Tag tag = tagRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Tag not found"));

        tagRepository.save(tag.toBuilder()
                .deleted(true)
                .updatedTime(System.currentTimeMillis() / 1000)
                .build());
    }

}