package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.lang.reflect.Field;

/**
 * Special converter for java.util.Properties that stores
 * properties in a more compact form than java.util.Map.
 * <p/>
 * <p>Because all entries of a Properties instance
 * are Strings, a single element is used for each property
 * with two attributes; one for key and one for value.</p>
 * <p>Additionally, default properties are also serialized, if they are present.</p>
 *
 * @author Joe Walnes
 * @author Kevin Ring
 */
public class PropertiesConverter implements Converter {

    private static Field defaultsField;

    static {
        try {
            defaultsField = Properties.class.getDeclaredField("defaults");
            defaultsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            defaultsField = null;
        }
    }

    public boolean canConvert(Class type) {
        return Properties.class == type;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Properties properties = (Properties) source;
        for (Iterator iterator = properties.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            writer.startNode("property");
            writer.addAttribute("name", entry.getKey().toString());
            writer.addAttribute("value", entry.getValue().toString());
            writer.endNode();
        }
        Properties defaults = getDefaults(properties);
        if (defaults != null) {
            writer.startNode("defaults");
            marshal(defaults, writer, context);
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Properties properties = new Properties();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("defaults")) {
                Properties defaults = (Properties) unmarshal(reader, context);
                setDefaults(properties, defaults);
            } else {
                String name = reader.getAttribute("name");
                String value = reader.getAttribute("value");
                properties.setProperty(name, value);
            }
            reader.moveUp();
        }
        return properties;
    }

    private Properties getDefaults(Properties properties) {
        if (defaultsField == null) {
            throw new ConversionException("Could not access java.util.Properties.defaults field");
        }
        try {
            return (Properties) defaultsField.get(properties);
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not read java.util.Properties.default field");
        }
    }

    private void setDefaults(Properties properties, Properties defaults) {
        if (defaultsField == null) {
            throw new ConversionException("Could not access java.util.Properties.defaults field");
        }
        try {
            defaultsField.set(properties, defaults);
        } catch (IllegalAccessException e) {
            throw new ConversionException("Could not write java.util.Properties.defaults field");
        }
    }

}
