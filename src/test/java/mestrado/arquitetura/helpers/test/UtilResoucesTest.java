package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.*;

import org.junit.Test;

import arquitetura.helpers.UtilResources;

public class UtilResoucesTest {
	
	@Test
	public void shouldExtractPackageNameFromNamespace(){
		String str = "teste::teste";
		assertEquals("teste", UtilResources.extractPackageName(str));
		
		String str1 = "teste::teste::teste1";
		assertEquals("teste1", UtilResources.extractPackageName(str1));
		
		String str2 = "teste::teste::teste1::293039t";
		assertEquals("293039t", UtilResources.extractPackageName(str2));
	}

}


