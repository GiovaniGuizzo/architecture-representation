package operationsOverElementsTests;

import static org.junit.Assert.*;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.relationship.AssociationClassRelationship;

public class OperationOverConcernsTest extends TestHelper {
	
	
	@Test
	public void testConcernsAssociationClass() throws Exception{
		Architecture a = givenAArchitecture("associationClass/associationClassWithAttrAndMethod");
		
		AssociationClassRelationship associationClass = a.getRelationshipHolder().getAllAssociationsClass().get(0);
		assertEquals(1, associationClass.getOwnConcerns().size());
		assertEquals(4, associationClass.getAllConcerns().size());
	}
	
}
