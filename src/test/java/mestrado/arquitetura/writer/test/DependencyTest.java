package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class DependencyTest extends TestHelper {
	
	private Element employee;
	private Element casa;
	private Package controllers;
	private Package models;
	private Element post;
	private Element category;
	private Element user;
	private Element comment;
	
	
	
	@Before
	public void setUp() throws Exception{
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
		
		controllers = Mockito.mock(Package.class);
		Mockito.when(controllers.getName()).thenReturn("controllers");
		Mockito.when(controllers.getId()).thenReturn("10100010303");
		
		models = Mockito.mock(Package.class);
		Mockito.when(models.getName()).thenReturn("models");
		Mockito.when(models.getId()).thenReturn("232121212121212");
		
		post = Mockito.mock(Class.class);
		Mockito.when(post.getName()).thenReturn("Post");
		Mockito.when(post.getId()).thenReturn("101001001010");
		
		category = Mockito.mock(Class.class);
		Mockito.when(category.getName()).thenReturn("Category");
		Mockito.when(category.getId()).thenReturn("101001001012");
		
		comment = Mockito.mock(Class.class);
		Mockito.when(comment.getName()).thenReturn("Comment");
		Mockito.when(comment.getId()).thenReturn("1010010010123");
		
		user = Mockito.mock(Class.class);
		Mockito.when(user.getName()).thenReturn("User");
		Mockito.when(user.getId()).thenReturn("1010123001010");
		
	}
	
	
	@Test
	public void shouldCreateADependencyClassClass() throws Exception{
		DocumentManager doc = givenADocument("testeDependencia1");
		Operations op = new Operations(doc,null);
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> managerKlass = op.forClass().createClass(casa).build();
		
		op.forDependency().createRelation()
				            .withName("Dependency #12")
							.between(employeeKlass.get("id"))
							.and(managerKlass.get("id"))
							.build();
		
		Architecture a = givenAArchitecture2("testeDependencia1");
		assertNotNull(a);
		assertNotNull(a.getRelationshipHolder().getAllDependencies());
		assertEquals("Dependency #12", a.getRelationshipHolder().getAllDependencies().get(0).getName());
		assertEquals("Employee", a.getRelationshipHolder().getAllDependencies().get(0).getClient().getName());
		assertEquals("Casa", a.getRelationshipHolder().getAllDependencies().get(0).getSupplier().getName());
	}

	
	@Test
	public void shouldCreateADependencyClassPackage() throws Exception{
		DocumentManager doc = givenADocument("testeDependenciClassPackage");
		Operations op = new Operations(doc,null);
		
		Map<String, String> id = op.forPackage().createPacakge(controllers).build();
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		
		op.forDependency().createRelation()
		                    .withName("Dependency #12")
							.between(employeeKlass.get("id"))
							.and(id.get("packageId")).build();

		Architecture a = givenAArchitecture2("testeDependenciClassPackage");
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().get(0);
		
		assertNotNull(a);
		assertEquals(1, a.getAllPackages().size());
		assertNotNull(a.getRelationshipHolder().getAllDependencies().size());
		assertEquals("Employee", dependency.getClient().getName());
		assertEquals("controllers", dependency.getSupplier().getName());
	}
	
	@Test
	public void shouldCreateDependencyPackageClass() throws Exception{
		DocumentManager doc = givenADocument("testeDependenciPackagePackage");
		Operations op = new Operations(doc,null);
		
		Map<String, String> pacoteControllers = op.forPackage().createPacakge(controllers).build();
		Map<String, String> pacoteModels = op.forPackage().createPacakge(models).build();
		
		
		op.forDependency().createRelation()
		                    .withName("Dependency #12")
							.between(pacoteControllers.get("packageId"))
							.and(pacoteModels.get("packageId"))
							.build();
		
		Architecture a = givenAArchitecture2("testeDependenciPackagePackage");
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().get(0);
		
		assertNotNull(a);
		assertEquals(2, a.getAllPackages().size());
		assertNotNull(a.getRelationshipHolder().getAllDependencies().size());
		assertEquals("controllers", dependency.getClient().getName());
		assertEquals("models", dependency.getSupplier().getName());
	}
	
	@Test
	public void shouldCreateDependencyWithMultiplesSuppliers() throws Exception{
		DocumentManager doc = givenADocument("dependenciaMultipla");
		Operations op = new Operations(doc,null);
		
		String postKlass = op.forClass().createClass(post).build().get("id");
		String commentKlass = op.forClass().createClass(comment).build().get("id");
		String userKlass = op.forClass().createClass(user).build().get("id");
		String categoryKlass = op.forClass().createClass(category).build().get("id");
		
		op.forDependency().createRelation().withName("Dependency #1").between(postKlass).and(commentKlass).build();
		op.forDependency().createRelation().between(postKlass).and(userKlass).build();
		op.forDependency().createRelation().between(postKlass).and(categoryKlass).build();
		
		Architecture a = givenAArchitecture2("dependenciaMultipla");
		
		assertEquals(3, a.getRelationshipHolder().getAllDependencies().size());
		assertEquals("Post", a.getRelationshipHolder().getAllDependencies().get(0).getClient().getName());
		assertEquals("Post", a.getRelationshipHolder().getAllDependencies().get(1).getClient().getName());
		assertEquals("Post", a.getRelationshipHolder().getAllDependencies().get(2).getClient().getName());
		
		//TODO TODO TESTE VER
	//	assertEquals(3, a.getAllDependencies().get(0).getAllSuppliersForClientClass().size());
		//assertContains(a.getAllDependencies().get(0).getAllSuppliersForClientClass(), "User", "Category", "Comment");
	}
	
	@Test
	public void shouldCreeateDependencyWithMultipleClients() throws Exception{
		DocumentManager doc = givenADocument("dependenciaMultipla2");
		Operations op = new Operations(doc,null);
		
		String postKlass = op.forClass().createClass(post).build().get("id");
		String commentKlass = op.forClass().createClass(comment).build().get("id");
		String userKlass = op.forClass().createClass(user).build().get("id");
		String categoryKlass = op.forClass().createClass(category).build().get("id");
	
		
		op.forDependency().createRelation().withName("Dependency #1").between(userKlass).and(commentKlass).build();
		op.forDependency().createRelation().withName("Dependency #2").between(postKlass).and(commentKlass).build();
		op.forDependency().createRelation().withName("Dependency #3").between(categoryKlass).and(commentKlass).build();
	}
	
	@Test
	public void shouldCreateDependencyClassClassPackage() throws Exception{
		DocumentManager doc = givenADocument("dependenciaClassClassPackage");
		Operations op = new Operations(doc,null);
		
		String klassId = op.forClass().createClass(comment).build().get("id");
		String fooId = op.forClass().createClass(post).build().get("id");
		op.forPackage().createPacakge(controllers).withClass(klassId).build();
		
		op.forDependency().createRelation().withName("Dependency #12").between(klassId).and(fooId).build();

		Architecture a = givenAArchitecture2("dependenciaClassClassPackage");
		
		assertTrue(modelContainId("dependenciaClassClassPackage", klassId));
		assertTrue(modelContainId("dependenciaClassClassPackage", fooId));
		
		assertEquals(1, a.getRelationshipHolder().getAllDependencies().size());
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().get(0);
		assertEquals("Comment",dependency.getClient().getName());
		assertEquals("Post",dependency.getSupplier().getName());
	}
	
	@Test
	public void createDependencyPacakgeClassClass() throws Exception{
		DocumentManager doc = givenADocument("dependencyPacakgeClassClass");
		Operations op = new Operations(doc,null);
		
		String klassId = op.forClass().createClass(post).build().get("id");
		String fooId = op.forClass().createClass(comment).build().get("id");
		//op.forPackage().createPacakge("Controllers").withClass(klassId).build();
		
		op.forDependency().createRelation().withName("Dependency #12").between(fooId).and(klassId).build();

		Architecture a = givenAArchitecture2("dependencyPacakgeClassClass");
		
		assertTrue(modelContainId("dependencyPacakgeClassClass", klassId));
		assertTrue(modelContainId("dependencyPacakgeClassClass", fooId));
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().get(0);
		assertEquals("Comment",dependency.getClient().getName());
		assertEquals("Post",dependency.getSupplier().getName());
	}
	
	@Test
	public void whenDependencyNotHaveANameSetEmpty() throws Exception{
		DocumentManager doc = givenADocument("dependencySemNome");
		Operations op = new Operations(doc,null);
		
		String postId = op.forClass().createClass(post).build().get("id");
		String commentId = op.forClass().createClass(comment).build().get("id");
		
		op.forDependency().createRelation().withName("").between(commentId).and(postId).build();

		Architecture a = givenAArchitecture2("dependencySemNome");
		
		assertEquals("", a.getRelationshipHolder().getAllDependencies().get(0).getName());
	}
	
	//Estereótipos
	@Test
	public void shouldLoadCreateStereotype() throws Exception{
		Architecture arch = givenAArchitecture("loadDependencyWithCreateStereotype");
		
		DependencyRelationship dep = arch.getRelationshipHolder().getAllDependencies().get(0);
		assertNotNull(dep.getStereotypes());
		assertEquals("create", dep.getStereotypes().get(0));
	}
	
	@Test
	public void shouldAddCreateStereotype() throws Exception{
		Architecture a = givenAArchitecture("dependencySte");
		
		DependencyRelationship dep = a.getRelationshipHolder().getAllDependencies().get(0);
		dep.setStereotype("create");
		
		GenerateArchitecture generate = new GenerateArchitecture();
		generate.generate(a, "saidaTesteRela2");
		
		Architecture archOutput = givenAArchitecture2("saidaTesteRela2");
		assertNotNull(archOutput);
		
		DependencyRelationship depOutput = archOutput.getRelationshipHolder().getAllDependencies().get(0);
		assertNotNull(depOutput.getStereotypes());
		assertEquals("create", depOutput.getStereotypes().get(0));
	}
	
	@Test
	public void shouldRemoveCreateStereotype() throws Exception{
		
		Architecture arch = givenAArchitecture("loadDependencyWithCreateStereotype");
		
		DependencyRelationship dep = arch.getRelationshipHolder().getAllDependencies().get(0);
		assertNotNull(dep.getStereotypes());
		assertEquals("create", dep.getStereotypes().get(0));
		
		dep.removeStereotype("create");
		
		GenerateArchitecture generate = new GenerateArchitecture();
		generate.generate(arch, "saidaTesteRela3");
		
		Architecture archOutput = givenAArchitecture2("saidaTesteRela3");
		
		DependencyRelationship depOutput = archOutput.getRelationshipHolder().getAllDependencies().get(0);
		assertTrue(depOutput.getStereotypes().isEmpty());
	}
	
	
}