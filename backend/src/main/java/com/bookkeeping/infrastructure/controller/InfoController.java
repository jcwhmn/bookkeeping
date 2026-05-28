package com.bookkeeping.infrastructure.controller;

import com.bookkeeping.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "System", description = "System information")
public class InfoController {

    @Value("${app.version:dev}")
    private String appVersion;

    @Value("${app.build-time:unknown}")
    private String buildTime;

    @Value("${app.name:Bookkeeping}")
    private String appName;

    @GetMapping("/info")
    @Operation(summary = "Get application info")
    public ApiResponse<AppInfo> getInfo() {
        return ApiResponse.success(new AppInfo(
                appName,
                appVersion,
                buildTime
        ));
    }

    public record AppInfo(
            String name,
            String version,
            String buildTime
    ) {}
}