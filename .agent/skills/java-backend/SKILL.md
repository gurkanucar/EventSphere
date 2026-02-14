
# Java Spring Boot Backend Developer Skill

## Role
You are an expert Java Spring Boot backend developer working on the **Meerkat** project. Your goal is to write clean, maintainable, and scalable code following the project's established Clean Architecture patterns.

## Architecture Overview
The project follows a **Domain-Driven Design (DDD)** inspired structure, organized by feature/domain in `src/main/java/com/toolyverse/meerkat/domain/`.

### Key Components
1.  **Domain Layer** (`domain.<feature>`):
    -   **Entity**: JPA Entities representing the database structure.
    -   **Repository**: Spring Data JPA interfaces.
    -   **Service/UseCase**: Business logic encapsulated in granular classes implementing `UseCase<I, O>` or `UseCaseWithInput<I>`.
    -   **Mapper**: MapStruct interfaces for converting between Entity and DTO (Entity <-> Request/Response).
    -   **Model**:
        -   `dto`: Response objects.
        -   `request`: Request payloads.
        -   `parameter`: Wrapper objects for UseCase inputs (e.g., `UpdateUserParam`).
    -   **Controller**: REST Controllers exposing the functionality.
2.  **Infrastructure Layer** (`infrastructure`): shared utilities, base classes, and configurations (Cache, Exception Handling).

## Coding Standards & Patterns

### 1. Use Case Pattern
-   **Granularity**: Create individual classes for each operation (e.g., `CreateUserUseCase`, `GetAllUsersUseCase`).
-   **Interfaces**:
    -   Use `UseCase<I, O>` when returning a value.
    -   Use `UseCaseWithInput<I>` when the operation returns `void`.
-   **Transactional**: Annotate data-modifying operations with `@Transactional`. See section on Transaction Management for detailed guidelines.
-   **Input Wrappers**: If a UseCase needs multiple inputs (e.g., `id` from path and `body` from request), create a wrapper record/class (e.g., `UpdateLookupUseCaseParam`) to keep the `execute(I input)` signature clean.
-   **Validation**: Perform business validation (uniqueness checks, cross-field validation) inside the UseCase before persisting data.

**Example**:
```java
@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements UseCase<CreateUserRequest, UserResponseDto> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto execute(CreateUserRequest request) {
        // 1. Validation
        if (userRepository.existsByEmail(request.getEmail())) {
             throw new ValidationException("Email already exists");
        }
        // 2. Logic
        User user = userMapper.toEntity(request);
        // 3. Persistence
        return userMapper.toDto(userRepository.save(user));
    }
}
```

### 2. Caching
-   Use Spring's Caching annotations (`@Cacheable`, `@CacheEvict`) on the UseCase level, NOT the Controller.
-   **Configuration**: Refer to keys and managers in `RedisCacheConfig` and `CacheNames`.
-   **Eviction**: Ensure modification UseCases (Create, Update, Delete) evict relevant caches (e.g., `allEntries = true` for list caches).

### 2.5. Transaction Management
**When to Use `@Transactional`:**
-   ✅ **Write Operations**: Methods that perform INSERT, UPDATE, or DELETE (create, update, delete operations)
-   ✅ **Multiple DB Operations**: When you need atomicity across multiple repository calls
-   ✅ **Business Logic with Side Effects**: Operations that modify state and need rollback on failure

**When NOT to Use `@Transactional`:**
-   ❌ **Simple Read Operations**: Single `findById()`, `findAll()`, or simple queries
-   ❌ **Repository Methods**: Spring Data JPA repositories are already transactional
-   ❌ **Controllers**: Never put `@Transactional` on controllers

**Read-Only Optimization:**
-   Use `@Transactional(readOnly = true)` for complex read operations (multiple joins, aggregations)
-   Benefits: Performance optimization, prevents accidental writes, better connection pool management

**Best Practices:**
```java
// ✅ GOOD: Write operation with @Transactional
@Transactional
public UserResponseDto createUser(CreateUserRequest request) {
    // Validation + Save + Side effects
    User user = userRepository.save(...);
    emailService.sendWelcome(user); // If this fails, user creation rolls back
    return mapper.toDto(user);
}

// ✅ GOOD: Read-only for complex queries
@Transactional(readOnly = true)
public Page<UserDto> searchUsers(FilterRequest filter) {
    // Complex query with joins
    return userRepository.findAll(spec, pageable).map(mapper::toDto);
}

// ✅ GOOD: Simple read - NO @Transactional needed
public UserDto getUserById(UUID id) {
    return userRepository.findById(id)
        .map(mapper::toDto)
        .orElseThrow(...);
}

// ❌ BAD: Unnecessary @Transactional on simple read
@Transactional // <-- Remove this!
public UserDto getUserById(UUID id) {
    return userRepository.findById(id).map(mapper::toDto).orElseThrow(...);
}
```

**Service-Level vs Method-Level:**
-   **Avoid** `@Transactional` at class level - be explicit per method
-   **Exception**: If 90%+ of methods need transactions, use class-level and override with `@Transactional(readOnly = true)` for reads

### 3. Data Access & Specifications
-   **Repository Pattern**:
    -   **Always** extend `BaseJpaRepository<Entity, ID>` instead of `JpaRepository`
    -   `BaseJpaRepository` already includes `JpaSpecificationExecutor` for dynamic queries
    -   Provides built-in soft delete: `softDelete(id, reason)`
    -   Example:
        ```java
        @Repository
        public interface UserRepository extends BaseJpaRepository<User, UUID> {
            @EntityGraph(attributePaths = {"roles", "roles.permissions"})
            Optional<User> findByEmail(String email);
        }
        ```
-   **N+1 Prevention**: Use `@EntityGraph` or JPQL `JOIN FETCH` in Repositories.
-   **Dynamic Filtering**:
    -   Use `Specification<entity>` for search/filter endpoints.
    -   Start with a neutral spec: `Specification<T> spec = (root, query, cb) -> null;`.
    -   Chain specifications using `.and(...)`.
    -   Extend/Utilize `BaseSpecification` for common operations (`like`, `equals`, `createdBetween`, `deleted`).
-   **Pagination**: Construct `Pageable` inside the UseCase using the Filter Request's page, size, and sort fields.

### 4. Object Mapping
-   Use **MapStruct** for all Entity <-> DTO conversions.
-   Annotate mappers with `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`.
-   Use `void updateEntityFromRequest(RequestDto dto, @MappingTarget Entity entity)` for updates.

### 5. Controllers (Web Layer)
-   **Dependencies**: Inject `UseCase` classes directly.
-   **Response Wrapper**: Use `ApiResponseWrapper.success(...)` and `PageableResponse` for lists.
-   **Swagger**:
    -   **Class Level**: `@Tag(name = "...", description = "...")` is mandatory.
    -   **Method Level**: `@Operation(summary = "...", description = "...")` is mandatory for every endpoint.
    -   **Responses**: `@ApiResponses` must define success (200/201) and error (400/404) cases.
        -   Example: `@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponse.class)))`
    -   **Generics**: Use `OpenAPI Schema Helper Classes` (static inner classes) to document generic types like `ApiResponseWrapper<UserDto>`.
-   **Filtering**:
    -   Use `@ParameterObject` for GET search parameters (e.g., `UserFilterRequest`).
    -   Extend `BaseFilterRequest` for common pagination/sorting fields.
-   **Standardized Endpoints**:
    -   **`GET /all`**: Return all entities without pagination (List or Map)
        ```java
        @GetMapping("/all")
        public ResponseEntity<ApiResponseWrapper<List<RoleDto>>> getAllRoles() {
            return ResponseEntity.ok(ApiResponseWrapper.success(service.getAllRoles()));
        }
        ```
    -   **`GET /search`**: Search with pagination using `@ParameterObject`
        ```java
        @GetMapping("/search")
        public ResponseEntity<ApiResponseWrapper<PageableResponse<RoleDto>>> searchRoles(
                @Valid @ParameterObject RoleFilterRequest filter) {
            Page<RoleDto> page = service.searchRoles(filter);
            return ResponseEntity.ok(ApiResponseWrapper.success(page));
        }
        ```
    -   **`GET /{id}`**: Get single entity by ID
    -   **`POST`**: Create new entity
    -   **`PUT /{id}`**: Update existing entity
    -   **`DELETE /{id}`**: Delete entity

### 6. Data Validation & Documentation
-   **DTOs**: All Request DTOs (`create`, `update`, `filter`) must use **Jakarta Validation** annotations.
    -   `@NotBlank`, `@Size`, `@Min`, `@Max`.
    -   **Always** provide a custom `message`.
-   **OpenAPI / Swagger**:
    -   **Classes**: `@Schema(description = "...")`.
    -   **Fields**: `@Schema(description = "...", example = "...", requiredMode = ...)`
    -   **Access Mode**: Use `accessMode = Schema.AccessMode.READ_ONLY` for fields like IDs in payloads.

### 6. Exception Handling
-   **Structure**: Centralized handling via `@RestControllerAdvice` in `GlobalExceptionHandler`.
-   **Throwing Exceptions**:
    -   **DO NOT** throw `RuntimeException` or `new BusinessException(...)` directly in business logic.
    -   **USE** `ExceptionUtil` static factory methods.
    -   **Standard**: `throw ExceptionUtil.of(ExceptionType.SOME_ERROR);`
    -   **Helpers**: `throw ExceptionUtil.notFound("Entity", id);` or `alreadyExists(...)`.
-   **Defining New Errors**:
    -   Add new errors to the `ExceptionType` enum.
    -   Define the translation key (`error.domain.reason`), HTTP Status, and unique numeric code.
-   **Response Format**: Errors are automatically formatted into `ApiResponseWrapper.error(code, message, details)`.

### 7. Entity Relationships

> [!IMPORTANT]
> Always default to `FetchType.LAZY` for *Many* relationships (and explicit *One* relationships) to avoid performance issues.

#### One-to-One Relationship
-   **Owner Side**: `@OneToOne(fetch = FetchType.LAZY) @JoinColumn`
-   **Inverse Side**: `@OneToOne(mappedBy = "...")`
-   **Helper**: Use a custom setter to sync both sides

```java
// Owner Side (IDCard)
@Entity
public class IDCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;
}

// Inverse Side (Person) - with setter override
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private IDCard idCard;

    // ✅ Setter override to maintain bidirectional consistency
    public void setIdCard(IDCard idCard) {
        if (idCard == null) {
            if (this.idCard != null) {
                this.idCard.setPerson(null);
            }
        } else {
            idCard.setPerson(this);
        }
        this.idCard = idCard;
    }
}
```

---

#### One-to-Many / Many-to-One Relationship
-   **Parent (One)**: `@OneToMany(mappedBy = "...", cascade = CascadeType.ALL, orphanRemoval = true)`
-   **Child (Many)**: `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(nullable = false)`
-   **Helpers**: `addChild()` and `removeChild()` to sync both sides

```java
// Parent Side (Author)
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
        mappedBy = "author",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Book> books = new HashSet<>();

    // ✅ Helper method to add child
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    // ✅ Helper method to remove child
    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}

// Child Side (Book)
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}
```

---

#### Many-to-Many (Simple) Relationship
-   **Owner Side**: `@ManyToMany` with `@JoinTable`
-   **Inverse Side**: `@ManyToMany(mappedBy = "...")`
-   **Lombok**: Exclude collections from `@ToString` and `@EqualsAndHashCode` to prevent recursion
-   **Helpers**: `addCategory()` / `removeCategory()` to sync both Sets

```java
// Owner Side (Product)
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "categories")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "product_category",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // ✅ Helper method to add relationship
    public void addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
    }

    // ✅ Helper method to remove relationship
    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
    }
}

// Inverse Side (Category)
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "products")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
}
```

---

#### Many-to-Many (Complex with Attributes) Relationship
-   **Structure**: Create an intermediate Entity with extra attributes
-   **Key**: Use `@IdClass` or `@EmbeddedId` for composite key
-   **Helpers**: Add convenience methods on parent entities

```java
// Composite Key Class
@EqualsAndHashCode
public class EnrollmentId implements Serializable {
    private Long student;
    private Long course;
}

// Intermediate Entity (Enrollment)
@Entity
@IdClass(EnrollmentId.class)
public class Enrollment {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "grade")
    private String grade; // Extra attribute
}

// Student Entity
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "enrollments")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(
        mappedBy = "student",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();

    // ✅ Helper method to enroll in a course with grade
    public void addCourse(Course course, String grade) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(this);
        enrollment.setCourse(course);
        enrollment.setGrade(grade);

        this.enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);
    }

    // ✅ Helper method to unenroll from a course
    public void removeCourse(Course course) {
        enrollments.removeIf(enrollment -> {
            if (enrollment.getCourse().equals(course)) {
                enrollment.getCourse().getEnrollments().remove(enrollment);
                enrollment.setStudent(null);
                enrollment.setCourse(null);
                return true;
            }
            return false;
        });
    }
}

// Course Entity
@Entity
@EqualsAndHashCode(of = "id")
@ToString(exclude = "enrollments")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(
        mappedBy = "course",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Enrollment> enrollments = new HashSet<>();
}
```

### 8. Code Quality & Style
-   **Control Flow**:
    -   **Avoid Nested Ifs**: Use **Guard Clauses** (Early Return) to keep code flat and readable.
        ```java
        // BAD
        if (param != null) {
            if (isValid(param)) {
                // logic
            }
        }
        // GOOD
        if (param == null) return;
        if (!isValid(param)) throw new ValidationException(...);
        // logic
        ```
-   **Naming**: Use descriptive, meaningful variable names (`userEmail` vs `ue`, `orderList` vs `list`).
-   **Logging**:
    -   Use `@Slf4j`.
    -   **DEBUG**: For development details, payloads, variable values.
    -   **INFO**: For strictly business-significant events (e.g., "User created", "Order placed").
    -   **WARN**: For expected business errors (e.g., "User not found", "Validation failed").
    -   **ERROR**: For unexpected system failures (e.g., "Database connection lost", "NullPointerException").
-   **Refactoring**: Continually refactor "magic numbers/strings" into Constants or Enums.

### 9. Standardized Responses
-   **Mandatory**: All Controller endpoints MUST return `ResponseEntity<ApiResponseWrapper<T>>`.
-   **Factory Methods**:
    -   Single Object: `ApiResponseWrapper.success(data)`
    -   List/Page: `ApiResponseWrapper.success(page)` (Automatically handles pagination metadata)
    -   Empty: `ApiResponseWrapper.successWithEmptyData()`
    -   **NEVER** return raw entities or lists directly.

---

## Advanced Best Practices

### 10. Programming by Intention
Write code that reads like a story. The main method should describe *what* is happening, while private helper methods handle *how*.

```java
// ✅ GOOD: Expressive, intent-revealing code
@Service
public class RegistrationService {
    public void register(UserRequest req) {
        validateRequest(req);
        User user = createUser(req);
        saveUser(user);
        sendWelcomeNotification(user);
    }

    private void validateRequest(UserRequest req) {
        if (!isValidEmail(req.getEmail())) throw new InvalidEmailException();
        if (repo.existsByEmail(req.getEmail())) throw new EmailExistsException();
    }
    // ... helper methods follow
}
```

### 11. Null Safety & Modern Java
-   **Entity Lookups**: Use `orElseThrow()` instead of `orElse(null)` + manual checks.
    ```java
    User user = repo.findById(id).orElseThrow(() -> ExceptionUtil.notFound("User", id));
    ```
-   **Strings & Collections**: Use Spring Utils.
    ```java
    if (StringUtils.hasText(str)) { ... }
    if (!CollectionUtils.isEmpty(list)) { ... }
    ```
-   **Safe Comparisons**: `Objects.equals(a, b)` to prevent NPE.
-   **Records for DTOs**: Prefer Java Records for simple, immutable DTOs.
    ```java
    public record UserDTO(String name, String email) {}
    ```
-   **Semantic Boolean Naming**: `isActive`, `hasAccess`, `canLogin` (not `status`, `flag`).

### 12. Database Performance
-   **Projections**: Don't fetch entire entities when you only need a few fields.
    ```java
    // Interface Projection
    public interface UserNameProjection { String getName(); String getEmail(); }
    List<UserNameProjection> findAllProjectedBy();

    // DTO Projection via @Query
    @Query("SELECT new com.example.UserDTO(u.name, u.email) FROM User u")
    List<UserDTO> findAllDTO();
    ```
-   **Batch Processing**: Use `saveAll(list)` instead of looping `save(item)`.
-   **Async for Non-Blocking**: Use `@Async` for operations like sending emails.
    ```java
    @Async
    public CompletableFuture<Void> sendBulkEmails(List<String> recipients) { ... }
    ```

### 13. Testing Best Practices

**Tech Stack**:
-   **Java 25**
-   **Spring Boot 4.0.2**
-   **Spring Data JPA** (Specifications, EntityGraph)
-   **MapStruct**
-   **Lombok**
-   **Redis** (Spring Cache)
-   **SpringDoc OpenAPI** (Swagger)
-   **Jakarta Validation**
-   **AssertJ**
-   **Mockito**
-   **JUnit 5**

**Test Structure**:
-   Place tests in `src/test/java` mirroring the production package structure
-   Test file naming: `{ClassName}Test.java` (e.g., `RoleServiceTest.java`)
-   Use `@ExtendWith(MockitoExtension.class)` for unit tests
-   Use `@SpringBootTest` only for integration tests

**Unit Testing Services/UseCases**:
```java
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PermissionRepository permissionRepository;
    
    @Mock
    private RoleMapper roleMapper;
    
    @InjectMocks
    private RoleService roleService;
    
    @Test
    void shouldCreateRole_whenValidRequest() {
        // Arrange (Given)
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .displayName("Administrator")
                .build();
        
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("ADMIN");
        
        RoleResponseDto expectedDto = new RoleResponseDto();
        expectedDto.setId(role.getId());
        
        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDto(role)).thenReturn(expectedDto);
        
        // Act (When)
        RoleResponseDto result = roleService.createRole(request);
        
        // Assert (Then)
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(role.getId());
        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository).save(any(Role.class));
        verify(roleMapper).toDto(role);
    }
    
    @Test
    void shouldThrowException_whenRoleAlreadyExists() {
        // Arrange
        CreateRoleRequest request = CreateRoleRequest.builder()
                .name("ADMIN")
                .build();
        
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> roleService.createRole(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("already exists");
        
        verify(roleRepository).existsByName("ADMIN");
        verify(roleRepository, never()).save(any());
    }
}
```

**Integration Testing Controllers**:
Use `RestTestClient` for declarative, fluent API testing.

**Required Imports**:
```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import static org.assertj.core.api.Assertions.assertThat;
```

**Example**:
```java
@SpringBootTest
@AutoConfigureRestTestClient
class UserControllerTest {

    @Autowired
    private RestTestClient client;

    @Test
    void shouldCreateUser_whenValidRequest() {
        CreateUserRequest request = new CreateUserRequest("user@example.com", "John Doe");

        client.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.id").isNotEmpty()
                .jsonPath("$.data.email").isEqualTo("user@example.com");
    }
}
```

**Best Practices**:
-   **Naming**: Use Given-When-Then pattern
    -   `shouldReturnUser_whenUserExists()`
    -   `shouldThrowException_whenEmailIsInvalid()`
-   **AAA Pattern**: Arrange-Act-Assert (clearly separated with comments)
-   **AssertJ**: Use fluent assertions (`assertThat()`) instead of JUnit assertions
-   **Mocking**:
    -   `@Mock` for dependencies
    -   `@InjectMocks` for the class under test
    -   `verify()` to ensure methods were called correctly
    -   `never()` to ensure methods were NOT called
-   **Test Data Builders**: Create reusable builders for complex objects
    ```java
    public class RoleTestBuilder {
        public static Role.RoleBuilder aRole() {
            return Role.builder()
                    .id(UUID.randomUUID())
                    .name("USER")
                    .displayName("Regular User");
        }
    }
    ```
-   **Edge Cases**: Test null inputs, empty collections, boundary conditions
-   **Exception Testing**: Use `assertThatThrownBy()` for exception scenarios
-   **Response Wrapper Testing**:
    - When asserting `ApiResponseWrapper` responses with `JsonPath`:
    - Use `.doesNotExist()` for fields that are null (due to `@JsonInclude(NON_NULL)`).
    - Use `.isEmpty()` only for empty collections or strings that are actually present.
    ```java
    // Check for null data (field missing in JSON)
    .jsonPath("$.data").doesNotExist()
    
    // Check for empty list (field present but empty)
    .jsonPath("$.data").isEmpty()
    ```
-   **Avoid Over-Mocking**: Don't mock value objects or simple DTOs

### 14. Security Best Practices
-   **Password Encoding**: Always use `PasswordEncoder.encode()`.
-   **Input Sanitization**: Rely on Bean Validation (`@NotBlank`, `@Email`, `@Pattern`).

#### IDOR (Insecure Direct Object Reference) Protection

> [!CAUTION]
> Every data-modifying endpoint that operates on a specific resource **MUST** verify that the authenticated user owns that resource or has an admin role. Failing to do so is an IDOR vulnerability.

**Core Principle**: Authorization checks belong in the **UseCase** layer, NOT in controllers. The UseCase receives the authenticated user's ID via `AuthService` and validates ownership before proceeding.

**Reusable Ownership Validator**:
Create a shared utility to keep ownership checks consistent across all UseCases:
```java
@Component
@RequiredArgsConstructor
public class ResourceOwnershipValidator {

    private final AuthService authService;

    /**
     * Verifies the current user owns the resource or has ADMIN role.
     * @param resourceOwnerId UUID of the resource owner
     * @throws CustomException if the user is not the owner and not an admin
     */
    public void validateOwnership(UUID resourceOwnerId) {
        UserResponseDto currentUser = authService.getAuthenticatedUser();
        boolean isOwner = currentUser.getId().equals(resourceOwnerId);
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw ExceptionUtil.of(ExceptionType.ACCESS_DENIED);
        }
    }

    /**
     * Strict ownership check — admins are NOT exempt.
     * Use for sensitive personal operations (e.g., change own password).
     */
    public void validateStrictOwnership(UUID resourceOwnerId) {
        UserResponseDto currentUser = authService.getAuthenticatedUser();
        if (!currentUser.getId().equals(resourceOwnerId)) {
            throw ExceptionUtil.of(ExceptionType.ACCESS_DENIED);
        }
    }

    public UUID getCurrentUserId() {
        return authService.getAuthenticatedUser().getId();
    }
}
```

**Example — Update User (owner or admin)**:
```java
@Service
@RequiredArgsConstructor
public class UpdateUserUseCase implements UseCase<UpdateUserParam, UserResponseDto> {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public UserResponseDto execute(UpdateUserParam param) {
        User user = userRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("User", param.id()));

        // ✅ IDOR check: only the user itself or an admin can update
        ownershipValidator.validateOwnership(user.getId());

        userMapper.updateEntityFromRequest(param.request(), user);
        return userMapper.toDto(userRepository.save(user));
    }
}
```

**Example — Update Product (only owner)**:
```java
@Service
@RequiredArgsConstructor
public class UpdateProductUseCase implements UseCase<UpdateProductParam, ProductResponseDto> {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public ProductResponseDto execute(UpdateProductParam param) {
        Product product = productRepository.findById(param.id())
                .orElseThrow(() -> ExceptionUtil.notFound("Product", param.id()));

        // ✅ IDOR check: only the product owner can update
        ownershipValidator.validateOwnership(product.getCreatedByUserId());

        productMapper.updateEntityFromRequest(param.request(), product);
        return productMapper.toDto(productRepository.save(product));
    }
}
```

**Example — Delete (owner or admin)**:
```java
@Service
@RequiredArgsConstructor
public class DeleteProductUseCase implements UseCaseWithInput<UUID> {

    private final ProductRepository productRepository;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional
    public void execute(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> ExceptionUtil.notFound("Product", productId));

        // ✅ IDOR check
        ownershipValidator.validateOwnership(product.getCreatedByUserId());

        productRepository.softDelete(productId, "Deleted by user");
    }
}
```

**When to use which check**:
| Scenario | Method | Admin Exempt? |
|---|---|---|
| Update/delete own resource | `validateOwnership()` | ✅ Yes |
| Change own password | `validateStrictOwnership()` | ❌ No |
| Admin-only operations | Use `@PreAuthorize("hasRole('ADMIN')")` on controller | N/A |
| List own resources | Filter by `getCurrentUserId()` in query/spec | N/A |

**Filtering in List/Search endpoints**:
```java
// In the UseCase — always scope to current user unless admin
public Page<ProductResponseDto> execute(ProductFilterRequest filter) {
    UUID currentUserId = ownershipValidator.getCurrentUserId();
    UserResponseDto currentUser = authService.getAuthenticatedUser();
    boolean isAdmin = currentUser.getAuthorities().stream()
            .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

    Specification<Product> spec = (root, query, cb) -> null;

    // Non-admin users only see their own resources
    if (!isAdmin) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("createdByUserId"), currentUserId));
    }
    // ... add other filters
    return productRepository.findAll(spec, pageable).map(productMapper::toDto);
}
```

---

### 15. Concurrency Control (Optimistic & Pessimistic Locking)

#### Optimistic Locking (`@Version`)
Use optimistic locking as the **default** strategy. It protects against lost updates with minimal performance overhead — no database locks are held.

**Add `@Version` to BaseEntity** so all entities inherit it:
```java
@MappedSuperclass
public abstract class BaseEntity {
    // ... existing audit fields ...

    @Version
    @Column(name = "version")
    protected Long version;
}
```

**How it works**:
1. JPA reads the entity along with its `version` value.
2. On `save()`, JPA issues `UPDATE ... SET version = version + 1 WHERE id = ? AND version = ?`.
3. If another transaction modified the row, the version won't match → `OptimisticLockException` is thrown.

**Handle the exception** in `GlobalExceptionHandler`:
```java
@ExceptionHandler(OptimisticLockException.class)
public ResponseEntity<ApiResponseWrapper<Void>> handleOptimisticLock(OptimisticLockException ex) {
    log.warn("Optimistic lock conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponseWrapper.error(409, "Resource was modified by another request. Please retry."));
}

// Also handle Spring's wrapper
@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
public ResponseEntity<ApiResponseWrapper<Void>> handleOptimisticLockSpring(
        ObjectOptimisticLockingFailureException ex) {
    log.warn("Optimistic lock conflict: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponseWrapper.error(409, "Resource was modified by another request. Please retry."));
}
```

**Include version in DTOs** to detect conflicts from the client side:
```java
// Response DTO
public class ProductResponseDto {
    private UUID id;
    private String name;
    private Long version; // ✅ Return version to client
}

// Update Request
public class UpdateProductRequest {
    @NotBlank private String name;
    @NotNull private Long version; // ✅ Client sends back the version it read
}
```

**Use in UseCase** — verify the client's version matches:
```java
@Transactional
public ProductResponseDto execute(UpdateProductParam param) {
    Product product = productRepository.findById(param.id())
            .orElseThrow(() -> ExceptionUtil.notFound("Product", param.id()));

    // ✅ Early conflict detection: reject if client has stale version
    if (!product.getVersion().equals(param.request().getVersion())) {
        throw ExceptionUtil.of(ExceptionType.CONFLICT);
    }

    productMapper.updateEntityFromRequest(param.request(), product);
    return productMapper.toDto(productRepository.save(product));
}
```

> [!TIP]
> Optimistic locking is sufficient for most CRUD operations (user profile updates, product edits, order modifications). Use it everywhere by default.

---

#### Pessimistic Locking (`@Lock`)
Use pessimistic locking for operations where **data integrity is critical** and concurrent access is expected — e.g., balance top-ups, inventory decrements, counter increments.

**Define a locked query in the Repository**:
```java
@Repository
public interface WalletRepository extends BaseJpaRepository<Wallet, UUID> {

    // ✅ Pessimistic write lock — blocks other transactions from reading/writing this row
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);

    // ✅ Pessimistic write lock — lock by owner
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    Optional<Wallet> findByUserIdForUpdate(@Param("userId") UUID userId);
}
```

**Example — Balance Top-Up**:
```java
@Service
@RequiredArgsConstructor
public class TopUpBalanceUseCase implements UseCase<TopUpRequest, WalletResponseDto> {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final ResourceOwnershipValidator ownershipValidator;

    @Override
    @Transactional // ✅ REQUIRED — pessimistic lock only works within a transaction
    public WalletResponseDto execute(TopUpRequest request) {
        // ✅ Acquire row-level lock — other transactions will WAIT here
        Wallet wallet = walletRepository.findByUserIdForUpdate(request.userId())
                .orElseThrow(() -> ExceptionUtil.notFound("Wallet", request.userId()));

        // IDOR: only the wallet owner can top up
        ownershipValidator.validateStrictOwnership(wallet.getUserId());

        // Safe to modify — no other transaction can read/write this row
        wallet.setBalance(wallet.getBalance().add(request.amount()));
        return walletMapper.toDto(walletRepository.save(wallet));
    }
}
```

**Example — Inventory Decrement** (e.g., placing an order):
```java
@Transactional
public void decrementStock(UUID productId, int quantity) {
    Product product = productRepository.findByIdForUpdate(productId)
            .orElseThrow(() -> ExceptionUtil.notFound("Product", productId));

    if (product.getStock() < quantity) {
        throw ExceptionUtil.of(ExceptionType.INSUFFICIENT_STOCK);
    }

    product.setStock(product.getStock() - quantity);
    productRepository.save(product);
}
```

**When to use which**:
| Scenario | Strategy | Why |
|---|---|---|
| User profile update | Optimistic (`@Version`) | Low contention, rare conflicts |
| Product/entity CRUD | Optimistic (`@Version`) | Standard operations |
| Balance top-up / withdrawal | **Pessimistic** (`@Lock`) | Money — must be exact |
| Inventory decrement | **Pessimistic** (`@Lock`) | Overselling prevention |
| Counter / sequence increment | **Pessimistic** (`@Lock`) | Must be atomic |
| Concurrent report generation | Optimistic (`@Version`) | Read-heavy, conflicts unlikely |

> [!WARNING]
> Pessimistic locks hold database row locks. Always use them inside `@Transactional` and keep the transaction as **short as possible** to avoid deadlocks. Never call external APIs (HTTP, messaging) while holding a pessimistic lock.

**Lock timeout** — prevent indefinite waits:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")) // 3 seconds
@Query("SELECT w FROM Wallet w WHERE w.id = :id")
Optional<Wallet> findByIdForUpdate(@Param("id") UUID id);
```

---

### 16. Architecture Principles
-   **Single Responsibility**: Each UseCase/Service should do one thing well.
-   **Dependency Inversion**: Depend on abstractions (interfaces), not concrete implementations.
    ```java
    public interface NotificationService { void send(String to, String msg); }
    // UserService injects NotificationService, not EmailNotificationServiceImpl
    ```
-   **Configuration Classes**: Centralize beans (`@Configuration`, `@Bean`) for cross-cutting concerns (Security, Async, Cache).