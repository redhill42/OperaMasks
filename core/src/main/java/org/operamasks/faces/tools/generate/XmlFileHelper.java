package org.operamasks.faces.tools.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlFileHelper {
    public static Document read(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });
            document = builder.parse(new File(filename));
            document.getDocumentElement().normalize();
        } catch (Exception e) {
        }
        return document;
    }

    public static boolean write(String filename, Document document, boolean addDocType) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(4));
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            if(addDocType){
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN");
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/facelet-taglib_1_0.dtd");
            }
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, new StreamResult(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)))));
            return true;
        } catch (Exception e) {
            return false;
        }
        
    }
}
