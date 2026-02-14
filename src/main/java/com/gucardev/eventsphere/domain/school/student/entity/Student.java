package com.gucardev.eventsphere.domain.school.student.entity;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.school.classroom.entity.Classroom;
import com.gucardev.eventsphere.domain.school.parent.entity.Parent;
import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = { "user", "classroom", "parents" })
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "school_number", unique = true, length = 20)
    private String schoolNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @ManyToMany(mappedBy = "students")
    @Builder.Default
    private Set<Parent> parents = new HashSet<>();

    /**
     * Helper method to set user (maintains bidirectional consistency)
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Helper method to set classroom
     */
    public void setClassroom(Classroom classroom) {
        if (this.classroom != null) {
            this.classroom.getStudents().remove(this);
        }
        this.classroom = classroom;
        if (classroom != null) {
            classroom.getStudents().add(this);
        }
    }
}
