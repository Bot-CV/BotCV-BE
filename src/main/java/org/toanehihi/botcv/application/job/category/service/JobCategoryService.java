package org.toanehihi.botcv.application.job.category.service;

import org.toanehihi.botcv.interfaces.web.dtos.PageResult;
import org.toanehihi.botcv.interfaces.web.dtos.job.category.CreateCategoryRequest;
import org.toanehihi.botcv.interfaces.web.dtos.job.category.JobCategoryResponse;

import java.util.List;

public interface JobCategoryService {
    PageResult<JobCategoryResponse> getCategories(int page, int size, String sortBy, String sortDir);
    JobCategoryResponse createCategory(Long parentId, CreateCategoryRequest request);
    List<JobCategoryResponse> getRootCategories();
    List<JobCategoryResponse> getChildCategories(Long parentId);
}
