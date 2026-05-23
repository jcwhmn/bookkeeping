package com.bookkeeping.core.tag;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@Tag(name = "Tags", description = "Tag management APIs")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @Operation(summary = "Get all tags")
    public ApiResponse<List<TagDto>> getAll() {
        return ApiResponse.success(tagService.getAllTags());
    }

    @PostMapping
    @Operation(summary = "Create tag")
    public ApiResponse<TagDto> create(@Valid @RequestBody CreateTagRequest request) {
        return ApiResponse.success(tagService.createTag(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag")
    public ApiResponse<TagDto> update(@PathVariable Long id, 
                                      @Valid @RequestBody UpdateTagRequest request) {
        return ApiResponse.success(tagService.updateTag(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tag")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ApiResponse.success(null);
    }
}