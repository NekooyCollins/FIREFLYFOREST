package com.example.android.fireflyforest;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Lyra Malfoy on 2018/4/16.
 */

/*
* 一个XMLParser对象，该对象把XML解析用的SAXParserFactory等获取实例的工作封装起来
* 有了这个对象，解析XML时只需创建一个XMLParser对象，调用的该对象的XMLParser()方法即可
**/
public class XMLParser {
    private SAXParserFactory factory = null;
    private XMLReader reader = null;

    public XMLParser() {

        try {
            factory = SAXParserFactory.newInstance();
            reader = factory.newSAXParser().getXMLReader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void parseXML(DefaultHandler content, InputSource inSource) {
        if (inSource == null)
            return;
        try {
            reader.setContentHandler(content);
            reader.parse(inSource);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
