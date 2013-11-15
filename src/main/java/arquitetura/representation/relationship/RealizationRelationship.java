package arquitetura.representation.relationship;

import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Package;

/**
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class RealizationRelationship extends Relationship {
	
	private Element client;
	private Element supplier;
	
	
	public RealizationRelationship(Element client, Element supplier, String name, String id){
		setClient(client);
		setSupplier(supplier);
		setId(id);
		super.setName(name);
		
		if((client instanceof Package) && (supplier instanceof Interface)){
			((Package) client).addImplementedInterface(supplier);
		}
	}

	/**
	 * @return the client
	 */
	public Element getClient() {
		return client;
	}


	/**
	 * @param client the client to set
	 */
	public void setClient(Element client) {
		this.client = client;
	}


	/**
	 * @return the supplier
	 */
	public Element getSupplier() {
		return supplier;
	}


	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Element supplier) {
		this.supplier = supplier;
	}


	
}