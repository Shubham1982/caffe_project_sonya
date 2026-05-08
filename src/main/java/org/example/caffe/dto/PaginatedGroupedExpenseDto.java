package org.example.caffe.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PaginatedGroupedExpenseDto {
    private Map<String, List<ExpenseDetailDto>> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
