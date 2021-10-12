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
    <version>1.4.1</version>
</dependency>
```

Do the same if you are using gradle:

```
implementation 'nl.michelbijnen.jsonapi:json-api:1.4.1'
```

(Don't forget to (re)import all your dependencies afterwards)

# Conflicts

We know this library has conflicts with spring-boot-starter-test. This is due to a dependency in that library called
android-json. This can be solved by changing the dependency to the following:

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
using `alt`+`ins`)

## Adding annotations

There is a list of annotations that can be added.

| Annotation | Description | Mandatory |
|----------------|------------------|-----|
| @JsonApiObject | The whole object | Yes |
| @JsonApiId | The id of the object | Yes |
| @JsonApiProperty | The properties of the object | No |
| @JsonApiLink | The link with references | No |
| @JsonApiRelation | A relation with another class | No |

Now to implement the example class above with the annotations:

```java
import nl.michelbijnen.jsonapi.annotation.*;

@JsonApiObject("User")
public class User {
    @JsonApiId
    private String id;
    @JsonApiProperty
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

Links can be completely customized. According to JSON:API, the possible link types are: first, previous, self, next and
last. By default, `@JsonApiLink` is self. You can customize this by changing the value parameter.

```java
@JsonApiLink
private String selfRel;
@JsonApiLink(JsonApiLinkType.NEXT)
private String nextRel;
```

Now to add links to a relation. This is done by adding a property to the annotation named
relation `@JsonApiLink(relation = "box")`. The relation name should match the name from `@JsonApiRelation`. Here can
also the type be added. According to JSON:API, the types you can add here are self and related.

```java
@JsonApiRelation("box")
private Box box;
@JsonApiLink(relation = "box")
private String boxSelfRel;
@JsonApiLink(value = JsonApiLinkType.RELATED, relation = "box")
private String boxRelatedRel;
```

## Creating the string

Now to convert the object to a string, you can do that the following way with a full user object.

```java
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;

String result = JsonApiConverter.convert(user);
```

## Adding depth to the included
(Added version 1.4.0)

Default, there is a depth of 1. This means that the included will go 1 relation deep. So only the direct relations of
the base object. If you want to have the relations of the relations, you can change this to 2 or higher for more depth.

```java
import nl.michelbijnen.jsonapi.parser.JsonApiConverter;

String result = JsonApiConverter.convert(user, 2);
```

if you want to change the default value, you can set an environment variable (or put this in your server.properties):

```
jsonapi.depth=2
```

# Conclusion

I hope this helps some people who had trouble with using JSON:API. If there are any problems or bugs, just create an
issue with what is wrong! I'm happy to help! Also if someone wants to clean up some code or make it more efficient, you
can always open a pull request!

Other (full) examples can be found in `src/main/java/nl/michelbijnen/jsonapi/test`
