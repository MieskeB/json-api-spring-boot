package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.test.ObjectDto;
import nl.michelbijnen.jsonapi.test.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Application {
    public static void main(String[] args) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setUsername("MieskeB");
        userDto.setEmail("test@test.nl");
        userDto.setSelfRel("http://localhost:8080/users/4");
        userDto.setNextRel("http://localhost:8080/users/5");

        ObjectDto objectDto = new ObjectDto();
        objectDto.setId(UUID.randomUUID().toString());
        objectDto.setName("object1");
        objectDto.setSelfRel("http://localhost:8080/objects/2");
        objectDto.setNextRel("http://localhost:8080/objects/3");
        objectDto.setOwner(userDto);

        ObjectDto objectDto1 = new ObjectDto();
        objectDto1.setId(UUID.randomUUID().toString());
        objectDto1.setName("object2");
        objectDto1.setSelfRel("http://localhost:8080/objects/5");
        objectDto1.setNextRel("http://localhost:8080/objects/6");
        objectDto1.setOwner(userDto);

        userDto.setMainObject(objectDto);
        userDto.setMainObjectSelfRel("http://localhost:8080/users/4/relationships/mainObject");
        userDto.setMainObjectRelatedRel("http://localhost:8080/users/4/mainObject");
        List<ObjectDto> childObjects = new ArrayList<>();
        childObjects.add(objectDto);
        childObjects.add(objectDto1);
        userDto.setChildObjects(childObjects);
        userDto.setChildObjectSelfRel("http://localhost:8080/users/4/relationships/childObjects");
        userDto.setChildObjectRelatedRel("http://localhost:8080/users/4/childObjects");

        JsonApiConverter jsonApiConverter = new JsonApiConverter(userDto);
        System.out.println(jsonApiConverter.convert());
    }
}
