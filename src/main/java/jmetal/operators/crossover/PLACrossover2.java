package jmetal.operators.crossover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.ArchitectureSolutionType;
import jmetal.problems.OPLA;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import arquitetura.exceptions.ClassNotFound;
import arquitetura.exceptions.ConcernNotFoundException;
import arquitetura.exceptions.ElementNotFound;
import arquitetura.exceptions.InterfaceNotFound;
import arquitetura.exceptions.NotFoundException;
import arquitetura.exceptions.PackageNotFound;
import arquitetura.helpers.UtilResources;
import arquitetura.representation.Architecture;
import arquitetura.representation.Attribute;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Method;
import arquitetura.representation.Package;
import arquitetura.representation.Variability;
import arquitetura.representation.VariationPoint;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.Relationship;

public class PLACrossover2 extends Crossover {

	private static final long serialVersionUID = -51015356906090226L;

	private Double crossoverProbability_ = null;
	private CrossoverUtils crossoverutils;

	private static List VALID_TYPES = Arrays.asList(ArchitectureSolutionType.class);
	//use "oneLevel" para não verificar a presença de interesses nos atributos e métodos
	private static String SCOPE_LEVEL = "allLevels"; 
	private boolean variabilitiesOk= true;
	
	public PLACrossover2(HashMap<String, Object> parameters) {
		super(parameters);
		if (parameters.get("probability") != null)
			crossoverProbability_ = (Double) getParameter("probability");
		
		crossoverutils = new CrossoverUtils();
	}
	
	public Object execute(Object object) throws JMException, CloneNotSupportedException, ClassNotFound, PackageNotFound, NotFoundException, ConcernNotFoundException {
		Solution [] parents = (Solution []) object;
		if (!(VALID_TYPES.contains(parents[0].getType().getClass())  && VALID_TYPES.contains(parents[1].getType().getClass())) ) {
			Configuration.logger_.severe("PLACrossover.execute: the solutions " + "are not of the right type. The type should be 'Permutation', but " + parents[0].getType() + " and " + parents[1].getType() + " are obtained");
		} 
		crossoverProbability_ = (Double)getParameter("probability");
		if (parents.length < 2){
			Configuration.logger_.severe("PLACrossover.execute: operator needs two " +	"parents");
			java.lang.Class<String> cls = java.lang.String.class;
			String name = cls.getName(); 
			throw new JMException("Exception in " + name + ".execute()") ;      
		}

		Solution [] offspring = doCrossover(crossoverProbability_, parents[0], parents[1]); 
		return offspring; 
	} 
	
    public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMException, CloneNotSupportedException, ClassNotFound, PackageNotFound, NotFoundException, ConcernNotFoundException {
        Solution[] offspring = new Solution[2];
        
        Solution[] crossFeature = this.crossoverFeatures(crossoverProbability_, parent1, parent2, SCOPE_LEVEL);
        offspring[0] = crossFeature[0];
        offspring[1] = crossFeature[1];
     
        return offspring;
    }
    
    public Solution[] crossoverFeatures(double probability, Solution parent1, Solution parent2, String scope) throws JMException, CloneNotSupportedException, ClassNotFound, PackageNotFound, NotFoundException, ConcernNotFoundException {

    	// STEP 0: Create two offsprings
        Solution[] offspring = new Solution[2];
        offspring[0] = new Solution(parent1);
        offspring[1] = new Solution(parent2);
               
        try {
            if (parent1.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
               if (PseudoRandom.randDouble() < probability) {
              
                    // STEP 1: Get feature to crossover
                    List<Concern> concernsArchitecture = new ArrayList<Concern> (((Architecture) offspring[0].getDecisionVariables()[0]).getAllConcerns());
                    Concern feature = randomObject(concernsArchitecture);
                   
                    obtainChild(feature, (Architecture) parent2.getDecisionVariables()[0], (Architecture) offspring[0].getDecisionVariables()[0], scope);
                    //Thelma - Dez2013 adicionado para descartar as solucoes com interfaces desconectadas de componentes na PLA e com variabilidades cujos pontos de variacao não fazem parte da solucao
        	        if (!(isValidSolution((Architecture) offspring[0].getDecisionVariables()[0]))){
        	        	//offspring[0] = new Solution(parent1);
        	        	offspring[0] =parent1;
        	        	OPLA.contDiscardedSolutions_++;
        	        }
        	        this.variabilitiesOk = true;
                    obtainChild(feature, (Architecture) parent1.getDecisionVariables()[0], (Architecture) offspring[1].getDecisionVariables()[0], scope);
                    //Thelma - Dez2013 adicionado para descartar as solucoes com interfaces desconectadas de componentes na PLA e com variabilidades cujos pontos de variacao não fazem parte da solucao
        	        if (!(isValidSolution((Architecture) offspring[1].getDecisionVariables()[0]))){
        	        	//offspring[0] = new Solution(parent1);
        	        	offspring[0] =parent1;
        	        	OPLA.contDiscardedSolutions_++;
        	        }
        	        concernsArchitecture.clear();
               }
            }
            else {
            	Configuration.logger_.log(Level.SEVERE, "PLACrossover.doCrossover: "+ "invalid type{0}", parent1.getDecisionVariables()[0].getVariableType());
            	java.lang.Class<String> cls = java.lang.String.class;
            	String name = cls.getName();
            	throw new JMException("Exception in " + name + ".doCrossover()");
            }
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();            
        }

        return offspring;
    }
    
    public void obtainChild(Concern feature, Architecture parent, Architecture offspring, String scope) throws CloneNotSupportedException, ClassNotFound, PackageNotFound, NotFoundException, ConcernNotFoundException{
    	//eliminar os elementos arquiteturais que realizam feature em offspring
    	crossoverutils.removeArchitecturalElementsRealizingFeature(feature, offspring, scope);
		//adicionar em offspring os elementos arquiteturais que realizam feature em parent
		addElementsToOffspring(feature, offspring, parent, scope);
		this.variabilitiesOk = updateVariabilitiesOffspring(offspring);
    }

	public <T> T randomObject(List<T> allObjects)  throws JMException   {
	    int numObjects= allObjects.size(); 
	    int key;
	    T object;
	    if (numObjects == 0) {
	    	object = null;
	    } else{
	    	key = PseudoRandom.randInt(0, numObjects-1); 
	    	object = allObjects.get(key);
	    }
	    return object;      	    	
	}
	
	public void addElementsToOffspring(Concern feature, Architecture offspring, Architecture parent, String scope) {
		for(Package parentPackage : parent.getAllPackages()){
			//Cria ou adiciona o pacote de parent em offspring
			addOrCreatePackageIntoOffspring(feature, offspring, parent, parentPackage);
		}
		CrossoverRelationship.createRelationshipsInOffspring(offspring);
		CrossoverRelationship.cleanRelationships();
		
	}

	public void addOrCreatePackageIntoOffspring(Concern feature, Architecture offspring, Architecture parent, Package parentPackage) {
		Package packageInOffspring = null;
		
		/*
		 * Caso parentPackage cuide somente de UM interesse. Tenta localizar Pacote em offspring
		 * Caso não encontrar o cria.
		 */
		if(parentPackage.containsConcern(feature) && (parentPackage.getOwnConcerns().size() == 1)){
			try {
				packageInOffspring = offspring.findPackageByName(parentPackage.getName());
			} catch (PackageNotFound e) {
				packageInOffspring = offspring.createPackage(parentPackage.getName());
			}
			addImplementedInterfacesByPackageInOffspring(parentPackage, offspring, parent);
			addRequiredInterfacesByPackageInOffspring(parentPackage, offspring, parent);
			addInterfacesToPackageInOffSpring(parentPackage, packageInOffspring, offspring, parent);
			
			addClassesToOffspring(feature, parentPackage, packageInOffspring, offspring, parent);
		}else{
			addInterfacesRealizingFeatureToOffspring(feature, parentPackage, offspring, parent);
			addClassesRealizingFeatureToOffspring(feature, parentPackage, offspring, parent, SCOPE_LEVEL);
		}
		
		CrossoverRelationship.saveAllRelationshiopForElement(parentPackage, parent);
		
	}

	private void addClassesRealizingFeatureToOffspring(Concern feature,	Package parentPackage, Architecture offspring, Architecture parent,	String sCOPE_LEVEL2) {
    	Package newComp = null;
    	
    	try {
			newComp = offspring.findPackageByName(parentPackage.getName());
		} catch (PackageNotFound e1) {
			newComp = null;
		}
    	
    	List<Class> allClasses = new ArrayList<Class> (parentPackage.getAllClasses());
		Iterator<Class> iteratorClasses = allClasses.iterator();
		
		while (iteratorClasses.hasNext()){
        	Class classComp = iteratorClasses.next();
    		if (classComp.containsConcern(feature) && classComp.getOwnConcerns().size() == 1){
    			if(newComp == null){
    				newComp = offspring.createPackage(parentPackage.getName());
    			}
        		if (!searchForGeneralizations(classComp)){
        			addClassToOffspring(classComp, newComp, offspring, parent);
        		} else {
        			if (this.isHierarchyInASameComponent(classComp,parent)){
        				moveHierarchyToSameComponent(classComp, newComp, parentPackage, offspring, parent, feature);
        				CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
        			}else{
        				newComp.addExternalClass(classComp);
        				moveHierarchyToDifferentPackage(classComp, newComp, parentPackage, offspring, parent);
        				CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
        			}
        		}
        	}	
    		else{
				if ((SCOPE_LEVEL.equals("allLevels")) && (!searchForGeneralizations(classComp))){
					addAttributesRealizingFeatureToOffspring(feature, classComp, parentPackage, offspring, parent);
					addMethodsRealizingFeatureToOffspring(feature, classComp, parentPackage, offspring, parent);
				}
    		}
    		addInterfacesImplementedByClass(classComp, offspring, parent, newComp);
    		addInterfacesRequiredByClass(classComp, offspring, parent, newComp);
		}
		allClasses = null;
	}
	
	public void addAttributesRealizingFeatureToOffspring(Concern feature, Class classComp, Package comp, Architecture offspring, Architecture parent) {

		Class targetClass;
		try {
			targetClass = offspring.findClassByName(classComp.getName()).get(0);
		} catch (ClassNotFound e1) {
			targetClass = null;
		}
		List<Attribute> allAttributes = new ArrayList<Attribute>(classComp.getAllAttributes());
		if (!allAttributes.isEmpty()) {
			Iterator<Attribute> iteratorAttributes = allAttributes.iterator();
			while (iteratorAttributes.hasNext()) {
				Attribute attribute = iteratorAttributes.next();
				if (attribute.containsConcern(feature) && attribute.getOwnConcerns().size() == 1) {
					if (targetClass == null) {
						Package newComp = null;
						try {
							newComp = offspring.findPackageByName(comp.getName());
						} catch (PackageNotFound e) {
							newComp = offspring.createPackage(comp.getName());
						}
						try {
							targetClass = newComp.createClass(classComp.getName(), false);
							targetClass.addConcern(feature.getName());
							CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						CrossoverRelationship.saveAllRelationshiopForElement(newComp,parent);
					}
					classComp.moveAttributeToClass(attribute, targetClass);
					CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
				}
			}
		}
		allAttributes.clear();
	}
	
    private void addMethodsRealizingFeatureToOffspring(Concern feature, Class classComp, Package comp, Architecture offspring, Architecture parent){
    	Class targetClass;
		try {
			targetClass = offspring.findClassByName(classComp.getName()).get(0);
		} catch (ClassNotFound e2) {
			targetClass = null;
		}
    	
    	List<Method> allMethods = new ArrayList<Method> (classComp.getAllMethods());
		if (!allMethods.isEmpty()) {
			Iterator<Method> iteratorMethods = allMethods.iterator();
			while (iteratorMethods.hasNext()){
				Method method= iteratorMethods.next();
            	if (method.containsConcern(feature) && method.getOwnConcerns().size()==1){
        			if (targetClass == null){
        				Package newComp;
						try {
							newComp = offspring.findPackageByName(comp.getName());
						} catch (PackageNotFound e1) {
							newComp = offspring.createPackage(comp.getName());
						}
            			try {
							targetClass=newComp.createClass(classComp.getName(), false);
							targetClass.addConcern(feature.getName());
						} catch (Exception e) {e.printStackTrace();}
            		}
        			CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
            		classComp.moveMethodToClass(method, targetClass);
            	}
			}
		}
		allMethods.clear();
    }

	/**
	 * Adicionar as interfaces que o pacote possuia em parent no pacote em offspring
	 * 
	 * @param parentPackage
	 * @param packageInOffspring
	 * @param offspring
	 * @param parent
	 */
    private void addInterfacesToPackageInOffSpring(Package parentPackage,Package packageInOffspring, Architecture offspring, Architecture parent) {
    	Set<Interface> interfacesOfPackage = parentPackage.getAllInterfaces();
    	for(Interface inter : interfacesOfPackage){
    		packageInOffspring.addExternalInterface(inter);
    		CrossoverRelationship.saveAllRelationshiopForElement(inter, parent);
    	}
    	interfacesOfPackage = null;
	}

	private void addInterfacesRealizingFeatureToOffspring (Concern feature, Package comp, Architecture offspring, Architecture parent) {
    	Package newComp;
    	List<Interface> allInterfaces = new ArrayList<Interface> (comp.getOnlyInterfacesImplementedByPackage());
    	
		Iterator<Interface> iteratorInterfaces = allInterfaces.iterator();
		while (iteratorInterfaces.hasNext()){
			Interface interfaceComp = iteratorInterfaces.next();
        	if (interfaceComp.containsConcern(feature) && interfaceComp.getOwnConcerns().size() == 1){
    			try {
					newComp = offspring.findPackageByName(comp.getName());
				} catch (PackageNotFound e1) { newComp = null; }
    			if (newComp == null){
					try {
						newComp = offspring.createPackage(comp.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					CrossoverRelationship.saveAllRelationshiopForElement(newComp, parent);
    			}
    			if(interfaceComp.getNamespace().equalsIgnoreCase("model"))
    				offspring.addExternalInterface(interfaceComp);
    			else{
					String interfaceCompPackageName = UtilResources.extractPackageName(interfaceComp.getNamespace());
					Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
					interfaceComp = packageToAddInterface.createInterface(interfaceComp.getName());
    			}
    			CrossoverRelationship.saveAllRelationshiopForElement(interfaceComp, parent);
        	}else{
        		addOperationsRealizingFeatureToOffspring(feature, interfaceComp, comp, offspring, parent);
        	}
		}
		allInterfaces = null;
    }
    
    private void addOperationsRealizingFeatureToOffspring(Concern feature, Interface interfaceComp, Package comp, Architecture offspring, Architecture parent) {
    	Interface targetInterface = null;
    	
		try {
			targetInterface = offspring.findInterfaceByName(interfaceComp.getName());
		} catch (InterfaceNotFound e1) { targetInterface = null;}
		
    	List<Method> allOperations = new ArrayList<Method> (interfaceComp.getOperations());
		Iterator<Method> iteratorOperations = allOperations.iterator();
		while (iteratorOperations.hasNext()){
			Method operation = iteratorOperations.next();
        	if (operation.containsConcern(feature) && operation.getOwnConcerns().size() == 1){
        		if (targetInterface == null){
        			Package newComp;
        			try {
						newComp = offspring.findPackageByName(comp.getName());
					} catch (PackageNotFound e1) {newComp = null;}
					
        			if (newComp == null) {
        				try {
							newComp = offspring.createPackage(comp.getName());
							CrossoverRelationship.saveAllRelationshiopForElement(newComp, parent);
						} catch (Exception e) {	e.printStackTrace();}
        			}
        			try {
        				if(interfaceComp.getNamespace().equalsIgnoreCase("model"))
        					targetInterface = offspring.createInterface(interfaceComp.getName());
        				else{
        					String interfaceCompPackageName = UtilResources.extractPackageName(interfaceComp.getNamespace());
        					Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
        					targetInterface = packageToAddInterface.createInterface(interfaceComp.getName());
        				}
						targetInterface.addConcern(feature.getName());
						CrossoverRelationship.saveAllRelationshiopForElement(interfaceComp, parent);
					} catch (Exception e) {e.printStackTrace();}
        		}
        		interfaceComp.moveOperationToInterface(operation, targetInterface);
        	}
		}
		allOperations.clear();
    }    
    
    private Package findOrCreatePakage(String packageName,Architecture offspring) {
    	Package pkg = null;
    	try{
    		pkg = offspring.findPackageByName(packageName);
    	}catch(PackageNotFound e){
    		pkg = offspring.createPackage(packageName);
    	}
    	
    	return pkg;
	}

	/**
     * Adiciona as classes do pacote em parent no pacote em offspring
     * 
     * @param feature
     * @param parentPackage
     * @param packageInOffspring
     * @param offspring
     * @param parent
     */
	private void addClassesToOffspring(Concern feature, Package parentPackage, Package packageInOffspring, Architecture offspring, Architecture parent) {
    	List<Class> allClasses = new ArrayList<Class> (parentPackage.getAllClasses());
		Iterator<Class> iteratorClasses = allClasses.iterator();
		while (iteratorClasses.hasNext()){
        	Class classComp = iteratorClasses.next();
    		if (!searchForGeneralizations(classComp)){
    			addClassToOffspring(classComp, packageInOffspring, offspring, parent);
    		} else {	
    			if (this.isHierarchyInASameComponent(classComp, parent)){
    				moveHierarchyToSameComponent(classComp, packageInOffspring, parentPackage, offspring, parent, feature);
    			}else{
    				packageInOffspring.addExternalClass(classComp);
    				moveHierarchyToDifferentPackage(classComp, packageInOffspring, parentPackage, offspring, parent);
    			}
    			CrossoverRelationship.saveAllRelationshiopForElement(classComp, parent);
    		}
    		addInterfacesImplementedByClass(classComp, offspring, parent, parentPackage);
    		addInterfacesRequiredByClass(classComp, offspring, parent, parentPackage);
        }
		allClasses = null;
	}

	/**
	 * Adicionar as interfaces implementadas pelo PACOTE em parent a offspring.
	 * 
	 * @param parentPackage
	 * @param offspring
	 * @param parent
	 */
	private void addImplementedInterfacesByPackageInOffspring(Package parentPackage, Architecture offspring, Architecture parent) {
		List<Interface> allInterfaces = new ArrayList<Interface>(parentPackage.getOnlyInterfacesImplementedByPackage());
		
		if(!allInterfaces.isEmpty()){
			Iterator<Interface> iteratorInterfaces = allInterfaces.iterator();
			while (iteratorInterfaces.hasNext()) {
				Interface interfaceComp = iteratorInterfaces.next();
				if(interfaceComp.getNamespace().equalsIgnoreCase("model"))
					offspring.addExternalInterface(interfaceComp);
				else{
					String interfaceCompPackageName = UtilResources.extractPackageName(interfaceComp.getNamespace());
					Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
					packageToAddInterface.addExternalInterface(interfaceComp);
				}
				CrossoverRelationship.saveAllRelationshiopForElement(interfaceComp, parent);
			}
		}
		allInterfaces = null;
	}
	
	private void addRequiredInterfacesByPackageInOffspring(Package parentPackage, Architecture offspring, Architecture parent) {
		List<Interface> allInterfaces = new ArrayList<Interface>(parentPackage.getOnlyInterfacesRequiredByPackage());
		
		if(!allInterfaces.isEmpty()){
			Iterator<Interface> iteratorInterfaces = allInterfaces.iterator();
			while (iteratorInterfaces.hasNext()) {
				Interface interfaceComp = iteratorInterfaces.next();
				if(interfaceComp.getNamespace().equalsIgnoreCase("model"))
					offspring.addExternalInterface(interfaceComp);
				else{
					String interfaceCompPackageName = UtilResources.extractPackageName(interfaceComp.getNamespace());
					Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
					packageToAddInterface.addExternalInterface(interfaceComp);
				}
				CrossoverRelationship.saveAllRelationshiopForElement(interfaceComp, parent);
			}
		}
		allInterfaces = null;
	}
	
    private boolean searchForGeneralizations(Class cls){
    	Collection<Relationship> relationships = cls.getRelationships();
    	for (Relationship relationship: relationships){
	    	if (relationship instanceof GeneralizationRelationship){
	    		GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
	    		if (generalization.getChild().equals(cls) || generalization.getParent().equals(cls))
	    		return true;
	    	}
	    }
    	relationships = null;
    	return false;
    }
    
	private boolean isHierarchyInASameComponent (Class class_, Architecture architecture){
		boolean sameComponent = true;
		Class parent = class_;
		Package componentOfClass = null;
		try {
			componentOfClass = architecture.findPackageOfClass(class_);
		} catch (PackageNotFound e) {
			e.printStackTrace();
		}
		Package componentOfParent = componentOfClass;
		while (CrossoverOperations.isChild(parent)){
			parent = CrossoverOperations.getParent(parent);	
			try {
				componentOfParent = architecture.findPackageOfClass(parent);
			} catch (PackageNotFound e) {
				e.printStackTrace();
			}
			if (!(componentOfClass.equals(componentOfParent))){
				sameComponent = false;
				return false;
			}
		}
		return sameComponent;
	}
	
    private void moveChildrenToSameComponent(Class parent, Package sourceComp, Package targetComp, Architecture offspring, Architecture parentArch){
	  	
		Collection<Element> children = CrossoverOperations.getChildren(parent);
		//move cada subclasse
		for (Element child: children){
			moveChildrenToSameComponent((Class) child, sourceComp, targetComp, offspring, parentArch);			
		}	
		children = null;
		//move a super classe
		if (sourceComp.getAllClasses().contains(parent)){
			addClassToOffspring(parent, targetComp, offspring, parentArch);
		} else{
			for (Package auxComp: parentArch.getAllPackages()){
				if (auxComp.getAllClasses().contains(parent)){
					sourceComp = auxComp;
					if (sourceComp.getName()!=targetComp.getName()){
						try {
							targetComp = offspring.findPackageByName(sourceComp.getName());
						} catch (PackageNotFound e1) {
							targetComp = null;
						}
						if (targetComp == null){
							try {
								targetComp=offspring.createPackage(sourceComp.getName());
								for (Concern feature:sourceComp.getOwnConcerns())
									targetComp.addConcern(feature.getName());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					addClassToOffspring(parent, targetComp, offspring, parentArch);
					break;
				}
			}
		}
	}
    
	private void moveChildrenToDifferentComponent(Class root, Package newComp, Architecture offspring, Architecture parent) {
		Collection<Element> children = CrossoverOperations.getChildren(root);
		
		String rootPackageName = UtilResources.extractPackageName(root.getNamespace());
		Package rootTargetPackage = null;
		try {
			rootTargetPackage = offspring.findPackageByName(rootPackageName);
		} catch (PackageNotFound e1) {
			rootTargetPackage = offspring.createPackage(rootPackageName);
		}
		
		addClassToOffspring(root, rootTargetPackage, offspring, parent);
		
		try {
			CrossoverRelationship.saveAllRelationshiopForElement(parent.findPackageByName(rootPackageName), parent);
		} catch (PackageNotFound e1) {
			
		}
		for (Element child: children){
			String packageName = UtilResources.extractPackageName(child.getNamespace());
			Package targetPackage = null;
			try {
				targetPackage = parent.findPackageByName(packageName);
			} catch (PackageNotFound e) {
				e.printStackTrace();
			}
			moveChildrenToDifferentComponent((Class) child, targetPackage, offspring, parent);			
		}
		children.clear();
	}
    
    /**
     * Adicionar klass a targetComp em offspring.
     * 
     * @param klass
     * @param targetComp
     * @param offspring
     * @param parent
     */
	public void addClassToOffspring(Class klass, Package targetComp, Architecture offspring, Architecture parent){
//		Element classComp = null;
//		try {
//			classComp = ((Class) klass).deepClone();
//		} catch (CloneNotSupportedException e) {
//			e.printStackTrace();
//		}			
		targetComp.addExternalClass(klass);
		CrossoverRelationship.saveAllRelationshiopForElement(klass, parent);
	}
	
	/**
	 * Adiciona as interfaces implementadas por klass em offspring.
	 * 
	 * @param klass
	 * @param offspring
	 * @param parent
	 * @param targetComp
	 */
    private void addInterfacesImplementedByClass(Class klass, Architecture offspring, Architecture parent, Package targetComp) {
    	Set<Interface> interfaces = klass.getImplementedInterfaces();
    	
    	for(Interface itf : interfaces){
    		if(itf.getNamespace().equalsIgnoreCase("model"))
    			offspring.addExternalInterface(itf);
    		else{
    			String interfaceCompPackageName = UtilResources.extractPackageName(itf.getNamespace());
				Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
				packageToAddInterface.addExternalInterface(itf);
    		}
    		CrossoverRelationship.saveAllRelationshiopForElement(itf, parent);
    	}
	}
    
    /**
     * Adiciona as interfaces requeridas por klass em offspring
     * 
     * @param klass
     * @param offspring
     * @param parent
     * @param targetComp
     */
    private void addInterfacesRequiredByClass(Class klass, Architecture offspring, Architecture parent, Package targetComp) {
    	Set<Interface> interfaces = klass.getRequiredInterfaces();
    	
    	for(Interface itf : interfaces){
    		if(itf.getNamespace().equalsIgnoreCase("model"))
    			offspring.addExternalInterface(itf);
    		else{
    			String interfaceCompPackageName = UtilResources.extractPackageName(itf.getNamespace());
				Package packageToAddInterface = findOrCreatePakage(interfaceCompPackageName, offspring);
				packageToAddInterface.addExternalInterface(itf);
    		}
    		CrossoverRelationship.saveAllRelationshiopForElement(itf, parent);
    	}
	}
    
    private  void moveHierarchyToSameComponent(Class classComp, Package targetComp, Package sourceComp, Architecture offspring, Architecture parent, Concern concern){
    	Class root = classComp;
		while (isChild(root)){
			root = getParent(root);		
		} 
		if (sourceComp.getAllClasses().contains(root)){
			moveChildrenToSameComponent(root, sourceComp, targetComp, offspring, parent);
		} 				
	}
    
	private void moveHierarchyToDifferentPackage(Class classComp, Package newComp, Package parentPackage, Architecture offspring, Architecture parent) {
		Class root = classComp;
		while(isChild(root)){
			root = getParent(root);
		}
		moveChildrenToDifferentComponent(root, newComp, offspring, parent);
	}
	
	public static  boolean isChild(Class cls){
    	boolean child=false;
    	
    	for (Relationship relationship: cls.getRelationships()){
  	    	if (relationship instanceof GeneralizationRelationship){
  	    		GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
  	    		if (generalization.getChild().equals(cls)) {
  	    			child = true;
  	    			return child;
  	    		}
  	    	}
  	    }
    	return child;
    }
    
	public static Class getParent(Class cls){
	  Class parent = null;
	  for (Relationship relationship: cls.getRelationships()){
	    	if (relationship instanceof GeneralizationRelationship){
	    		GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
	    		if (generalization.getChild().equals(cls)){
	    			parent = (Class) generalization.getParent();
	    			return parent;
	    		}
	    	}
	  }	    			
	  return parent;
	}
	
	private boolean updateVariabilitiesOffspring(Architecture offspring){ 
		boolean variabilitiesOk = true;
		for (Variability variability: offspring.getAllVariabilities()){	    	
			VariationPoint variationPoint = variability.getVariationPoint();
			if(variationPoint != null){
				Element elementVP = variationPoint.getVariationPointElement();
				Element VP = null;
				try {
					VP = offspring.findElementByName(elementVP.getName());
				} catch (ElementNotFound e) {
					System.out.println("- - - - - - NAO ACHOU o PONTO de VARIACAO - - - - - - -   :   " + elementVP.getName());
					return false;
				}
	
				if (!(VP.equals(elementVP)))
					variationPoint.replaceVariationPointElement(offspring.findElementByName(elementVP.getName(), "class"));
			}
		}
		return variabilitiesOk;
	}
	
	// Thelma - Dez2013 método adicionado
	// verify if the architecture contains a valid PLA design, i.e., if there is not any interface without relationships in the architecture. 
	private boolean isValidSolution(Architecture solution){
		boolean isValid=true;
			
		List<Interface> allInterfaces = new ArrayList<Interface> (solution.getAllInterfaces());
		if (!allInterfaces.isEmpty()){
			for (Interface itf: allInterfaces){
				if ((itf.getImplementors().isEmpty()) && (itf.getDependents().isEmpty()) && (!itf.getOperations().isEmpty())){
					allInterfaces.clear();
					return false;
				}
			}
		}
		allInterfaces.clear();
		if (!(this.variabilitiesOk))
			return false;
		return isValid;
	}
}