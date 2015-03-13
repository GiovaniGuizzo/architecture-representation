package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mestrado.arquitetura.factories.Klass;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.ModelElementHelper;

/**
 * 
 * @author edipofederle
 *
 */
public class ModelElementHelperTest extends TestHelper {

	@Test
	public void shouldReturnAllStereotypeOfOneELement() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion {
		Classifier klass = Klass.create().withName("Order")
				                         .withStereotypes("variationPoint", "optional")
				                         .build();
		
		List<Stereotype> stereotypes = ModelElementHelper.getAllStereotypes(klass);
		
		assertEquals(2, stereotypes.size());
		assertEquals("variationPoint", stereotypes.get(0).getName());
		assertEquals("optional", stereotypes.get(1).getName());
	}
	
	@Test
	public void shouldReturnAEmptyCollectioWhenElementsWithoutStereotypes(){
		Classifier klass = Klass.create().withName("Person").build();
		assertEquals("should return empty collection", 0, ModelElementHelper.getAllStereotypes(klass).size());
	}

	@Test
	public void isInterfaceWhenClassWithStereotypeInterface() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion {
		Package model = givenAModel("interface");
		NamedElement klass = modelHelper.getAllClasses(model).get(0);

		assertTrue("should be a interface.", ModelElementHelper.isInterface(klass));
	}

	@Test
	public void isNotInterfaceWhenClassWithoutStereotypeInterface()	throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion {
		NamedElement klass = givenAClass();
		assertFalse("should NOT be a interface", ModelElementHelper.isInterface(klass));
	}
	
	@Test
	public void shouldReturnTrueWhenIsAClass() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		NamedElement klass = givenAClass();
		assertTrue("should be a class", ModelElementHelper.isClass(klass));
	}
	
	@Test
	public void shouldReturnFalseWhenIsNotAClass() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		 Classifier interfac = Klass.create().withName("Order")
                .withStereotypes("interface")
                .build();
		assertFalse("should NOT be a class", ModelElementHelper.isClass(interfac));
	}


}