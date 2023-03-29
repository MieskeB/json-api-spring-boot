package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.annotation.JsonApiObject;
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import nl.michelbijnen.jsonapi.test.mock.UserDto;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Console;
import java.net.MalformedURLException;

public class LinkTest {

    private ObjectDto objectDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    @Test
    public void testIfLinksExists() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertNotNull(jsonObject.getJSONObject("links"));
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfFirstRelWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=0",
                jsonObject.getJSONObject("links").getString("first"));
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfPreviousRelWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=0",
                jsonObject.getJSONObject("links").getString("previous"));
    }

    @Test
    public void testIfSelfRelWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId(),
                jsonObject.getJSONObject("links").getString("self"));
    }

    @Test
    public void testIfMalformedUrlThrowsException() {
        String tempUrl = this.objectDto.getSelfRel();
        this.objectDto.setSelfRel("NotValidUrl");
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(this.objectDto));
        this.objectDto.setSelfRel(tempUrl);
        try {
            jsonObject.getJSONObject("links");
            jsonObject.getJSONObject("links").getString("self");
            fail();
        } catch (JSONException e) {
            assertTrue(e.getMessage().endsWith("not found."));
        }
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfNextRelWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=2",
                jsonObject.getJSONObject("links").getString("next"));
    }

    @Test
    @Ignore("Planned for future update")
    public void testIfLastRelWorks() {
        JSONObject jsonObject = new JSONObject(JsonApiConverter.convert(objectDto));
        assertEquals("http://localhost:8080/object/" + this.objectDto.getId() + "?page=2",
                jsonObject.getJSONObject("links").getString("last"));
    }
}
