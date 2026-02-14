package com.gucardev.eventsphere.domain.school.parent.entity;

import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.school.student.entity.Student;
import com.gucardev.eventsphere.domain.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "parents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString(exclude = { "user", "students" })
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profession", length = 100)
    private String profession;

    @Column(name = "workplace", length = 200)
    private String workplace;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "parent_student", joinColumns = @JoinColumn(name = "parent_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    /**
     * Helper method to add student relationship
     */
    public void addStudent(Student student) {
        this.students.add(student);
        student.getParents().add(this);
    }

    /**
     * Helper method to remove student relationship
     */
    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getParents().remove(this);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
