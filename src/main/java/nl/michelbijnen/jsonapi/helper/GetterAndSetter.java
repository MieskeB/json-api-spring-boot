package nl.michelbijnen.jsonapi.helper;

import java.beans.PropertyDescriptor;

public class GetterAndSetter {
    public void callSetter(Object obj, String fieldName, Object value) throws Exception {
        PropertyDescriptor pd;
        pd = new PropertyDescriptor(fieldName, obj.getClass());
        pd.getWriteMethod().invoke(obj, value);
    }

    public Object callGetter(Object obj, String fieldName) throws Exception {
        PropertyDescriptor pd;
        pd = new PropertyDescriptor(fieldName, obj.getClass());
        return pd.getReadMethod().invoke(obj);
    }
}
