package com.gucardev.eventsphere.domain.auth.role.controller;

import com.jayway.jsonpath.JsonPath;
import com.gucardev.eventsphere.domain.auth.role.model.request.CreateRoleRequest;
import com.gucardev.eventsphere.domain.auth.role.model.request.UpdateRoleRequest;
import com.gucardev.eventsphere.infrastructure.config.security.test.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureRestTestClient
@WithMockCustomUser
class RoleControllerTest {

        @Autowired
        private RestTestClient client;

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE" })
        void createRole_ShouldReturnCreated_WhenRequestIsValid() {
                // Given
                CreateRoleRequest request = new CreateRoleRequest();
                request.setName("TEST_ROLE");
                request.setDisplayName("Test Role");
                request.setDescription("Test Role Description");

                // When/Then
                client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isNotEmpty()
                                .jsonPath("$.data.name").isEqualTo("TEST_ROLE")
                                .jsonPath("$.data.displayName").isEqualTo("Test Role")
                                .jsonPath("$.data.description").isEqualTo("Test Role Description");
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE" })
        void createRole_ShouldReturnBadRequest_WhenRequestIsInvalid() {
                // Given
                CreateRoleRequest request = new CreateRoleRequest();
                // Missing required fields

                // When/Then
                client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isBadRequest();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE", "ROLE:READ" })
        void getRoleById_ShouldReturnRole_WhenIdExists() {
                // Given
                CreateRoleRequest createRequest = new CreateRoleRequest();
                createRequest.setName("READ_ROLE");
                createRequest.setDisplayName("Read Role");
                createRequest.setDescription("Read Role");

                String responseBody = client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String id = JsonPath.read(responseBody, "$.data.id");

                // When/Then
                client.get()
                                .uri("/api/v1/roles/{id}", id)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isEqualTo(id)
                                .jsonPath("$.data.name").isEqualTo("READ_ROLE");
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:READ" })
        void getRoleById_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/roles/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE", "ROLE:UPDATE" })
        void updateRole_ShouldReturnUpdatedRole_WhenIdExists() {
                // Given
                CreateRoleRequest createRequest = new CreateRoleRequest();
                createRequest.setName("UPDATE_ROLE");
                createRequest.setDisplayName("Update Role");
                createRequest.setDescription("Update Role");

                String responseBody = client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String id = JsonPath.read(responseBody, "$.data.id");

                UpdateRoleRequest updateRequest = new UpdateRoleRequest();
                updateRequest.setName("UPDATED_ROLE_NAME");
                updateRequest.setDisplayName("Updated Role Name");
                updateRequest.setDescription("Updated Description");

                // When/Then
                client.put()
                                .uri("/api/v1/roles/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isEqualTo(id)
                                .jsonPath("$.data.name").isEqualTo("UPDATED_ROLE_NAME")
                                .jsonPath("$.data.displayName").isEqualTo("Updated Role Name")
                                .jsonPath("$.data.description").isEqualTo("Updated Description");
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:UPDATE" })
        void updateRole_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();
                UpdateRoleRequest updateRequest = new UpdateRoleRequest();
                updateRequest.setName("Updated Name");
                updateRequest.setDisplayName("Updated Name");

                // When/Then
                client.put()
                                .uri("/api/v1/roles/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE", "ROLE:DELETE", "ROLE:READ" })
        void deleteRole_ShouldReturnSuccess_WhenIdExists() {
                // Given
                CreateRoleRequest createRequest = new CreateRoleRequest();
                createRequest.setName("DELETE_ROLE");
                createRequest.setDisplayName("Delete Role");
                createRequest.setDescription("Delete Role");

                String responseBody = client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String id = JsonPath.read(responseBody, "$.data.id");

                // When/Then
                client.delete()
                                .uri("/api/v1/roles/{id}", id)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").doesNotExist();

                // Verify it is gone
                client.get()
                                .uri("/api/v1/roles/{id}", id)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:DELETE" })
        void deleteRole_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/roles/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE", "ROLE:READ" })
        void getAllRoles_ShouldReturnList() {
                // Given
                CreateRoleRequest createRequest1 = new CreateRoleRequest();
                createRequest1.setName("LIST_ROLE_1");
                createRequest1.setDisplayName("List Role 1");
                createRequest1.setDescription("Desc");
                client.post().uri("/api/v1/roles").contentType(MediaType.APPLICATION_JSON).body(createRequest1)
                                .exchange();

                CreateRoleRequest createRequest2 = new CreateRoleRequest();
                createRequest2.setName("LIST_ROLE_2");
                createRequest2.setDisplayName("List Role 2");
                createRequest2.setDescription("Desc");
                client.post().uri("/api/v1/roles").contentType(MediaType.APPLICATION_JSON).body(createRequest2)
                                .exchange();

                // When/Then
                client.get()
                                .uri("/api/v1/roles/all")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").isArray()
                                .jsonPath("$.data.length()")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(2));
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE", "ROLE:READ" })
        void searchRoles_ShouldFilterAndPaginate() {
                // Given
                CreateRoleRequest createRequest = new CreateRoleRequest();
                createRequest.setName("SEARCH_ROLE");
                createRequest.setDisplayName("Search Role");
                createRequest.setDescription("Desc");
                client.post().uri("/api/v1/roles").contentType(MediaType.APPLICATION_JSON).body(createRequest)
                                .exchange();

                // When/Then
                client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/v1/roles/search")
                                                .queryParam("name", "SEARCH_ROLE")
                                                .build())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data.content").isArray()
                                .jsonPath("$.data.content[0].name").isEqualTo("SEARCH_ROLE");
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:READ" })
        void createRole_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                CreateRoleRequest request = new CreateRoleRequest();
                request.setName("FORBIDDEN_ROLE");
                request.setDisplayName("Forbidden Role");

                // When/Then
                client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE" })
        void getRoleById_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/roles/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:READ" })
        void updateRole_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();
                UpdateRoleRequest request = new UpdateRoleRequest();
                request.setName("Updated");

                // When/Then
                client.put()
                                .uri("/api/v1/roles/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:READ" })
        void deleteRole_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/roles/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE" })
        void searchRoles_ShouldReturnForbidden_WhenNotAuthorized() {
                // When/Then
                client.get()
                                .uri("/api/v1/roles/search")
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "ROLE:CREATE" })
        void createRole_ShouldReturnConflict_WhenNameAlreadyExists() {
                // Given
                CreateRoleRequest request = new CreateRoleRequest();
                request.setName("CONFLICT_ROLE");
                request.setDisplayName("Conflict Role");
                request.setDescription("Role to test conflict");

                // First creation succeeds
                client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated();

                // Second creation with same name should fail
                client.post()
                                .uri("/api/v1/roles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isEqualTo(409); // Conflict
        }
}
