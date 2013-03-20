package mestrado.arquitetura.builders;

import mestrado.arquitetura.base.RelationshipBase;
import mestrado.arquitetura.representation.Architecture;
import mestrado.arquitetura.representation.Element;
import mestrado.arquitetura.representation.relationship.DependencyRelationship;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.NamedElement;

/**
 * 
 * @author edipofederle
 *
 */
public class DependencyRelationshipBuilder  extends RelationshipBase{

	private Architecture architecture;

	public DependencyRelationshipBuilder( Architecture architecture) {
		this.architecture = architecture;
	}
	public DependencyRelationship create(Dependency element) {

		EList<NamedElement> suppliers = element.getSuppliers();
		EList<NamedElement> clieents = element.getClients();
		
		Element client = architecture.getElementByXMIID(getModelHelper().getXmiId(clieents.get(0)));
		Element supplier = architecture.getElementByXMIID(getModelHelper().getXmiId(suppliers.get(0)));
		
		return new DependencyRelationship(supplier, client, element.getName(), architecture, getModelHelper().getXmiId(element));
	}

}
