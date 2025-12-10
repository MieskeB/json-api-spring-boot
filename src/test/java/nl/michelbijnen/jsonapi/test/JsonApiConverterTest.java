package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.parser.JsonApiOptions;
import nl.michelbijnen.jsonapi.parser.JsonApiParser;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JsonApiConverterTest {

    private final MockDataGenerator generator = MockDataGenerator.getInstance();
    private ObjectDto objectDto;


    @Before
    public void setUp() throws Exception {
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    @Test(expected = JsonApiException.class)
    public void testConvertThrowsJsonApiException() {

        try (MockedConstruction<JsonApiParser> mockedParser = mockConstruction(JsonApiParser.class,
                (mock, context) ->
                        when(mock.parse(any(), anyInt(), any(ObjectMapper.class)))
                                .thenThrow(new JsonApiException("Test exception")))) {
            JsonApiConverter.convert(objectDto);
        }
    }

    @Test(expected = JsonApiException.class)
    public void testConvertWithOptionsThrowsJsonApiException() {
        JsonApiOptions options = JsonApiOptions.builder().build();
        try (MockedConstruction<JsonApiParser> mockedParser = mockConstruction(JsonApiParser.class,
                (mock, context) -> {
                    when(mock.parse(any(), anyInt(), any(ObjectMapper.class), eq(options))).thenThrow(new JsonApiException("Test exception"));
                })) {
            JsonApiConverter.convert(objectDto, options);
        }
    }
}