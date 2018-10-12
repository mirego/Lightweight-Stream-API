package com.annimon.stream.test.mockito;

import com.annimon.stream.CompatOptional;
import org.mockito.ArgumentMatcher;
import static org.mockito.ArgumentMatchers.argThat;

public class OptionalMatcher {

    private OptionalMatcher() { }

    public static <T> CompatOptional<T> anyPresentOptional() {
        return argThat(new PresentOptionalMatcher<T>());
    }

    public static <T> CompatOptional<T> anyPresentOptional(@SuppressWarnings("UnusedParameters") Class<T> clazz) {
        return argThat(new PresentOptionalMatcher<T>());
    }

    public static <T> CompatOptional<T> anyEmptyOptional() {
        return argThat(new EmptyOptionalMatcher<T>());
    }

    public static <T> CompatOptional<T> anyEmptyOptional(@SuppressWarnings("UnusedParameters") Class<T> clazz) {
        return argThat(new EmptyOptionalMatcher<T>());
    }

    public static class PresentOptionalMatcher<T> implements ArgumentMatcher<CompatOptional<T>> {

        @Override
        public boolean matches(CompatOptional<T> argument) {
            return argument != null && argument.isPresent();
        }

        @Override
        public String toString() {
            return "anyPresentOptional()";
        }
    }

    public static class EmptyOptionalMatcher<T> implements ArgumentMatcher<CompatOptional<T>> {

        @Override
        public boolean matches(CompatOptional<T> argument) {
            return argument != null && argument.isEmpty();
        }

        @Override
        public String toString() {
            return "anyEmptyOptional()";
        }
    }
}
