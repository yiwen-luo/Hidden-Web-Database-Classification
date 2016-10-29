import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BingResult {
    private static final int NUMBER = 4;
    private static final String accountKey = "spuDgqTzB1Em10nV/N7wPQBoXrv0eSskmbDEBN/mkwc";

    /* Return the list of 4 url regarding on a query and site combination */
    public List<String> getResult(String siteName, String query) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringBuilder sb = new StringBuilder("https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a");
        sb.append(siteName);
        sb.append("%20");
        sb.append(query);
        sb.append("%27&$top=");
        sb.append(NUMBER);
        sb.append("&$format=Atom");

        String bingUrl = sb.toString();
        byte[] accountKeyBytes = Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);

        URL url = new URL(bingUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

        InputStream inputStream = (InputStream) urlConnection.getContent();
        Document doc = db.parse(inputStream);
        doc.getDocumentElement().normalize();
        NodeList entry_list = doc.getElementsByTagName("entry");

        assert(entry_list.getLength() <= NUMBER + 1);

        String[] ret = new String[entry_list.getLength()];
        List<String> list = new ArrayList<>();
        for(int i = 1; i < entry_list.getLength(); i++) {
            Node entry = entry_list.item(i);
            Element ele_entry = (Element)entry;
            NodeList url_list = ele_entry.getElementsByTagName("d:Url");
            Element ele_url = (Element)url_list.item(0);
            NodeList urls = ele_url.getChildNodes();
            list.add(urls.item(0).getNodeValue().trim().replaceAll("<b>", "").replaceAll("</b>", ""));
        }

        for (String str : list) {
            System.out.println(str);
        }
        return list;
    }

}
