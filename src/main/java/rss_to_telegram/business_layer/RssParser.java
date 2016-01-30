package rss_to_telegram.business_layer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import rss_to_telegram.dto_layer.Article;
import rss_to_telegram.service_layer.HttpRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;

public class RssParser {
    private HttpRequest _request;
    private Transliterator _transliterator;

    public RssParser(HttpRequest request, Transliterator transliterator) {
        this._request = request;
        this._transliterator = transliterator;
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
            try {
                ArrayList<Node> authors = this.getNode(item, "author");
                if (authors.size() > 0) {
                    String name = this._transliterator.transliterate(authors.get(0).getTextContent());
                    if (name != null && name.contains(authorName)) {
                        list.add(item);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
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
            ArrayList<Node> authorList = this.getNode(item, "author");
            if (authorList.size() > 0) {
                article.author = authorList.get(0).getTextContent();
            }
            article.title = this.getNode(item, "title").get(0).getTextContent();
            article.link = this.getNode(item, "link").get(0).getTextContent();
            article.description = this.getNode(item, "description").get(0).getTextContent();
            article.pubDate = this.getNode(item, "pubDate").get(0).getTextContent();
            result.add(article);
        }
        return result;
    }
}
