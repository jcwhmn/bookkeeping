package com.bookkeeping.core.insights;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/insights/explorers")
@Tag(name = "Insights Explorers", description = "Insights explorer management APIs")
public class InsightsExplorerController {

    private final InsightsExplorerService explorerService;

    public InsightsExplorerController(InsightsExplorerService explorerService) {
        this.explorerService = explorerService;
    }

    @GetMapping("/list.json")
    @Operation(summary = "List explorers")
    public ApiResponse<List<InsightsExplorerService.InsightsExplorerDto>> list() {
        return ApiResponse.success(explorerService.listExplorers());
    }

    @GetMapping("/get.json")
    @Operation(summary = "Get explorer by ID")
    public ApiResponse<InsightsExplorerService.InsightsExplorerDto> get(@RequestParam Long id) {
        return ApiResponse.success(explorerService.getExplorer(id));
    }

    @PostMapping("/add.json")
    @Operation(summary = "Create explorer")
    public ApiResponse<InsightsExplorerService.InsightsExplorerDto> create(
            @RequestBody InsightsExplorerService.CreateRequest request) {
        return ApiResponse.success(explorerService.createExplorer(request));
    }

    @PostMapping("/modify.json")
    @Operation(summary = "Modify explorer")
    public ApiResponse<InsightsExplorerService.InsightsExplorerDto> modify(
            @RequestBody InsightsExplorerService.ModifyRequest request) {
        return ApiResponse.success(explorerService.modifyExplorer(request));
    }

    @PostMapping("/hide.json")
    @Operation(summary = "Hide/unhide explorer")
    public ApiResponse<Void> hide(@RequestBody IdHiddenRequest request) {
        explorerService.hideExplorer(request.id(), request.hidden());
        return ApiResponse.success(null);
    }

    @PostMapping("/move.json")
    @Operation(summary = "Reorder explorers")
    public ApiResponse<Void> move(@RequestBody OrderedIdsRequest request) {
        explorerService.reorderExplorers(request.orderedIds());
        return ApiResponse.success(null);
    }

    @PostMapping("/delete.json")
    @Operation(summary = "Delete explorer")
    public ApiResponse<Void> delete(@RequestBody IdRequest request) {
        explorerService.deleteExplorer(request.id());
        return ApiResponse.success(null);
    }

    public record IdRequest(long id) {}
    public record IdHiddenRequest(long id, boolean hidden) {}
    public record OrderedIdsRequest(List<Long> orderedIds) {}
}