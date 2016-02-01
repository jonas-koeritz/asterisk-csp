package org.opencsp.csta.messages.encoding.xml;

public @interface CSTAXml {
    String name();
    boolean required() default true;
}