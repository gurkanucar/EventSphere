package com.gucardev.eventsphere.domain.school.teacher.entity;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.school.classroom.entity.Classroom;
import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teachers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = { "user", "classrooms" })
public class Teacher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "employee_number", unique = true, length = 20)
    private String employeeNumber;

    @Column(name = "branch", length = 50)
    private String branch;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @OneToMany(mappedBy = "homeroomTeacher", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Classroom> classrooms = new HashSet<>();

    public void setUser(User user) {
        this.user = user;
    }
}
