package com.project.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedResult<T> {
    private List<T> items;
    private long totalItems;
    private int currentPage;
    private int pageSize;
    private int totalPages;

    public PaginatedResult(List<T> items, long totalItems, int currentPage, int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    // Helper methods
    public boolean hasNext() {
        return currentPage < totalPages;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public int getNextPage() {
        return hasNext() ? currentPage + 1 : currentPage;
    }

    public int getPreviousPage() {
        return hasPrevious() ? currentPage - 1 : 1;
    }
}
