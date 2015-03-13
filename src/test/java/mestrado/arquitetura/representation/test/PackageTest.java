package mestrado.arquitetura.representation.test;

import static org.junit.Assert.*;

import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Interface;

public class PackageTest extends TestHelper {
	
	@Test
	public void test1() throws Exception{
		Architecture a = givenAArchitecture("pacoteInterface");
		
		Set<Interface> implementedInterfaces = a.findPackageByName("Package1").getImplementedInterfaces();
		assertEquals(1, implementedInterfaces.size());
		assertEquals("InterfaceTeste", implementedInterfaces.iterator().next().getName());
	}

}
