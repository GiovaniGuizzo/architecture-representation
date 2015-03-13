package mestrado.arquitetura.writer.test;

import static org.junit.Assert.*;

import java.util.Map;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class RealizationTest extends TestHelper {
	
	private Element employee;
	private Element casa;
	
	@Before
	public void setUp() throws Exception{
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
	}
	
	@Test
	public void shouldGenerateARealization() throws Exception{
		DocumentManager doc = givenADocument("realization");
		Operations op = new Operations(doc,null);
		
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> manager = op.forClass().createClass(casa).build();
		
		op.forRealization()
			.createRelation()
			.withName("FooBar")
			.between(employeeKlass.get("id"))
			.and(manager.get("id"))
			.build();
		
		Architecture a = givenAArchitecture2("realization");
		assertNotNull(a.getRelationshipHolder().getAllRealizations());
	}
	
}
