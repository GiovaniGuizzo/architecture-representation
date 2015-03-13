package mestrado.arquitetura.base.test;

import static org.junit.Assert.*;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Test;

import arquitetura.base.InitializeResources;

/**
 * 
 * @author edipofederle
 *
 */
public class InitializeResourcesTestSingleton {
	
	
	@Test
	public void shouldHaveSameInstance(){
		InitializeResources initializerResources1 = InitializeResources.getInstance();
		InitializeResources initializerResources2 = InitializeResources.getInstance();
		
		assertTrue(initializerResources1 == initializerResources2);
		
		ResourceSet resources1 = initializerResources1.getResources();
		ResourceSet resources2 = initializerResources1.getResources();
		
		assertNotNull(resources1);
		assertNotNull(resources2);
		assertSame(resources1, resources2);
	}
}