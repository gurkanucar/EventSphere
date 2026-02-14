package com.gucardev.eventsphere.infrastructure.usecase;

public interface UseCaseWithOutput<O> {
    O execute();
}
