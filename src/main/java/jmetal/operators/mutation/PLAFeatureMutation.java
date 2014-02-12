package jmetal.operators.mutation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import jmetal.core.Solution;
import jmetal.problems.OPLA;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import arquitetura.exceptions.ConcernNotFoundException;
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
import arquitetura.representation.Variant;
import arquitetura.representation.VariationPoint;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.RealizationRelationship;
import arquitetura.representation.relationship.Relationship;

public class PLAFeatureMutation extends Mutation {

    private static final long serialVersionUID = 9039316729379302747L;
    static Logger LOGGER = LogManager.getLogger(PLAFeatureMutation.class.getName());

    private Double mutationProbability_ = null;

    public PLAFeatureMutation(HashMap<String, Object> parameters) {
        super(parameters);

        if (parameters.get("probability") != null) {
            mutationProbability_ = (Double) parameters.get("probability");
        }
    }

    public void doMutation(double probability, Solution solution) throws Exception {
        String scope = "sameComponent"; //"allComponents" usar "sameComponent" para que a troca seja realizada dentro do mesmo componente da arquitetura
        String scopeLevels = "allLevels"; //usar "oneLevel" para não verificar a presença de interesses nos atributos e métodos

        int r = PseudoRandom.randInt(0, 5);
        switch (r) {
            case 0:
                FeatureMutation(probability, solution, scopeLevels);
                break;
            case 1:
                MoveMethodMutation(probability, solution, scope);
                break;
            case 2:
                MoveAttributeMutation(probability, solution, scope);
                break;
            case 3:
                MoveOperationMutation(probability, solution);
                break;
            case 4:
                AddClassMutation(probability, solution, scope);
                break;
            case 5:
                AddManagerClassMutation(probability, solution);
                break;
        }
    }

    //--------------------------------------------------------------------------
    //método para verificar se algum dos relacionamentos recebidos é generalização
    private boolean searchForGeneralizations(Class cls) {
        Collection<Relationship> Relationships = cls.getRelationships();
        for (Relationship relationship : Relationships) {
            if (relationship instanceof GeneralizationRelationship) {
                GeneralizationRelationship generalization = (GeneralizationRelationship) relationship;
                if (generalization.getChild().equals(cls) || generalization.getParent().equals(cls)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void MoveAttributeMutation(double probability, Solution solution, String scope) throws JMException {
        LOGGER.info("Executando MoveAttributeMutation");
        try {
            if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
                Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);

                if (PseudoRandom.randDouble() < probability) {
                    if (scope == "sameComponent") {
                        Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                        List<Class> ClassesComp = new ArrayList<Class>(sourceComp.getAllClasses());
                        if (ClassesComp.size() > 1) {
                            Class targetClass = randomObject(ClassesComp);
                            Class sourceClass = randomObject(ClassesComp);
                            if ((sourceClass != null) && (!searchForGeneralizations(sourceClass))
                                    && (sourceClass.getAllAttributes().size() > 1)
                                    && (sourceClass.getAllMethods().size() > 1)
                                    && (!isVarPoint(arch, sourceClass))
                                    && (!isVariant(arch, sourceClass))
                                    && (!isOptional(arch, sourceClass))) { //sourceClass n�o tem relacionamentos de generaliza��o
                                if ((targetClass != null) && (!(targetClass.equals(sourceClass)))) {
                                    moveAttribute(arch, targetClass, sourceClass);
                                }
                            }
                        }

                    } else {//considerando todos os componentes
                        if (scope == "allComponents") {
                            Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                            List<Class> ClassesSourceComp = new ArrayList<Class>(sourceComp.getAllClasses());
                            if (ClassesSourceComp.size() >= 1) {
                                Class sourceClass = randomObject(ClassesSourceComp);
                                if ((sourceClass != null) && (!searchForGeneralizations(sourceClass))
                                        && (sourceClass.getAllAttributes().size() > 1)
                                        && (sourceClass.getAllMethods().size() > 1)
                                        && (!isVarPoint(arch, sourceClass))
                                        && (!isVariant(arch, sourceClass))
                                        && (!isOptional(arch, sourceClass))) { //sourceClass n�o tem relacionamentos de generaliza��o{
                                    Package targetComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                                    if (checkSameLayer(sourceComp, targetComp)) {
                                        List<Class> ClassesTargetComp = new ArrayList<Class>(targetComp.getAllClasses());
                                        if (ClassesTargetComp.size() >= 1) {
                                            Class targetClass = randomObject(ClassesTargetComp);
                                            if ((targetClass != null) && (!(targetClass.equals(sourceClass)))) {
                                                moveAttribute(arch, targetClass, sourceClass);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }//pseudoRandom
            } else {
                Configuration.logger_.log(Level.SEVERE, "MoveAttributeMutation.doMutation: invalid type. " + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void moveAttribute(Architecture arch, Class targetClass, Class sourceClass) throws JMException, Exception {
        List<Attribute> attributesClass = new ArrayList<Attribute>(sourceClass.getAllAttributes());
        if (attributesClass.size() >= 1) {
            Attribute targetAttribute = randomObject(attributesClass);
            if (sourceClass.moveAttributeToClass(targetAttribute, targetClass)) {
                createAssociation(arch, targetClass, sourceClass);
            }
        }
    }

    //Add por Édipo
    private void createAssociation(Architecture arch, Class targetClass, Class sourceClass) {
        AssociationRelationship newRelationship = new AssociationRelationship(targetClass, sourceClass);
        arch.addRelationship(newRelationship);
    }

    //--------------------------------------------------------------------------
    public void MoveMethodMutation(double probability, Solution solution, String scope) throws JMException {
        LOGGER.info("Executando MoveMethodMutation");
        try {
            if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
                Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);

                if (PseudoRandom.randDouble() < probability) {
                    if (scope == "sameComponent") {
                        Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                        List<Class> ClassesComp = new ArrayList<Class>(sourceComp.getAllClasses());
                        if (ClassesComp.size() > 1) {
                            Class targetClass = randomObject(ClassesComp);
                            Class sourceClass = randomObject(ClassesComp);
                            if ((sourceClass != null) && (!searchForGeneralizations(sourceClass))
                                    && (sourceClass.getAllAttributes().size() > 1)
                                    && (sourceClass.getAllMethods().size() > 1)
                                    && (!isVarPoint(arch, sourceClass))
                                    && (!isVariant(arch, sourceClass))
                                    && (!isOptional(arch, sourceClass))) {
                                if ((targetClass != null) && (!(targetClass.equals(sourceClass)))) {
                                    moveMethod(arch, targetClass, sourceClass, sourceComp, sourceComp);
                                }
                            }
                        }
                    } else {
                        if (scope == "allComponents") {
                            Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                            List<Class> ClassesSourceComp = new ArrayList<Class>(sourceComp.getAllClasses());
                            if (ClassesSourceComp.size() >= 1) {
                                Class sourceClass = randomObject(ClassesSourceComp);
                                if ((sourceClass != null) && (!searchForGeneralizations(sourceClass))
                                        && (sourceClass.getAllAttributes().size() > 1)
                                        && (sourceClass.getAllMethods().size() > 1)
                                        && (!isVarPoint(arch, sourceClass))
                                        && (!isVariant(arch, sourceClass))
                                        && (!isOptional(arch, sourceClass))) {
                                    Package targetComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                                    if (checkSameLayer(sourceComp, targetComp)) {
                                        List<Class> ClassesTargetComp = new ArrayList<Class>(targetComp.getAllClasses());
                                        if (ClassesTargetComp.size() >= 1) {
                                            Class targetClass = randomObject(ClassesTargetComp);
                                            if ((targetClass != null) && (!(targetClass.equals(sourceClass)))) {
                                                moveMethod(arch, targetClass, sourceClass, targetComp, sourceComp);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }//PseudoRandom
            } else {
                Configuration.logger_.log(
                        Level.SEVERE, "MoveMethodMutation.doMutation: invalid type. "
                        + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveMethod(Architecture arch, Class targetClass, Class sourceClass, Package targetComp, Package sourceComp) throws JMException, Exception {
        List<Method> MethodsClass = new ArrayList<Method>(sourceClass.getAllMethods());
        if (MethodsClass.size() >= 1) {
            Method targetMethod = randomObject(MethodsClass);
            if (sourceClass.moveMethodToClass(targetMethod, targetClass)) {
                createAssociation(arch, targetClass, sourceClass);
            }

        }
    }

    //--------------------------------------------------------------------------
    public void MoveOperationMutation(double probability, Solution solution) throws JMException {
        LOGGER.info("Executando MoveOperationMutation");
        try {
            if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
                if (PseudoRandom.randDouble() < probability) {
                    Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);

                    Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                    Package targetComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));

                    if (checkSameLayer(sourceComp, targetComp)) {
                        List<Interface> InterfacesSourceComp = new ArrayList<Interface>();
                        List<Interface> InterfacesTargetComp = new ArrayList<Interface>();

                        InterfacesSourceComp.addAll(sourceComp.getImplementedInterfaces());
                        InterfacesTargetComp.addAll(targetComp.getImplementedInterfaces());

                        if ((InterfacesSourceComp.size() >= 1) && (InterfacesTargetComp.size() >= 1)) {
                            Interface targetInterface = randomObject(InterfacesTargetComp);
                            Interface sourceInterface = randomObject(InterfacesSourceComp);

                            if (targetInterface != sourceInterface) {
                                List<Method> OpsInterface = new ArrayList<Method>();
                                OpsInterface.addAll(sourceInterface.getOperations());
                                if (OpsInterface.size() >= 1) {
                                    Method op = randomObject(OpsInterface);
                                    sourceInterface.moveOperationToInterface(op, targetInterface);
                                    for (Element implementor : sourceInterface.getImplementors()) {
                                        if (implementor instanceof Package) {
                                            arch.addImplementedInterface(targetInterface, (Package) implementor);
                                        }
                                        if (implementor instanceof Class) {
                                            arch.addImplementedInterface(targetInterface, (Class) implementor);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }//PseudoRandom
            } else {
                Configuration.logger_.log(Level.SEVERE, "MoveOperationMutation.doMutation: invalid type. "
                        + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    public void AddClassMutation(double probability, Solution solution, String scope) throws JMException {
        LOGGER.info("Executand AddClassMutation ");
        try {
            if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
                if (PseudoRandom.randDouble() < probability) {
                    Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);
                    Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                    List<Class> ClassesComp = new ArrayList<Class>(sourceComp.getAllClasses());
                    if (ClassesComp.size() >= 1) {
                        Class sourceClass = randomObject(ClassesComp);
                        if ((sourceClass != null) && (!searchForGeneralizations(sourceClass))
                                && (sourceClass.getAllAttributes().size() > 1)
                                && (sourceClass.getAllMethods().size() > 1)
                                && (!isVarPoint(arch, sourceClass))
                                && (!isVariant(arch, sourceClass))
                                && (!isOptional(arch, sourceClass))) {
                            int option = PseudoRandom.randInt(0, 1);
                            if (option == 0) { //attribute
                                List<Attribute> AttributesClass = new ArrayList<Attribute>(sourceClass.getAllAttributes());
                                if (AttributesClass.size() >= 1) {
                                    if (scope == "sameComponent") {
                                        Class newClass = sourceComp.createClass("Class" + OPLA.contClass_++, false);
                                        moveAttributeToNewClass(arch, sourceClass, AttributesClass, newClass);
                                    } else {
                                        if (scope == "allComponents") {
                                            Package targetComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                                            if (checkSameLayer(sourceComp, targetComp)) {
                                                Class newClass = targetComp.createClass("Class" + OPLA.contClass_++, false);
                                                moveAttributeToNewClass(arch, sourceClass, AttributesClass, newClass);
                                            }
                                        }
                                    }
                                }
                            } else { //method
                                List<Method> MethodsClass = new ArrayList<Method>(sourceClass.getAllMethods());
                                if (MethodsClass.size() >= 1) {
                                    if (scope == "sameComponent") {
                                        Class newClass = sourceComp.createClass("Class" + OPLA.contClass_++, false);
                                        moveMethodToNewClass(arch, sourceClass, MethodsClass, newClass);
                                    } else {
                                        if (scope == "allComponents") {
                                            Package targetComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                                            if (checkSameLayer(sourceComp, targetComp)) {
                                                Class newClass = targetComp.createClass("Class" + OPLA.contClass_++, false);
                                                moveMethodToNewClass(arch, sourceClass, MethodsClass, newClass);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } //ClassesComp não é vazia
                }//PseudoRandom
            } else {
                Configuration.logger_.log(
                        Level.SEVERE, "AddClassMutation.doMutation: invalid type. "
                        + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void moveMethodToNewClass(Architecture arch, Class sourceClass, List<Method> MethodsClass, Class newClass) throws JMException {
        Method targetMethod = randomObject(MethodsClass);
        sourceClass.moveMethodToClass(targetMethod, newClass);
        //if (targetMethod.isAbstract()) targetMethod.setAbstract(false);
        for (Concern con : targetMethod.getOwnConcerns()) {
            try {
                newClass.addConcern(con.getName());
            } catch (ConcernNotFoundException e) {
                e.printStackTrace();
            }
        }
        createAssociation(arch, newClass, sourceClass);
    }

//	private void moveMethodAllComponents(Architecture arch, Class sourceClass, List<Method> MethodsClass, Class newClass) throws JMException {
//		Method targetMethod = randomObject (MethodsClass);
//		sourceClass.moveMethodToClass(targetMethod, newClass);
//		//if (targetMethod.isAbstract()) targetMethod.setAbstract(false);
//		for (Concern con: targetMethod.getOwnConcerns()){
//			try {
//				newClass.addConcern(con.getName());
//			} catch (ConcernNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
//		AssociationRelationship newRelationship = new AssociationRelationship(newClass, sourceClass);
//		arch.getAllRelationships().add(newRelationship);
//	}
    private void moveAttributeSameComponent(Architecture arch, Class sourceClass, List<Attribute> AttributesClass, Class newClass) throws JMException {
        Attribute targetAttribute = randomObject(AttributesClass);
        sourceClass.moveAttributeToClass(targetAttribute, newClass);
        for (Concern con : targetAttribute.getOwnConcerns()) {
            try {
                newClass.addConcern(con.getName());
            } catch (ConcernNotFoundException e) {
                e.printStackTrace();
            }
        }
        createAssociation(arch, newClass, sourceClass);
    }

    private void moveAttributeToNewClass(Architecture arch, Class sourceClass, List<Attribute> AttributesClass, Class newClass) throws JMException {
        Attribute targetAttribute = randomObject(AttributesClass);
        sourceClass.moveAttributeToClass(targetAttribute, newClass);
        for (Concern con : targetAttribute.getOwnConcerns()) {
            try {
                newClass.addConcern(con.getName());
            } catch (ConcernNotFoundException e) {
                e.printStackTrace();
            }
        }
        createAssociation(arch, newClass, sourceClass);

    }

    //--------------------------------------------------------------------------
    public void AddManagerClassMutation(double probability, Solution solution) throws JMException {
        LOGGER.info("Executando AddManagerClassMutation");
        try {
            if (solution.getDecisionVariables()[0].getVariableType() == java.lang.Class.forName(Architecture.ARCHITECTURE_TYPE)) {
                if (PseudoRandom.randDouble() < probability) {
                    Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);

                    Package sourceComp = randomObject(new ArrayList<Package>(arch.getAllPackages()));
                    List<Interface> InterfacesComp = new ArrayList<Interface>();
                    InterfacesComp.addAll(sourceComp.getImplementedInterfaces());

                    if (InterfacesComp.size() >= 1) {
                        Interface sourceInterface = randomObject(InterfacesComp);
                        List<Method> OpsInterface = new ArrayList<Method>();
                        OpsInterface.addAll(sourceInterface.getOperations());
                        if (OpsInterface.size() >= 1) {
                            Method op = randomObject(OpsInterface);

                            Package newComp = arch.createPackage("Package" + OPLA.contComp_ + getSuffix(sourceComp));
                            OPLA.contComp_++;
                            Interface newInterface = newComp.createInterface("Interface" + OPLA.contInt_++);

                            sourceInterface.moveOperationToInterface(op, newInterface);

                            for (Element implementor : sourceInterface.getImplementors()) {
                                if (implementor instanceof Package) {
                                    arch.addImplementedInterface(newInterface, (Package) implementor);
                                }
                                if (implementor instanceof Class) {
                                    arch.addImplementedInterface(newInterface, (Class) implementor);
                                }
                            }
                            for (Concern con : op.getOwnConcerns()) {
                                newInterface.addConcern(con.getName());
                            }
                        }
                    }
                }
            } else {
                Configuration.logger_.log(
                        Level.SEVERE, "AddManagerClassMutation.doMutation: invalid type. "
                        + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------
    public void FeatureMutation(double probability, Solution solution, String scope) throws JMException {
        LOGGER.info("Executando FeatureMutation.");
        try {
            if (solution.getDecisionVariables()[0].getVariableType().toString().equals("class " + Architecture.ARCHITECTURE_TYPE)) {
                if (PseudoRandom.randDouble() < probability) {
                    Architecture arch = ((Architecture) solution.getDecisionVariables()[0]);
                    List<Package> allComponents = new ArrayList<Package>(arch.getAllPackages());
                    if (!allComponents.isEmpty()) {
                        Package selectedComp = randomObject(allComponents);

                        List<Concern> concernsSelectedComp = new ArrayList<Concern>(selectedComp.getAllConcerns());
                        if (concernsSelectedComp.size() > 1) { // = somente para testes
                            Concern selectedConcern = randomObject(concernsSelectedComp);
                            List<Package> allComponentsAssignedOnlyToConcern = new ArrayList<Package>(searchComponentsAssignedToConcern(selectedConcern, allComponents));
                            if (allComponentsAssignedOnlyToConcern.size() == 0) {
                                Package newComponent = arch.createPackage("Package" + OPLA.contComp_ + getSuffix(selectedComp));
                                OPLA.contComp_++;
                                modularizeConcernInComponent(newComponent, selectedConcern, arch);
                            } else {
                                if (allComponentsAssignedOnlyToConcern.size() == 1) {
                                    Package targetComponent = allComponentsAssignedOnlyToConcern.get(0);
                                    modularizeConcernInComponent(targetComponent, selectedConcern, arch);
                                } else {
                                    Package targetComponent = randomObject(allComponentsAssignedOnlyToConcern);
                                    modularizeConcernInComponent(targetComponent, selectedConcern, arch);
                                }
                            }
                        }
                    }
                }
            } else {
                Configuration.logger_.log(Level.SEVERE, "FeatureMutation.doMutation: invalid type. " + "{0}", solution.getDecisionVariables()[0].getVariableType());
                java.lang.Class<String> cls = java.lang.String.class;
                String name = cls.getName();
                throw new JMException("Exception in " + name + ".doMutation()");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Package> searchComponentsAssignedToConcern(Concern concern, List<Package> allComponents) {
        List<Package> allComponentsAssignedToConcern = new ArrayList<Package>();
        for (Package component : allComponents) {
            Set<Concern> numberOfConcernsForPackage = getNumberOfConcernsFor(component);
            if (numberOfConcernsForPackage.size() == 1 && (numberOfConcernsForPackage.contains(concern))) {
                allComponentsAssignedToConcern.add(component);
            }
        }
        return allComponentsAssignedToConcern;
    }

    private Set<Concern> getNumberOfConcernsFor(Package pkg) {
        Set<Concern> listOfOwnedConcern = new HashSet<Concern>();

        for (Class klass : pkg.getAllClasses()) {
            listOfOwnedConcern.addAll(klass.getOwnConcerns());
        }
        for (Interface inte : pkg.getAllInterfaces()) {
            listOfOwnedConcern.addAll(inte.getOwnConcerns());
        }

        return listOfOwnedConcern;
    }

    private void modularizeConcernInComponent(Package targetComponent, Concern concern, Architecture arch) {
        List<Package> allComponents = new ArrayList<Package>(arch.getAllPackages());
        for (Package comp : allComponents) {
            if (!comp.equals(targetComponent) && checkSameLayer(comp, targetComponent)) {
                Set<Interface> allInterfaces = new HashSet<Interface>(comp.getAllInterfaces());
                allInterfaces.addAll(comp.getImplementedInterfaces());

                if (allInterfaces.size() >= 1) {
                    for (Interface interfaceComp : allInterfaces) {
                        if (interfaceComp.containsConcern(concern) && interfaceComp.getOwnConcerns().size() == 1) {
                            moveInterfaceToComponent(interfaceComp, targetComponent, comp, arch, concern); // EDIPO TESTADO
                        } else {
                            List<Method> operationsInterfaceComp = new ArrayList<Method>(interfaceComp.getOperations());
                            if (operationsInterfaceComp.size() >= 1) {
                                for (Method operation : operationsInterfaceComp) {
                                    if (operation.containsConcern(concern) && operation.getOwnConcerns().size() == 1) {
                                        moveOperationToComponent(operation, interfaceComp, targetComponent, comp, arch, concern);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Package comp : allComponents) {

            if (!comp.equals(targetComponent) && checkSameLayer(comp, targetComponent)) {// &&	// !comp.containsConcern(concern)){
                List<Class> allClasses = new ArrayList<Class>(comp.getAllClasses());
                if (allClasses.size() >= 1) {
                    for (Class classComp : allClasses) {
                        if (comp.getAllClasses().contains(classComp)) {
                            if ((classComp.containsConcern(concern)) && (classComp.getOwnConcerns().size() == 1)) {
                                if (!searchForGeneralizations(classComp)) //realiza a mutação em classe que nãoo estão numa hierarquia de herança
                                {
                                    moveClassToComponent(classComp, targetComponent, comp, arch, concern);
                                } else {
                                    moveHierarchyToComponent(classComp, targetComponent, comp, arch, concern); //realiza a mutação em classes estão numa hierarquia de herarquia
                                }
                            } else {
                                if (!searchForGeneralizations(classComp)) {
                                    if (!isVarPointOfConcern(arch, classComp, concern) && !isVariantOfConcern(arch, classComp, concern)) {
                                        List<Attribute> attributesClassComp = new ArrayList<Attribute>(classComp.getAllAttributes());
                                        if (attributesClassComp.size() >= 1) {
                                            for (Attribute attribute : attributesClassComp) {
                                                if (attribute.containsConcern(concern) && attribute.getOwnConcerns().size() == 1) {
                                                    moveAttributeToComponent(attribute, classComp, targetComponent, comp, arch, concern);
                                                }
                                            }
                                        }
                                        List<Method> methodsClassComp = new ArrayList<Method>(classComp.getAllMethods());
                                        if (methodsClassComp.size() >= 1) {
                                            for (Method method : methodsClassComp) {
                                                if (method.containsConcern(concern) && method.getOwnConcerns().size() == 1) {
                                                    moveMethodToComponent(method, classComp, targetComponent, comp, arch, concern);
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void moveClassToComponent(Class classComp, Package targetComp, Package sourceComp, Architecture architecture, Concern concern) {
        sourceComp.moveClassToPackage(classComp, targetComp);
    }

    private void moveAttributeToComponent(Attribute attribute, Class classComp, Package targetComp, Package sourceComp, Architecture architecture, Concern concern) {
        Class targetClass = findOrCreateClassWithConcern(targetComp, concern);
        classComp.moveAttributeToClass(attribute, targetClass);
        createAssociation(architecture, targetClass, classComp);
    }

    private void moveMethodToComponent(Method method, Class classComp, Package targetComp, Package sourceComp, Architecture architecture, Concern concern) {
        Class targetClass = findOrCreateClassWithConcern(targetComp, concern);
        classComp.moveMethodToClass(method, targetClass);
        createAssociation(architecture, targetClass, classComp);
    }

    //add por Édipo
    private Class findOrCreateClassWithConcern(Package targetComp, Concern concern) {
        Class targetClass = null;
        for (Class cls : targetComp.getAllClasses()) {
            if (cls.containsConcern(concern)) {
                targetClass = cls;
            }
        }

        if (targetClass == null) {
            try {
                targetClass = targetComp.createClass("Class" + OPLA.contClass_++, false);
                targetClass.addConcern(concern.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return targetClass;
    }

    private void moveInterfaceToComponent(Interface interfaceComp, Package targetComp, Package sourceComp, Architecture architecture, Concern concernSelected) {
        if (!sourceComp.moveInterfaceToPackage(interfaceComp, targetComp)) {
            architecture.moveElementToPackage(interfaceComp, targetComp);
        }

        for (Element implementor : interfaceComp.getImplementors()) {
            if (implementor instanceof Package) {
                if (targetComp.getAllClasses().size() == 1) {
                    Class klass = targetComp.getAllClasses().iterator().next();
                    for (Concern concern : klass.getOwnConcerns()) {
                        if (interfaceComp.containsConcern(concern)) {
                            architecture.removeImplementedInterface(interfaceComp, sourceComp);
                            addExternalInterface(targetComp, architecture, interfaceComp);
                            addImplementedInterface(targetComp, architecture, interfaceComp, klass);
                            return;
                        }
                    }
                } else if (targetComp.getAllClasses().size() > 1) {
                    List<Class> targetClasses = allClassesWithConcerns(concernSelected, targetComp.getAllClasses());
                    Class klass = randonClass(targetClasses);
                    architecture.removeImplementedInterface(interfaceComp, sourceComp);
                    addExternalInterface(targetComp, architecture, interfaceComp);
                    addImplementedInterface(targetComp, architecture, interfaceComp, klass);
                    return;
                } else {
                    //Busca na arquitetura como um todo
                    List<Class> targetClasses = allClassesWithConcerns(concernSelected, architecture.getAllClasses());
                    Class klass = randonClass(targetClasses);
                    architecture.removeImplementedInterface(interfaceComp, sourceComp);
                    addExternalInterface(targetComp, architecture, interfaceComp);
                    addImplementedInterface(targetComp, architecture, interfaceComp, klass);
                }
            }
        }
    }

    private Class randonClass(List<Class> targetClasses) {
        Collections.shuffle(targetClasses);
        Class randonKlass = targetClasses.get(0);
        return randonKlass;
    }

    /**
     * Retorna todas as classes que tiverem algum dos concerns presentes na lista ownConcerns.
     *
     * @param ownConcerns
     * @param allClasses
     * @return
     */
    private List<Class> allClassesWithConcerns(Concern c, Set<Class> allClasses) {
        List<Class> klasses = new ArrayList<Class>();
        for (Class klass : allClasses) {
            for (Concern concernKlass : klass.getOwnConcerns()) {
                if (concernKlass.getName().equalsIgnoreCase(c.getName())) {
                    klasses.add(klass);
                }
            }
        }
        return klasses;
    }

    private void moveOperationToComponent(Method operation, Interface sourceInterface, Package targetComp, Package sourceComp, Architecture architecture, Concern concern) {
        Interface targetInterface = null;
        targetInterface = searchForInterfaceWithConcern(concern, targetComp);

        if (targetInterface == null) {
            targetInterface = targetComp.createInterface("Interface" + OPLA.contInt_++);
            sourceInterface.moveOperationToInterface(operation, targetInterface);
            try {
                targetInterface.addConcern(concern.getName());
            } catch (ConcernNotFoundException e) {
                e.printStackTrace();
            }
            //addConcernToNewInterface(concern, targetInterface, sourceInterface);
        } else {
            sourceInterface.moveOperationToInterface(operation, targetInterface);
        }

        addRelationship(sourceInterface, targetComp, sourceComp, architecture, concern, targetInterface);
    }

    private void addRelationship(Interface sourceInterface, Package targetComp, Package sourceComp, Architecture architecture, Concern concern, Interface targetInterface) {
        for (Element implementor : sourceInterface.getImplementors()) {
            // Se quem estiver implementando a interface que teve a operacao movida for um pacote.
            if (implementor instanceof Package) {
                /**
                 * Verifica se o pacote tem somente um classe, recupera a mesma e verifica se a interface destino (targetInterface) possui algum interesse da classe recuperada. Caso tiver, remove implemented interface (sourceInterface) de sourceComp. Adiciona a interface tergetInterface em seu pacote ou na arquitetura Verifica se já existe um relacionamento de realização entre targetInterface e klass, caso não tiver adiciona targetInterface como sendo implemenda por klass.
                 */
                if (targetComp.getAllClasses().size() == 1) {
                    Class klass = targetComp.getAllClasses().iterator().next();
                    for (Concern c : klass.getOwnConcerns()) {
                        if (targetInterface.containsConcern(c)) {
                            architecture.removeImplementedInterface(sourceInterface, sourceComp);
                            addExternalInterface(targetComp, architecture, targetInterface);
                            addImplementedInterface(targetComp, architecture, targetInterface, klass);
                            return;
                        }
                    }

                    /**
                     * Caso o pacote destino tiver mais de uma classe. Busca dentre essas classes todas com o interesse em questão (concern), e seleciona um aleatoriamente. Remove implemented interface (sourceInterface) de sourceComp. Adiciona a interface tergetInterface em seu pacote ou na arquitetura Verifica se já existe um relacionamento de realização entre targetInterface e klass, caso não tiver adiciona targetInterface como sendo implemenda por klass.
                     */
                } else if (targetComp.getAllClasses().size() > 1) {
                    List<Class> targetClasses = allClassesWithConcerns(concern, targetComp.getAllClasses());
                    Class klass = randonClass(targetClasses);
                    architecture.removeImplementedInterface(sourceInterface, sourceComp);
                    addExternalInterface(targetComp, architecture, targetInterface);
                    addImplementedInterface(targetComp, architecture, targetInterface, klass);
                    return;
                } else {
                    /**
                     * Caso o pacote for vazio, faz um busca nas classes da arquitetura como um todo.
                     */
                    List<Class> targetClasses = allClassesWithConcerns(concern, architecture.getAllClasses());
                    Class klass = randonClass(targetClasses);
                    architecture.removeImplementedInterface(sourceInterface, sourceComp);
                    addExternalInterface(targetComp, architecture, targetInterface);
                    addImplementedInterface(targetComp, architecture, targetInterface, klass);
                }
            }

            /**
             * Recupera quem estava implementando a interface que teve a operacao movida e cria uma realizacao entre a interface que recebeu a operacao (targetInterface) e quem tava implementando a interface que teve a operacao movida (sourceInterface).
             *
             */
            if (implementor instanceof Class) {
                architecture.removeImplementedInterface(sourceInterface, sourceComp);
                addExternalInterface(targetComp, architecture, targetInterface);
                addImplementedInterface(targetComp, architecture, targetInterface, (Class) implementor);
            }
        }
    }

    private void addImplementedInterface(Package targetComp, Architecture architecture, Interface targetInterface, Class klass) {
        if (!packageTargetHasRealization(targetComp, targetInterface)) {
            architecture.addImplementedInterface(targetInterface, klass);
        }
    }

    private void addExternalInterface(Package targetComp,
            Architecture architecture, Interface targetInterface) {
        String packageNameInterface = UtilResources.extractPackageName(targetInterface.getNamespace().trim());
        if (packageNameInterface.equalsIgnoreCase("model")) {
            architecture.addExternalInterface(targetInterface);
        } else {
            targetComp.addExternalInterface(targetInterface);
        }
    }

    private boolean packageTargetHasRealization(Package targetComp, Interface targetInterface) {
        for (Relationship r : targetComp.getRelationships()) {
            if (r instanceof RealizationRelationship) {
                RealizationRelationship realization = (RealizationRelationship) r;
                if (realization.getSupplier().equals(targetInterface)) {
                    return true;
                }
            }
        }

        return false;
    }

    //Édipo
    private void addConcernToNewInterface(Concern concern, Interface targetInterface, Interface sourceInterface) {
        Set<Concern> interfaceConcerns = sourceInterface.getOwnConcerns();
        try {
            for (Concern c : interfaceConcerns) {
                targetInterface.addConcern(c.getName());
            }
        } catch (ConcernNotFoundException e) {
            e.printStackTrace();
        }

        for (Method operation : sourceInterface.getOperations()) {
            Set<Concern> operationConcerns = operation.getOwnConcerns();
            for (Method o : targetInterface.getOperations()) {
                for (Concern c : operationConcerns) {
                    try {
                        o.addConcern(c.getName());
                    } catch (ConcernNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    //Édipo Método
    private Interface searchForInterfaceWithConcern(Concern concern, Package targetComp) {
        for (Interface itf : targetComp.getImplementedInterfaces()) {
            if (itf.containsConcern(concern)) {
                return itf;
            }
        }

        for (Interface itf : targetComp.getAllInterfaces()) {
            if (itf.containsConcern(concern)) {
                return itf;
            }
        }

        return null;
    }

    public Object execute(Object object) throws JMException {
        Solution solution = (Solution) object;
        Double probability = (Double) getParameter("probability");

        if (probability == null) {
            Configuration.logger_.severe("FeatureMutation.execute: probability not specified");
            java.lang.Class<String> cls = java.lang.String.class;
            String name = cls.getName();
            throw new JMException("Exception in " + name + ".execute()");
        }

        try {
            this.doMutation(mutationProbability_, solution);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!this.isValidSolution(((Architecture) solution.getDecisionVariables()[0]))) {
            Architecture clone = null;
            clone = ((Architecture) solution.getDecisionVariables()[0]).deepClone();
            solution.getDecisionVariables()[0] = clone;
            OPLA.contDiscardedSolutions_++;
        }

        return solution;
    }

    //-------------------------------------------------------------------------------------------------
    public <T> T randomObject(List<T> allObjects) throws JMException {

        int numObjects = allObjects.size();
        int key;
        T object;
        if (numObjects == 0) {
            object = null;
        } else {
            key = PseudoRandom.randInt(0, numObjects - 1);
            object = allObjects.get(key);
        }
        return object;
    }

    //-------------------------------------------------------------------------------------------------
    //Thelma: método adicionado para verificar se os componentes nos quais as mutacoes serao realizadas estao na mesma camada da arquitetura
    private boolean checkSameLayer(Package source, Package target) {
        boolean sameLayer = false;
        if ((source.getName().endsWith("Mgr") && target.getName().endsWith("Mgr"))
                || (source.getName().endsWith("Ctrl") && target.getName().endsWith("Ctrl"))
                || (source.getName().endsWith("GUI") && target.getName().endsWith("GUI"))) {
            sameLayer = true;
        }
        return sameLayer;
    }

    //-------------------------------------------------------------------------------------------------
    //Thelma: método adicionado para retornar o sufixo do nome do componente
    private String getSuffix(Package comp) {
        String suffix;
        if (comp.getName().endsWith("Mgr")) {
            suffix = "Mgr";
        } else if (comp.getName().endsWith("Ctrl")) {
            suffix = "Ctrl";
        } else if (comp.getName().endsWith("GUI")) {
            suffix = "GUI";
        } else {
            suffix = "";
        }
        return suffix;
    }

    //-------------------------------------------------------------------------------------------------
    //Thelma: método adicionado para verificar se a classe tem uma variabilidade relativa ao concern
    private boolean isVarPointOfConcern(Architecture arch, Class cls, Concern concern) {
        boolean isVariationPointConcern = false;
        Collection<Variability> variabilities = arch.getAllVariabilities();
        for (Variability variability : variabilities) {
            VariationPoint varPoint = variability.getVariationPoint();
            if (varPoint != null) {
                Class classVP = (Class) varPoint.getVariationPointElement();
                if (classVP.equals(cls) && variability.getName().equals(concern.getName())) {
                    isVariationPointConcern = true;
                }
            }
        }
        return isVariationPointConcern;
    }

    //-------------------------------------------------------------------------------------------------
    //Thelma: método adicionado para verificar se a classe é variant de uma variabilidade relativa ao concern
    private boolean isVariantOfConcern(Architecture arch, Class cls, Concern concern) {
        boolean isVariantConcern = false;
        Collection<Variability> variabilities = arch.getAllVariabilities();
        for (Variability variability : variabilities) {
            VariationPoint varPoint = variability.getVariationPoint();
            if (varPoint != null) {
                for (Variant variant : varPoint.getVariants()) {
                    if (variant.getVariantElement().equals(cls) && variability.getName().equals(concern.getName())) {
                        isVariantConcern = true;
                    }
                }
            } else {
                if (cls.getVariantType() != null) {
                    if (cls.getVariantType().equalsIgnoreCase("optional")) {
                        isVariantConcern = true;
                    }
                }
            }
        }
        return isVariantConcern;
    }

    /**
     * metodo que move a hierarquia de classes para um outro componente que esta modularizando o interesse concern
     *
     *
     * @param classComp - Classe selecionada
     * @param targetComp - Pacote destino
     * @param sourceComp - Pacote de origem
     * @param architecture - arquiteutra
     * @param concern - interesse sendo modularizado
     */
    private void moveHierarchyToComponent(Class classComp, Package targetComp, Package sourceComp, Architecture architecture, Concern concern) {
        GeneralizationRelationship gene = getGeneralizationRelationshipForClass(classComp);
        architecture.forGeneralization().moveGeneralizationToPackage(gene, targetComp);
    }

    //EDIPO Identifica quem é o parent para a classComp
    /**
     * Dado um {@link Element} retorna a {@link GeneralizationRelationship} no qual o mesmo pertence.
     *
     * @param element
     * @return {@link GeneralizationRelationship}
     */
    private GeneralizationRelationship getGeneralizationRelationshipForClass(Element element) {
        for (Relationship r : element.getRelationships()) {
            if (r instanceof GeneralizationRelationship) {
                GeneralizationRelationship g = (GeneralizationRelationship) r;
                if (g.getParent().equals(element) || (g.getChild().equals(element))) {
                    return g;
                }
            }
        }
        return null;
    }

    // verificar se a classe é variant de uma variabilidade
    private boolean isOptional(Architecture arch, Class cls) {
        boolean isOptional = false;
        if (cls.getVariantType() != null) {
            if (cls.getVariantType().toString().equalsIgnoreCase("optional")) {
                return true;
            }
        }
        return isOptional;
    }
    //-------------------------------------------------------------------------------------------------
    // verificar se a classe é variant de uma variabilidade

    private boolean isVariant(Architecture arch, Class cls) {
        boolean isVariant = false;
        Collection<Variability> variabilities = arch.getAllVariabilities();
        for (Variability variability : variabilities) {
            VariationPoint varPoint = variability.getVariationPoint();
            if (varPoint != null) {
                for (Variant variant : varPoint.getVariants()) {
                    if (variant.getVariantElement().equals(cls)) {
                        isVariant = true;
                    }
                }
            }
        }
        return isVariant;
    }

    private boolean isVarPoint(Architecture arch, Class cls) {
        boolean isVariationPoint = false;
        Collection<Variability> variabilities = arch.getAllVariabilities();
        for (Variability variability : variabilities) {
            VariationPoint varPoint = variability.getVariationPoint();
            if (varPoint != null) {
                Class classVP = (Class) varPoint.getVariationPointElement();
                if (classVP.equals(cls)) {
                    isVariationPoint = true;
                }
            }
        }
        return isVariationPoint;
    }

    // Thelma - Dez2013 método adicionado
    // verify if the architecture contains a valid PLA design, i.e., if there is not any interface without relationships in the architecture.
    private boolean isValidSolution(Architecture solution) {
        boolean isValid = true;
        List<Interface> allInterfaces = new ArrayList<Interface>(solution.getAllInterfaces());
        if (!allInterfaces.isEmpty()) {
            for (Interface itf : allInterfaces) {
                if ((itf.getImplementors().isEmpty()) && (itf.getDependents().isEmpty()) && (!itf.getOperations().isEmpty())) {
                    return false;
                }
            }
        }
        return isValid;
    }

}
