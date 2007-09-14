package com.thoughtworks.xstream.benchmark.xmlfriendly.product;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses a combined lookup and appends characters.
 *
 * @author J&ouml;rg Schaible
 */
public class CombinedLookupAppender implements Product {

    private final XStream xstream;
    private final int bufferIncrement;

    public CombinedLookupAppender(int bufferIncrement) {
        this.bufferIncrement = bufferIncrement;
        this.xstream = new XStream(new XppDriver(new XmlFriendlyReplacer(bufferIncrement)));
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "Combined Lookup Appending" + (bufferIncrement == 0 ? "" : (" (" + bufferIncrement + ")"));
    }
    
    public static class XmlFriendlyReplacer extends AbstractXmlFriendlyReplacer {

        public XmlFriendlyReplacer(int bufferIncrement) {
            super("_-", "__", bufferIncrement);
        }

        public XmlFriendlyReplacer(String dollarReplacement, String underscoreReplacement, int bufferIncrement) {
            super(dollarReplacement, underscoreReplacement, bufferIncrement);
        }
        
        public String escapeName(String name) {
            return super.escapeByCombinedLookupAppending(name);
        }
        
        public String unescapeName(String name) {
            return super.unescapeByCombinedLookupAppending(name);
        }
    }
}
