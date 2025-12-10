package nl.michelbijnen.jsonapi.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.UserDto;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserTest {

    private UserDto userDto;
    private ObjectMapper mapper;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.userDto = (UserDto) generator.getUserDto().clone();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void testIfLinksExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNotNull(json.get("links"));
    }

    @Test
    public void testIfDataExists() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertNotNull(json.get("data"));
    }

    @Test
    public void testIfDataContainsId() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertEquals(userDto.getId(), json.get("data").get("id").asText());
    }

    @Test
    public void testIfDataContainsType() throws Exception {
        JsonNode json = mapper.readTree(JsonApiConverter.convert(userDto));
        assertEquals("User", json.get("data").get("type").asText());
    }

    @Test
    public void testIfEmptyArrayWillWork() throws Exception {
        String result = JsonApiConverter.convert(new ArrayList<>());
        JsonNode json = mapper.readTree(result);
        assertEquals("[]", json.get("data").toString());
    }
}
