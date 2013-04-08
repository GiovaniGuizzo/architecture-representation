package mestrado.arquitetura.parser;

import mestrado.arquitetura.helpers.UtilResources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 *  <packagedElement xmi:type="uml:Association" xmi:id="_kA-5o5V4EeKR5_LfMQQPUg" name="class1_class2_1" memberEnd="_kA-5pJV4EeKR5_LfMQQPUg _kA-5oJV4EeKR5_LfMQQPUg">
    <ownedEnd xmi:id="_kA-5pJV4EeKR5_LfMQQPUg" name="class1" type="_jJGPYJV4EeKR5_LfMQQPUg" association="_kA-5o5V4EeKR5_LfMQQPUg">
      <lowerValue xmi:type="uml:LiteralInteger" xmi:id="_kA-5pZV4EeKR5_LfMQQPUg" value="1"/>
      <upperValue xmi:type="uml:LiteralUnlimitedNatural" xmi:id="_kA-5ppV4EeKR5_LfMQQPUg" value="1"/>
    </ownedEnd>
    </packagedElement>
  
 */
public class AssociationNode extends XmiHelper{
	
	private Document docUml;
	private Document docNotation;
	
	private String idClassOwnnerAssociation;
	private String idClassDestinationAssociation;
	
	private final String idAssocation;
	private final String memberEndId;
	
	public AssociationNode(Document docUml, Document docNotation) {
		this.docUml = docUml;
		this.docNotation = docNotation;
		
		this.idAssocation = UtilResources.getRandonUUID();
		this.memberEndId  = UtilResources.getRandonUUID();
	}

	public void createAssociation(String idClassOwnnerAssociation, String idClassDestinationAssociation) {
		
		this.idClassDestinationAssociation = idClassDestinationAssociation;
		this.idClassOwnnerAssociation = idClassOwnnerAssociation;
		
		Node modelRoot = this.docUml.getElementsByTagName("uml:Model").item(0);
		
		Element packageElement = this.docUml.createElement("packagedElement");
		packageElement.setAttribute("xmi:type", "uml:Association");
		packageElement.setAttribute("xmi:id", this.idAssocation);
		packageElement.setAttribute("name", "associationName"); 
		
		String memberEnd = UtilResources.getRandonUUID();
		packageElement.setAttribute("memberEnd", memberEndId + " "+ memberEnd);	
		
		Element ownedEnd = this.docUml.createElement("ownedEnd");
		ownedEnd.setAttribute("xmi:id", memberEnd);
		ownedEnd.setAttribute("name", "ClassName");
		ownedEnd.setAttribute("type", this.idClassOwnnerAssociation); // Class 1 No exemplo acima
		ownedEnd.setAttribute("association", this.idAssocation); //No exemplo _kA-5o5V4EeKR5_LfMQQPUg
		packageElement.appendChild(ownedEnd);
		
		Element lowerValue = this.docUml.createElement("lowerValue");
		lowerValue.setAttribute("xmi:type", "uml:LiteralInteger");
		lowerValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		lowerValue.setAttribute("value", "1");
		ownedEnd.appendChild(lowerValue);
		
		Element upperValue = this.docUml.createElement("upperValue");
		upperValue.setAttribute("xmi:type", "uml:LiteralInteger");
		upperValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		upperValue.setAttribute("value", "1");
		ownedEnd.appendChild(upperValue);
		
		modelRoot.appendChild(packageElement);
		
		ownedAttibute();
		
		createEgdeAssocationOnNotationFile();
		
	}
	
	/**
 *    <ownedAttribute xmi:id="_kA-5oJV4EeKR5_LfMQQPUg" name="class2" type="_jV8_oJV4EeKR5_LfMQQPUg" association="_kA-5o5V4EeKR5_LfMQQPUg">
  		<lowerValue xmi:type="uml:LiteralInteger" xmi:id="_kA-5oZV4EeKR5_LfMQQPUg" value="1"/>
  		<upperValue xmi:type="uml:LiteralUnlimitedNatural" xmi:id="_kA-5opV4EeKR5_LfMQQPUg" value="1"/>
	  </ownedAttribute>
	 */
	private void ownedAttibute(){
		
		//Primeiro busca pela class que seja a "dona" da associção. Isso é feito por meio do ID.
		Node packageElementNode = findByID(docUml, this.idClassOwnnerAssociation, "packagedElement");
		System.out.println(packageElementNode.getAttributes().getNamedItem("name"));
		
		Element ownedAttibute = this.docUml.createElement("ownedAttribute");
		ownedAttibute.setAttribute("xmi:id", memberEndId);
		ownedAttibute.setAttribute("name", "ClassDestination");
		ownedAttibute.setAttribute("type", this.idClassDestinationAssociation);
		ownedAttibute.setAttribute("association", this.idAssocation);
		
		
		Element lowerValue = this.docUml.createElement("lowerValue");
		lowerValue.setAttribute("xmi:type", "uml:LiteralInteger");
		lowerValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		lowerValue.setAttribute("value", "1");
		ownedAttibute.appendChild(lowerValue);
		
		Element upperValue = this.docUml.createElement("upperValue");
		upperValue.setAttribute("xmi:type", "uml:LiteralUnlimitedNatural");
		upperValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		upperValue.setAttribute("value", "1");
		ownedAttibute.appendChild(upperValue);
		
		packageElementNode.appendChild(ownedAttibute);
	}


	private void createEgdeAssocationOnNotationFile(){
		
		Node node = this.docNotation.getElementsByTagName("notation:Diagram").item(0);
		
		//Buscar no Notation O id do "shape" para essa classe
		NamedNodeMap attributesOwnner = findByIDInNotationFile(docNotation,idClassOwnnerAssociation).getAttributes();
		NamedNodeMap attributesDestination = findByIDInNotationFile(docNotation, idClassDestinationAssociation).getAttributes();
		String idSource = attributesOwnner.getNamedItem("xmi:id").getNodeValue();
		String idTarget = attributesDestination.getNamedItem("xmi:id").getNodeValue();
		
		
		Element edges = this.docNotation.createElement("edges");
		edges.setAttribute("xmi:type", "notation:Connector");
		edges.setAttribute("xmi:id", UtilResources.getRandonUUID());
		edges.setAttribute("type", "4001");
		edges.setAttribute("source", idSource);
		edges.setAttribute("target", idTarget);
		edges.setAttribute("lineColor", "0");
		
		
		//<element xmi:type="uml:Association" href="simples.uml#f4f2b06e-5be7-43b2-bb52-8ee7de1384b8"/>
		Element elementAssociation = this.docNotation.createElement("element");
		elementAssociation.setAttribute("xmi:type", "uml:Association");
		elementAssociation.setAttribute("href", "simples.uml#"+this.idAssocation);
		edges.appendChild(elementAssociation);
		
		Element styles = docNotation.createElement("styles");
		styles.setAttribute("xmi:type", "notation:FontStyle");
		styles.setAttribute("xmi:id", UtilResources.getRandonUUID());
		styles.setAttribute("xmi:id", UtilResources.getRandonUUID());
		styles.setAttribute("fontName", "Lucida Grande");
		styles.setAttribute("fontHeight", "11");
		edges.appendChild(styles);
		
		Element bendpoints = docNotation.createElement("bendpoints");
		bendpoints.setAttribute("xmi:type", "notation:RelativeBendpoints");
		bendpoints.setAttribute("xmi:id", UtilResources.getRandonUUID());
		bendpoints.setAttribute("points", "[0, 0, -200, -20]$[255, -30, -6, -50]");
		edges.appendChild(bendpoints);
		
		Element sourceAnchor = docNotation.createElement("sourceAnchor");
		sourceAnchor.setAttribute("xmi:type", "notation:IdentityAnchor");
		sourceAnchor.setAttribute("xmi:id", UtilResources.getRandonUUID());
		sourceAnchor.setAttribute("id", "(1.0,0.36)");
		edges.appendChild(sourceAnchor);
		
		node.appendChild(edges);
		
		
	}
	
	
	public void  removeAssociation(String id){
		//Busca por node "edges" no arquivo notation.
		
		Node nodeToRemove = null;
		Node notationNode = this.docNotation.getElementsByTagName("notation:Diagram").item(0);
		try{
			NodeList nodesEdges = docNotation.getElementsByTagName("edges");
			for (int i = 0; i < nodesEdges.getLength(); i++) {
				NodeList childNodes = nodesEdges.item(i).getChildNodes();
				for (int j = 0; j < childNodes.getLength(); j++) {
					if(childNodes.item(j).getNodeName().equalsIgnoreCase("element")){
						String idHref = childNodes.item(j).getAttributes().getNamedItem("href").getNodeValue();
						if(idHref.contains(id)){
							nodeToRemove =  nodesEdges.item(i);
						}
					}
						
				}
			}
			
			notationNode.removeChild(nodeToRemove);
		}catch(Exception e){
			System.out.println("Cannot remove Association with id: " + id +"." + e.getMessage());
		}
	}
	
	public String getIdAssocation(){
		return this.idAssocation;
	}
	

}
