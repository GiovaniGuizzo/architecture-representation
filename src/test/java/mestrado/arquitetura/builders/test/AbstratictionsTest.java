package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.relationship.AbstractionRelationship;

public class AbstratictionsTest  extends TestHelper{
	
	
	@Test
	public void shouldLoadAbstractionInterElement() throws Exception {
		Architecture architecture7 = givenAArchitecture("moveAbstraction");

		assertNotNull(architecture7);

		AbstractionRelationship abstractionInterElement = architecture7.getRelationshipHolder().getAllAbstractions().iterator().next();

		assertNotNull(abstractionInterElement);
		assertEquals("Client should be Class2", "Class2",	abstractionInterElement.getClient().getName());
		assertEquals("Supplier should be Class1", "Class1", abstractionInterElement.getSupplier().getName());
	}

}
