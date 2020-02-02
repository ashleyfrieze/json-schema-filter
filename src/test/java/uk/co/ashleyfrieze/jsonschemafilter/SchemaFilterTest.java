package uk.co.ashleyfrieze.jsonschemafilter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SchemaFilterTest {
    @Test
    void nullInputIsError() {
        assertThatThrownBy(() -> SchemaFilter.filter(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}