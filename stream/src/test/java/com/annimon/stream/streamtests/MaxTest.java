package com.annimon.stream.streamtests;

import com.annimon.stream.Functions;
import com.annimon.stream.CompatOptional;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import org.junit.Test;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.isPresent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public final class MaxTest {

    @Test
    public void testMax() {
        CompatOptional<Integer> max = Stream.of(6, 3, 9, 0, -7, 19)
                .max(Functions.naturalOrder());

        assertThat(max, isPresent());
        assertNotNull(max.get());
        assertEquals(19, (int) max.get());
    }

    @Test
    public void testMaxDescendingOrder() {
        CompatOptional<Integer> max = Stream.of(6, 3, 9, 0, -7, 19)
                .max(Functions.descendingAbsoluteOrder());

        assertThat(max, isPresent());
        assertNotNull(max.get());
        assertEquals(0, (int) max.get());
    }

    @Test
    public void testMaxEmpty() {
        CompatOptional<Integer> max = Stream.<Integer>empty()
                .max(Functions.naturalOrder());

        assertThat(max, OptionalMatcher.isEmpty());
    }
}
