package mestrado.arquitetura.parser;

import mestrado.arquitetura.exceptions.CustonTypeNotFound;
import mestrado.arquitetura.exceptions.InvalidMultiplictyForAssociationException;
import mestrado.arquitetura.exceptions.NodeNotFound;

public class DependencyOperations implements Dependency {
	
	private DocumentManager documentManager;
	
	private String clientElement;
	private String supplierElement;
	private String name;
	
	public DependencyOperations(DocumentManager doc) {
		this.documentManager = doc;
	}

	public DependencyOperations(DocumentManager documentManager2, String name2) {
		this.documentManager = documentManager2;
		this.name = name2;
	}

	public Dependency createDependency(String name) {
		if(("".equals(name) || name == null)) name = "dependency";
		return new DependencyOperations(this.documentManager, name);
	}

	public Dependency between(String idElement) {
		this.clientElement = idElement;
		return this;
	}

	public Dependency and(String idElement) {
		this.supplierElement = idElement;
		return this;
	}

	public String build() throws CustonTypeNotFound, NodeNotFound,	InvalidMultiplictyForAssociationException {
		final DependencyNode dependencyNode = new DependencyNode(this.documentManager, this.name, this.clientElement, this.supplierElement);
		
		mestrado.arquitetura.parser.Document.executeTransformation(documentManager, new Transformation(){
			public void useTransformation() throws NodeNotFound, InvalidMultiplictyForAssociationException {
				dependencyNode.createDependency("dependency");
			}
		});
		
		return ""; //TODO return id;

	}

}