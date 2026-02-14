package com.gucardev.eventsphere.domain.school.student.mapper;

import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.school.student.model.dto.StudentResponseDto;
import com.gucardev.eventsphere.domain.school.student.model.request.UpdateStudentRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    @Mapping(target = "classroomName", source = "classroom.name")
    @Mapping(target = "classroomId", source = "classroom.id")
    @Mapping(target = "parentsCount", expression = "java(student.getParents() != null ? student.getParents().size() : 0)")
    StudentResponseDto toDto(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "schoolNumber", ignore = true)
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "parents", ignore = true)
    void updateEntityFromRequest(UpdateStudentRequest request, @MappingTarget Student student);

    /**
     * After mapping, update User fields if they are present in the request
     */
    @AfterMapping
    default void updateUserFields(UpdateStudentRequest request, @MappingTarget Student student) {
        if (student.getUser() != null) {
            if (request.getName() != null) {
                student.getUser().setName(request.getName());
            }
            if (request.getSurname() != null) {
                student.getUser().setSurname(request.getSurname());
            }
            if (request.getPhoneNumber() != null) {
                student.getUser().setPhoneNumber(request.getPhoneNumber());
            }
        }
    }
}
