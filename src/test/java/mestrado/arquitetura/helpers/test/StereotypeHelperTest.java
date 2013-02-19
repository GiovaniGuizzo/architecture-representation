package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mestrado.arquitetura.factories.Klass;
import mestrado.arquitetura.helpers.ConcernNotFoundException;
import mestrado.arquitetura.helpers.ModelElementHelper;
import mestrado.arquitetura.helpers.ModelIncompleteException;
import mestrado.arquitetura.helpers.ModelNotFoundException;
import mestrado.arquitetura.helpers.SMartyProfileNotAppliedToModelExcepetion;
import mestrado.arquitetura.helpers.StereotypeHelper;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.junit.Test;

public class StereotypeHelperTest extends TestHelper {

	@Test
	public void shouldReturnTrueIfIsVariantPointClass() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion {

		NamedElement a = Klass.create()
				            .withName("Car")
							.withStereotypes("variationPoint").build();
		
		boolean result = StereotypeHelper.isVariationPoint(a);
		assertEquals("isVariationPoint should return true", true, result);
	}
	
	@Test
	public void shouldReturnFalseIfIsNOTVariantPointClass() {

		NamedElement a = Klass.create().build();
		boolean result = StereotypeHelper.isVariationPoint(a);
		
		assertEquals("isVariationPoint should return false", false, result);
	}
	
	@Test
	public void shouldReturnFalseIfIsNotConcern() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Classifier a = Klass.create()
	            .withName("Car")
				.withStereotypes("interface").build();
		
		boolean result = StereotypeHelper.isConcern(a);
		
		assertEquals("isConcern should return false", false, result);
	}
	
	@Test
	public void shouldBeAConcern() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion, ConcernNotFoundException{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<Classifier> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(0));
		assertEquals("Foo", c.get(0).getName());
		assertEquals(2, ModelElementHelper.getAllStereotypes(c.get(0)).size());
		assertTrue(StereotypeHelper.isConcern(c.get(0)));
		assertEquals("concern Name should be 'Persistence'", "Persistence", StereotypeHelper.getConcernName(c.get(0)));
	}
	
	@Test
	public void shouldNotBeAConcern() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<Classifier> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(1));
		assertEquals("Bar", c.get(1).getName());
		assertFalse(StereotypeHelper.isConcern(c.get(1)));
	}
	
	@Test
	public void test() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("modelDois");
		Package model = uml2Helper.load(uri);
		List<Classifier> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(0));
		assertEquals(1, ModelElementHelper.getAllStereotypes(c.get(0)).size());
		assertEquals("Class1", c.get(0).getName());
		assertTrue(StereotypeHelper.isConcern2(c.get(0)));
		
	}
	
	@Test(expected=ConcernNotFoundException.class)
	public void shouldReturnExceptionWhenTryGetNameOfNonStereotype() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion, ConcernNotFoundException{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<Classifier> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(1));
		assertEquals("Bar", c.get(1).getName());
		assertFalse(StereotypeHelper.isConcern(c.get(1)));
		StereotypeHelper.getConcernName(c.get(1));
	}
	
	@Test
	public void shouldReturnTrueIfIsVariability() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion , SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("variability");
		String absolutePath = new File(uri).getAbsolutePath();
		Package model = uml2Helper.load(absolutePath);
		NamedElement klass = modelHelper.getAllClasses(model).get(0);
		assertNotNull(klass);
		assertEquals("Class1", ((Class)klass).getName());
		assertTrue(StereotypeHelper.isVariability(klass));
	}
	
	@Test
	public void shouldReturnFalseIfIsNotVariability() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Classifier a = Klass.create()
	            .withName("game")
				.withStereotypes("interface", "variationPoint").build();
		
		boolean result = StereotypeHelper.isVariability(a);
		
		assertEquals("isVariability should return false", false, result);
	}
	
	
	@Test
	public void shouldReturnFalseIfIsNotVariability2() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion, IOException{
		
		String uri = getUrlToModel("variabilitySem");
		String absolutePath = new File(uri).getAbsolutePath();
		Package model = uml2Helper.load(absolutePath);

		NamedElement klass = modelHelper.getAllClasses(model).get(0);
		assertNotNull(klass);
		
		assertEquals("Class1", ((Class)klass).getName());
		assertFalse(StereotypeHelper.isVariability(klass));
	}
	
	
	@Test
	public void shouldReturnFalseToNullStereotype(){
		assertFalse(StereotypeHelper.hasStereotype(null, "variantionPoint"));
	}
	
	@Test
	public void shouldTestHasStereotype() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Classifier a = Klass.create()
	            .withName("Bird")
				.withStereotypes("variationPoint", "alternative_OR").build();
		
		assertTrue(StereotypeHelper.hasStereotype(a, "variationPoint"));
		assertFalse(StereotypeHelper.hasStereotype(a, "wtf"));
	}
	
	
	@Test
	public void shouldReturnStereotypesForModelWithTwoProfilesApplied() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("testArch");
	
		Package model = uml2Helper.load(uri);
		List<Classifier> p = modelHelper.getAllPackages(model);
		
		List<Classifier> c = modelHelper.getAllClasses(((Package)p.get(0)));
		assertNotNull(c);
		assertNotNull(ModelElementHelper.getAllStereotypes(c.get(0)));
		
	}
	

}