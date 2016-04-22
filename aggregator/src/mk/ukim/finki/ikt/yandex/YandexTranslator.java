package mk.ukim.finki.ikt.yandex;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Simona on 4/2/2016.
 */
public class YandexTranslator {

    private static String apiKey = "trnsl.1.1.20160402T160806Z.7da61e44ba773939.a11d1d2a74f1f618741e96c5e1c198bb88a39a90";

    public static void main(String[] args){
        System.out.println(translate("Здраво", "mk", "en"));
    }

    public static String translate(String text, String from, String to){
        String response = "";
        try {
            response = sendPOSTRequest(text, from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static String sendPOSTRequest(String html, String from, String to) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://translate.yandex.net/api/v1.5/tr/translate");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("key", apiKey));
        nvps.add(new BasicNameValuePair("text", html));
        nvps.add(new BasicNameValuePair("lang", from + "-" + to));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
        CloseableHttpResponse response2 = httpclient.execute(httpPost);

        try {
            HttpEntity entity2 = response2.getEntity();
            String response = readInput(entity2.getContent());
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(response));

            Document doc = db.parse(is);
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpathObj = xfactory.newXPath();
            String text = (String)xpathObj.evaluate("//text/text()", doc, XPathConstants.STRING);
            EntityUtils.consume(entity2);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } finally {
            try {
                response2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private static String readInput(InputStream stream){
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder sb = new StringBuilder();
        try {
            while((inputLine = in.readLine())!=null){
                sb.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while reading");
            return "";
        }

        return sb.toString();
    }
}
