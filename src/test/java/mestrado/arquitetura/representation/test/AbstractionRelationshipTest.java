package mestrado.arquitetura.representation.test;

import static org.junit.Assert.*;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Package;

public class AbstractionRelationshipTest extends TestHelper {
	
	@Test
	public void testPackageInterfaceAbstraction() throws Exception{
		Architecture a = givenAArchitecture("abstractionTestPackageInterface");
		
		assertEquals(1,a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1,a.getRelationshipHolder().getAllAbstractions().size());
		
		System.out.println(a.getRelationshipHolder().getAllAbstractions().iterator().next().getClient().getName());
		System.out.println(a.getRelationshipHolder().getAllAbstractions().iterator().next().getSupplier().getName());
		System.out.println( ((Package) a.getRelationshipHolder().getAllAbstractions().iterator().next().getClient()).getImplementedInterfaces());
	}

}
