package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.relationship.GeneralizationRelationship;

public class GeneralizationsTest extends TestHelper {
	
	
	@Test
	public void shouldLoadGeneralization() throws Exception {
		Architecture arch1 = givenAArchitecture("replaceGeneralization");

		assertEquals(1, arch1.getRelationshipHolder().getAllGeneralizations().size());

		GeneralizationRelationship generalization = arch1.getRelationshipHolder().getAllGeneralizations().iterator().next();
		assertNotNull(generalization);
		assertEquals("Class1", generalization.getParent().getName());
		assertEquals(1, generalization.getAllChildrenForGeneralClass().size());
		assertContains(generalization.getAllChildrenForGeneralClass(),"Class2");
	}
	
	@Test
	public void shouldReplaceChildClass() throws Exception {
		Architecture a = givenAArchitecture("ReplaceGeneralization");
		Class class3 = a.findClassByName("Class3").get(0);
		assertNotNull(class3);

		GeneralizationRelationship generalization = a.getRelationshipHolder().getAllGeneralizations().iterator().next();

		assertContains(generalization.getAllChildrenForGeneralClass(), "Class2");
		generalization.replaceChild(class3);
		assertEquals(1, generalization.getAllChildrenForGeneralClass().size());
		assertContains(generalization.getAllChildrenForGeneralClass(), "Class3");
	}
	

	@Test
	public void shouldReplaceAParentClass() throws Exception {
		Architecture architecture = givenAArchitecture("generalaizationReplacce");
		Element professorKlass =  architecture.findElementByName("Class2", "class");
		assertNotNull(professorKlass);
		assertEquals("Class2", professorKlass.getName());

		GeneralizationRelationship generalization = architecture.getRelationshipHolder().getAllGeneralizations().iterator().next();

		assertEquals("Class1", generalization.getParent().getName());
		generalization.replaceParent((Class) professorKlass);
		assertEquals("Class2", generalization.getParent().getName());
	}
	
	@Test
	public void shouldLoadGeneralizationWithTwoChildreen() throws Exception {
		Architecture architecture = givenAArchitecture("generalaizationReplacce");					
		GeneralizationRelationship g1 = architecture.getRelationshipHolder().getAllGeneralizations().get(0);
		
		assertEquals(2, architecture.getRelationshipHolder().getAllGeneralizations().size());
		Set<Element> ch = g1.getAllChildrenForGeneralClass();
		assertEquals(2, ch.size());
		assertContains(ch, "Class2", "Class3");
	};
	
	@Test
	public void givenAParentClassShouldReturnAllChildren() throws Exception {
		Architecture a = givenAArchitecture("generalizationTwoChild");


		GeneralizationRelationship r = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		assertEquals(2, r.getAllChildrenForGeneralClass().size());
		assertContains(r.getAllChildrenForGeneralClass(), "Class1", "Class2");
	}
	
	@Test
	public void resursiveGeneralization() throws Exception {
		Architecture arch1 = givenAArchitecture("generalizationRecur");

		assertEquals(4, arch1.getRelationshipHolder().getAllGeneralizations().size());
	}
	
}
