package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mestrado.arquitetura.factories.Klass;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.junit.Test;

import arquitetura.exceptions.ConcernNotFoundException;
import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.ModelElementHelper;
import arquitetura.helpers.StereotypeHelper;

/**
 * 
 * @author edipofederle
 *
 */
public class StereotypeHelperTest extends TestHelper {

	@Test
	public void shouldReturnTrueIfIsVariantPointClass() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion {

		NamedElement a = Klass.create()
				            .withName("Car")
							.withStereotypes("variationPoint").build();
		
		Stereotype ste = ModelElementHelper.getAllStereotypes(a).get(0);
		
		boolean result = StereotypeHelper.isVariationPoint(ste);
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
		
		boolean result = StereotypeHelper.hasConcern(a);
		
		assertEquals("isConcern should return false", false, result);
	}
	
	@Test
	public void shouldHaveAConcern() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion, ConcernNotFoundException{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<org.eclipse.uml2.uml.Class> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(0));
		assertEquals("Foo", c.get(0).getName());
		assertEquals(2, ModelElementHelper.getAllStereotypes(c.get(0)).size());
		assertTrue(StereotypeHelper.hasConcern(c.get(0)));
		assertEquals("concern Name should be 'Persistence'", "Persistence", StereotypeHelper.getConcernName(c.get(0)));
	}
	
	@Test
	public void shouldNotHaveAConcern() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<org.eclipse.uml2.uml.Class> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(1));
		assertEquals("Bar", c.get(1).getName());
		assertFalse(StereotypeHelper.hasConcern(c.get(1)));
	}
	
	@Test(expected=ConcernNotFoundException.class)
	public void shouldReturnExceptionWhenTryGetNameOfNonStereotype() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion, ConcernNotFoundException{
		String uri = getUrlToModel("concerns");
		Package model = uml2Helper.load(uri);
		List<org.eclipse.uml2.uml.Class> c = modelHelper.getAllClasses(model);
		assertNotNull(c.get(1));
		assertEquals("Bar", c.get(1).getName());
		assertFalse(StereotypeHelper.hasConcern(c.get(1)));
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

		org.eclipse.uml2.uml.Class klass = modelHelper.getAllClasses(model).get(0);
		assertNotNull(klass);
		
		assertEquals("Class1",klass.getName());
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
	public void shouldReturnStereotypesForModelWithTwoProfilesApplied() throws Exception {
		String uri = getUrlToModel("testArch");
	
		Package model = uml2Helper.load(uri);
		List<Package> p = modelHelper.getAllPackages(model);
		
		List<org.eclipse.uml2.uml.Class> c = modelHelper.getAllClasses(((Package)p.get(0)));
		assertNotNull(c);
		assertNotNull(ModelElementHelper.getAllStereotypes(c.get(0)));
	}
	
	@Test
	public void shouldGetPerpertyOfVariabilityStereotype() throws Exception{
		String uri = getUrlToModel("variability");
		String absolutePath = new File(uri).getAbsolutePath();
		Package model = uml2Helper.load(absolutePath);
		
		NamedElement klass = modelHelper.getAllClasses(model).get(0);
		
		assertTrue(StereotypeHelper.isVariability(klass));
		List<Comment> commentVariability  = StereotypeHelper.getCommentVariability(klass);
		Map<String, String> variabilityAttrubutes = StereotypeHelper.getVariabilityAttributes(klass, commentVariability.get(0));
		
		assertEquals("casa", variabilityAttrubutes.get("name"));
		assertEquals("DESIGN_TIME", variabilityAttrubutes.get("bindingTime"));
		assertEquals("2", variabilityAttrubutes.get("maxSelection"));
		assertEquals("1", variabilityAttrubutes.get("minSelection"));
		assertEquals("teste1, teste2, teste3", variabilityAttrubutes.get("variants"));
		assertEquals("false", variabilityAttrubutes.get("allowAddingVar"));
	}
	
	
	@Test
	public void shouldLoadPatternsStereotypes() throws Exception{
		String uri = getUrlToModel("patternsSte");
		String absolutePath = new File(uri).getAbsolutePath();
		Package model = uml2Helper.load(absolutePath);
		
		NamedElement klass = modelHelper.getAllClasses(model).get(0);
		assertNotNull(klass);
		
		Set<String> stereotypes = StereotypeHelper.getAllPatternsStereotypes(klass);
		
		assertNotNull(stereotypes);
		assertEquals(1, stereotypes.size());
		assertEquals("facade", stereotypes.iterator().next());
	}
	
}