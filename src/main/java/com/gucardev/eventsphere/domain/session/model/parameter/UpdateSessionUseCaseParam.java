package com.gucardev.eventsphere.domain.session.model.parameter;

import com.gucardev.eventsphere.domain.session.model.request.UpdateSessionRequest;
import java.util.UUID;

public record UpdateSessionUseCaseParam(UUID id, UpdateSessionRequest request) {
}
