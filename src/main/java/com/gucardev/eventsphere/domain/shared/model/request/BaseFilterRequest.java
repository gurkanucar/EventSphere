package com.gucardev.eventsphere.domain.shared.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BaseFilterRequest {

    @Schema(description = "Page number (starts from 0)", example = "0")
    @NotNull
    @Min(0)
    @Max(99999)
    private int page = 0;

    @Schema(description = "Number of records per page", example = "30")
    @NotNull
    @Min(1)
    @Max(150)
    @Positive
    private int size = 30;

    @Schema(description = "Sort direction", example = "desc", allowableValues = {"asc", "desc"})
    @Pattern(regexp = "^(asc|desc)$", message = "{sort.direction.pattern.exception}")
    private String sortDir = "desc";

    @Schema(description = "Sort by field", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Filter entity created after this date", example = "2024-01-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "Filter entity created before this date", example = "2028-12-31")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    public Sort.Direction getSortDir() {
        return this.sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public org.springframework.data.domain.Pageable toPageable() {
        return org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(getSortDir(), sortBy));
    }

}
