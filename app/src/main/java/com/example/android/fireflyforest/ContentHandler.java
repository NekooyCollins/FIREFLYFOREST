package com.example.android.fireflyforest;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Lyra Malfoy on 2018/4/16.
 */

/*
* 使用SAX 解析器对XML进行解析
**/
public class ContentHandler extends DefaultHandler {
    public Word wordValue = null;
    private String tagName = null;
    private String posOfWord = "";
    private String interpret = "";
    private int flag = 0;

    public ContentHandler() {
        wordValue = new Word();
    }

    public Word getWordValue() {
        return wordValue;
    }

    // 获取节点中内容时调用
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        super.characters(ch, start, length);
        if (length <= 0)
            return;

        // 去掉文本中原有的换行
        for (int i = start; i < start + length; i++) {
            if (ch[i] == '\n')
                return;
        }

        // 一行一行的开始设置文本
        String str = new String(ch, start, length);
        if (tagName == "key") {
            wordValue.setWord(str);
        } else if (tagName == "pos") {
            if (str.equals("n.") || str.equals("abbr.")) {
                posOfWord = posOfWord + str + " ";
                flag = 1;
            }
        } else if (tagName == "acceptation" && (flag == 1)) {
            interpret = interpret + str;
            interpret = wordValue.getInterpret() + interpret;
            wordValue.setInterpret(interpret);
            interpret = ""; //初始化操作，预防有多个释义
        }
    }

    // 结束解析节点时调用
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, qName);
        tagName = null;
    }

    // 开始解析节点时调用
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        super.startElement(uri, localName, qName, attributes);
        tagName = localName;
    }

    // 结束解析XML 时调用
    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.endDocument();
        // 将所有解析出来的内容赋予words 并去掉解释的最后一个换行符
        String interpret = wordValue.getInterpret();
        if (interpret != null && interpret.length() > 0) {
            char[] strArray = interpret.toCharArray();
            wordValue.setInterpret(new String(strArray, 0, interpret.length() - 1));
        }
    }
}
