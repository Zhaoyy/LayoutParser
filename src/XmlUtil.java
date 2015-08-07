import config.Config;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XmlUtil
 *
 * @author Mislead
 *         DATE: 2015/7/7
 *         DESC:
 **/
public class XmlUtil {

  public static int NORMAL_TYPE = 0;
  public static int AA_TYPE = 1;//android annotations
  private static String TAG = "XmlUtil";
  private int outType = 0;
  private List<String> result;
  private List<String> methods = new ArrayList<String>();

  public XmlUtil() {
    result = new ArrayList<String>();
  }

  public static void main(String args[]) {
    XmlUtil xmlUtil = new XmlUtil();
    //        xmlUtil.setOutType(AA_TYPE);
    //        xmlUtil.readXmlFile("d:\\test.xml");
    xmlUtil.writeXmlVal("d:\\test.xml", "LinearLayout/LinearLayout/TextView", "@+id/tv");
  }

  public int getOutType() {
    return outType;
  }

  public void setOutType(int type) {
    outType = type;
  }

  public List<String> readXmlFile(File path) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(path);
      Element element = document.getDocumentElement();
      String attr = getAttributeOfName(element, "android:id");
      if (attr != null) {
        result.add(attr);
      }
      NodeList nodeList = element.getChildNodes();
      searchSubNodes(nodeList);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (outType == NORMAL_TYPE) {
      result.add("\n");
      for (String s : methods) {
        result.add(s);
      }
    }

    return result;
  }

  private void searchSubNodes(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        String att = getAttributeOfName((Element) node, "android:id");
        if (att != null) {
          result.add(att);
        }
      }
      NodeList list = node.getChildNodes();
      searchSubNodes(list);
    }
  }

  public void writeXmlVal(String fpath, String valPath, String val) {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = builderFactory.newDocumentBuilder();
      Document document = builder.parse(fpath);
      Element root = document.getDocumentElement();
      String[] valPaths = valPath.split("/", -1);
      int i = 0;
      if (root.getNodeName().equals(valPaths[0])) {
        i = 1;
      }
      for (; i < valPaths.length; i++) {
        Element element = null;
        NodeList nodeList = root.getChildNodes();
        for (int j = 0; j < nodeList.getLength(); j++) {
          Node node = nodeList.item(j);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (node.getNodeName().equals(valPaths[i])) {
              element = (Element) node;
            }
          }
        }
        if (element == null) {
          element = document.createElement(valPaths[i]);
          root.appendChild(element);
        }
        root = element;
        if (i == (valPaths.length - 1)) {
          element.setAttribute("android:id", val);
        }
      }
      Transformer t = TransformerFactory.newInstance().newTransformer();
      t.setOutputProperty("indent", "yes");
      t.transform(new DOMSource(document), new StreamResult(new FileOutputStream(fpath)));
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }

  private String getAttributeOfName(Element node, String name) {
    String attr = node.getAttribute(name);
    if (attr.isEmpty()) {
      return null;
    }
    if (AA_TYPE == getOutType()) {
      return "@ViewById\n" + node.getNodeName() + " " + attr.substring(attr.indexOf("/") + 1) + ";";
    }
    if (NORMAL_TYPE == getOutType()) {

      if (Config.ROOT_TYPE == 0) {
        methods.add(attr.substring(attr.indexOf("/") + 1)
            + " = ("
            + node.getNodeName()
            + ") findViewById(R.id."
            + attr.substring(attr.indexOf("/") + 1)
            + ");");
      } else {
        methods.add(attr.substring(attr.indexOf("/") + 1)
            + " = ("
            + node.getNodeName()
            + ") view.findViewById(R.id."
            + attr.substring(attr.indexOf("/") + 1)
            + ");");
      }

      return "private " + node.getNodeName() + " " + attr.substring(attr.indexOf("/") + 1) + ";";
    }
    return null;
  }
}
