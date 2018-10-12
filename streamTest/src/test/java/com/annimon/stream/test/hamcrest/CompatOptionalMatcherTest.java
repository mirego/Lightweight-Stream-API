package com.annimon.stream.test.hamcrest;

import com.annimon.stream.CompatOptional;

import org.junit.Test;

import static com.annimon.stream.test.hamcrest.CommonMatcher.description;
import static com.annimon.stream.test.hamcrest.CommonMatcher.hasOnlyPrivateConstructors;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.isEmpty;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.isPresent;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.hasValue;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.hasValueThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class CompatOptionalMatcherTest {

    @Test
    public void testPrivateConstructor() throws Exception {
        assertThat(OptionalMatcher.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testIsPresent() {
        CompatOptional<Integer> compatOptional = CompatOptional.of(5);
        assertThat(compatOptional, isPresent());
        assertThat(compatOptional, not(isEmpty()));

        assertThat(isPresent(), description(is("CompatOptional value should be present")));
    }

    @Test
    public void testIsEmpty() {
        CompatOptional<Integer> compatOptional = CompatOptional.empty();
        assertThat(compatOptional, isEmpty());
        assertThat(compatOptional, not(isPresent()));

        assertThat(isEmpty(), description(is("CompatOptional value should be empty")));
    }

    @Test
    public void testHasValue() {
        CompatOptional<String> compatOptional = CompatOptional.of("text");
        assertThat(compatOptional, hasValue("text"));
        assertThat(compatOptional, not(hasValue("test")));

        assertThat(hasValue(42), description(is("CompatOptional value is <42>")));
    }

    @Test
    public void testHasValueThat() {
        CompatOptional<String> compatOptional = CompatOptional.of("text");
        assertThat(compatOptional, hasValueThat(startsWith("te")));

        assertThat(hasValueThat(is(42)), description(is("CompatOptional value is <42>")));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnEmptyOptional() {
        assertThat(CompatOptional.<String>empty(), hasValue(""));
    }

    @Test(expected = AssertionError.class)
    public void testIsEmptyOnNullValue() {
        assertThat(null, isEmpty());
    }

    @Test(expected = AssertionError.class)
    public void testIsPresentOnNullValue() {
        assertThat(null, isPresent());
    }

    @Test(expected = AssertionError.class)
    public void testHasValueOnNullValue() {
        assertThat(null, hasValue(""));
    }

    @Test(expected = AssertionError.class)
    public void testHasValueThatOnNullValue() {
        assertThat(null, hasValueThat(is("")));
    }
}
