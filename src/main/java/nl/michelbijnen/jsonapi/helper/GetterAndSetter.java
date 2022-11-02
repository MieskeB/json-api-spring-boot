package nl.michelbijnen.jsonapi.helper;

import nl.michelbijnen.jsonapi.exception.JsonApiException;

import java.beans.PropertyDescriptor;

public class GetterAndSetter {
    public static Object callGetter(Object obj, String fieldName) {
        try {
            PropertyDescriptor pd;
            pd = new PropertyDescriptor(fieldName, obj.getClass());
            return pd.getReadMethod().invoke(obj);
        }
        catch (Exception e) {
            try {
                PropertyDescriptor pd;
                pd = new PropertyDescriptor(fieldName, obj.getClass().getSuperclass());
                return pd.getReadMethod().invoke(obj);
            }
            catch (Exception e1) {
                throw new JsonApiException("Getter for field '" + fieldName + "' does not exist");
            }
        }
    }
}
