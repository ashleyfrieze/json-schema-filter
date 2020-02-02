package uk.co.ashleyfrieze.jsonschemafilter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.ashleyfrieze.jsonschemafilter.SchemaFilter.normalize;
import static uk.co.ashleyfrieze.jsonschemafilter.SchemaFilter.load;

class SchemaFilterTest {

    @Test
    void nullInputIsError() {
        assertThatThrownBy(() -> normalize(null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validJsonWithValidSchemaIsUnchanged() {
        String validJson = "{\"name\":\"Bill\"}";
        String schema = "{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}}}";

        assertEquals(validJson, normalize(validJson, load(schema)));
    }

    @Test
    void canApplyDefaultNameToEmptyObject() {
        String validJson = "{\"name\":\"Namey McNameFace\"}";
        String emptyObject = "{}";
        String schema = "{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"default\":\"Namey McNameFace\"}}}";

        assertEquals(validJson, normalize(emptyObject, load(schema)));
    }

    @Test
    void canApplyDefaultObjectToEmptyObject() {
        String validJson = "{\"identity\":{\"name\":\"Namey McNameFace\",\"age\":19}}";
        String emptyObject = "{}";
        String schema = "{\"type\":\"object\",\"properties\":{\"identity\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\",\"default\":\"Namey McNameFace\"},\"age\":{\"type\":\"number\",\"default\":19}},\"default\":{\"name\":\"Namey McNameFace\",\"age\":19}}}}";
        assertEquals(validJson, normalize(emptyObject, load(schema)));
    }

    @Test
    void removeUnknownField() {
        String validJson = "{\"name\":\"Bill\"}";
        String jsonWithExtraField = "{\"name\":\"Bill\", \"history\":[]}";
        String schema = "{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}}}";

        assertEquals(validJson, normalize(jsonWithExtraField, load(schema)));
    }

    @Test
    void removeUnknownFieldInArray() {
        String validJson = "{\"names\":[{\"name\":\"Bill\"}]}";
        String jsonWithExtraField = "{\"names\":[{\"name\":\"Bill\", \"history\":[]}]}";
        String schema = "{\"type\":\"object\",\"properties\":{\"names\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"}}},\"default\":[]}}}";

        assertEquals(validJson, normalize(jsonWithExtraField, load(schema)));
    }
}