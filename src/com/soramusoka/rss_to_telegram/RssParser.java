package com.soramusoka.rss_to_telegram;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;

public class RssParser {
    private HttpRequest _request;

    public RssParser(HttpRequest request) {
        this._request = request;
    }

    public ArrayList<Node> getRawData(String url, String authorName) throws Exception {
        String body = this._request.getUrl(url);
        Document xml = loadXML(body);
        Node rss = this.getNode(xml, "rss").get(0);
        Node channel = this.getNode(rss, "channel").get(0);
        ArrayList<Node> items = this.getNode(channel, "item");

        if (authorName == null) {
            return items;
        }

        ArrayList<Node> list = new ArrayList<>();
        for (Node item : items) {
            Node author = this.getNode(item, "author").get(0);
            String name = author.getTextContent();
            if (name != null && name.equals(authorName)) {
                list.add(item);
            }
        }
        return list;
    }

    private ArrayList<Node> getNode(Node root, String pattern) throws Exception {
        NodeList nodes = root.getChildNodes();
        ArrayList<Node> list = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String name = node.getNodeName();
            if (name.equals(pattern)) {
                list.add(node);
            }
        }
        return list;
    }

    public Document loadXML(String xml) throws Exception {
        DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
        DocumentBuilder bldr = fctr.newDocumentBuilder();
        InputSource insrc = new InputSource(new StringReader(xml));
        return bldr.parse(insrc);
    }

    public ArrayList<Article> convertToArticle(ArrayList<Node> list) throws Exception {
        ArrayList<Article> result = new ArrayList<>();
        for (Node item : list) {
            Article article = new Article();
            article.author = this.getNode(item, "author").get(0).getTextContent();
            article.title = this.getNode(item, "title").get(0).getTextContent();
            article.link = this.getNode(item, "link").get(0).getTextContent();
            article.description = this.getNode(item, "description").get(0).getTextContent();
            article.pubDate = this.getNode(item, "pubDate").get(0).getTextContent();
            result.add(article);
        }
        return result;
    }
}
