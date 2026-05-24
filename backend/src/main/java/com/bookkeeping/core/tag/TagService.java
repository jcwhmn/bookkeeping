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
    private final TagGroupRepository tagGroupRepository;
    private final SecurityUtils securityUtils;
    private final TagMapper tagMapper;
    private final TagGroupMapper tagGroupMapper;

    public TagService(TagRepository tagRepository,
                     TagGroupRepository tagGroupRepository,
                     SecurityUtils securityUtils,
                     TagMapper tagMapper,
                     TagGroupMapper tagGroupMapper) {
        this.tagRepository = tagRepository;
        this.tagGroupRepository = tagGroupRepository;
        this.securityUtils = securityUtils;
        this.tagMapper = tagMapper;
        this.tagGroupMapper = tagGroupMapper;
    }

    @Transactional(readOnly = true)
    public List<TagDto> getAllTags() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return tagRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId)
                .stream().map(tagMapper::toDto).toList();
    }

    @Transactional
    public TagDto createTag(CreateTagRequest request) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Long now = System.currentTimeMillis() / 1000;

        if (tagRepository.existsByUserIdAndNameAndDeletedFalse(userId, request.name())) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "Tag with this name already exists");
        }

        Tag tag = Tag.builder()
                .userId(userId)
                .name(request.name())
                .color(request.color() != null ? request.color() : "#1976D2")
                .groupId(request.groupId())
                .sortOrder(0)
                .hidden(false)
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

        if (request.name() != null && !request.name().equals(tag.getName())) {
            if (tagRepository.existsByUserIdAndNameAndDeletedFalse(userId, request.name())) {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "Tag with this name already exists");
            }
            builder.name(request.name());
        }

        if (request.color() != null) {
            builder.color(request.color());
        }
        if (request.groupId() != null) {
            builder.groupId(request.groupId());
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

    @Transactional
    public void hideTag(Long id, boolean hidden) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Tag tag = tagRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Tag not found"));
        tagRepository.save(tag.toBuilder().hidden(hidden).build());
    }

    @Transactional
    public void reorderTags(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            tagRepository.updateSortOrder(orderedIds.get(i), userId, i);
        }
    }

    // === Tag Groups ===

    @Transactional(readOnly = true)
    public List<TagGroupDto> getAllTagGroups() {
        Long userId = securityUtils.requireCurrentUser().getId();
        return tagGroupRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId)
                .stream().map(tagGroupMapper::toDto).toList();
    }

    @Transactional
    public TagGroupDto createTagGroup(String name, String color) {
        Long userId = securityUtils.requireCurrentUser().getId();
        Long now = System.currentTimeMillis() / 1000;

        TagGroup group = TagGroup.builder()
                .userId(userId)
                .name(name)
                .color(color != null ? color : "#607D8B")
                .sortOrder(0)
                .createdTime(now)
                .build();

        return tagGroupMapper.toDto(tagGroupRepository.save(group));
    }

    @Transactional
    public TagGroupDto updateTagGroup(Long id, String name, String color) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TagGroup group = tagGroupRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Tag group not found"));

        TagGroup.TagGroupBuilder builder = group.toBuilder();
        if (name != null) builder.name(name);
        if (color != null) builder.color(color);
        builder.updatedTime(System.currentTimeMillis() / 1000);

        return tagGroupMapper.toDto(tagGroupRepository.save(builder.build()));
    }

    @Transactional
    public void deleteTagGroup(Long id) {
        Long userId = securityUtils.requireCurrentUser().getId();
        TagGroup group = tagGroupRepository.findByIdAndUserIdAndDeletedFalse(id, userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Tag group not found"));

        // Unassign all tags from this group
        List<Tag> tags = tagRepository.findByUserIdAndDeletedFalseOrderBySortOrderAsc(userId);
        for (Tag tag : tags) {
            if (id.equals(tag.getGroupId())) {
                tagRepository.save(tag.toBuilder().groupId(null).build());
            }
        }

        tagGroupRepository.save(group.toBuilder()
                .deleted(true)
                .updatedTime(System.currentTimeMillis() / 1000)
                .build());
    }

    @Transactional
    public void reorderTagGroups(List<Long> orderedIds) {
        Long userId = securityUtils.requireCurrentUser().getId();
        for (int i = 0; i < orderedIds.size(); i++) {
            tagGroupRepository.updateSortOrder(orderedIds.get(i), userId, i);
        }
    }
}