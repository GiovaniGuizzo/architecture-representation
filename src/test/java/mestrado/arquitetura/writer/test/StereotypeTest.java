package mestrado.arquitetura.writer.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.eclipse.uml2.uml.Package;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.helpers.StereotypeHelper;
import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Variant;
import arquitetura.touml.BindingTime;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;
public class StereotypeTest extends TestHelper {
	
	
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
	public void shouldCreateClassWithSteretorypeMandatoryAndVariationPoint() throws Exception{
		
		DocumentManager doc = givenADocument("edipo2");
		Operations op = new Operations(doc, null);
		
		String idEmployee = op.forClass().createClass(employee).build().get("id");
		
		
		String idFoo = op.forClass()
				         .createClass(casa).isVariationPoint("Bar1,Bar2,Bar3", "variability", BindingTime.DESIGN_TIME)
				         .build().get("id");
		
		Variant mandatory = givenAVariant("mandatory", idFoo, "mandatory");
		op.forClass().addStereotype(idFoo, mandatory);
		
		op.forGeneralization().between(idEmployee).and(idFoo).build();
		
		
		Architecture a = givenAArchitecture2("edipo2");
		
		Class klassFoo = a.findClassByName("Employee").get(0);
		assertNotNull(klassFoo);
		
		//assertTrue(klassFoo.isVariationPoint());
		//assertEquals("mandatory", klassFoo.getVariantType().getVariantName());
 	}
	
	@Test
	public void shouldCreateClassWithSteretorypeOptional() throws Exception{
		
		DocumentManager doc = givenADocument("ste2");
		Operations op = new Operations(doc,null);
		
		
		String idFoo = op.forClass().createClass(casa).build().get("id");
		Variant optional = givenAVariant("optional", idFoo, "optional");
		op.forClass().addStereotype(idFoo, optional);
		
		Architecture a = givenAArchitecture2("ste2");
		
		Class klassFoo = a.findClassByName("Casa").get(0);
		assertNotNull(klassFoo);
		
		assertFalse(klassFoo.isVariationPoint());
	}
	
	@Test
	public void shouldAddStereotypeToClass() throws Exception{
		DocumentManager doc = givenADocument("ste3");
		Operations op = new Operations(doc,null);
		
		String id = op.forClass().createClass(casa).build().get("id");
		
		Variant optional = givenAVariant("optional", id, "optional");
		op.forClass().addStereotype(id, optional);
		
		Architecture a = givenAArchitecture2("ste3");
		Class klassFoo = a.findClassByName("Casa").get(0);
		assertNotNull(klassFoo);
		
		assertFalse(klassFoo.isVariationPoint());
		Package model = modelHelper.getModel(ReaderConfig.getDirExportTarget()+"ste3.uml");
		assertNotNull(StereotypeHelper.getStereotypeByName(modelHelper.getClassByName("Casa", model), "optional"));
	}
	
	
	@Test
	public void shouldAddMandatoryAndVariationPointToClass() throws Exception{
		DocumentManager doc = givenADocument("ste4");
		Operations op = new Operations(doc,null);
		
		String idClassFoo = op.forClass().createClass(casa).isVariationPoint("Bar1,Bar2,Bar3", "variability", BindingTime.DESIGN_TIME)
					.build().get("id");
		Variant mandatory = givenAVariant("mandatory", idClassFoo, "mandatory");
		op.forClass().addStereotype(idClassFoo, mandatory);
		
		Architecture a = givenAArchitecture2("ste4");
		Class klassFoo = a.findClassByName("Casa").get(0);
		
		assertNotNull(klassFoo);
		Package model = modelHelper.getModel(ReaderConfig.getDirExportTarget()+"ste4.uml");
		assertNotNull(StereotypeHelper.getStereotypeByName(modelHelper.getClassByName("Casa", model), "mandatory"));
		assertNotNull(StereotypeHelper.getStereotypeByName(modelHelper.getClassByName("Casa", model), "variationPoint"));
		
	}
	
	@Test
	public void shouldAddMandatoryAndVariationPointToClassInvertOrder() throws Exception{
		DocumentManager doc = givenADocument("ste5");
		Operations op = new Operations(doc,null);
		
		String idClass = op.forClass().createClass(casa).isVariationPoint("Bar1,Bar2,Bar3", "variability", BindingTime.DESIGN_TIME).build().get("id");
		Variant mandatory = givenAVariant("mandatory", idClass, "mandatory");
		/*
		 * Inverte ordem dos estereotipos em relação ao caso de teste anterior. Esses testes servem para ver se alguma informação
		 * Não esta sendo reescrita dentro da classe ElementBuilder.
		*/ 
		op.forClass().addStereotype(idClass, mandatory);
		
		Architecture a = givenAArchitecture2("ste5");
		Class klassFoo = a.findClassByName("Casa").get(0);
		
		assertNotNull(klassFoo);
		
		Package model = modelHelper.getModel(ReaderConfig.getDirExportTarget()+"ste5.uml");
		org.eclipse.uml2.uml.Stereotype steMandatory = StereotypeHelper.getStereotypeByName(modelHelper.getClassByName("Casa", model), "mandatory");
		org.eclipse.uml2.uml.Stereotype steVariationPoint = StereotypeHelper.getStereotypeByName(modelHelper.getClassByName("Casa", model), "variationPoint");
		
		assertNotNull(steMandatory);
		assertEquals("mandatory", steMandatory.getName());
		assertNotNull(steVariationPoint);
		assertEquals("variationPoint",steVariationPoint.getName());
	}
	
	@Test
	public void shouldAddConcernToClass() throws Exception{
		DocumentManager doc = givenADocument("addConcern");
		Operations op = new Operations(doc,null);
		
		Concern persistence = Mockito.mock(Concern.class);
		Mockito.when(persistence.getName()).thenReturn("persistence");
		
		
			String id = op.forClass()
			   .createClass(casa)
			   .build().get("id");
			
			op.forConcerns().withStereotype(persistence, id);
		
		ArchitectureBuilder builder = new ArchitectureBuilder();
		Architecture a = builder.create(ReaderConfig.getDirExportTarget()+"addConcern.uml");
		assertNotNull(a.getAllConcerns());
		assertEquals("deve ter um interesse", 1, a.getAllConcerns().size());
		assertEquals("interese deve ser Persistence", "persistence", a.getAllConcerns().get(0).getName());
		
	}
	
	@Test
	public void mandatoryCaseWhithoutPackage() throws Exception{
		Architecture a = givenAArchitecture("mandatory/somenteComMandatory");
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "saidaTeste4444");
		
//		Architecture output = givenAArchitecture2("saidaTeste4444");
//		assertNotNull(output);
//		
//		Class klassCasa = output.findClassByName("Casa").get(0);
//		assertNotNull(klassCasa.getVariant().getName());
//		assertEquals("mandatory",klassCasa.getVariant().getVariantType());
	}

}