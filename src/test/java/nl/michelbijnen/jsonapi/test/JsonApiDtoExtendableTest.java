package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonApiDtoExtendableTest {

    private ObjectDto objectDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    @Test
    public void testIfGettersAndSettersWork() {
        String baseUrl = "http://localhost:8080/";
        this.objectDto.setSelfRel(baseUrl + "self");
        assertEquals(baseUrl + "self", this.objectDto.getSelfRel());

        this.objectDto.setAllSelfRel(baseUrl + "selfall");
        assertEquals(baseUrl + "selfall", this.objectDto.getAllSelfRel());

        this.objectDto.setFirstRel(baseUrl + "first");
        assertEquals(baseUrl + "first", this.objectDto.getFirstRel());

        this.objectDto.setLastRel(baseUrl + "last");
        assertEquals(baseUrl + "last", this.objectDto.getLastRel());

        this.objectDto.setNextRel(baseUrl + "next");
        assertEquals(baseUrl + "next", this.objectDto.getNextRel());

        this.objectDto.setPreviousRel(baseUrl + "previous");
        assertEquals(baseUrl + "previous", this.objectDto.getPreviousRel());
    }

    @Test
    public void testIfIdIsNotSetGivesError() {
        try {
            this.objectDto.setId(null);
            this.objectDto.generate("/self", "/selfall");
            fail();
        } catch (JsonApiException e) {
            assertEquals("Id not entered", e.getMessage());

            try {
                this.objectDto.setId("");
                this.objectDto.generate("/self", "/selfall");
                fail();
            } catch (JsonApiException ex) {
                assertEquals("Id not entered", ex.getMessage());
            }
        }
    }

    @Test
    public void testIfDoubleSlashIsHandledBaseUrl() {
        System.setProperty("jsonapi.baseUrl", "http://localhost:8080/");
        this.objectDto.generate("/self", "/selfall");
        System.clearProperty("jsonapi.baseUrl");
        assertTrue(this.objectDto.getSelfRel().startsWith("http://localhost:8080/self"));
        assertTrue(this.objectDto.getAllSelfRel().startsWith("http://localhost:8080/selfall"));
    }

    @Test
    public void testIfNoSlashAddsASlash() {
        this.objectDto.generate("self", "selfall");
        assertTrue(this.objectDto.getSelfRel().startsWith("http://localhost:8080/self"));
        assertTrue(this.objectDto.getAllSelfRel().startsWith("http://localhost:8080/selfall"));
    }
}
