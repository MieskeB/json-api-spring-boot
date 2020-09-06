package nl.michelbijnen.jsonapi;

import java.util.UUID;

public class Application {
    public static void main(String[] args) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setUsername("MieskeB");
        userDto.setEmail("test@test.nl");
        userDto.setSelfRel("http://localhost:8080/users/4");
        userDto.setNextRel("http://localhost:8080/users/5");

        JsonApiConverter jsonApiConverter = new JsonApiConverter(userDto);
        System.out.println(jsonApiConverter.convert());
    }
}
