package com.gucardev.eventsphere.infrastructure.config;

import com.gucardev.eventsphere.infrastructure.exception.ExceptionType;
import com.gucardev.eventsphere.infrastructure.response.ApiResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.gucardev.eventsphere.infrastructure.exception.ExceptionUtil.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/test/exceptions")
@Tag(name = "Exception Test", description = "Endpoints to test exception handling")
@Profile({"dev", "local"}) // Only available in dev/local environments
public class ExceptionTestController {

    private final AsyncExceptionService asyncService;
    private final ScheduledExceptionService scheduledService;

    public ExceptionTestController(AsyncExceptionService asyncService,
                                   ScheduledExceptionService scheduledService) {
        this.asyncService = asyncService;
        this.scheduledService = scheduledService;
    }

    // ==================== BUSINESS EXCEPTIONS ====================

    @GetMapping("/not-found/{id}")
    @Operation(summary = "Simulate NOT_FOUND exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testNotFound(@PathVariable Long id) {
        throw notFound("Product", id);
    }

    @GetMapping("/already-exists")
    @Operation(summary = "Simulate ALREADY_EXISTS exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testAlreadyExists() {
        throw alreadyExists("User with email test@example.com");
    }

    @GetMapping("/forbidden")
    @Operation(summary = "Simulate FORBIDDEN exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testForbidden() {
        throw forbidden();
    }

    @GetMapping("/unauthorized")
    @Operation(summary = "Simulate UNAUTHORIZED exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testUnauthorized() {
        throw unauthorized();
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Simulate OUT_OF_STOCK exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testOutOfStock() {
        throw of(ExceptionType.OUT_OF_STOCK, "iPhone 15 Pro");
    }

    @GetMapping("/payment-failed")
    @Operation(summary = "Simulate PAYMENT_FAILED exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testPaymentFailed() {
        throw of(ExceptionType.PAYMENT_FAILED);
    }

    @GetMapping("/custom/{type}")
    @Operation(summary = "Simulate any ExceptionType by name")
    public ResponseEntity<ApiResponseWrapper<Object>> testCustomException(
            @PathVariable String type,
            @RequestParam(required = false) String arg1,
            @RequestParam(required = false) String arg2) {

        ExceptionType exceptionType;
        try {
            exceptionType = ExceptionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw of(ExceptionType.DEFAULT);
        }

        if (arg1 != null && arg2 != null) {
            throw of(exceptionType, arg1, arg2);
        } else if (arg1 != null) {
            throw of(exceptionType, arg1);
        } else {
            throw of(exceptionType);
        }
    }

    // ==================== VALIDATION EXCEPTIONS ====================

    @PostMapping("/validation")
    @Operation(summary = "Simulate validation exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testValidation(
            @Valid @RequestBody ValidationTestRequest request) {
        return ResponseEntity.ok(ApiResponseWrapper.success(request));
    }

    @GetMapping("/validation/params")
    @Operation(summary = "Simulate validation exception with query params")
    public ResponseEntity<ApiResponseWrapper<Object>> testParamValidation(
            @RequestParam @Min(1) Long id,
            @RequestParam @NotBlank String name,
            @RequestParam @Email String email) {
        return ResponseEntity.ok(ApiResponseWrapper.success("Valid params"));
    }

    // ==================== SYSTEM EXCEPTIONS ====================

    @GetMapping("/null-pointer")
    @Operation(summary = "Simulate NullPointerException")
    public ResponseEntity<ApiResponseWrapper<Object>> testNullPointer() {
        String nullString = null;
        return ResponseEntity.ok(ApiResponseWrapper.success(nullString.length())); // NPE!
    }

    @GetMapping("/illegal-argument")
    @Operation(summary = "Simulate IllegalArgumentException")
    public ResponseEntity<ApiResponseWrapper<Object>> testIllegalArgument() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    @GetMapping("/illegal-state")
    @Operation(summary = "Simulate IllegalStateException")
    public ResponseEntity<ApiResponseWrapper<Object>> testIllegalState() {
        throw new IllegalStateException("Object is in invalid state");
    }

    @GetMapping("/runtime")
    @Operation(summary = "Simulate generic RuntimeException")
    public ResponseEntity<ApiResponseWrapper<Object>> testRuntimeException() {
        throw new RuntimeException("Something unexpected happened");
    }

    @GetMapping("/arithmetic")
    @Operation(summary = "Simulate ArithmeticException (divide by zero)")
    public ResponseEntity<ApiResponseWrapper<Object>> testArithmetic() {
        int result = 10 / 0; // ArithmeticException
        return ResponseEntity.ok(ApiResponseWrapper.success(result));
    }

    @GetMapping("/array-index")
    @Operation(summary = "Simulate ArrayIndexOutOfBoundsException")
    public ResponseEntity<ApiResponseWrapper<Object>> testArrayIndex() {
        int[] arr = {1, 2, 3};
        return ResponseEntity.ok(ApiResponseWrapper.success(arr[10])); // Out of bounds
    }

    // ==================== NESTED EXCEPTIONS ====================

    @GetMapping("/nested")
    @Operation(summary = "Simulate nested exception (cause chain)")
    public ResponseEntity<ApiResponseWrapper<Object>> testNestedException() {
        try {
            try {
                throw new IllegalArgumentException("Root cause: invalid input");
            } catch (Exception e) {
                throw new IllegalStateException("Middle layer: processing failed", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Top layer: operation failed", e);
        }
    }

    @GetMapping("/deep-nested/{depth}")
    @Operation(summary = "Simulate deeply nested exception")
    public ResponseEntity<ApiResponseWrapper<Object>> testDeepNested(@PathVariable int depth) {
        throw createNestedExceptions(depth, 0);
    }

    private RuntimeException createNestedExceptions(int maxDepth, int current) {
        if (current >= maxDepth) {
            return new RuntimeException("Root cause at depth " + current);
        }
        return new RuntimeException("Exception at depth " + current,
                createNestedExceptions(maxDepth, current + 1));
    }

    // ==================== ASYNC EXCEPTIONS ====================

    @GetMapping("/async/void")
    @Operation(summary = "Trigger async void method exception (check logs)")
    public ResponseEntity<ApiResponseWrapper<String>> testAsyncVoid() {
        asyncService.asyncVoidMethod();
        return ResponseEntity.ok(ApiResponseWrapper.success(
                "Async void method triggered - check logs for exception"));
    }

    @GetMapping("/async/future")
    @Operation(summary = "Trigger async CompletableFuture exception")
    public ResponseEntity<ApiResponseWrapper<String>> testAsyncFuture() {
        CompletableFuture<String> future = asyncService.asyncFutureMethod();

        try {
            String result = future.get(); // Will throw ExecutionException
            return ResponseEntity.ok(ApiResponseWrapper.success(result));
        } catch (Exception e) {
            throw new RuntimeException("Async execution failed", e);
        }
    }

    @GetMapping("/async/business")
    @Operation(summary = "Trigger async BusinessException (check logs)")
    public ResponseEntity<ApiResponseWrapper<String>> testAsyncBusiness() {
        asyncService.asyncBusinessException();
        return ResponseEntity.ok(ApiResponseWrapper.success(
                "Async business exception triggered - check logs"));
    }

    // ==================== SCHEDULER EXCEPTIONS ====================

    @PostMapping("/scheduler/trigger")
    @Operation(summary = "Trigger immediate scheduler exception")
    public ResponseEntity<ApiResponseWrapper<String>> triggerScheduler() {
        scheduledService.setTriggerException(true);
        return ResponseEntity.ok(ApiResponseWrapper.success(
                "Scheduler exception will trigger on next run (every 30 seconds) - check logs"));
    }

    @DeleteMapping("/scheduler/reset")
    @Operation(summary = "Reset scheduler to not throw")
    public ResponseEntity<ApiResponseWrapper<String>> resetScheduler() {
        scheduledService.setTriggerException(false);
        return ResponseEntity.ok(ApiResponseWrapper.success("Scheduler reset"));
    }

    // ==================== SECURITY EXCEPTIONS ====================

    @GetMapping("/security/access-denied")
    @PreAuthorize("hasRole('SUPER_ADMIN_THAT_DOESNT_EXIST')")
    @Operation(summary = "Simulate Spring Security access denied")
    public ResponseEntity<ApiResponseWrapper<Object>> testAccessDenied() {
        return ResponseEntity.ok(ApiResponseWrapper.success("You shouldn't see this"));
    }

    // ==================== SLOW / TIMEOUT SIMULATION ====================

    @GetMapping("/slow/{seconds}")
    @Operation(summary = "Simulate slow endpoint (for timeout testing)")
    public ResponseEntity<ApiResponseWrapper<String>> testSlow(@PathVariable int seconds)
            throws InterruptedException {
        if (seconds > 30) seconds = 30; // Cap at 30 seconds
        Thread.sleep(seconds * 1000L);
        return ResponseEntity.ok(ApiResponseWrapper.success("Completed after " + seconds + " seconds"));
    }

    // ==================== MEMORY / RESOURCE EXCEPTIONS ====================

    @GetMapping("/out-of-memory")
    @Operation(summary = "Simulate OutOfMemoryError (USE WITH CAUTION!)")
    public ResponseEntity<ApiResponseWrapper<Object>> testOutOfMemory(
            @RequestParam(defaultValue = "false") boolean confirm) {
        if (!confirm) {
            return ResponseEntity.ok(ApiResponseWrapper.success(
                    "Add ?confirm=true to actually trigger OOM - this may crash the app!"));
        }

        // This will cause OOM
        var list = new java.util.ArrayList<byte[]>();
        while (true) {
            list.add(new byte[1024 * 1024 * 10]); // 10MB chunks
        }
    }

    // ==================== REQUEST DTOs ====================

    @Data
    public static class ValidationTestRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @Min(value = 1, message = "Age must be at least 1")
        private Integer age;

        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
    }
}

// ==================== ASYNC SERVICE ====================

@Slf4j
@Service
class AsyncExceptionService {

    @Async("asyncExecutor")
    public void asyncVoidMethod() {
        log.info("Async void method started");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new RuntimeException("Exception in async void method!");
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> asyncFutureMethod() {
        log.info("Async future method started");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        throw new RuntimeException("Exception in async future method!");
    }

    @Async("asyncExecutor")
    public void asyncBusinessException() {
        log.info("Async business exception method started");
        throw notFound("AsyncProduct", 999L);
    }
}

// ==================== SCHEDULED SERVICE ====================

@Setter
@Slf4j
@Service
class ScheduledExceptionService {

    private volatile boolean triggerException = false;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void scheduledTask() {
        if (triggerException) {
            log.info("Scheduled task triggering exception...");
            triggerException = false; // Reset after triggering
            throw new RuntimeException("Exception in scheduled task!");
        }
    }

//    @Scheduled(fixedRate = 60000) // Every minute
//    public void scheduledBusinessException() {
//        // This one triggers based on time for demo
//        if (System.currentTimeMillis() % 5 == 0) { // Rarely triggers
//            throw notFound("ScheduledEntity", 123L);
//        }
//    }
}