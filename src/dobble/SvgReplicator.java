package dobble;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class SvgReplicator {

	  public static void main(String argv[]) throws Exception{
	
	  }
	  
	  public static void createSvgCards(int[][] symbolMap) throws Exception{
		  File dir = new File("images");
		  File [] files = dir.listFiles();
		  for (File xmlfile : files) {
		      System.out.println(xmlfile);  
		  }
		File fXmlFile = new File("cards/dobble_vorlage.svg");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
        
		//create the card dir to save all the svgs
		File dir2 = new File("cards");
        dir2.mkdir();
		
        //optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
        
		Document dobbleSvg = dBuilder.parse(fXmlFile);
		
		NodeList nList = dobbleSvg.getElementsByTagName("g");
		
		//find the correct layer
        for (int i = 0; i < nList.getLength(); i++) 
        {
        	
            Node node = nList.item(i);
            Element element = (Element) node;
        
            if (element.getAttribute("id").equals("layer2")) {
            	 System.out.println("accessing now: "+ element.getAttribute("id"));
            	// get all image nodes
            	NodeList imList =  element.getElementsByTagName("image");
            	
            	//k loops through all combinations of cards
            	for( int k = 0; k < symbolMap.length; k++)
            	{
            	//j loops through all image tags in xml
            	// set path and save a new svg.
            	
            	for(int j = 0; j < imList.getLength(); j++) {
            		
            		Node imNode = imList.item(j);
                    Element imElement = (Element) imNode;
	            	
                    System.out.println("changing now: " + imElement.getAttribute("id") + " xlink:href = "+ imElement.getAttribute("xlink:href"));
	            	imElement.setAttribute("xlink:href","../" + files[symbolMap[k][j]].getPath());
	            	System.out.println("changed to:   "+ imElement.getAttribute("id")   + " xlink:href = "+imElement.getAttribute("xlink:href"));
	            	
	            }	            	
            	
	            	String filename = "cards/card" + k + ".svg";
		            File file = new File(filename);
		            Result result = new StreamResult(file);
		            
		    		// Source is needed for saving the svg later again.
		    		Source source = new DOMSource(dobbleSvg);
		    		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		    		
		            xformer.transform(source, result);
	        
		            System.out.println("saved " + filename);
            	}
            }
            
        }
        
        
        

        

	  }
}
