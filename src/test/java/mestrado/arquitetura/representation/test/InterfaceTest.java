package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.representation.Architecture;
import arquitetura.representation.Interface;
import arquitetura.representation.Method;
import arquitetura.representation.Patterns;

public class InterfaceTest extends TestHelper {
	
	private Architecture architecture;
	private GenerateArchitecture generate;
	
	/**
	 * @see <a href="https://dl.dropbox.com/u/6730822/generico.png">Modelo usado para architecture (Imagem)</a>
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		
		String uriToArchitecture = getUrlToModel("generico");
		architecture = new ArchitectureBuilder().create(uriToArchitecture);
		generate = new GenerateArchitecture();
		
	}
	
	@Test
	public void shouldCreateOperationInterface() throws Exception{
		Interface interfacee = architecture.createInterface("myInterface");
		assertNotNull(interfacee);
		
		assertEquals(0, interfacee.getOperations().size());
		assertNotNull(interfacee.createOperation("myOperation"));
		assertEquals(1, interfacee.getOperations().size());
	}
	
	@Test
	public void shouldRemoveOperationFromInterface() throws Exception{
		Architecture a = givenAArchitecture("interface");
		assertEquals(1, a.getInterfaces().size());
		
		Interface i = a.findInterfaceByName("myInterface");
		assertNotNull(i);
		
		Method o = i.getOperations().iterator().next();
		assertNotNull(o);
		assertEquals(1,i.getOperations().size());
		
		i.removeOperation(o);
		assertEquals(0, i.getOperations().size());
	}
	
	@Test
	public void shouldMoveOperationFromOneInterfaceToOther() throws Exception{
		Architecture a = givenAArchitecture("interface");
		Interface i = a.findInterfaceByName("myInterface");
		Interface i2 = a.createInterface("fooInterface");
		
		Method m = i2.createOperation("myOperation");
		
		assertEquals(1, i2.getOperations().size());
		assertEquals(1, i.getOperations().size());
		
		i2.moveOperationToInterface(m, i);
		assertEquals(2, i.getOperations().size());
		assertEquals(0, i2.getOperations().size());
	}
	
	@Test
	public void shouldGetImplementorsReturnAClass() throws Exception{
		Architecture a = givenAArchitecture("InterfaceImplementorsdi");
		Interface i = a.findInterfaceByName("Interface0");
		
		assertNotNull(i);
		assertEquals(2, i.getImplementors().size());
		assertContains(i.getImplementors(), "Class1", "Package1");
	}
	
	/**
	 * 
	 * @see <a href="https://dl.dropbox.com/u/6730822/dependencyPackageInterface.png"> Modelo usado no teste (imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldGetDepedentsReturnAPackage() throws Exception{
		Architecture a = givenAArchitecture("dependencyPackageInterface");
		Interface i = a.findInterfaceByName("class1");
		assertNotNull(i);
		
		assertEquals(1, i.getDependents().size());
		assertEquals("Package1", i.getDependents().iterator().next().getName());
	}
	
	@Test
	public void shouldReturunsDependenciesForInterface() throws Exception{
		Architecture a = givenAArchitecture("interfaceGetDependencies");
		Interface inter = a.findInterfaceByName("Interface1");
		
		assertNotNull(inter.getDependencies());
	}
	
	@Test
	public void shouldReturnPatternsStereotype() throws Exception{
		Architecture a = givenAArchitecture("patternsSte");
		Interface inter = a.findInterfaceByName("MyInterface");
		
		assertEquals(2, inter.getPatternsOperations().getAllPatterns().size());
		assertTrue(inter.getPatternsOperations().getAllPatterns().contains("bridge"));
		
		generate.generate(a, "pattern_interface");
		Architecture output = givenAArchitecture2("pattern_interface");
		Interface interOutput = output.findInterfaceByName("MyInterface");
		
		assertEquals(2, interOutput.getPatternsOperations().getAllPatterns().size());
		assertTrue(interOutput.getPatternsOperations().getAllPatterns().contains("bridge"));
	
	}
	
	@Test
	public void shouldCheckIfClassAsAPatternStereotypeApplied() throws Exception {
		Architecture a = givenAArchitecture("patternsSte");
		Interface inter = a.getAllInterfaces().iterator().next();
		
		assertTrue(inter.getPatternsOperations().hasPatternApplied());
	}
	
	@Test
	public void shouldAddPatternOnNewInterface() throws Exception{
		Architecture a = givenAArchitecture("patternsSte");
		Interface inter = a.createInterface("InterfaceTest");
		inter.getPatternsOperations().applyPattern(Patterns.ADAPTER);
		
		assertTrue(inter.getPatternsOperations().hasPatternApplied());
		
		generate.generate(a, "pattern_interface_new");
		Architecture output = givenAArchitecture2("pattern_interface_new");
		
		Interface inter2 = output.findInterfaceByName("InterfaceTest");
		assertTrue(inter2.getPatternsOperations().hasPatternApplied());
		
	}
	

}