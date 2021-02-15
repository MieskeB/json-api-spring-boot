package nl.michelbijnen.jsonapi.test.mock;

import java.util.ArrayList;

public class MockDataGenerator {
    private final UserDto owner = new UserDto();

    private final ObjectDto mainObject = new ObjectDto();
    private final ObjectDto childObject1 = new ObjectDto();
    private final ObjectDto childObject2 = new ObjectDto();

    private final AppleDto apple1 = new AppleDto();
    private final AppleDto apple2 = new AppleDto();

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

        // Defining the apple dtos
        {
            apple1.setId("Apple1");
            apple1.setName("Golden");
        }
        {
            apple2.setId("Apple2");
            apple2.setName("Iron");
        }

        // Adding the apples to the objects
        {
            mainObject.setApple(apple1);
            mainObject.setAppleSelfRel("http://localhost:8080/objects/2/relationships/apple");
        }
        {
            childObject1.setApple(apple1);
            childObject1.setAppleSelfRel("http://localhost:8080/objects/3/relationships/apple");
        }
        {
            childObject2.setApple(apple2);
            childObject2.setAppleSelfRel("http://localhost:8080/objects/4/relationships/apple");
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
        {
            childObject1.setOwner(owner);
            childObject1.setOwnerSelfRel("http://localhost:8080/objects/3/relationships/owner");
            childObject1.setOwnerRelatedRel("http://localhost:8080/objects/3/owner");
        }
        {
            childObject2.setOwner(owner);
            childObject2.setOwnerSelfRel("http://localhost:8080/objects/4/relationships/owner");
            childObject2.setOwnerRelatedRel("http://localhost:8080/objects/4/owner");
        }
    }

    private static final MockDataGenerator mockDataGenerator = new MockDataGenerator();

    public static MockDataGenerator getInstance() {
        return mockDataGenerator;
    }

    public UserDto getUserDto() {
        return this.owner;
    }

    public ObjectDto getObjectDto() {
        return this.mainObject;
    }

    public AppleDto getAppleDto() {
        return this.apple1;
    }
}
