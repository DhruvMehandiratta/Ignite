import java.lang.reflect.*;
import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

public class TestReflection {
	public static void main(String[] args) {
		try {
			File inputFile = new File("test.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			Node rootname = doc.getDocumentElement();
			System.out.println("Root element :" + rootname.getNodeName());
			NodeList tables = rootname.getChildNodes();
			System.out.println("----------------------------"+tables.getLength());
			for (int temp = 0; temp < tables.getLength(); temp++) {
				Node thisTable = tables.item(temp);
				System.out.println("\nCurrent Element :" + thisTable.getNodeName());

				if (thisTable.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) thisTable;
					System.out.println("name : " 
							+ eElement.getElementsByTagName("name").item(0).getTextContent());
					System.out.println("datafile : " 
							+ eElement
							.getElementsByTagName("datafile").item(0).getTextContent());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}