import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

public class BingHandler {

    private byte[] credential;
    private String auth;
    private String host;
    private static final int TOPMAX = 4;


    public BingHandler() {
        this.credential = Base64.encodeBase64((Main.bingKey + ":" + Main.bingKey).getBytes());
        this.auth = "Basic " + new String(this.credential);
        this.host = Main.host;
    }

    public int getCount(List<String> terms) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringBuilder sb = new StringBuilder("https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a");
        sb.append(host);
        sb.append("%20");
        for (Iterator<String> i = terms.iterator(); i.hasNext(); ) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append("%20");
            }
        }
        sb.append("%27&$format=Atom&$top=1");
        String _url = sb.toString();
        URL url = new URL(_url);
        URLConnection url_connect = url.openConnection();
        url_connect.setRequestProperty("Authorization", auth);
        InputStream is = url_connect.getInputStream();
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        NodeList total_list = doc.getElementsByTagName("d:WebTotal");
        Element ele_total = (Element) total_list.item(0);
        NodeList totals = ele_total.getChildNodes();
        return Integer.parseInt(totals.item(0).getNodeValue());
    }

    public String[] getTop(List<String> terms) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringBuilder sb = new StringBuilder("https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=%27site%3a");
        sb.append(host);
        sb.append("%20");
        for (Iterator<String> i = terms.iterator(); i.hasNext(); ) {
            //testing

            sb.append(i.next());
            if (i.hasNext()) {
                sb.append("%20");
            }
        }
        sb.append("%27&$format=Atom&$top=");
        sb.append(TOPMAX);
        String _url = sb.toString();
        //System.out.println(_url);
        URL url = new URL(_url);
        URLConnection url_connect = url.openConnection();
        url_connect.setRequestProperty("Authorization", auth);
        InputStream is = url_connect.getInputStream();
        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        NodeList entry_list = doc.getElementsByTagName("entry");

        assert (entry_list.getLength() <= TOPMAX);
        String[] ret = new String[entry_list.getLength()];

        for (int i = 0, j = entry_list.getLength(); i != j; ++i) {
            Node entry = entry_list.item(i);
            Element ele_entry = (Element) entry;
            NodeList url_list = ele_entry.getElementsByTagName("d:Url");
            Element ele_url = (Element) url_list.item(0);
            NodeList urls = ele_url.getChildNodes();
            ret[i] = urls.item(0).getNodeValue().trim().replaceAll("<b>", "").replaceAll("</b>", "");
        }

        return ret;
    }
}
