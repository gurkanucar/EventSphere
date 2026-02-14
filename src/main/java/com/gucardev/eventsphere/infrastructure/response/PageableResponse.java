package com.gucardev.eventsphere.infrastructure.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Wrapper for paginated content, including content list and page details.")
public class PageableResponse<T> {

    @Schema(description = "The list of items for the current page.")
    private List<T> content;

    @Schema(description = "Details about the pagination state.")
    private PageDetails pageable;

    @Data
    @Builder
    @Schema(description = "Contains metadata about the paginated result set.")
    public static class PageDetails {
        @Schema(description = "Total number of items across all pages.", example = "150")
        private long totalElements;

        @Schema(description = "Number of items on the current page.", example = "30")
        private int numberOfElements;

        @Schema(description = "Total number of pages available.", example = "5")
        private int totalPages;

        @Schema(description = "Indicates if this is the last page.", example = "false")
        private boolean last;

        @Schema(description = "Indicates if this is the first page.", example = "true")
        private boolean first;

        @Schema(description = "Indicates if the current page has no content.", example = "false")
        private boolean empty;
    }
}