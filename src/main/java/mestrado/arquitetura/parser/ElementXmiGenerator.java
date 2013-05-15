package mestrado.arquitetura.parser;


import mestrado.arquitetura.exceptions.CustonTypeNotFound;
import mestrado.arquitetura.exceptions.InvalidMultiplictyForAssociationException;
import mestrado.arquitetura.exceptions.NodeNotFound;
import mestrado.arquitetura.exceptions.NullReferenceFoundException;
import mestrado.arquitetura.helpers.UtilResources;
import mestrado.arquitetura.parser.method.Argument;
import mestrado.arquitetura.parser.method.Attribute;
import mestrado.arquitetura.parser.method.Method;
import mestrado.arquitetura.parser.method.Types;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Cria XMI para elementos UML.
 * 
 * @author edipofederle
 *
 */
public class ElementXmiGenerator extends XmiHelper {
	

	static Logger LOGGER = LogManager.getLogger(ElementXmiGenerator.class.getName());
	
	private Element element;
	private DocumentManager documentManager;
	private static final String METHOD_ID = "3013";
	private static final String METHODO_TYPE = "uml:Operation";
	private static final String LOCATION_TO_ADD_METHOD_IN_NOTATION_FILE = "7018";
	private static final String LOCATION_TO_ADD_ATTR_IN_NOTATION_FILE = "7017";
	private Node notatioChildren;
	private Node umlModelChild;
	private Element notationBasicOperation;
	private String id;

	private Node klass;
	private static final String PROPERTY_ID = "3012";
	private static final String PROPERTY_TYPE = "uml:Property";
	
	private ClassNotation notation;
	/**
	 * documentUml é o arquivo .uml
	 * 
	 * @param documentUml
	 */
	public ElementXmiGenerator(DocumentManager documentManager){
		this.documentManager = documentManager;
		this.umlModelChild = documentManager.getDocUml().getElementsByTagName("uml:Model").item(0);
		this.notatioChildren = documentManager.getDocNotation().getElementsByTagName("notation:Diagram").item(0);
		notation = new ClassNotation(this.documentManager, notatioChildren);
	}

	public Node generateClass(final String klassName, final String idPackage) throws CustonTypeNotFound, NodeNotFound, InvalidMultiplictyForAssociationException {
		
		mestrado.arquitetura.parser.Document.executeTransformation(documentManager, new Transformation(){

			public void useTransformation() throws NodeNotFound {
				id = UtilResources.getRandonUUID();
				element = documentManager.getDocUml().createElement("packagedElement");
				element.setAttribute("xmi:type", "uml:Class");
				element.setAttribute("xmi:id", id);
				element.setAttribute("name", klassName);
				klass = element;
				try {
					
					notation.createXmiForClassInNotationFile(id, idPackage);
					
					if((idPackage != null) && !("".equals(idPackage))){
						//Busca pacote para adicionar a class;
						Node packageToAppend = findByID(documentManager.getDocUml(), idPackage, "packagedElement");
						packageToAppend.appendChild(element);
					}else{
						umlModelChild.appendChild(element);
					}
				
				} catch (NullReferenceFoundException e) {
					LOGGER.error("A null reference has been found. The process will be interrupted");
				}
			}

		});
		
		return klass;
	}
	
	
	public void generateMethod(Method method, String idClass) throws NodeNotFound{
		final Element ownedOperation = documentManager.getDocUml().createElement("ownedOperation");
		ownedOperation.setAttribute("name", method.getName());
		ownedOperation.setAttribute("xmi:id", method.getId());
		ownedOperation.setAttribute("isAbstract", method.isAbstract());

		for (Argument arg : method.getArguments()) {
			Element ownedParameter  = documentManager.getDocUml().createElement("ownedParameter");
			ownedParameter.setAttribute("xmi:id", UtilResources.getRandonUUID());
			ownedParameter.setAttribute("name", arg.getName());
			ownedParameter.setAttribute("isUnique", "false");
			
			Element typeOperation = documentManager.getDocUml().createElement("type");
			typeOperation.setAttribute("xmi:type", "uml:PrimitiveType");
			typeOperation.setAttribute("href", "pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#"+arg.getType().getName());
			ownedParameter.appendChild(typeOperation);
			ownedOperation.appendChild(ownedParameter);
		}
		

		if(idClass != null){
			final Node klassToAddMethod = findByID(documentManager.getDocUml(), idClass, "packagedElement");
			klassToAddMethod.appendChild(ownedOperation);
			writeOnNotationFile(method.getId(), METHOD_ID, METHODO_TYPE, getNodeToAddMethodInNotationFile(idClass, LOCATION_TO_ADD_METHOD_IN_NOTATION_FILE));
		}else{
			klass.appendChild(ownedOperation);
			writeOnNotationFile(method.getId(), METHOD_ID, METHODO_TYPE, notationBasicOperation);
		}
	}
	
	public void generateAttribute(Attribute attribute, String idClass) throws CustonTypeNotFound, NodeNotFound{
		if(idClass != null){
			this.klass = findByID(documentManager.getDocUml(), idClass, "packagedElement");
			writeAttributeIntoUmlFile(attribute);
			writeOnNotationFile(attribute.getId(), PROPERTY_ID, PROPERTY_TYPE, getNodeToAddMethodInNotationFile(idClass, LOCATION_TO_ADD_ATTR_IN_NOTATION_FILE));
		}else{
			writeAttributeIntoUmlFile(attribute);
			writeOnNotationFile(attribute.getId(), PROPERTY_ID, PROPERTY_TYPE, null);
		}
	}
	
	
	private void writeOnNotationFile(String idProperty, String typeId, String typeElement, Element appendTo) {
		notation.createNodeForElementType(idProperty, typeId, typeElement, appendTo);
	}
	
	private String writeAttributeIntoUmlFile(Attribute attribute) throws CustonTypeNotFound {
		Element ownedAttribute = documentManager.getDocUml().createElement("ownedAttribute");
		ownedAttribute.setAttribute("xmi:id", attribute.getId());
		ownedAttribute.setAttribute("name", attribute.getName());
		ownedAttribute.setAttribute("visibility", attribute.getVisibility());
		ownedAttribute.setAttribute("isUnique", "false");
		klass.appendChild(ownedAttribute);
		
		if(Types.isCustomType(attribute.getType())){
			String id = findIdByName(attribute.getType(), documentManager.getDocUml());
			if ("".equals(id))	throw new CustonTypeNotFound("Type " + attribute.getType() + " not found");
			ownedAttribute.setAttribute("type", id);
		}else{
			Element typeProperty = documentManager.getDocUml().createElement("type");
			typeProperty.setAttribute("xmi:type", "uml:PrimitiveType");
			typeProperty.setAttribute("href", "pathmap://UML_LIBRARIES/UMLPrimitiveTypes.library.uml#"+attribute.getType());
			ownedAttribute.appendChild(typeProperty);
		}
		
		Element lowerValue = documentManager.getDocUml().createElement("lowerValue");
		lowerValue.setAttribute("xmi:type", "uml:LiteralInteger");
		lowerValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		lowerValue.setAttribute("value", "1");
		ownedAttribute.appendChild(lowerValue);
		
		Element upperValue = documentManager.getDocUml().createElement("upperValue");
		upperValue.setAttribute("xmi:type", "uml:LiteralUnlimitedNatural");
		upperValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		upperValue.setAttribute("value", "1");
		ownedAttribute.appendChild(upperValue);
		
		Element defaultValue = documentManager.getDocUml().createElement("defaultValue");
		defaultValue.setAttribute("xmi:type", "uml:LiteralString");
		defaultValue.setAttribute("xmi:id", UtilResources.getRandonUUID());
		ownedAttribute.appendChild(defaultValue);
		
		Element value = documentManager.getDocUml().createElement("value");
		value.setAttribute("xmi:nil", "true"); // TODO VER ISSO
		defaultValue.appendChild(value);
		return attribute.getId();
	}
	
	
	
	private Element getNodeToAddMethodInNotationFile(final String idClass, String location) throws NodeNotFound {
		Node nodeNotationToAddMethod = findByIDInNotationFile(documentManager.getDocNotation(), idClass);
		for(int i =0; i <  nodeNotationToAddMethod.getChildNodes().getLength(); i++){
			if("children".equalsIgnoreCase(nodeNotationToAddMethod.getChildNodes().item(i).getNodeName())){
				if(isLocationToAddMethodInNotationFile(nodeNotationToAddMethod, i, location))
					return (Element) nodeNotationToAddMethod.getChildNodes().item(i);
			}
		}
		return null; //TODO remover NULL
	}
	

	private boolean isLocationToAddMethodInNotationFile(Node nodeNotationToAddMethod, int i, String location ) {
		return nodeNotationToAddMethod.getChildNodes().item(i).getAttributes().getNamedItem("type").getNodeValue().equals(location);
	}
	
	public void createEgdeAssocationOnNotationFile(Document docNotation, String newModelName, String client, String target, String idEdge) throws NodeNotFound{
		
		Node node = docNotation.getElementsByTagName("notation:Diagram").item(0);
		
		NamedNodeMap attributesOwnner = findByIDInNotationFile(docNotation, client).getAttributes();
		NamedNodeMap attributesDestination = findByIDInNotationFile(docNotation, target).getAttributes();
		String idSource = attributesOwnner.getNamedItem("xmi:id").getNodeValue();
		String idTarget = attributesDestination.getNamedItem("xmi:id").getNodeValue();
		
		Element edges = docNotation.createElement("edges");
		edges.setAttribute("xmi:type", "notation:Connector");
		edges.setAttribute("xmi:id", UtilResources.getRandonUUID());
		edges.setAttribute("type", "4001");
		edges.setAttribute("source", idSource);
		edges.setAttribute("target", idTarget);
		edges.setAttribute("lineColor", "0");
		
		//Aparecer nome no relacionamento
		Element childrenDecorationNode = docNotation.createElement("children");
		childrenDecorationNode.setAttribute("xmi:type", "notation:DecorationNode");
		childrenDecorationNode.setAttribute("xmi:id", UtilResources.getRandonUUID());
		childrenDecorationNode.setAttribute("type", "6033");
		
		Element childrenDecorationNodeName = docNotation.createElement("children");
		childrenDecorationNodeName.setAttribute("xmi:type", "notation:DecorationNode");
		childrenDecorationNodeName.setAttribute("xmi:id", UtilResources.getRandonUUID());
		childrenDecorationNodeName.setAttribute("type", "6002");
		edges.appendChild(childrenDecorationNodeName);
		
		//FIm aparecer nome no relacionamento
		
		Element layoutConstraintName = docNotation.createElement("layoutConstraint");
		layoutConstraintName.setAttribute("xmi:type", "notation:Location");
		layoutConstraintName.setAttribute("xmi:id", UtilResources.getRandonUUID());
		layoutConstraintName.setAttribute("y", "20");
		childrenDecorationNodeName.appendChild(layoutConstraintName);
		
		Element layoutConstraint = docNotation.createElement("layoutConstraint");
		layoutConstraint.setAttribute("xmi:type", "notation:Location");
		layoutConstraint.setAttribute("xmi:id", UtilResources.getRandonUUID());
		layoutConstraint.setAttribute("y", "20");
		childrenDecorationNode.appendChild(layoutConstraint);
		
		
		Element childrenDecorationNode2 = docNotation.createElement("children");
		childrenDecorationNode2.setAttribute("xmi:type", "notation:DecorationNode");
		childrenDecorationNode2.setAttribute("xmi:id", UtilResources.getRandonUUID());
		childrenDecorationNode2.setAttribute("type", "6034");
		edges.appendChild(childrenDecorationNode2);
		
		Element layoutConstraint2 = docNotation.createElement("layoutConstraint");
		layoutConstraint2.setAttribute("xmi:type", "notation:Location");
		layoutConstraint2.setAttribute("xmi:id", UtilResources.getRandonUUID());
		layoutConstraint2.setAttribute("y", "-20");
		childrenDecorationNode2.appendChild(layoutConstraint2);
		
		//Fim multiplicidade
		
		Element elementAssociation = docNotation.createElement("element");
		elementAssociation.setAttribute("xmi:type", "uml:Association");
		elementAssociation.setAttribute("href", newModelName +".uml#"+idEdge);
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

	public void createStereotype(String stereotypeName, String idClass) {
		Node nodeXmi = this.documentManager.getDocUml().getElementsByTagName("xmi:XMI").item(0);
		
		Element stereotype = this.documentManager.getDocUml().createElement("smartyProfile:"+stereotypeName);
		stereotype.setAttribute("xmi:id", UtilResources.getRandonUUID());
		stereotype.setAttribute("base_Class", idClass); // A classe que tem o estereotype
		
		nodeXmi.appendChild(stereotype);
		
	}
	

}