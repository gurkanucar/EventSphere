package com.gucardev.eventsphere.domain.auth.permission.controller;

import com.jayway.jsonpath.JsonPath;
import com.gucardev.eventsphere.domain.auth.permission.model.request.CreatePermissionRequest;
import com.gucardev.eventsphere.domain.auth.permission.model.request.UpdatePermissionRequest;
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
// @WithMockUser(username = "admin", roles = { "ADMIN" })
// @WithMockCustomUser
class PermissionControllerTest {

        @Autowired
        private RestTestClient client;

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE" })
        void createPermission_ShouldReturnCreated_WhenRequestIsValid() {
                // Given
                CreatePermissionRequest request = new CreatePermissionRequest();
                request.setAction("CREATE_MVC");
                request.setResource("TEST_RESOURCE_MVC");
                request.setDisplayName("MockMvc Test Permission");
                request.setDescription("Desc");

                // When/Then
                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isNotEmpty()
                                .jsonPath("$.data.action").isEqualTo("CREATE_MVC")
                                .jsonPath("$.data.resource").isEqualTo("TEST_RESOURCE_MVC")
                                .jsonPath("$.data.displayName").isEqualTo("MockMvc Test Permission")
                                .jsonPath("$.data.description").isEqualTo("Desc");
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE" })
        void createPermission_ShouldReturnBadRequest_WhenRequestIsInvalid() {
                // Given
                CreatePermissionRequest request = new CreatePermissionRequest();
                // Missing required fields: action, resource, displayName

                // When/Then
                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isBadRequest();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:READ" })
        void getPermissionById_ShouldReturnPermission_WhenIdExists() {
                // Given - Create a permission first
                CreatePermissionRequest createRequest = new CreatePermissionRequest();
                createRequest.setAction("READ_MVC");
                createRequest.setResource("TEST_RES_MVC");
                createRequest.setDisplayName("Read MVC");
                createRequest.setDescription("Desc");

                String responseBody = client.post()
                                .uri("/api/v1/permissions")
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
                                .uri("/api/v1/permissions/{id}", id)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isEqualTo(id)
                                .jsonPath("$.data.action").isEqualTo("READ_MVC")
                                .jsonPath("$.data.resource").isEqualTo("TEST_RES_MVC")
                                .jsonPath("$.data.displayName").isEqualTo("Read MVC")
                                .jsonPath("$.data.description").isEqualTo("Desc");
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:READ" })
        void getPermissionById_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/permissions/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:UPDATE" })
        void updatePermission_ShouldReturnUpdatedPermission_WhenIdExists() {
                // Given - Create a permission first
                CreatePermissionRequest createRequest = new CreatePermissionRequest();
                createRequest.setAction("UPDATE_MVC");
                createRequest.setResource("TEST_RES_MVC");
                createRequest.setDisplayName("Update MVC");
                createRequest.setDescription("Desc");

                String responseBody = client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectBody(String.class)
                                .returnResult()
                                .getResponseBody();

                String id = JsonPath.read(responseBody, "$.data.id");

                UpdatePermissionRequest updateRequest = new UpdatePermissionRequest();
                updateRequest.setDisplayName("Updated Name MVC");
                updateRequest.setDescription("Updated Description");

                // When/Then
                client.put()
                                .uri("/api/v1/permissions/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.id").isEqualTo(id)
                                .jsonPath("$.data.displayName").isEqualTo("Updated Name MVC")
                                .jsonPath("$.data.description").isEqualTo("Updated Description")
                                .jsonPath("$.data.action").isEqualTo("UPDATE_MVC")
                                .jsonPath("$.data.resource").isEqualTo("TEST_RES_MVC");
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:UPDATE" })
        void updatePermission_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();
                UpdatePermissionRequest updateRequest = new UpdatePermissionRequest();
                updateRequest.setDisplayName("Updated Name");

                // When/Then
                client.put()
                                .uri("/api/v1/permissions/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(updateRequest)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:DELETE", "PERMISSION:READ" })
        void deletePermission_ShouldReturnSuccess_WhenIdExists() {
                // Given - Create a permission first
                CreatePermissionRequest createRequest = new CreatePermissionRequest();
                createRequest.setAction("DELETE_MVC");
                createRequest.setResource("TEST_RES_MVC");
                createRequest.setDisplayName("Delete MVC");
                createRequest.setDescription("Desc");

                String responseBody = client.post()
                                .uri("/api/v1/permissions")
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
                                .uri("/api/v1/permissions/{id}", id)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").doesNotExist();

                // Verify it is gone
                client.get()
                                .uri("/api/v1/permissions/{id}", id)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:DELETE" })
        void deletePermission_ShouldReturnNotFound_WhenIdDoesNotExist() {
                // Given
                UUID nonExistentId = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/permissions/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isNotFound();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:READ" })
        void getAllPermissions_ShouldReturnList() {
                // Given - Create some permissions
                CreatePermissionRequest createRequest1 = new CreatePermissionRequest();
                createRequest1.setAction("LIST1_MVC");
                createRequest1.setResource("RES");
                createRequest1.setDisplayName("L1");
                createRequest1.setDescription("D");

                CreatePermissionRequest createRequest2 = new CreatePermissionRequest();
                createRequest2.setAction("LIST2_MVC");
                createRequest2.setResource("RES");
                createRequest2.setDisplayName("L2");
                createRequest2.setDescription("D");

                CreatePermissionRequest createRequest3 = new CreatePermissionRequest();
                createRequest3.setAction("LIST3_MVC");
                createRequest3.setResource("RES");
                createRequest3.setDisplayName("L3");
                createRequest3.setDescription("D");

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest1)
                                .exchange()
                                .expectStatus().isCreated();

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest2)
                                .exchange()
                                .expectStatus().isCreated();

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest3)
                                .exchange()
                                .expectStatus().isCreated();

                // When/Then
                client.get()
                                .uri("/api/v1/permissions/all")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").isArray()
                                .jsonPath("$.data.length()")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(3))
                                .jsonPath("$.data[?(@.action == 'LIST1_MVC')]").exists()
                                .jsonPath("$.data[?(@.action == 'LIST2_MVC')]").exists()
                                .jsonPath("$.data[?(@.action == 'LIST3_MVC')]").exists()
                                .jsonPath("$.data[*].id").isNotEmpty()
                                .jsonPath("$.data[*].action").isNotEmpty()
                                .jsonPath("$.data[*].resource").isNotEmpty()
                                .jsonPath("$.data[*].displayName").isNotEmpty();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:READ" })
        void searchPermissions_ShouldFilterAndPaginate() {
                // Given - Create multiple permissions
                CreatePermissionRequest createRequest1 = new CreatePermissionRequest();
                createRequest1.setAction("SEARCH1_MVC");
                createRequest1.setResource("XYZ_RESOURCE");
                createRequest1.setDisplayName("Search 1");
                createRequest1.setDescription("Desc");

                CreatePermissionRequest createRequest2 = new CreatePermissionRequest();
                createRequest2.setAction("SEARCH2_MVC");
                createRequest2.setResource("XYZ_RESOURCE");
                createRequest2.setDisplayName("Search 2");
                createRequest2.setDescription("Desc");

                CreatePermissionRequest createRequest3 = new CreatePermissionRequest();
                createRequest3.setAction("OTHER_ACTION");
                createRequest3.setResource("OTHER_RESOURCE");
                createRequest3.setDisplayName("Other");
                createRequest3.setDescription("Desc");

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest1)
                                .exchange()
                                .expectStatus().isCreated();

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest2)
                                .exchange()
                                .expectStatus().isCreated();

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest3)
                                .exchange()
                                .expectStatus().isCreated();

                // When/Then: Search by Action
                client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/v1/permissions/search")
                                                .queryParam("action", "SEARCH1_MVC")
                                                .queryParam("page", "0")
                                                .queryParam("size", "10")
                                                .build())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.content").isArray()
                                .jsonPath("$.data.content.length()")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(1))
                                .jsonPath("$.data.content[0].action").isEqualTo("SEARCH1_MVC")
                                .jsonPath("$.data.content[0].resource").isEqualTo("XYZ_RESOURCE")
                                .jsonPath("$.data.content[0].displayName").isEqualTo("Search 1")
                                .jsonPath("$.data.content[0].id").isNotEmpty()
                                .jsonPath("$.data.pageable.totalElements")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(1));
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE", "PERMISSION:READ" })
        void searchPermissions_ShouldFilterByResource() {
                // Given
                CreatePermissionRequest createRequest = new CreatePermissionRequest();
                createRequest.setAction("ACTION_RES");
                createRequest.setResource("UNIQUE_RESOURCE_XYZ");
                createRequest.setDisplayName("Resource Test");
                createRequest.setDescription("Desc");

                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(createRequest)
                                .exchange()
                                .expectStatus().isCreated();

                // When/Then: Search by Resource
                client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/v1/permissions/search")
                                                .queryParam("resource", "UNIQUE_RESOURCE_XYZ")
                                                .queryParam("page", "0")
                                                .queryParam("size", "10")
                                                .build())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.content").isArray()
                                .jsonPath("$.data.content.length()")
                                .value(length -> assertThat((Integer) length).isGreaterThanOrEqualTo(1));
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:READ" })
        void searchPermissions_ShouldReturnEmptyPage_WhenNoMatches() {
                // When/Then: Search for non-existent action
                client.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api/v1/permissions/search")
                                                .queryParam("action", "NON_EXISTENT_ACTION_XYZ_123")
                                                .queryParam("page", "0")
                                                .queryParam("size", "10")
                                                .build())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.data").exists()
                                .jsonPath("$.data.content").isArray()
                                .jsonPath("$.data.content.length()").isEqualTo(0)
                                .jsonPath("$.data.pageable.totalElements").isEqualTo(0);
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:READ" })
        void createPermission_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                CreatePermissionRequest request = new CreatePermissionRequest();
                request.setAction("FORBIDDEN_ACTION");
                request.setResource("FORBIDDEN_RESOURCE");
                request.setDisplayName("Forbidden Permission");
                request.setDescription("Testing forbidden access");

                // When/Then
                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE" })
        void getPermissionById_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.get()
                                .uri("/api/v1/permissions/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:READ" })
        void updatePermission_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();
                UpdatePermissionRequest request = new UpdatePermissionRequest();
                request.setDescription("Updated Description");

                // When/Then
                client.put()
                                .uri("/api/v1/permissions/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:READ" })
        void deletePermission_ShouldReturnForbidden_WhenNotAuthorized() {
                // Given
                UUID id = UUID.randomUUID();

                // When/Then
                client.delete()
                                .uri("/api/v1/permissions/{id}", id)
                                .exchange()
                                .expectStatus().isForbidden();
        }

        @Test
        @WithMockCustomUser(authorities = { "PERMISSION:CREATE" })
        void createPermission_ShouldReturnConflict_WhenActionAndResourceAlreadyExists() {
                // Given
                CreatePermissionRequest request = new CreatePermissionRequest();
                request.setAction("CONFLICT_ACTION");
                request.setResource("CONFLICT_RESOURCE");
                request.setDisplayName("Conflict Permission");
                request.setDescription("Testing conflict");

                // First creation succeeds
                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isCreated();

                // Second creation with same action/resource should fail
                client.post()
                                .uri("/api/v1/permissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .exchange()
                                .expectStatus().isEqualTo(409); // Conflict
        }
}