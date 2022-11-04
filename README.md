[![Maven Central](https://maven-badges.herokuapp.com/maven-central/nl.michelbijnen.jsonapi/json-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/nl.michelbijnen.jsonapi/json-api)
[![codecov](https://codecov.io/gh/MieskeB/json-api-spring-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/MieskeB/json-api-spring-boot)

# json-api-spring-boot

This project converts a normal Java object to json:api standard. More information about json:api can be found
here: https://jsonapi.org/

# Installation

For maven, add the following dependency to your dependencies:

```xml

<dependency>
    <groupId>nl.michelbijnen.jsonapi</groupId>
    <artifactId>json-api</artifactId>
    <version>1.5.6</version>
</dependency>
```

Do the same if you are using gradle:

```
implementation 'nl.michelbijnen.jsonapi:json-api:1.5.5'
```

(Don't forget to (re)import all your dependencies afterwards)

# Conflicts

We are aware this library has conflicts with spring-boot-starter-test. This is due to a dependency in that library
called android-json. This can be solved by changing the dependency to the following:

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>com.vaadin.external.google</groupId>
            <artifactId>android-json</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

# Usage

## Setting up

To start, you'll need a project. In this project, you have to define models. These models (with properties) need the
default getters and setters according to the Java standards.

An example for this:

```java
public class User {
    private String id;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

Naming in this step is very important! So if possible, let them be generated by your editor (IntelliJ can do this
using `alt`+`ins` and then clicking on getter and setter) or by letting Lombok handle it (put @Getter and @Setter above
the class)

## Adding annotations

For links to be generated correctly, a class needs to be extended. This class will help generate the links and makes
sure that the id is included into the object.

```java
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

public class User extends JsonApiDtoExtendable {
    // ...
}
```

By adding this class, you can remove id and all links.

There is a list of annotations that can be added.

| Annotation | Description | Mandatory | JsonApiDtoExtendable |
|----------------|------------------|-----|-----|
| @JsonApiObject | The whole object | Yes | No |
| @JsonApiId | The id of the object | Yes | Yes |
| @JsonApiProperty | The properties of the object | No | No |
| @JsonApiLink | The link with references | No | Yes |
| @JsonApiRelation | A relation with another class | No | No |

Now to implement the example class above with the annotations:

```java
import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("User")
public class User extends JsonApiDtoExtendable {
    @JsonApiProperty
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

### Adding relations

JSON:API says that you can add a relation to another class, but only the ID will appear. In the includes, the whole
object can be found. To do this, add a field with `@JsonApiRelation`. A value is mandatory.

```java
@JsonApiRelation("owner")
private Box boxOwner;
```

This box object should also have the `@JsonApiObject("box")` annotation, and it should also contain an id.

Many to many relations are supported and won't cause any recursions.

### Adding links

After filling in all the fields, a generate method can be called. This method will require you to input the self URI and
the all URI. After that it will automatically generate all URLs required for that object. For generation, the library
needs to know the base URL. Default for spring boot is `http://localhost:8080` and this is also the default value for
this URL.

if you want to change the default value, you can set an environment variable (or put this in your
server.properties): `jsonapi.baseurl=2`

```java
import nl.michelbijnen.jsonapi.annotation.*;
import nl.michelbijnen.jsonapi.generator.JsonApiDtoExtendable;

@JsonApiObject("User")
public class User extends JsonApiDtoExtendable {
    @JsonApiProperty
    private String username;

    public User(String id, String username) {
        this.setId(id);
        this.setUsername(username);
        this.generate("/user", "/users");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

If you would create the json for this object, you would get the following results:

As a single object:

```json
{
  "data": {
    "attributes": {
      "username": "the username"
    },
    "id": "the id",
    "type": "User"
  },
  "links": {
    "self": "http://localhost:8080/user/owner"
  }
}
```

As a list:

```json
{
  "data": [
    {
      "attributes": {
        "username": "the username 1"
      },
      "id": "the id 1",
      "type": "User"
    },
    {
      "attributes": {
        "name": "the username 2"
      },
      "id": "the id 2",
      "type": "User"
    }
  ],
  "links": {
    "self": "http://localhost:8080/users"
  }
}
```

## Creating the string

Now to convert the object to a string, you can do that the following way with a full user object.

```java
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;

String result=JsonApiConverter.convert(user);
```

## Adding depth to the included

Default, there is a depth of 1. This means that the included will go 1 relation deep. So only the direct relations of
the base object. If you want to have the relations of the relations, you can change this to 2 or higher for more depth.

```java
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;

String result=JsonApiConverter.convert(user,2);
```

if you want to change the default value, you can set an environment variable (or put this in your
server.properties): `jsonapi.depth=2`

# Conclusion

I hope this helps some people who had trouble with using JSON:API. If there are any problems or bugs, just create an
issue with what is wrong! I'm happy to help! Also if someone wants to clean up some code or make it more efficient, you
can always open a pull request!

Other (full) examples can be found in `src/main/java/nl/michelbijnen/jsonapi/test`
