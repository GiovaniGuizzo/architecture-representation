package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.exceptions.VariationPointElementTypeErrorException;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Package;
import arquitetura.representation.Variant;
import arquitetura.representation.VariationPoint;

public class VariationPointTest extends TestHelper {
	
	@Test
	public void shouldVaritionPointBeAClass() throws VariationPointElementTypeErrorException{
		Element klass = Mockito.mock(Class.class);
		Variant variant = Mockito.mock(Variant.class);
		List<Variant> variants = new ArrayList<Variant>();
		variants.add(variant);
			
		VariationPoint vp = new VariationPoint(klass, variants, "DESIGN_TIME");
		assertNotNull(vp);
		assertTrue(vp.getVariationPointElement() instanceof Class);
	}
	
	@Test
	public void shouldVaritionPointBeAInteface() throws VariationPointElementTypeErrorException{
		Element klass = Mockito.mock(Interface.class);
		Variant variant = Mockito.mock(Variant.class);
		List<Variant> variants = new ArrayList<Variant>();
		variants.add(variant);
			
		VariationPoint vp = new VariationPoint(klass, variants, "DESIGN_TIME");
		assertNotNull(vp);
		assertTrue(vp.getVariationPointElement() instanceof Interface);
	}
	
	@Test(expected=VariationPointElementTypeErrorException.class)
	public void shouldRaiseExcepetionWhenVariationPointIsntAInterfaceOrClass() throws VariationPointElementTypeErrorException{
		Element klass = Mockito.mock(Package.class);
		Variant variant = Mockito.mock(Variant.class);
		List<Variant> variants = new ArrayList<Variant>();
		variants.add(variant);
			
		new VariationPoint(klass, variants, "DESIGN_TIME");
	}
	

}
