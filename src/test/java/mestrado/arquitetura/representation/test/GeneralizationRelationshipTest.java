package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mestrado.arquitetura.helpers.test.HelperTest;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Interface;
import arquitetura.representation.relationship.GeneralizationRelationship;


public class GeneralizationRelationshipTest extends HelperTest {
	
	@Test
	public void generalizationBetweenInterfaceAndClass() throws Exception{
		String path = "herancaInterfaceClass/";
		Architecture arch = givenAArchitecture(path+"herancaInterfaceClass");
		
		List
		<GeneralizationRelationship> generalization = arch.getRelationshipHolder().getAllGeneralizations();
		assertEquals(1, generalization.size());
		
		GeneralizationRelationship g = generalization.iterator().next();
		assertTrue(g.getChild() instanceof Interface);
		assertTrue(g.getParent() instanceof Class);
	
	}
	
}
