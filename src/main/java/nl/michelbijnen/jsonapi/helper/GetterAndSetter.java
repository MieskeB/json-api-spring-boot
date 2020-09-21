package nl.michelbijnen.jsonapi.helper;

import java.beans.PropertyDescriptor;

public class GetterAndSetter {
    public Object callGetter(Object obj, String fieldName) throws Exception {
        PropertyDescriptor pd;
        pd = new PropertyDescriptor(fieldName, obj.getClass());
        return pd.getReadMethod().invoke(obj);
    }
}
