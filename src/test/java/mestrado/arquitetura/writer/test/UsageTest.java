package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.UsageRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class UsageTest extends TestHelper {
	
	private Element employee;
	private Element casa;
	private Package pacote1,pacote2;
	
	@Before
	public void setUp() throws Exception{
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
		
		pacote1 = Mockito.mock(Package.class);
		Mockito.when(pacote1.getName()).thenReturn("Pacote1");
		Mockito.when(pacote1.getId()).thenReturn("10100010303");
		
		pacote2 = Mockito.mock(Package.class);
		Mockito.when(pacote2.getName()).thenReturn("Pacote2");
		Mockito.when(pacote2.getId()).thenReturn("10100010312303");
	}
	
	

	@Test
	public void shouldCreateUsageClassClass() throws Exception{
		DocumentManager doc = givenADocument("usageTeste1");
		Operations op = new Operations(doc,null);
		
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> manager = op.forClass().createClass(casa).build();
		
		op.forUsage().createRelation("Usage #1").between(employeeKlass.get("id")).and(manager.get("id")).build();
	
		Architecture a = givenAArchitecture2("usageTeste1");
		assertNotNull(a.getRelationshipHolder().getAllUsage());
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
		assertEquals(0, a.getRelationshipHolder().getAllDependencies().size());
		
		UsageRelationship usage = a.getRelationshipHolder().getAllUsage().get(0);
		assertEquals("Usage #1",usage.getName());
		
		assertEquals("Employee",usage.getClient().getName());
		assertEquals("Casa",usage.getSupplier().getName());
	}
	
	@Test
	public void shouldCreateUsageClassPackageClass() throws Exception{
		DocumentManager doc = givenADocument("usageTeste2");
		Operations op = new Operations(doc,null);
		
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> managerKlass = op.forClass().createClass(casa).build();
		
		op.forPackage().createPacakge(pacote1).withClass(employeeKlass.get("id"));
		
		op.forUsage().createRelation("Usage #2").between(employeeKlass.get("id")).and(managerKlass.get("id")).build();
		
		Architecture a = givenAArchitecture2("usageTeste2");
		assertEquals(1,a.getRelationshipHolder().getAllUsage().size());
		
	}
	
	@Test
	public void shouldCreateUsageClassClassPackage() throws Exception{
		DocumentManager doc = givenADocument("usageTeste3");
		Operations op = new Operations(doc,null);
		
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> managerKlass = op.forClass().createClass(casa).build();
		
		op.forPackage().createPacakge(pacote1).withClass(employeeKlass.get("id"));
		
		op.forUsage().createRelation("Usage #2").between(managerKlass.get("id")).and(employeeKlass.get("id")).build();
		
		Architecture a = givenAArchitecture2("usageTeste3");
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void shouldCreateUsagePackagePackage() throws Exception{
		DocumentManager doc = givenADocument("usageTeste4");
		Operations op = new Operations(doc,null);
		
		String p1 = op.forPackage().createPacakge(pacote1).build().get("packageId");
		String p2 = op.forPackage().createPacakge(pacote2).build().get("packageId");
		
		op.forUsage().createRelation("Usage com Nome").between(p1).and(p2).build();
		
		Architecture a = givenAArchitecture2("usageTeste4");
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
	}
	
}
