package com.annimon.stream.test.mockito;

import com.annimon.stream.CompatOptional;
import com.annimon.stream.test.mockito.OptionalMatcher.EmptyOptionalMatcher;
import com.annimon.stream.test.mockito.OptionalMatcher.PresentOptionalMatcher;

import org.junit.Test;
import org.mockito.Mockito;

import static com.annimon.stream.test.hamcrest.CommonMatcher.hasOnlyPrivateConstructors;
import static com.annimon.stream.test.mockito.OptionalMatcher.anyEmptyOptional;
import static com.annimon.stream.test.mockito.OptionalMatcher.anyPresentOptional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class CompatOptionalMatcherTest {

    private static final CompatOptional<Object> PRESENT_OBJECT_COMPAT_OPTIONAL = CompatOptional.of(new Object());
    private static final CompatOptional<Object> EMPTY_OBJECT_COMPAT_OPTIONAL = CompatOptional.empty();
    private static final CompatOptional<String> PRESENT_STRING_COMPAT_OPTIONAL = CompatOptional.of("ANY_STRING");
    private static final CompatOptional<String> EMPTY_STRING_COMPAT_OPTIONAL = CompatOptional.empty();

    @Test
    public void testPrivateConstructor() throws Exception {
        assertThat(OptionalMatcher.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testPresentOptionalMatcherMatching() {
        PresentOptionalMatcher<Object> matcher = new PresentOptionalMatcher<Object>();

        assertTrue(matcher.matches(PRESENT_OBJECT_COMPAT_OPTIONAL));
        assertFalse(matcher.matches(EMPTY_OBJECT_COMPAT_OPTIONAL));
        assertFalse(matcher.matches(null));
    }

    @Test
    public void testPresentOptionalMatcherToString() {
        PresentOptionalMatcher<Object> matcher = new PresentOptionalMatcher<Object>();

        assertEquals(matcher.toString(), "anyPresentOptional()");
    }

    @Test
    public void testAnyPresentObjectOptionalWhenStubbing() {
        Foo foo = Mockito.mock(Foo.class);
        when(foo.barObject(anyPresentOptional())).thenReturn(true);

        assertFalse(foo.barObject(EMPTY_OBJECT_COMPAT_OPTIONAL));
        assertTrue(foo.barObject(PRESENT_OBJECT_COMPAT_OPTIONAL));
    }

    @Test
    public void testAnyPresentObjectOptionalWhenVerifying() {
        Foo foo = Mockito.mock(Foo.class);

        foo.barObject(PRESENT_OBJECT_COMPAT_OPTIONAL);

        verify(foo, times(1)).barObject(anyPresentOptional());
        verify(foo, never()).barObject(anyEmptyOptional());
    }

    @Test
    public void testAnyPresentStringOptionalWhenStubbing() {
        Foo foo = Mockito.mock(Foo.class);
        when(foo.barString(anyPresentOptional(String.class))).thenReturn(true);

        assertFalse(foo.barString(EMPTY_STRING_COMPAT_OPTIONAL));
        assertTrue(foo.barString(PRESENT_STRING_COMPAT_OPTIONAL));
    }

    @Test
    public void testAnyPresentStringOptionalWhenVerifying() {
        Foo foo = Mockito.mock(Foo.class);

        foo.barString(PRESENT_STRING_COMPAT_OPTIONAL);

        verify(foo, times(1)).barString(anyPresentOptional(String.class));
        verify(foo, never()).barString(anyEmptyOptional(String.class));
    }

    @Test
    public void testEmptyOptionalMatcherMatching() {
        EmptyOptionalMatcher<Object> matcher = new EmptyOptionalMatcher<Object>();

        assertFalse(matcher.matches(PRESENT_OBJECT_COMPAT_OPTIONAL));
        assertTrue(matcher.matches(EMPTY_OBJECT_COMPAT_OPTIONAL));
        assertFalse(matcher.matches(null));
    }

    @Test
    public void testEmptyOptionalMatcherToString() {
        EmptyOptionalMatcher<Object> matcher = new EmptyOptionalMatcher<Object>();

        assertEquals(matcher.toString(), "anyEmptyOptional()");
    }

    @Test
    public void testAnyEmptyObjectOptionalWhenStubbing() {
        Foo foo = Mockito.mock(Foo.class);
        when(foo.barObject(anyEmptyOptional())).thenReturn(true);

        assertTrue(foo.barObject(EMPTY_OBJECT_COMPAT_OPTIONAL));
        assertFalse(foo.barObject(PRESENT_OBJECT_COMPAT_OPTIONAL));
    }

    @Test
    public void testAnyEmptyObjectOptionalWhenVerifying() {
        Foo foo = Mockito.mock(Foo.class);

        foo.barObject(EMPTY_OBJECT_COMPAT_OPTIONAL);

        verify(foo, never()).barObject(anyPresentOptional());
        verify(foo, times(1)).barObject(anyEmptyOptional());
    }

    @Test
    public void testAnyEmptyStringOptionalWhenStubbing() {
        Foo foo = Mockito.mock(Foo.class);
        when(foo.barString(anyEmptyOptional(String.class))).thenReturn(true);

        assertTrue(foo.barString(EMPTY_STRING_COMPAT_OPTIONAL));
        assertFalse(foo.barString(PRESENT_STRING_COMPAT_OPTIONAL));
    }

    @Test
    public void testAnyEmptyStringOptionalWhenVerifying() {
        Foo foo = Mockito.mock(Foo.class);

        foo.barString(EMPTY_STRING_COMPAT_OPTIONAL);

        verify(foo, never()).barString(anyPresentOptional(String.class));
        verify(foo, times(1)).barString(anyEmptyOptional(String.class));
    }

    private interface Foo {
        boolean barObject(CompatOptional<Object> argument);

        boolean barString(CompatOptional<String> argument);
    }
}
