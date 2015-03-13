package mestrado.arquitetura.writer.test;

import static org.junit.Assert.*;
import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Ignore;
import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Package;

public class PackagesTest extends TestHelper {
	
	
	@Test
	public void shouldCreateAClassIntoPackage() throws Exception{
		Architecture a = givenAArchitecture("package");
		a.createPackage("PacoteUm");
		
		assertEquals("Deve ter dois pacote", 2, a.getAllPackages().size());
		assertEquals("Deve ter o namespace correto", "package::PacoteUm", a.findPackageByName("PacoteUm").getNamespace());
	}
	
	@Test
	public void shouldCreateClasseIntoPackage() throws Exception{
		Architecture a = givenAArchitecture("package");
		a.createPackage("PacoteUm");
		
		assertEquals("Deve ter dois pacote", 2, a.getAllPackages().size());
		Package p = a.findPackageByName("PacoteUm");
		assertTrue(p.getElements().isEmpty());
		p.createClass("Foo", false);
		assertFalse(p.getElements().isEmpty());
		
		arquitetura.representation.Class foo = a.findClassByName("Foo").get(0);
		assertNotNull(foo);
		assertEquals("package::PacoteUm",foo.getNamespace());
	}
	
	@Test @Ignore("Verificar pacote dentro de pacote...")
	public void nestedUm() throws Exception{
		Architecture a = givenAArchitecture("pacotes/nestedPackage");
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "nestedPackageGerado");
		
		Architecture ag = givenAArchitecture2("nestedPackageGerado");
		
		assertEquals(3,ag.getAllPackages().size());	

		Package p1 = ag.findPackageByName("Package1");
		assertEquals(1, p1.getNestedPackages().size());
		//assertEquals("nestedUm", p1.getNestedPackages().iterator().next().getName());
		//assertEquals("nestedDois", p1.getNestedPackages().get(1).getName());
	}
	
	@Test
	public void nestedDois() throws Exception{
		Architecture a = givenAArchitecture("pacotes/nestedNestedPackage");
	
//		Package p1 = a.findPackageByName("Package1");
//		Package p2 = a.findPackageByName("NestedUm");
//		
//		assertEquals(2, p1.getNestedPackages().size());
//		assertEquals(1, p2.getNestedPackages().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "nestedNestedPackageGerado");
		
//		Architecture ag = givenAArchitecture2("nestedNestedPackageGerado");
		
//		assertEquals(3,ag.getAllPackages().size());	
//
//		Package p1 = ag.findPackageByName("Package1");
//		assertEquals(2, p1.getNestedPackages().size());
//		assertEquals("nestedUm", p1.getNestedPackages().get(0).getName());
//		assertEquals("nestedDois", p1.getNestedPackages().get(1).getName());
	}
	

}
