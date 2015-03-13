package mestrado.arquitetura.writer.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Interface;
import arquitetura.representation.Variability;
import arquitetura.representation.Variant;
import arquitetura.touml.BindingTime;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;
import arquitetura.touml.VariabilityStereotype;

public class VaritionPointTest extends TestHelper {
	
	private Class casa, employee;
	private Interface casaInterface;
	
	@Before
	public void setUp(){
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
		
		casaInterface = Mockito.mock(Interface.class);
		Mockito.when(casaInterface.getName()).thenReturn("CasaInterface");
		Mockito.when(casaInterface.getId()).thenReturn("121231233123123123");
		
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
	}

	@Test
	public void interfaceShouldBeVariationPoint() throws Exception{
		DocumentManager doc = givenADocument("variationPointDecoding");
		Operations op = new Operations(doc, null);
		
		
		String idNote = op.forNote().createNote().build();
		Variability variability = createAVariability(op, "Casa");
		VariabilityStereotype a = new VariabilityStereotype(variability);
		op.forNote().addVariability(idNote, a).build();
		
		String idEmployee = op.forClass()
							  .createClass(employee)
							  .asInterface()
							  .isVariationPoint("Casa", "varibiality", BindingTime.DESIGN_TIME)
							  .linkToNote(idNote)
							  .build().get("id");
		
		String idFoo = op.forClass()
				         .createClass(casa)
				         .build().get("id");
		
		Variant mandatory = givenAVariant("mandatory", "Employee", "mandatory");
		op.forClass().addStereotype(idFoo, mandatory);
		
		op.forGeneralization().between(idEmployee).and(idFoo).build();
		
		Architecture a1 = givenAArchitecture2("variationPointDecoding");
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a1, "gevariationPointDecoding");
		
		Architecture a2 = givenAArchitecture2("gevariationPointDecoding");
		Interface inter = a2.findInterfaceByName("Employee");
		assertTrue(inter.isVariationPoint());
		assertEquals("Employee", a1.findClassByName("Casa").get(0).getVariant().getRootVP());
	}
	
	@Test
	public void shouldGenerateAIntefaceWithVariant() throws Exception{
		DocumentManager doc = givenADocument("interfaceWithVariant");
		Operations op = new Operations(doc, null);
		
		
		String idNote = op.forNote().createNote().build();
		Variability variability = createAVariability(op, "CasaInterface");
		VariabilityStereotype a = new VariabilityStereotype(variability);
		op.forNote().addVariability(idNote, a).build();
		
		String idEmployee = op.forClass()
							  .createClass(employee)
							  .isVariationPoint("CasaInterface", "varibiality", BindingTime.DESIGN_TIME)
							  .linkToNote(idNote)
							  .build().get("id");
		
		String idFoo = op.forClass()
				         .createClass(casaInterface)
				         .asInterface()
				         .build().get("id");
		
		Variant mandatory = givenAVariant("mandatory", "Employee", "mandatory");
		
		op.forClass().addStereotype(idFoo, mandatory);
		
		op.forGeneralization().between(idEmployee).and(idFoo).build();
		
		Architecture a1 = givenAArchitecture2("interfaceWithVariant");
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a1, "geinterfaceWithVariant");
		
		Architecture a2 = givenAArchitecture2("geinterfaceWithVariant");
		Interface inter = a2.findInterfaceByName("CasaInterface");
		assertEquals("mandatory", inter.getVariant().getVariantType());
	}

	private Variability createAVariability(Operations op, String variantName) {
		Variability v = Mockito.mock(Variability.class);
		Mockito.when(v.getName()).thenReturn("varibiality");
		Mockito.when(v.getMinSelection()).thenReturn("1");
		Mockito.when(v.getMaxSelection()).thenReturn("1");
		Mockito.when(v.allowAddingVar()).thenReturn(false);
		Mockito.when(v.getBindingTime()).thenReturn(BindingTime.DESIGN_TIME);
		
		Variant variant = Mockito.mock(Variant.class);
		Mockito.when(variant.getName()).thenReturn(variantName);
		
		Mockito.when(v.getVariants()).thenReturn(Arrays.asList(variant));
		
		return v;
	}
	
}
