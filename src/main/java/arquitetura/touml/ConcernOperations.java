package arquitetura.touml;

import java.util.Set;

import arquitetura.representation.Concern;

public class ConcernOperations {
	
	private ElementXmiGenerator elementXmiGenerator;

	public ConcernOperations(DocumentManager documentManager) {
		this.elementXmiGenerator = new ElementXmiGenerator(documentManager, null);
	}
	
	public ConcernOperations withConcerns(Set<Concern> concerns, String id) {
		if(!concerns.isEmpty()){
			for(final Concern concern : concerns){
				elementXmiGenerator.generateConcern(concern, id);
			}
		}
		return this;
	}
	
	public ConcernOperations withConcern(Concern concern, String id) {
		elementXmiGenerator.generateConcern(concern, id);
		return this;
	}
}