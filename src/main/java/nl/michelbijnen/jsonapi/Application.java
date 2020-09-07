package nl.michelbijnen.jsonapi;

import nl.michelbijnen.jsonapi.test.ObjectDto;
import nl.michelbijnen.jsonapi.test.UserDto;

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
        objectDto.setName("yes");
        objectDto.setSelfRel("http://localhost:8080/objects/2");
        objectDto.setSelfRel("http://localhost:8080/objects/3");
        objectDto.setOwner(userDto);

        userDto.setTheCoolObject(objectDto);

        JsonApiConverter jsonApiConverter = new JsonApiConverter(userDto);
        System.out.println(jsonApiConverter.convert());
    }
}
