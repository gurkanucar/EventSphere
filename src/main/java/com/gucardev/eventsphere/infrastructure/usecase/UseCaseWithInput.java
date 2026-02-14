package com.gucardev.eventsphere.infrastructure.usecase;

public interface UseCaseWithInput<I> {

    void execute(I input);
}
