package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;


import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;
import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;

/**
 * 
 * @author edipofederle
 *
 */
public class ModelHelperTest extends TestHelper {
	
	@Test
	public void shouldReturnAllClasses() throws ModelNotFoundException , ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		List<org.eclipse.uml2.uml.Class> elementsClass = modelHelper.getAllClasses(content);
		assertEquals(10, elementsClass.size());
	}
	
	 @Test
	 public void shouldReturnAllAttributesForAClass() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		 NamedElement aClass = givenAClass();
		 assertEquals("Person", ((Class)aClass).getName());
		 List<Property> attrs = modelHelper.getAllAttributesForAClass(aClass);
		 assertEquals(2, attrs.size());
		 assertEquals("name", (attrs.get(1)).getLabel());
	 }

	
	@Test
	public void shouldReturnAllInterfaces() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion {
		Package content = modelHelper.getModel(getUrlToModel("interface"));
		List<Class> elementsInterfaces = modelHelper.getAllInterfaces(content);
		assertEquals(1, elementsInterfaces.size());
		assertEquals("myInterface", elementsInterfaces.get(0).getName());
	}

	@Test
	public void shouldReturnAllAssociations() throws ModelNotFoundException  , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		List<Association> elementsAssociations = modelHelper.getAllAssociations(content);
		assertEquals(2, elementsAssociations.size());
	}
	
	@Test
	public void shouldReturnAllAssociationClassAssociation() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("associationClass"));
		List<AssociationClass> elementsAssociations = modelHelper.getAllAssociationsClass(content);
		
		assertEquals(1, elementsAssociations.size());
	
	}
	
	@Test
	public void shouldReturnAllUsageRelationship() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("classUsageClass"));
		List<Usage> usages = modelHelper.getAllUsage(content);
		assertEquals(2, usages.size());
		
		assertEquals(1, usages.get(0).getSuppliers().size());
		assertEquals(1, usages.get(0).getClients().size());
	}
	
		
	@Test
	public void shouldReturnAllDependency() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		List<Dependency> dependency = modelHelper.getAllDependencies(content);
		assertEquals(1, dependency.size());
	}
	
	@Test
	public void shouldReturnAllPackages() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		List<Package> packages = modelHelper.getAllPackages(content);
		assertEquals(1, packages.size());
	}
	
	@Test
	public void shouldReturnTwoClassesForAPackage() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package p = modelHelper.getModel(getUrlToModel("package"));
		List<Package> packages = modelHelper.getAllPackages(p);
		assertEquals("Package1", ((Package)packages.get(0)).getName());
		Package p1 = (Package) packages.get(0);
		List<org.eclipse.uml2.uml.Class> classesForPackage = modelHelper.getAllClasses(p1);
		assertEquals(2, classesForPackage.size());
		ClassImpl class1 = (ClassImpl) classesForPackage.get(0);
		ClassImpl class2 = (ClassImpl) classesForPackage.get(1);
		assertEquals("Class1", class1.getName());
		assertEquals("Class2", class2.getName());
	}

	@Test
	public void shouldReturnAllComments() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{ 
		Package content = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		List<Classifier> comments = modelHelper.getAllComments(content);
		assertEquals(1, comments.size());
	}
	
	@Test
	public void shouldReturnAllGeneralizations() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package content = modelHelper.getModel(getUrlToModel("generalization"));
		List<EList<Generalization>> generalizations = modelHelper.getAllGeneralizations(content);
		assertEquals(3, generalizations.size());
	}
	
	@Test
	public void shouldReturnModelName() throws ModelNotFoundException , ModelIncompleteException{
		assertEquals("ExtendedPO2", modelHelper.getName(getUrlToModel("ExtendedPO2")));
	}
	
	@Test(expected=ModelNotFoundException.class)
	public void shouldReturnModelNotFoundWhenFileDontExists() throws ModelNotFoundException{
		modelHelper.getName("/not/found/path/model.uml");
	}
	
	@Test
	public void shouldReturnAModel() throws ModelNotFoundException , ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package model = modelHelper.getModel(getUrlToModel("ExtendedPO2"));
		assertNotNull(model);
	}
	
	@Test(expected=ModelNotFoundException.class)
	public void shouldReturnModelNotFoundmodelDontExists() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		modelHelper.getModel("/not/found/path/model.uml");
	}
	
	@Test
	public void shouldBeAbastractClass() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package model = givenAModel("testArch"); // Classes estao em pacotes
		assertNotNull(model);
		List<Package> a = modelHelper.getAllPackages(model);
		PackageImpl p = null;
		
		for(int i =0; i < a.size(); i++)
			if("Package1".equalsIgnoreCase(((PackageImpl)a.get(i)).getName()))
				p = (PackageImpl) a.get(i);
		
		assertEquals(4, a.size());
		List<org.eclipse.uml2.uml.Class> c = modelHelper.getAllClasses(p);
		assertEquals("Bar", c.get(2).getName());
		assertTrue(c.get(2).isAbstract());
	}
	
//	@Test
//	public void shouldReturnAllVariabilities() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
//		Package model = givenAModel("variability");
//		assertEquals(1, modelHelper.getAllVariabilities(model).size());
//	}
	
	@Test
	public void shouldLoadAllClassesOfAllPackages() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package model = givenAModel("testArch");
		assertEquals(6, modelHelper.getAllClasses(model).size());
	}
	
	@Test
	public void testRecursividade() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package model = givenAModel("realizationPackageClass");
		
		assertEquals(0,modelHelper.getAllDependencies(model).size());
		assertEquals(1, modelHelper.getAllRealizations(model).size());
	}
	
	@Test
	public void shouldLoadaPackagesRecursively() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Package model = givenAModel("pacotes");
		assertEquals(3, modelHelper.getAllPackages(model).size());
		assertEquals("Package1", modelHelper.getAllPackages(model).get(0).getName() );
		assertEquals("Package2", modelHelper.getAllPackages(model).get(1).getName() );
		assertEquals("Package3", modelHelper.getAllPackages(model).get(2).getName() );
	}
	
}