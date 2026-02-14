package com.gucardev.eventsphere.infrastructure.usecase;

public interface UseCase<I, O> {

    O execute(I input);
}
