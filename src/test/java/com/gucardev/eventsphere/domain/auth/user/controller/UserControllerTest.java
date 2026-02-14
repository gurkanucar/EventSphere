package com.gucardev.eventsphere.domain.auth.user.controller;

import com.jayway.jsonpath.JsonPath;
import com.gucardev.eventsphere.domain.auth.role.model.request.CreateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.service.RoleService;
import com.gucardev.eventsphere.domain.auth.user.model.request.CreateUserRequest;
import com.gucardev.eventsphere.domain.auth.user.model.request.UpdateUserRequest;
import com.gucardev.eventsphere.infrastructure.config.security.test.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureRestTestClient
@WithMockCustomUser
class UserControllerTest {

        @Autowired
        private RestTestClient client;

        @Autowired
        private RoleService roleService;

        private UUID existingRoleId;

        @BeforeEach
        void setUp() {
                // Create a role to be used in user creation tests
                CreateRoleRequest roleRequest = new CreateRoleRequest();
                roleRequest.setName("USER_TEST_ROLE_" + UUID.randomUUID());
                roleRequest.setDisplayName("User Test Role");
                roleRequest.setDescription("Role for User Controller Tests");
                existingRoleId = roleService.createRole(roleRequest).getId();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE" })
        void createUser_ShouldReturnCreated_WhenRequestIsValid() {
                // Given
                CreateUserRequest request = new CreateUserRequest();
                request.setEmail("test_" + UUID.randomUUID() + "@example.com");
                request.setPassword("SecurePass123!");
                request.setName("Test");
                request.setSurname("User");
                request.setRoleIds(Set.of(existingRoleId));
                request.setActivated(true);

                // When/Then
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.data.id").isNotEmpty()
                                .jsonPath("$.data.email").isEqualTo(request.getEmail())
                                .jsonPath("$.data.name").isEqualTo("Test");
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE" })
        void createUser_ShouldReturnBadRequest_WhenRequestIsInvalid() {
                // Given
                CreateUserRequest request = new CreateUserRequest();
                request.setEmail("invalid-email"); // Invalid email
                request.setPassword("short"); // Too short
                // Missing name, surname, roles

                // When/Then
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isBadRequest();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE", "USER:READ" })
        void getUserById_ShouldReturnUser_WhenIdExists() {
                // Given - Create a user first
                CreateUserRequest createRequest = new CreateUserRequest();
                createRequest.setEmail("get_" + UUID.randomUUID() + "@example.com");
                createRequest.setPassword("Password123!");
                createRequest.setName("Get");
                createRequest.setSurname("User");
                createRequest.setRoleIds(Set.of(existingRoleId));
                createRequest.setActivated(true);

                String responseBody = client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String userId = JsonPath.read(responseBody, "$.data.id");

                // When/Then
                client.get()
                                .uri("/api/v1/users/{id}", userId)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data.id").isEqualTo(userId)
                                .jsonPath("$.data.email").isEqualTo(createRequest.getEmail());
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:READ" })
        void getUserById_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/users/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE", "USER:UPDATE" })
        void updateUser_ShouldReturnUpdatedUser_WhenIdExists() {
                // Given - Create a user first
                CreateUserRequest createRequest = new CreateUserRequest();
                createRequest.setEmail("update_" + UUID.randomUUID() + "@example.com");
                createRequest.setPassword("Password123!");
                createRequest.setName("Original");
                createRequest.setSurname("Name");
                createRequest.setRoleIds(Set.of(existingRoleId));
                createRequest.setActivated(true);

                String responseBody = client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String userId = JsonPath.read(responseBody, "$.data.id");

                UpdateUserRequest updateRequest = new UpdateUserRequest();
                updateRequest.setName("Updated");
                updateRequest.setSurname("NewName");

                // When/Then
                client.put()
                                .uri("/api/v1/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data.id").isEqualTo(userId)
                                .jsonPath("$.data.name").isEqualTo("Updated")
                                .jsonPath("$.data.surname").isEqualTo("NewName");
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:UPDATE" })
        void updateUser_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();
                UpdateUserRequest updateRequest = new UpdateUserRequest();
                updateRequest.setName("Updated");

                // When/Then
                client.put()
                                .uri("/api/v1/users/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE", "USER:DELETE", "USER:READ" })
        void deleteUser_ShouldReturnSuccess_WhenIdExists() {
                // Given - Create a user first
                CreateUserRequest createRequest = new CreateUserRequest();
                createRequest.setEmail("delete_" + UUID.randomUUID() + "@example.com");
                createRequest.setPassword("Password123!");
                createRequest.setName("Delete");
                createRequest.setSurname("Me");
                createRequest.setRoleIds(Set.of(existingRoleId));
                createRequest.setActivated(true);

                String responseBody = client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String userId = JsonPath.read(responseBody, "$.data.id");

                // When/Then
                client.delete()
                                .uri("/api/v1/users/{id}", userId)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").doesNotExist();

                // Verify it is gone (assuming soft delete or hard delete, GET should return not
                // found or maybe OK but deleted? Usually 404/Gone or just not in list.
                // Based on other tests, 404 is expected for not found entities).
                // If soft delete happens, maybe GET returns it? Let's assume standard behavior:
                // if deleted, get returns 404 or filtering excludes it.
                // Actually, Role/Permission delete returned ok.
                // Let's check UserController.deleteUser implementation: returns
                // successWithEmptyData.

                // Verifying 404 might depend on if service throws 404 for deleted entities.
                client.get()
                                .uri("/api/v1/users/{id}", userId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:DELETE" })
        void deleteUser_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/users/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE", "USER:READ" })
        void getAllUsers_ShouldReturnList() {
                // Given - Create users
                for (int i = 0; i < 3; i++) {
                        CreateUserRequest req = new CreateUserRequest();
                        req.setEmail("list_" + i + "_" + UUID.randomUUID() + "@example.com");
                        req.setPassword("Password123!");
                        req.setName("ListUser" + i);
                        req.setSurname("Test");
                        req.setRoleIds(Set.of(existingRoleId));
                        req.setActivated(true);

                        client.post().uri("/api/v1/users").contentType(MediaType.APPLICATION_JSON).body(req).exchange();
                }

                // When/Then
                client.get()
                                .uri("/api/v1/users")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data.content").isArray()
                                .jsonPath("$.data.content.length()")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(3));
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:READ" })
        void createUser_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                CreateUserRequest request = new CreateUserRequest();
                request.setEmail("forbidden@example.com");
                request.setPassword("SecurePass123!");
                request.setName("Forbidden");
                request.setSurname("User");
                request.setRoleIds(Set.of(existingRoleId)); // Assuming existingRoleId is set in setUp

                // When/Then
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE" })
        void getUserById_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/users/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:READ" })
        void updateUser_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();
                UpdateUserRequest request = new UpdateUserRequest();
                request.setName("Updated");

                // When/Then
                client.put()
                                .uri("/api/v1/users/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:READ" })
        void deleteUser_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/users/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE" })
        void createUser_ShouldReturnConflict_WhenEmailAlreadyExists() {
                // Given
                CreateUserRequest request = new CreateUserRequest();
                request.setEmail("conflict" + UUID.randomUUID() + "@example.com");
                request.setPassword("Password123!");
                request.setName("Conflict");
                request.setSurname("User");
                request.setRoleIds(Set.of(existingRoleId));
                request.setActivated(true);

                // First creation succeeds
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated();

                // Second creation with same email should fail
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isEqualTo(409); // Conflict
        }

        @Test
        @WithMockCustomUser(authorities = { "USER:CREATE" })
        void createUser_ShouldReturnNotFound_WhenRoleIdDoesNotExist() {
                // Given
                CreateUserRequest request = new CreateUserRequest();
                request.setEmail("norole" + UUID.randomUUID() + "@example.com");
                request.setPassword("Password123!");
                request.setName("NoRole");
                request.setSurname("User");
                request.setRoleIds(Set.of(UUID.randomUUID())); // Non-existent Role ID
                request.setActivated(true);

                // When/Then
                client.post()
                                .uri("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isNotFound();
        }
}
