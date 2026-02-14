package com.gucardev.eventsphere.domain.school.student.model.parameter;

import com.gucardev.eventsphere.domain.school.student.model.request.UpdateStudentRequest;

import java.util.UUID;

/**
 * Wrapper parameter for UpdateStudentUseCase.
 * Contains both the student ID from the path and the update request from the
 * body.
 */
public record UpdateStudentParam(
        UUID id,
        UpdateStudentRequest request) {
}
