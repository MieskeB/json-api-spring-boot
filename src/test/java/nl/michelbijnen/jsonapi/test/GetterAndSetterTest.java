package nl.michelbijnen.jsonapi.test;

import nl.michelbijnen.jsonapi.exception.JsonApiException;
import nl.michelbijnen.jsonapi.helper.GetterAndSetter;
import nl.michelbijnen.jsonapi.test.mock.MockDataGenerator;
import nl.michelbijnen.jsonapi.test.mock.ObjectDto;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class GetterAndSetterTest {

    private ObjectDto objectDto;

    @Before
    public void before() throws CloneNotSupportedException {
        MockDataGenerator generator = MockDataGenerator.getInstance();
        this.objectDto = (ObjectDto) generator.getObjectDto().clone();
    }

    @Test
    public void testIfGetCanBeCalled() {
        String name = GetterAndSetter.callGetter(objectDto, "name").toString();
        assertNotNull(name);
    }

    @Test
    public void testIfGetIsProperValue() {
        String name = GetterAndSetter.callGetter(objectDto, "name").toString();
        assertEquals(name, this.objectDto.getName());
    }

    @Test
    public void testIfGetCanBeCalledFromSuperclass() {
        String url = GetterAndSetter.callGetter(objectDto, "selfRel").toString();
        assertNotNull(url);
    }

    @Test
    public void testIfGetCanBeCalledFromSuperclassAndHasProperValue() {
        String url = GetterAndSetter.callGetter(objectDto, "selfRel").toString();
        assertEquals(url, this.objectDto.getSelfRel());
    }

    @Test
    public void testIfErrorIsThrownWhenFieldNotExists() {
        try {
            GetterAndSetter.callGetter(objectDto, "doesNotExist");
            fail();
        } catch (JsonApiException e) {
            assertEquals("Getter for field 'doesNotExist' does not exist", e.getMessage());
        }
    }
}
