package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.helpers.UtilResources;
import arquitetura.representation.Architecture;
import arquitetura.representation.Method;
import arquitetura.representation.ParameterMethod;

public class MethodTest {
	
	@Test
	public void methodsShouldNotBeEquals(){
		Method foo = new Method("foo", "String", "Bar", false, UtilResources.getRandonUUID());
		ParameterMethod params = new ParameterMethod("name", "String", "in");
		foo.getParameters().add(params);
		
		Method foo2 = new Method("foo", "String", "Bar", false, UtilResources.getRandonUUID());
		ParameterMethod params2 = new ParameterMethod("address", "String", "in");
		foo2.getParameters().add(params2);
		
		assertFalse(foo.equals(foo2));
	}
	
	@Test
	public void methodsShouldBeEquals(){
		Method foo = new Method("foo", "String", "Bar", false, UtilResources.getRandonUUID());
		ParameterMethod params = new ParameterMethod("name", "String", "in");
		foo.getParameters().add(params);
		
		Method foo2 = new Method("foo", "String", "Bar", false, UtilResources.getRandonUUID());
		ParameterMethod params2 = new ParameterMethod("name", "String", "in");
		foo2.getParameters().add(params2);
		
		assertTrue(foo.equals(foo2));
	}

}
