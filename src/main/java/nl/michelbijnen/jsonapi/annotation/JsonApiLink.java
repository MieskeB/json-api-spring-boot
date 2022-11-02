package nl.michelbijnen.jsonapi.annotation;

import nl.michelbijnen.jsonapi.enumeration.JsonApiLinkType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonApiLink {
    public JsonApiLinkType value() default JsonApiLinkType.SELF;
}
