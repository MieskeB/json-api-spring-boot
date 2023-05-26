package nl.michelbijnen.jsonapi.generator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;

import java.io.IOException;

public class JsonApiSerializer extends StdSerializer<Object> {

    public JsonApiSerializer() {
        this(null);
    }

    public JsonApiSerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeRawValue(JsonApiConverter.convert(value));
    }
}
