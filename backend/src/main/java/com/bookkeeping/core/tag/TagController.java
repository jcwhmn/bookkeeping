package com.bookkeeping.core.tag;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction/tags")
@Tag(name = "Tags", description = "Tag management APIs")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/list.json")
    @Operation(summary = "Get all tags for current user")
    public ApiResponse<List<TagDto>> getAll() {
        return ApiResponse.success(tagService.getAllTags());
    }

    @PostMapping("/add.json")
    @Operation(summary = "Create tag")
    public ApiResponse<TagDto> create(@Valid @RequestBody CreateTagRequest request) {
        return ApiResponse.success(tagService.createTag(request));
    }

    @PostMapping("/modify.json")
    @Operation(summary = "Update tag")
    public ApiResponse<TagDto> update(@RequestBody UpdateTagRequest request) {
        return ApiResponse.success(tagService.updateTag(request.id(), request));
    }

    @PostMapping("/hide.json")
    @Operation(summary = "Hide/unhide tag")
    public ApiResponse<Void> hide(@RequestBody TagHideRequest request) {
        tagService.hideTag(request.id(), request.hidden());
        return ApiResponse.success(null);
    }

    @PostMapping("/move.json")
    @Operation(summary = "Reorder tags (drag-to-sort)")
    public ApiResponse<Void> reorder(@RequestBody TagReorderRequest request) {
        tagService.reorderTags(request.orderedIds());
        return ApiResponse.success(null);
    }

    @PostMapping("/delete.json")
    @Operation(summary = "Delete tag")
    public ApiResponse<Void> delete(@RequestBody TagDeleteRequest request) {
        tagService.deleteTag(request.id());
        return ApiResponse.success(null);
    }

    // === Tag Groups ===

    @GetMapping("/groups/list.json")
    @Operation(summary = "Get all tag groups")
    public ApiResponse<List<TagGroupDto>> getAllGroups() {
        return ApiResponse.success(tagService.getAllTagGroups());
    }

    @PostMapping("/groups/add.json")
    @Operation(summary = "Create tag group")
    public ApiResponse<TagGroupDto> createGroup(@RequestBody TagGroupCreateRequest request) {
        return ApiResponse.success(tagService.createTagGroup(request.name(), request.color()));
    }

    @PostMapping("/groups/modify.json")
    @Operation(summary = "Update tag group")
    public ApiResponse<TagGroupDto> updateGroup(@RequestBody TagGroupUpdateRequest request) {
        return ApiResponse.success(tagService.updateTagGroup(request.id(), request.name(), request.color()));
    }

    @PostMapping("/groups/delete.json")
    @Operation(summary = "Delete tag group")
    public ApiResponse<Void> deleteGroup(@RequestBody TagGroupDeleteRequest request) {
        tagService.deleteTagGroup(request.id());
        return ApiResponse.success(null);
    }

    @PostMapping("/groups/move.json")
    @Operation(summary = "Reorder tag groups (drag-to-sort)")
    public ApiResponse<Void> reorderGroups(@RequestBody TagGroupReorderRequest request) {
        tagService.reorderTagGroups(request.orderedIds());
        return ApiResponse.success(null);
    }

    // === Request DTOs ===

    public record TagHideRequest(long id, boolean hidden) {}
    public record TagReorderRequest(List<Long> orderedIds) {}
    public record TagDeleteRequest(long id) {}
    public record TagGroupCreateRequest(String name, String color) {}
    public record TagGroupUpdateRequest(long id, String name, String color) {}
    public record TagGroupDeleteRequest(long id) {}
    public record TagGroupReorderRequest(List<Long> orderedIds) {}
}