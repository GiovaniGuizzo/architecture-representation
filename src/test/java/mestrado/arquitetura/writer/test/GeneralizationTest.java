package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.exceptions.NotSuppportedOperation;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class GeneralizationTest extends TestHelper {
	
	private Element employee;
	private Element casa;
	private Element post;
	private Element user;
	private Element comment;
	private Package pacote;
	private Package pacote2;
	private Element category;
	
	@Before
	public void setUp() throws Exception{
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		casa = Mockito.mock(Class.class);
		Mockito.when(casa.getName()).thenReturn("Casa");
		Mockito.when(casa.getId()).thenReturn("123123123123");
		
		post = Mockito.mock(Class.class);
		Mockito.when(post.getName()).thenReturn("Post");
		Mockito.when(post.getId()).thenReturn("101001001010");
		
		user = Mockito.mock(Class.class);
		Mockito.when(user.getName()).thenReturn("User");
		Mockito.when(user.getId()).thenReturn("101001001012");
		
		comment = Mockito.mock(Class.class);
		Mockito.when(comment.getName()).thenReturn("Comment");
		Mockito.when(comment.getId()).thenReturn("1010010010123");
		
		pacote = Mockito.mock(Package.class);
		Mockito.when(pacote.getName()).thenReturn("Pacote1");
		Mockito.when(pacote.getId()).thenReturn("2131231231");
		
		pacote2 = Mockito.mock(Package.class);
		Mockito.when(pacote2.getName()).thenReturn("Pacote2");
		Mockito.when(pacote2.getId()).thenReturn("2131231234");
		
		category = Mockito.mock(Class.class);
		Mockito.when(category.getName()).thenReturn("Category");
		Mockito.when(category.getId()).thenReturn("101001001012");
		
		
	}
	
	@Test
	public void shouldCreateGeneralization() throws Exception{
		DocumentManager doc = givenADocument("generalization1");
		Operations op = new Operations(doc,null);
		
		Map<String, String> employeeKlass = op.forClass().createClass(employee).build();
		Map<String, String> managerKlass = op.forClass().createClass(casa).build();
		
		op.forGeneralization()
		  .createRelation()
		  .between(employeeKlass.get("id"))
		  .and(managerKlass.get("id")).build();
		
		Architecture arch = givenAArchitecture2("generalization1");
		
		assertEquals(1,arch.getRelationshipHolder().getAllGeneralizations().size());
		GeneralizationRelationship g = arch.getRelationshipHolder().getAllGeneralizations().get(0);
		assertEquals("Casa",g.getParent().getName());
		assertEquals("Employee",g.getChild().getName());
	}
	
	@Test
	public void shouldCreateGeneralizationBetweenIntefaceAndClass() throws Exception{
		String path = "herancaInterfaceClass/";
		Architecture arch = givenAArchitecture(path+"herancaInterfaceClass");
		
		GenerateArchitecture generateArch = new GenerateArchitecture();
		generateArch.generate(arch, "xpto");
		
		Architecture arch2 = givenAArchitecture2("xpto");
		
		assertEquals(1,arch2.getRelationshipHolder().getAllGeneralizations().size());
		
		GeneralizationRelationship g = arch.getRelationshipHolder().getAllGeneralizations().get(0);
		
		assertEquals("Class2",g.getParent().getName());
		assertEquals("Class1",g.getChild().getName());
		assertTrue(g.getChild() instanceof Interface);
		assertTrue(g.getParent() instanceof Class);
	}
	
	
	@Test
	public void shouldCreateGeneralization2() throws Exception{
		DocumentManager doc = givenADocument("generalization2");
		Operations op = new Operations(doc,null);
		
		Map<String, String> postInfo = op.forClass().createClass(post).build();
		Map<String, String> commentInfo = op.forClass().createClass(comment).build();
		Map<String, String> userInfo = op.forClass().createClass(user).build();
		
		op.forGeneralization()
		  .createRelation()
		  .between(postInfo.get("id"))
		  .and(commentInfo.get("id")).build();
		
		op.forGeneralization()
		  .createRelation()
		  .between(userInfo.get("id"))
		  .and(commentInfo.get("id")).build();
		
		Architecture arch = givenAArchitecture2("generalization2");
		
		assertEquals(2,arch.getRelationshipHolder().getAllGeneralizations().size());
		GeneralizationRelationship g = arch.getRelationshipHolder().getAllGeneralizations().get(0);
		assertEquals("Comment", g.getParent().getName());
		
		assertEquals(2, g.getAllChildrenForGeneralClass().size());
		assertContains(g.getAllChildrenForGeneralClass(), "Post",  "User");
	}
	
	@Test
	public void shouldCreateGeneralizationClassPackageClass() throws Exception{
		DocumentManager doc = givenADocument("generalization3");
		Operations op = new Operations(doc,null);
		

		String postClass = op.forClass().createClass(post).build().get("id");
		String categoryClass = op.forClass().createClass(category).build().get("id");
		op.forPackage().createPacakge(pacote).withClass(postClass).build();
		
		op.forGeneralization()
		  .createRelation()
		  .between(postClass).and(categoryClass).build();
	}
	
	@Test(expected=NotSuppportedOperation.class)
	public void shouldNotAllowGeneralizationBetweenPackages() throws Exception{
		DocumentManager doc = givenADocument("generalizationPacotes");
		Operations op = new Operations(doc,null);
		
		String id1 = op.forPackage().createPacakge(pacote).build().get("packageId");
		String id2 = op.forPackage().createPacakge(pacote2).build().get("packageId");
			
		op.forGeneralization().createRelation().between(id1).and(id2);
	}
	
	@Test(expected=NotSuppportedOperation.class)
	public void shouldNotAllowGeneralizationBetweenPackages2() throws Exception{
		DocumentManager doc = givenADocument("generalizationPacotes2");
		Operations op = new Operations(doc, null);
		
		String id1 = op.forPackage().createPacakge(pacote).build().get("packageId");
		String postClass = op.forClass().createClass(post).build().get("id");
			
		op.forGeneralization().createRelation().between(postClass).and(id1).build();
	}
	
	@Test
	public void testOperationsGeneralization() throws Exception{
		Architecture a = givenAArchitecture("operationsTests");
		
		arquitetura.representation.Class klass1 = a.findClassByName("Class1").get(0);
		arquitetura.representation.Class klass2 = a.findClassByName("Class2").get(0);
		arquitetura.representation.Class klass3 = a.findClassByName("Class3").get(0);
		
		assertEquals(1,klass1.getAllMethods().size());
		assertEquals(1,klass2.getAllMethods().size());
		assertEquals(2,klass3.getAllMethods().size());
	}
	
	@Test
	public void testOperationsGeneralizationInterface() throws Exception{
		Architecture a = givenAArchitecture("operationsGeneralizationInterface/interfaceGeneralizationInterface");
		
		arquitetura.representation.Interface klass2 = a.findInterfaceByName("Class2");
		
		assertEquals(1, klass2.getOperations().size());
	}

}