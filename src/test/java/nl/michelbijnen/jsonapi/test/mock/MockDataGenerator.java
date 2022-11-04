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
        }

        // Defining the object dtos
        {
            mainObject.setId("mainobject");
            mainObject.setName("the main object");
        }
        {
            childObject1.setId("childobject1");
            childObject1.setName("the first child object");
        }
        {
            childObject2.setId("childobject2");
            childObject2.setName("the second child object");
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
        }
        {
            childObject1.setApple(apple1);
        }
        {
            childObject2.setApple(apple2);
        }

        // Adding the objects to the user dtos
        {
            // Set the main objects
            {
                owner.setMainObject(mainObject);
            }
            // Set the child objects
            {
                ArrayList<ObjectDto> childObjects = new ArrayList<>();
                childObjects.add(childObject1);
                childObjects.add(childObject2);

                owner.setChildObjects(childObjects);
            }
        }

        // Adding the users to the object dtos
        {
            mainObject.setOwner(owner);
        }
        {
            childObject1.setOwner(owner);
        }
        {
            childObject2.setOwner(owner);
        }

        owner.generate("/user", "/user");
        mainObject.generate("/object", "/object");
        childObject1.generate("/object", "/object");
        childObject2.generate("/object", "/object");
        apple1.generate("/apple", "/apple");
        apple2.generate("/apple", "/apple");
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
