package nl.michelbijnen.jsonapi.helper;

import nl.michelbijnen.jsonapi.exception.JsonApiException;

import java.beans.PropertyDescriptor;

import static nl.michelbijnen.jsonapi.util.JsonApiConstants.GETTER_DOES_NOT_EXIST;

public abstract class GetterAndSetter {

    /**
     * Calls the getter method for the specified field on the given object.
     *
     * @param obj       the object to call the getter on
     * @param fieldName the name of the field
     * @return the value returned by the getter
     * @throws JsonApiException if the getter does not exist
     */
    public static Object callGetter(Object obj, String fieldName) {
        try {
            PropertyDescriptor pd;
            pd = new PropertyDescriptor(fieldName, obj.getClass());
            return pd.getReadMethod().invoke(obj);
        } catch (Exception e) {
            try {
                PropertyDescriptor pd;
                pd = new PropertyDescriptor(fieldName, obj.getClass().getSuperclass());
                return pd.getReadMethod().invoke(obj);
            } catch (Exception e1) {
                throw new JsonApiException(String.format(GETTER_DOES_NOT_EXIST, fieldName));
            }
        }
    }
}
