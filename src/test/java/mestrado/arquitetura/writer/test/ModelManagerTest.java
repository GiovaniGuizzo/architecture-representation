package mestrado.arquitetura.writer.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Method;
import arquitetura.representation.Package;
import arquitetura.touml.Argument;
import arquitetura.touml.Attribute;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;
import arquitetura.touml.Types;
import arquitetura.touml.VisibilityKind;
public class ModelManagerTest extends TestHelper {
	
	
	private Element employee;
	private Element person;
	private Element casa;
	private Element city;
	private Package pacote;


	@After
	public void tearDown(){
		cleanManipulationFolder();
	}
	
	@Before
	public void setUp(){
		cleanManipulationFolder();
		
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		person = Mockito.mock(Class.class);
		Mockito.when(person.getName()).thenReturn("Person");
		Mockito.when(person.getId()).thenReturn("1993393123");
		
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
		
		city = Mockito.mock(Class.class);
		Mockito.when(city.getName()).thenReturn("City");
		Mockito.when(city.getId()).thenReturn("123123123123");
		
		pacote = Mockito.mock(Package.class);
		Mockito.when(pacote.getName()).thenReturn("Pacote");
		Mockito.when(pacote.getId()).thenReturn("2343242");
		
	}
	
	private void cleanManipulationFolder() {
		File file = new File("manipulation/");     
		File file2 = new File(ReaderConfig.getDirExportTarget());

        String[] myFiles2;    
        if(file.isDirectory()){
            myFiles2 = file2.list();
            for (int i=0; i<myFiles2.length; i++) {
                File myFile = new File(file, myFiles2[i]); 
                myFile.delete();
            }
         }
		
        String[] myFiles;    
            if(file.isDirectory()){
                myFiles = file.list();
                for (int i=0; i<myFiles.length; i++) {
                    File myFile = new File(file, myFiles[i]); 
                    myFile.delete();
                }
             }
	}
	
	@Test 
	public void shouldCreateAssociationBetweenClasses() throws Exception{
		Architecture a = givenAArchitecture("newAssociation");
		
		Class foo = a.createClass("Foo", false);
		Class bar = a.createClass("subBar", false);
		
		a.forAssociation().createAssociationEnd().withKlass(foo).and().createAssociationEnd().withKlass(bar).build();
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "newAssociationGerada");
		
		Architecture gerada = givenAArchitecture2("newAssociationGerada");
		assertEquals(1, gerada.getRelationshipHolder().getAllAssociations().size());
		
	}
	
	@Test
	public void shouldRemoveAClass() throws Exception{
		DocumentManager doc = givenADocument("testeRemover");
		Operations op = new Operations(doc, null);
		String cityId = op.forClass().createClass(city).build().get("id");
		assertTrue(modelContainId("testeRemover", cityId));
		 op.forClass().removeClassById(cityId);
		assertFalse(modelContainId("testeRemover", cityId));
	}
	
	@Test
	public void shouldCreateAClass() throws Exception{
		DocumentManager doc = givenADocument("teste2");
		Operations op = new Operations(doc, null);
		assertNotNull(op.forClass().createClass(casa));
		Architecture a = givenAArchitecture2("teste2");
		assertNotNull(a.findClassByName("Casa"));
		assertFalse(a.findClassByName("Casa").get(0).isAbstract());
		
	}
	
	@Test
	public void shouldCreateAAbstractClass() throws Exception{
		DocumentManager doc = givenADocument("classAbstrata");
		Operations op = new Operations(doc, null);
		op.forClass().createClass(casa).isAbstract().build();
		Architecture a = givenAArchitecture2("classAbstrata");
		assertNotNull(a.findClassByName("Casa"));
		Class klass = a.findClassByName("Casa").get(0);
		assertTrue(klass.isAbstract());
	}
	
	@Test
	public void shouldCreateAClassWithAttribute() throws Exception{
		DocumentManager doc = givenADocument("testeCreateClassWithAttribute");
		Operations op = new Operations(doc, null);
		
		Attribute xpto = Attribute.create()
				 .withName("xpto")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		Attribute age = Attribute.create()
				 .withName("age")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.INTEGER);
		
		List<Attribute> classInfoAttrs = new ArrayList<Attribute>();
		classInfoAttrs.add(xpto);
		classInfoAttrs.add(age);
		
		Attribute id = Attribute.create()
				 .withName("id")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.INTEGER);
		
		Attribute name = Attribute.create()
				 .withName("name")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(id);
		classInfoAttrs2.add(name);
		
		Map<String, String> classInfo = op.forClass().createClass(casa).withAttribute(classInfoAttrs).build();
		op.forClass().createClass(person).withAttribute(classInfoAttrs2).build();
		
		assertNotNull(classInfo);
		String[] idsProperty = classInfo.get("idsProperties").split(" ");
		String idClass = classInfo.get("id");
		assertNotNull(idClass);
		assertThat("should contain two attributes", idsProperty.length, equalTo(2));
		
		Architecture a = givenAArchitecture2("testeCreateClassWithAttribute");
		assertNotNull(a.findClassByName("Casa"));
		
		Class klass43 = a.findClassByName("Casa").get(0);
		arquitetura.representation.Attribute attrXpto = klass43.findAttributeByName("xpto");
		assertNotNull(attrXpto);
		assertNotNull(attrXpto.getId());
		assertEquals("xpto", attrXpto.getName());
		assertEquals("String", attrXpto.getType());
		assertEquals("public", attrXpto.getVisibility());
		
		
	}
	
	@Test
	public void shouldRemoveAttributeFromClasse() throws Exception{
		DocumentManager doc = givenADocument("testRemoveAttribute");
		Operations op = new Operations(doc, null);
		
		Attribute name = Attribute.create()
				 .withName("name")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		Attribute age = Attribute.create()
				 .withName("age")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.INTEGER);
		
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(name);
		classInfoAttrs2.add(age);
		
		op.forClass().createClass(casa).withAttribute(classInfoAttrs2).build();
		
		assertTrue(modelContainId("testRemoveAttribute", name.getId()));
		
		op.forClass().removeAttribute(name.getId());
		
		assertFalse(modelContainId("testRemoveAttribute", name.getId()));
		
	}
	
	@Test
	public void shouldCreatAClassWithMethod() throws Exception{
		DocumentManager doc = givenADocument("teste4");
		Operations op = new Operations(doc, null);
		
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.STRING, "in"));
		
		arquitetura.touml.Method foo = arquitetura.touml.Method.create().withName("foo").withArguments(arguments)
							 .withVisibility(VisibilityKind.PRIVATE_LITERAL)
							 .withReturn(Types.INTEGER).build();
		
		List<Argument> argumentsTeste = new ArrayList<Argument>();
		argumentsTeste.add(Argument.create("name", Types.STRING, "in"));
		
		arquitetura.touml.Method teste = arquitetura.touml.Method.create().withName("teste").withArguments(argumentsTeste)
							 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 .withReturn(Types.INTEGER).build();
		
		Attribute name = Attribute.create()
				 .withName("name")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(name);
		
		Set<arquitetura.touml.Method> methods = new HashSet<arquitetura.touml.Method>();
		methods.add(foo);
		methods.add(teste);
		
		op.forClass().createClass(person).withMethods(methods).withAttribute(classInfoAttrs2).build();
		Architecture arch = givenAArchitecture2("teste4");
		Class barKlass = arch.findClassByName("Person").get(0);
		
		assertNotNull(barKlass);
		assertThat("Should have 2 methods", barKlass.getAllMethods().size() == 2);
		assertThat("Should have a method called 'foo'", barKlass.findMethodByName("foo").getName().equals("foo"));
		assertThat("Should have a method called 'teste'", barKlass.findMethodByName("teste").getName().equals("teste"));
		
		Method methodFoo = barKlass.findMethodByName("foo");
		
		assertEquals(2, methodFoo.getParameters().size());
		assertThat("Should parameter name be 'name'", methodFoo.getParameters().get(0).getName().equals("name") );
	}
	
	
	@Test
	public void testeMethod() throws Exception{
		DocumentManager doc = givenADocument("teste666");
		Operations op = new Operations(doc, null);
		
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.STRING, "in"));
		arguments.add(Argument.create("name", Types.INTEGER, "in"));
		
		arquitetura.touml.Method foo = arquitetura.touml.Method.create().withName("foo").withArguments(arguments)
							 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 .withReturn(Types.INTEGER).build();
		
		Set<arquitetura.touml.Method> methods = new HashSet<arquitetura.touml.Method>();
		methods.add(foo);
		op.forClass().createClass(person).withMethods(methods).build();
	}
	
	@Test
	public void shouldCreateAMethodWithMethodAbstract() throws Exception{
		DocumentManager doc = givenADocument("testeMethodoAbastrato");
		Operations op = new Operations(doc, null);
		
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.STRING, "in"));
		arguments.add(Argument.create("name", Types.INTEGER, "in"));
		
		arquitetura.touml.Method bar = arquitetura.touml.Method.create()
							 						.withName("bar").withArguments(arguments)
							 						.withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 						.withReturn(Types.INTEGER)
							 						.abstractMethod().build();
		
		Set<arquitetura.touml.Method> methods = new HashSet
				<arquitetura.touml.Method>();
		methods.add(bar);
		
		op.forClass().createClass(person).withMethods(methods).build();
		
		Architecture arch = givenAArchitecture2("testeMethodoAbastrato");
		
		assertThat("Should have 1 method", arch.findClassByName("Person").get(0).getAllMethods().size() == 1);
		assertTrue(arch.findClassByName("Person").get(0).findMethodByName("bar").isAbstract());
	}
	
	@Test
	public void shouldCreateAMethodWithOneCustonParameter() throws Exception{
		DocumentManager doc = givenADocument("empty_2");
		
		Class klassBar = Mockito.mock(Class.class);
		Mockito.when(klassBar.getName()).thenReturn("Bar");
		Mockito.when(klassBar.getId()).thenReturn("03030030303");
		
		List<Element> elements = new ArrayList<Element>();
		elements.add(klassBar);
		
		Architecture arch = Mockito.mock(Architecture.class);
		Mockito.when(arch.getElements()).thenReturn(elements);
		
		Operations op = new Operations(doc, arch);
		
		op.forClass().createClass(klassBar).build();
		
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.custom(klassBar.getName()), "in"));
		
		arquitetura.touml.Method bar = arquitetura.touml.Method.create()
							 						.withName("bar").withArguments(arguments)
							 						.withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 						.withReturn(Types.INTEGER)
							 						.abstractMethod().build();
		Set<arquitetura.touml.Method> methods = new HashSet<arquitetura.touml.Method>();
		methods.add(bar);
		op.forClass().createClass(person).withMethods(methods).build();
		
		Architecture a = givenAArchitecture2("empty_2");
		assertNotNull(a);
		assertEquals(2,a.getClasses().size());
		Class personKlass = a.findClassByName("Person").get(0);
		assertEquals(1,personKlass.getAllMethods().size());
		
		Method methodBarFromPerson = personKlass.getAllMethods().iterator().next();
		assertEquals("bar",methodBarFromPerson.getName());
		assertEquals("name",methodBarFromPerson.getParameters().get(0).getName());
		assertEquals("Bar",methodBarFromPerson.getParameters().get(0).getType());
	}
	
	
	@Test
	public void shouldRemoveMethodFromClass() throws Exception{
		DocumentManager doc = givenADocument("testeRemoveMethod");
		Operations op = new Operations(doc, null);
		
		List<Argument> arguments = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.STRING, "in"));
		
		arquitetura.touml.Method foo = arquitetura.touml.Method.create()
							 						.withName("bar").withArguments(arguments)
							 						.withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 						.withReturn(Types.INTEGER)
							 						.build();
		
		List<Argument> arguments2 = new ArrayList<Argument>();
		arguments.add(Argument.create("name", Types.STRING, "in"));
		
		arquitetura.touml.Method teste = arquitetura.touml.Method.create()
							 						.withName("teste").withArguments(arguments2)
							 						.withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 						.withReturn(Types.INTEGER)
							 						.build();
		
		Attribute xpto = Attribute.create()
								 .withName("xpto")
								 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
								 .withType(Types.STRING);
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(xpto);
		Set<arquitetura.touml.Method> methods = new HashSet<arquitetura.touml.Method>();
		methods.add(foo);
		methods.add(teste);
		op.forClass().createClass(person).withMethods(methods).withAttribute(classInfoAttrs2).build();
		
		assertTrue(modelContainId("testeRemoveMethod", teste.getId()));
		
		op.forClass().removeMethod(teste.getId());
		
		assertFalse(modelContainId("testeRemoveMethod", teste.getId()));
	}
	
	@Test
	public void shouldAddNewAttributeToExistClass() throws Exception{
		DocumentManager doc = givenADocument("addNewAttributeToClass");
		Operations op = new Operations(doc, null);
		
		Architecture arch = givenAArchitecture2("addNewAttributeToClass");
		assertNotNull(arch);
		assertEquals(0, arch.getClasses().size());
		
		String idClass = op.forClass().createClass(casa).build().get("id");
		
		Attribute xpto = Attribute.create()
				 .withName("xpto")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		
		assertFalse(modelContainId("addNewAttributeToClass", xpto.getId()));
		op.forClass().addAttributeToClass(idClass, xpto);
		assertTrue(modelContainId("addNewAttributeToClass", xpto.getId()));
		
	}
	
	@Test
	public void shouldAddNewPrivateAttributeToExistClass() throws Exception{
		DocumentManager doc = givenADocument("addNewPrivateAttributeToClass");
		Operations op = new Operations(doc, null);
		
		Architecture arch = givenAArchitecture("simples");
		assertNotNull(arch);
		
		String idClass = op.forClass().createClass(person).build().get("id");
		
		Attribute xpto = Attribute.create()
				 .withName("xpto")
				 .withVisibility(VisibilityKind.PRIVATE_LITERAL)
				 .withType(Types.STRING);
		
		
		assertFalse(modelContainId("addNewPrivateAttributeToClass", xpto.getId()));
		op.forClass().addAttributeToClass(idClass, xpto);
		assertTrue(modelContainId("addNewPrivateAttributeToClass", xpto.getId()));
		Architecture arch2 = givenAArchitecture2("addNewPrivateAttributeToClass");
		assertEquals("private", arch2.findClassByName("Person").get(0).getAllAttributes().iterator().next().getVisibility());
	}
	
	@Test
	public void shouldAddANewMethodInExistClass() throws Exception{
		DocumentManager doc = givenADocument("addNewMethodToClass");
		Operations op = new Operations(doc, null);
		
		Architecture arch = givenAArchitecture2("addNewMethodToClass");
		assertNotNull(arch);
		assertEquals(0, arch.getClasses().size());
		
		String idClass = op.forClass().createClass(casa).build().get("id");
		assertNotNull("class id should not be null", idClass);
		assertTrue("model should contain class id", modelContainId("addNewMethodToClass", idClass));
		
		List<Argument> arguments2 = new ArrayList<Argument>();
		arguments2.add(Argument.create("name", Types.STRING, "in"));
		
		arquitetura.touml.Method teste = arquitetura.touml.Method.create()
							 						.withName("teste").withArguments(arguments2)
							 						.withVisibility(VisibilityKind.PUBLIC_LITERAL)
							 						.withReturn(Types.INTEGER)
							 						.build();
		
		List<Argument> arguments3 = new ArrayList<Argument>();
		arguments3.add(Argument.create("age", Types.INTEGER, "in"));
		
		arquitetura.touml.Method xpto = arquitetura.touml.Method.create()
							 						.withName("xpto").withArguments(arguments3)
							 						.withVisibility(VisibilityKind.PRIVATE_LITERAL)
							 						.withReturn(Types.STRING)
							 						.build();
		
		op.forClass().addMethodToClass(idClass, teste);
		op.forClass().addMethodToClass(idClass, xpto);
		
		assertTrue(modelContainId("addNewMethodToClass", teste.getId()));
		assertTrue(modelContainId("addNewMethodToClass", xpto.getId()));
	}
	
	
	@Test
	public void shouldCreateAAttributeWithCustonType() throws Exception{
		
		DocumentManager doc = givenADocument("classWithAttrCuston");
		Architecture a = Mockito.mock(Architecture.class);
		ArrayList<Element> list = new ArrayList<Element>();
		list.add(employee);
		
		Mockito.when(a.getElements()).thenReturn(list);
		Operations op = new Operations(doc, a);
		
		op.forClass().createClass(employee);
		
		Attribute xpto = Attribute.create()
				 .withName("xpto")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.custom("Employee"));
		
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(xpto);
		op.forClass().createClass(casa).withAttribute(classInfoAttrs2);
		
		Architecture arch = givenAArchitecture2("classWithAttrCuston");
		Class klassFoo = arch.findClassByName("Casa").get(0);
		assertNotNull(klassFoo);
		assertTrue(modelContainId("classWithAttrCuston", xpto.getId()));
	}

	@Test
	public void shouldSaveModificationAndCopyFilesToDestination() throws Exception{
		givenADocument("teste5");
		String dirExportTarget = ReaderConfig.getDirExportTarget();
		
		Assert.assertTrue("should copy exist", new File(dirExportTarget+"teste5.notation").exists());
		Assert.assertTrue("should copy exist", new File(dirExportTarget+"teste5.uml").exists());
		Assert.assertTrue("should copy exist", new File(dirExportTarget+"teste5.di").exists());
	}

	/* Pacote Testes */
	@Test
	public void shouldCreateAPackage() throws Exception{
		DocumentManager doc = givenADocument("testePacote");
		Operations op = new Operations(doc, null);
		
		op.forPackage().createPacakge(pacote);
		
		Architecture arch = givenAArchitecture2("testePacote");
		
		assertThat("Should have one package", arch.getAllPackages().size() == 1);
		assertEquals("Pacote", arch.getAllPackages().iterator().next().getName());
	}
	
	@Test
	public void shouldCreateAClassInsideAPackage() throws Exception{
		DocumentManager doc = givenADocument("testePacoteComClasse");
		Operations op = new Operations(doc, null);
		
		Map<String, String> foo = op.forClass().createClass(casa).build();
		Map<String, String> teste = op.forClass().createClass(person).build();
		op.forPackage().createPacakge(pacote).withClass(foo.get("id")).withClass(teste.get("id")).build();
		
		Architecture arch = givenAArchitecture2("testePacoteComClasse");
		
		assertThat("Should have one package", arch.getAllPackages().size() == 1);
		assertEquals("Pacote", arch.getAllPackages().iterator().next().getName());
	}
	
	@Test
	public void shouldCreateAClassInsideAPackageWithAssociation() throws Exception{
		
		Architecture architecture = givenAArchitecture("testePacoteClassAsssociation");
		
		Class KlassCasa = architecture.createClass("Casas", false);
		Class klassPerson = architecture.createClass("Person", false);
		
		Package packagePacote = architecture.createPackage("Pacote");
		packagePacote.addExternalClass(klassPerson);
		packagePacote.addExternalClass(KlassCasa);
		
		architecture.forAssociation().createAssociationEnd().withKlass(KlassCasa).and().createAssociationEnd().withKlass(klassPerson).build();
		//op.forAssociation().createAssociation().betweenClass(foo).andClass(teste).build();
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(architecture, "testePacoteClassAsssociationGerado");
		
		Architecture gerada = givenAArchitecture2("testePacoteClassAsssociationGerado");
		
		assertEquals(2, gerada.findPackageByName("Pacote").getAllClasses().size());
		assertEquals(1, gerada.getRelationshipHolder().getAllAssociations().size());
	}
	
	@Test @Ignore
	public void associationClassPakackeClass() throws Exception{
//		DocumentManager doc = givenADocument("testeAssociationPackageClassClass");
//		Operations op = new Operations(doc, null);
//		
//		Map<String, String> foo = op.forClass().createClass(casa).build();
//
//		op.forPackage().createPacakge(pacote).withClass(foo.get("id")).build();
//	
//		Map<String, String> infosClass = op.forClass().createClass(person).build();
//		String idClassPerson = infosClass.get("id");
//		
//		op.forAssociation().createAssociation()
//							 .betweenClass(foo.get("id"))
//							 .andClass(idClassPerson).build();
//		
//		Architecture arch = givenAArchitecture2("testeAssociationPackageClassClass");
//		assertEquals(1, arch.getAllAssociations().size());
//		AssociationRelationship asssociation = arch.getAllAssociations().get(0);
//		assertNotNull(asssociation);
		
	}
	
	@Test
	public void shouldCreateAAbstractClassInsideAPackage() throws Exception{
		DocumentManager doc = givenADocument("classAbstrataDentroPacote");
		Operations op = new Operations(doc, null);
		
		Map<String, String> infosClass = op.forClass().createClass(casa).isAbstract().build();
		String idClassPerson = infosClass.get("id");
		
		op.forPackage().createPacakge(pacote).withClass(idClassPerson).build();
		
		Architecture a = givenAArchitecture2("classAbstrataDentroPacote");
		assertNotNull(a.findClassByName("Casa"));
		Class klass = a.findClassByName("Casa").get(0);
		assertTrue(klass.isAbstract());
	}

	
	@Test
	public void shouldCreateAPacakgeWithTwoClasses() throws Exception{
		
		DocumentManager doc = givenADocument("novoTest");
		Operations op = new Operations(doc, null);
		
		Attribute xpto = Attribute.create()
				 .withName("xpto")
				 .withVisibility(VisibilityKind.PUBLIC_LITERAL)
				 .withType(Types.STRING);
		
		
		List<Attribute> classInfoAttrs2 = new ArrayList<Attribute>();
		classInfoAttrs2.add(xpto);
		
		 Map<String, String> k = op.forClass().createClass(casa).withAttribute(classInfoAttrs2).build();
		 Map<String, String> k1 = op.forClass().createClass(person).build();
		 
		op.forPackage().createPacakge(pacote).withClass(k.get("id")).withClass(k1.get("id")).build();
		
		Architecture a = givenAArchitecture2("novoTest");
		
		assertEquals(1, a.getAllPackages().size());
		assertEquals(2 ,a.findPackageByName("Pacote").getElements().size());
	}

}