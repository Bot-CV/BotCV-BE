package org.toanehihi.botcv.interfaces.web.controllers.job.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.toanehihi.botcv.application.job.category.service.JobCategoryService;
import org.toanehihi.botcv.interfaces.web.dtos.DataResponse;
import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.job.category.CreateCategoryRequest;
import org.toanehihi.botcv.interfaces.web.dtos.job.category.JobCategoryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/categories")
@RequiredArgsConstructor
public class JobCategoryController {
    private final JobCategoryService jobCategoryService;

    @GetMapping("")
    public DataResponse<PageResult<JobCategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return DataResponse.<PageResult<JobCategoryResponse>>builder()
                .data(jobCategoryService.getCategories(page, size, sortBy, sortDir))
                .build();
    }

    @GetMapping("/roots")
    public DataResponse<List<JobCategoryResponse>> getRootCategories() {
        return DataResponse.<List<JobCategoryResponse>>builder()
                .data(jobCategoryService.getRootCategories())
                .build();
    }

    @GetMapping("/{parentId}/children")
    public DataResponse<List<JobCategoryResponse>> getChildCategories(@PathVariable Long parentId) {
        return DataResponse.<List<JobCategoryResponse>>builder()
                .data(jobCategoryService.getChildCategories(parentId))
                .build();
    }

    @PostMapping
    public DataResponse<JobCategoryResponse> createCategory(
            @RequestParam(required = false) Long parentId,
            @RequestBody CreateCategoryRequest request) {
        return DataResponse.<JobCategoryResponse>builder()
                .data(jobCategoryService.createCategory(parentId, request))
                .build();
    }
}
