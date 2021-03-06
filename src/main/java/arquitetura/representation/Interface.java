package arquitetura.representation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import arquitetura.helpers.UtilResources;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.RealizationRelationship;

/**
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class Interface extends Element {
	
	
	private static final long serialVersionUID = -1779316062511432020L;

	static Logger LOGGER = LogManager.getLogger(Interface.class.getName());
	private final Set<Method> operations = new HashSet<Method>();
	

	public Interface(Architecture architecture, String name, Variant variantType, String namespace, String id) {
		super(architecture, name, variantType, "interface", namespace, id);
	}
	
	/**
	 * Use este construtor quando você deseja criar uma interface.<br /><br />
	 * 
	 * OBS 1: O ID para esta interface será gerado automaticamente.<br/>
	 * OBS 2: Esse construtor automaticamente adicionar a interface na arquitetura<br/>
	 * 
	 * @param architecture Architecture em questão
	 * @param name - Nome da interface
	 */
	public Interface(Architecture a, String name) {
		this(a, name, null, UtilResources.createNamespace(a.getName(), name), UtilResources.getRandonUUID());
		a.addExternalInterface(this);
	}
	
	/**
	 * Use este construtor quando você deseja criar uma interface usando algum ID passado por você<br /><br />
	 * 
	 * OBS 2: Esse construtor automaticamente adicionar a interface na arquitetura<br/>
	 * 
	 * @param architecture Architecture em questão
	 * @param name - Nome da interface
	 */
	public Interface(Architecture a, String name, String id) {
		this(a, name, null, UtilResources.createNamespace(a.getName(), name), id);
		a.addExternalInterface(this);
	}

	public  Set<Method> getOperations() {
		return Collections.unmodifiableSet(operations);
	}
	
	public boolean removeOperation(Method operation) {
		if(operations.remove(operation)){
			LOGGER.info("Removeu operação '" + operation + "', da interface: " + this.getName());
			return true;
		}else{
			LOGGER.info("TENTOU removeu operação '" + operation + "', da interface: " + this.getName() + " porém não conseguiu");
			return false;
		}
	}
	
	public Method createOperation(String operationName) throws Exception {
		Method operation = new Method(getArchitecture(), operationName, false, null, "void", false, null,  "", ""); //Receber id 
		operations.add(operation);
		return operation;
	}

	public boolean moveOperationToInterface(Method operation, Interface interfaceToMove) {
		if(!interfaceToMove.addExternalOperation(operation))
			return false;
		
		if(!removeOperation(operation)){
			interfaceToMove.removeOperation(operation);
			return false;
		}
		operation.setNamespace(getArchitecture().getName() + "::" + interfaceToMove.getName());
		LOGGER.info("Moveu operação: "+  operation.getName() + " de " +this.getName() +" para " + interfaceToMove.getName());
		return true;
		
	}
	
	
	public boolean addExternalOperation(Method operation) {
		if(operations.add(operation)){
			LOGGER.info("Operação "+operation.getName() + " adicionado na interface "+ this.getName());
			return true;
		}else{
			LOGGER.info("TENTOU remover a operação: "+ operation.getName() + " da interface: "+ this.getName() + " porém não consegiu");
			return false;
		}
			
	}
	

	public Set<Element> getImplementors() {
		Set<Element> implementors = new HashSet<Element>();
		
		for(Class klass : getArchitecture().getAllClasses()){
			if(klass.getImplementedInterfaces().contains(this))
				implementors.add(klass);
		}
		
		for(Package p : getArchitecture().getAllPackages()){
			for(RealizationRelationship r : getArchitecture().getAllRealizations()){
				if(r.getClient().equals(p) && (r.getSupplier().equals(this))){
					implementors.add(p);
				}
			}
		}
					
		return Collections.unmodifiableSet(implementors);
	}
	
	public Set<Element> getRealImplementors() {
		Set<Element> implementors = new HashSet<Element>();
		
		for(Package p : getArchitecture().getAllPackages()){
			for(RealizationRelationship r : getArchitecture().getAllRealizations()){
				if(r.getClient().equals(p)){
					implementors.add(p);
				}
			}
		}
					
		return Collections.unmodifiableSet(implementors);
	}

	public Set<Element> getDependents() {
		Set<Element> dependents = new HashSet<Element>();
		
		for(Class klass : getArchitecture().getAllClasses()){
			if(klass.getRequiredInterfaces().contains(this))
				dependents.add(klass);
		}
		
		for(Package p : getArchitecture().getAllPackages()){
			if(p.getRequiredInterfaces().contains(this))
				dependents.add(p);
		}
		
		return Collections.unmodifiableSet(dependents);
	}
	
	@Override
	public Set<Concern> getAllConcerns() {
		Set<Concern> concerns = new HashSet<Concern>(getOwnConcerns());
		for (Method operation : getOperations())
			concerns.addAll(operation.getAllConcerns());
		concerns.addAll(this.getOwnConcerns());
		
		return Collections.unmodifiableSet(concerns);
	}
	
	public List<DependencyRelationship> getDependencies() {
		List<DependencyRelationship> dependencies = new ArrayList<DependencyRelationship>();
		
		for (DependencyRelationship dependency : getArchitecture().getAllDependencies()) {
			if (dependency.getSupplier().equals(this))
				dependencies.add(dependency);
		}
		
		return Collections.unmodifiableList(dependencies);
	}

}
