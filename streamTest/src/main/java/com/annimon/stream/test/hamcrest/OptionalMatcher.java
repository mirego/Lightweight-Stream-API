package com.annimon.stream.test.hamcrest;

import com.annimon.stream.CompatOptional;

import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class OptionalMatcher {

    private OptionalMatcher() { }

    public static Matcher<CompatOptional<?>> isPresent() {
        return new IsPresentMatcher();
    }

    public static Matcher<CompatOptional<?>> isEmpty() {
        return new IsEmptyMatcher();
    }

    public static <T> Matcher<CompatOptional<T>> hasValue(T object) {
        return hasValueThat(is(object));
    }

    public static <T> Matcher<CompatOptional<T>> hasValueThat(Matcher<? super T> matcher) {
        return new HasValueMatcher<T>(matcher);
    }

    public static class IsPresentMatcher extends TypeSafeDiagnosingMatcher<CompatOptional<?>> {

        @Override
        protected boolean matchesSafely(CompatOptional<?> compatOptional, Description mismatchDescription) {
            mismatchDescription.appendText("CompatOptional was empty");
            return compatOptional.isPresent();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("CompatOptional value should be present");
        }
    }

    public static class IsEmptyMatcher extends TypeSafeDiagnosingMatcher<CompatOptional<?>> {

        @Override
        protected boolean matchesSafely(CompatOptional<?> compatOptional, Description mismatchDescription) {
            mismatchDescription.appendText("CompatOptional was present");
            return compatOptional.isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("CompatOptional value should be empty");
        }
    }

    public static class HasValueMatcher<T> extends TypeSafeDiagnosingMatcher<CompatOptional<T>> {

        private final Matcher<? super T> matcher;

        public HasValueMatcher(Matcher<? super T> matcher) {
            this.matcher = matcher;
        }

        @Override
        protected boolean matchesSafely(CompatOptional<T> compatOptional, Description mismatchDescription) {
            if (compatOptional.isEmpty()) {
                mismatchDescription.appendText("CompatOptional was empty");
                return false;
            }
            final T value = compatOptional.get();
            mismatchDescription.appendText("CompatOptional value ");
            matcher.describeMismatch(value, mismatchDescription);
            return matcher.matches(value);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("CompatOptional value ").appendDescriptionOf(matcher);
        }
    }
}
