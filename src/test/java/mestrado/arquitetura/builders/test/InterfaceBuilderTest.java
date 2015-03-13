package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Method;

public class InterfaceBuilderTest extends TestHelper {
	
	@Test
	public void shouldLoadInterface() throws Exception{
		Architecture a = givenAArchitecture("interface");
		assertEquals(1, a.getInterfaces().size());
		
		Set<Method> operations = a.findInterfaceByName("myInterface").getOperations();
		
		assertEquals(1, operations.size());
		Method opreation = operations.iterator().next();
		assertEquals("Operation1", opreation.getName());
		assertEquals("String", opreation.getReturnType());
		assertEquals(2, opreation.getParameters().size());
		assertEquals("name", opreation.getParameters().get(0).getName());
		assertEquals("fullName", opreation.getParameters().get(1).getName());
	}

}