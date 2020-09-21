package nl.michelbijnen.jsonapi.test.mock;

import java.util.ArrayList;

public class MockDataGenerator {
    private final UserDto owner = new UserDto();

    private final ObjectDto mainObject = new ObjectDto();
    private final ObjectDto childObject1 = new ObjectDto();
    private final ObjectDto childObject2 = new ObjectDto();

    private MockDataGenerator() {

        // Defining the user dtos
        {
            owner.setId("owner");
            owner.setUsername("the owner");
            owner.setEmail("owner@michelbijnen.nl");
            owner.setSelfRel("http://localhost:8080/users/4");
            owner.setNextRel("http://localhost:8080/users/5");
        }

        // Defining the object dtos
        {
            mainObject.setId("mainobject");
            mainObject.setName("the main object");
            mainObject.setFirstRel("http://localhost:8080/objects/1");
            mainObject.setPreviousRel("http://localhost:8080/objects/1");
            mainObject.setSelfRel("http://localhost:8080/objects/2");
            mainObject.setNextRel("http://localhost:8080/objects/3");
            mainObject.setLastRel("http://localhost:8080/objects/5");
        }
        {
            childObject1.setId("childobject1");
            childObject1.setName("the first child object");
            childObject1.setFirstRel("http://localhost:8080/objects/1");
            childObject1.setPreviousRel("http://localhost:8080/objects/2");
            childObject1.setSelfRel("http://localhost:8080/objects/3");
            childObject1.setNextRel("http://localhost:8080/objects/4");
            childObject1.setLastRel("http://localhost:8080/objects/5");
        }
        {
            childObject2.setId("childobject2");
            childObject2.setName("the second child object");
            childObject2.setFirstRel("http://localhost:8080/objects/1");
            childObject2.setPreviousRel("http://localhost:8080/objects/3");
            childObject2.setSelfRel("http://localhost:8080/objects/4");
            childObject2.setNextRel("http://localhost:8080/objects/5");
            childObject2.setLastRel("http://localhost:8080/objects/5");
        }

        // Adding the objects to the user dtos
        {
            // Set the main objects
            {
                owner.setMainObject(mainObject);
                owner.setMainObjectSelfRel("http://localhost:8080/users/4/relationships/mainObject");
                owner.setMainObjectRelatedRel("http://localhost:8080/users/4/mainObject");
            }
            // Set the child objects
            {
                ArrayList<ObjectDto> childObjects = new ArrayList<>();
                childObjects.add(childObject1);
                childObjects.add(childObject2);

                owner.setChildObjects(childObjects);
                owner.setChildObjectSelfRel("http://localhost:8080/users/4/relationships/childObjects");
                owner.setChildObjectRelatedRel("http://localhost:8080/users/4/childObjects");
            }
        }

        // Adding the users to the object dtos
        {
            mainObject.setOwner(owner);
            mainObject.setOwnerSelfRel("http://localhost:8080/objects/2/relationships/owner");
            mainObject.setOwnerRelatedRel("http://localhost:8080/objects/2/owner");
        }
    }

    private static MockDataGenerator mockDataGenerator = new MockDataGenerator();

    public static MockDataGenerator getInstance() {
        return mockDataGenerator;
    }

    public UserDto getUserDto() {
        return this.owner;
    }

    public ObjectDto getObjectDto() {
        return this.mainObject;
    }
}
