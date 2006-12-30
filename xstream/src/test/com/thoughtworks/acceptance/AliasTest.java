package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Hammant
 */
public class AliasTest extends AbstractAcceptanceTest {

    public void testBarfsIfItDoesNotExist() {

        String xml = "" +
                "<X-array>\n" +
                "  <X>\n" +
                "    <anInt>0</anInt>\n" +
                "  </X>\n" +
                "</X-array>";

        // now change the alias
        xstream.alias("Xxxxxxxx", X.class);
        try {
            xstream.fromXML(xml);
            fail("ShouldCannotResolveClassException expected");
        } catch (CannotResolveClassException expectedException) {
            // expected
        }
    }
    
    //FIXME underscore is mapped to __ 
    public void FIXMEtestWithUnderscore() {
        String xml = "" +
                "<X_alias>\n" +
                "  <anInt>0</anInt>\n" +
                "</X_alias>";

        // now change the alias
        xstream.alias("X_alias", X.class);
        X x = new X(0);
        assertBothWays(x, xml);
    }

    final static class Software {
        String vendor;
        String name;
        
        public Software(String vendor, String name) {
            this.name = name;
            this.vendor = vendor;
        }
        
        protected Software() {
            // for JDK 1.3
        }
    }
    
    public void testForFieldAsAttribute() {
        Software software = new Software("walness", "xstream");
        
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("id", "name");
        
        String xml = "<software vendor=\"walness\" id=\"xstream\"/>";
        
        assertBothWays(software, xml);
    }
    
    public void testForReferenceAttribute() {
        List list = new ArrayList();
        Software software = new Software("walness", "xstream");
        list.add(software);
        list.add(software);
        
        xstream.alias("software", Software.class);
        xstream.useAttributeFor(String.class);
        xstream.aliasAttribute("refid", "reference");
        
        String xml = "" + 
            "<list>\n" +
            "  <software vendor=\"walness\" name=\"xstream\"/>\n" +
            "  <software refid=\"../software\"/>\n" +
            "</list>";
        
        assertBothWays(list, xml);
    }
    
    private static class FieldsWithInternalNames {
        String clazz;
        String ref;
    }

    public void testCanUseInternalNameAsFieldAlias() {
        FieldsWithInternalNames object = new FieldsWithInternalNames();
        object.clazz = "TestIt";
        object.ref = "MyReference";

        xstream.alias("internalNames", FieldsWithInternalNames.class);
        xstream.aliasField("class", FieldsWithInternalNames.class, "clazz");
        xstream.aliasField("reference", FieldsWithInternalNames.class, "ref");
        
        String xml = ""
                + "<internalNames>\n"
                + "  <class>TestIt</class>\n"
                + "  <reference>MyReference</reference>\n"
                + "</internalNames>";

        assertBothWays(object, xml);
    }
    
    //FIXME fails with an unknown type boolean 
    public void FIXMEtestCanAliasPrimitiveTypes() {
        Object object = new boolean[]{true, false};
        xstream.alias("bo", Boolean.TYPE);
        String xml = ""
            + "<bo-array>\n"
            + "  <bo>true</bo>\n"
            + "  <bo>false</bo>\n"
            + "</bo-array>";
        assertBothWays(object, xml);
    }
}
