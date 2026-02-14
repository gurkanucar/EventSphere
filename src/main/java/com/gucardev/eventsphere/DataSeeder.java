package com.gucardev.eventsphere;


import com.gucardev.eventsphere.domain.auth.permission.entity.Permission;
import com.gucardev.eventsphere.domain.auth.permission.repository.PermissionRepository;
import com.gucardev.eventsphere.domain.auth.role.entity.Role;
import com.gucardev.eventsphere.domain.auth.role.repository.RoleRepository;
import com.gucardev.eventsphere.domain.auth.user.entity.User;
import com.gucardev.eventsphere.domain.auth.user.repository.UserRepository;
import com.gucardev.eventsphere.domain.attendee.entity.Attendee;
import com.gucardev.eventsphere.domain.attendee.repository.AttendeeRepository;
import com.gucardev.eventsphere.domain.event.entity.Event;
import com.gucardev.eventsphere.domain.event.repository.EventRepository;
import com.gucardev.eventsphere.domain.organizer.entity.Organizer;
import com.gucardev.eventsphere.domain.organizer.repository.OrganizerRepository;
import com.gucardev.eventsphere.domain.session.entity.Session;
import com.gucardev.eventsphere.domain.session.repository.SessionRepository;
import com.gucardev.eventsphere.domain.ticket.entity.Ticket;
import com.gucardev.eventsphere.domain.ticket.entity.TicketStatus;
import com.gucardev.eventsphere.domain.ticket.repository.TicketRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;
    private final SessionRepository sessionRepository;
    private final TicketRepository ticketRepository;
    private final AttendeeRepository attendeeRepository;

    // Define standard constants
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @PostConstruct
    @Transactional
    public void seed() {
        log.info("Checking for initial data seeding...");

        if (roleRepository.count() == 0) {
            log.info("No roles found. seeding default data...");
            seedData();
        } else {
            log.info("Data already exists. Skipping seed.");
        }
    }

    private void seedData() {
        // 1. Create Permissions
        Map<String, Permission> perms = new HashMap<>();

        // Define resources and actions
        perms.put("USER_READ", createPermissionIfNotFound("READ", "USER", "Read Users", "Can view user details"));
        perms.put("USER_WRITE", createPermissionIfNotFound("WRITE", "USER", "Edit Users", "Can create or edit users"));
        perms.put("USER_DELETE", createPermissionIfNotFound("DELETE", "USER", "Delete Users", "Can remove users"));

        perms.put("ROLE_READ", createPermissionIfNotFound("READ", "ROLE", "Read Roles", "Can view roles"));
        perms.put("ROLE_WRITE", createPermissionIfNotFound("WRITE", "ROLE", "Edit Roles", "Can modify roles"));

        // 2. Create Roles
        Role adminRole = createRoleIfNotFound(ROLE_ADMIN, "Administrator", "System Administrator", new HashSet<>(perms.values()));

        // User only gets read permissions
        Set<Permission> userPerms = Set.of(perms.get("USER_READ"));
        Role userRole = createRoleIfNotFound(ROLE_USER, "Standard User", "Regular application user", userPerms);

        // 3. Create Users
        User admin = createUserIfNotFound("admin@mail.com", "Admin", "Super", "password", Set.of(adminRole));
        User user = createUserIfNotFound("user@mail.com", "John", "Doe", "password", Set.of(userRole));

        // 4. Create Domain Data
        seedDomainData(admin, user);

        log.info("Seeding completed successfully.");
    }

    private void seedDomainData(User admin, User regularUser) {
        // Create Organizer profile for Admin
        Organizer adminOrganizer = createOrganizerIfNotFound(admin, "Admin Events Inc.", "https://adminevents.com");

        // Create Attendee profile for Regular User
        Attendee regularAttendee = createAttendeeIfNotFound(regularUser, "Vegetarian");

        // Create Events
        Event techConf = createEvent(adminOrganizer, "Tech Conference 2024", "A great tech conference", "Convention Center");
        Event musicFest = createEvent(adminOrganizer, "Summer Music Fest", "Live music all day", "Central Park");

        // Create Sessions for Tech Conf
        createSession(techConf, "Keynote: Future of AI", "Dr. Alan Turing", LocalDateTime.now().plusDays(2).plusHours(9), LocalDateTime.now().plusDays(2).plusHours(10));
        createSession(techConf, "Microservices at Scale", "Jane Doe", LocalDateTime.now().plusDays(2).plusHours(11), LocalDateTime.now().plusDays(2).plusHours(12));

        // Create Tickets
        createTicket(techConf, regularAttendee, "TICKET-001", new BigDecimal("100.00"), TicketStatus.SOLD);
        createTicket(musicFest, regularAttendee, "TICKET-002", new BigDecimal("50.00"), TicketStatus.SOLD);
    }

    private Permission createPermissionIfNotFound(String action, String resource, String displayName, String desc) {
        return permissionRepository.findByActionAndResource(action, resource)
                .orElseGet(() -> {
                    Permission p = new Permission();
                    p.setAction(action);
                    p.setResource(resource);
                    p.setDisplayName(displayName);
                    p.setDescription(desc);
                    return permissionRepository.save(p);
                });
    }

    private Role createRoleIfNotFound(String name, String displayName, String desc, Set<Permission> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName(name); // Setter handles ROLE_ prefix logic automatically if you kept that logic
                    r.setDisplayName(displayName);
                    r.setDescription(desc);
                    r.setPermissions(permissions);
                    return roleRepository.save(r);
                });
    }

    private User createUserIfNotFound(String email, String name, String surname, String password, Set<Role> roles) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setSurname(surname);
            user.setPassword(passwordEncoder.encode(password));
            user.setActivated(true);
            user.setRoles(roles);
            User saved = userRepository.save(user);
            log.info("Created user: {}", email);
            return saved;
        });
    }

    private Organizer createOrganizerIfNotFound(User user, String orgName, String website) {
        return organizerRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseGet(() -> {
                    Organizer organizer = new Organizer();
                    organizer.setUser(user);
                    organizer.setOrganizationName(orgName);
                    organizer.setWebsiteUrl(website);
                    organizer.setContactEmail(user.getEmail());
                    return organizerRepository.save(organizer);
                });
    }

    private Attendee createAttendeeIfNotFound(User user, String prefs) {
        return attendeeRepository.findAll().stream()
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseGet(() -> {
                    Attendee attendee = new Attendee();
                    attendee.setUser(user);
                    attendee.setPreferences(prefs);
                    return attendeeRepository.save(attendee);
                });
    }

    private Event createEvent(Organizer organizer, String title, String desc, String location) {
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(title);
        event.setDescription(desc);
        event.setLocation(location);
        event.setStartTime(LocalDateTime.now().plusDays(2));
        event.setEndTime(LocalDateTime.now().plusDays(3));
        event.setIsPublished(true);
        return eventRepository.save(event);
    }

    private void createSession(Event event, String title, String speaker, LocalDateTime start, LocalDateTime end) {
        Session session = new Session();
        session.setEvent(event);
        session.setTitle(title);
        session.setSpeakerName(speaker);
        session.setStartTime(start);
        session.setEndTime(end);
        sessionRepository.save(session);
    }

    private void createTicket(Event event, Attendee attendee, String code, BigDecimal price, TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setAttendee(attendee);
        ticket.setTicketCode(code);
        ticket.setPrice(price);
        ticket.setStatus(status);
        ticketRepository.save(ticket);
    }
}