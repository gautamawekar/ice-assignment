package com.ice.model;

public record Pagination(int page, int size, long totalElements, int totalPages) {
}
