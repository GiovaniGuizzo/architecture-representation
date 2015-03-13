package mestrado.arquitetura.factories;

import static org.junit.Assert.*;


import org.eclipse.uml2.uml.Classifier;
import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
/**
 * 
 * @author edipofederle
 *
 */
public class KlassTest {
	
	
	@Test
	public void shouldReturnAClass(){
		Classifier klass = Klass.create().build();
		assertNotNull(klass);
		assertEquals("classe1", klass.getName());
	}
	
	@Test
	public void shouldReturnAClassWithNewName(){
		Classifier klass = Klass.create().withName("Person").build();
		assertNotNull(klass);
		assertEquals("Person", klass.getName());
	}
	
	@Test
	public void shouldReturnAClassWithStereotypeMandatoryAndNewName() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Classifier klass = Klass.create().withName("Person").withStereotypes("mandatory").build();
		assertNotNull(klass);
		assertEquals("Person", klass.getName());
		assertEquals("mandatory", klass.getAppliedStereotypes().get(0).getName());
	}
	
	@Test
	public void shouldReturnAClassWithStereotypeVariationPointAndNewName() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Classifier klass = Klass.create().withName("Person2").withStereotypes("variationPoint").build();
		assertNotNull(klass);
		assertEquals("Person2", klass.getName());
		assertEquals("variationPoint", klass.getAppliedStereotypes().get(0).getName());
	}
	
	@Test
	public void shouldReturnAClasseWithTwoStereotypesApplied() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Classifier klass = Klass.create().withName("Car").withStereotypes("variationPoint", "mandatory", "interface").build();
		assertNotNull(klass);
		assertEquals("Car", klass.getName());
		assertTrue(klass.getAppliedStereotypes().size() == 3);
		assertEquals("variationPoint", klass.getAppliedStereotypes().get(0).getName());
		assertEquals("mandatory", klass.getAppliedStereotypes().get(1).getName());
		assertEquals("interface", klass.getAppliedStereotypes().get(2).getName());
	}
	
}