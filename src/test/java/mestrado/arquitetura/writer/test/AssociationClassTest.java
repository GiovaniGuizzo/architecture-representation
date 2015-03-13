package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.relationship.AssociationClassRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class AssociationClassTest extends TestHelper {
	
	@Test
	public void testLoadAssociationClass() throws Exception{
		Architecture a = givenAArchitecture("associationClass/associationClass");
		
		List<AssociationClassRelationship> associationClasses = a.getRelationshipHolder().getAllAssociationsClass();
		
		assertEquals(1, associationClasses.size());
		assertEquals(2, associationClasses.get(0).getMemebersEnd().size());
	}
	
	@Test
	public void associationClassShouldBelongsToPackage() throws Exception{
		Architecture a = givenAArchitecture("associationClass/associationClassComPacote");
		assertNotNull(a.getRelationshipHolder().getAllAssociationsClass().get(0).getPackageOwner());
		
	}
	
	@Test
	public void associationClass1() throws Exception {
		DocumentManager doc = givenADocument("associationClassGerado");
		
		Architecture a = givenAArchitecture("associationClass/associationClass");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		
		for(AssociationClassRelationship asr : a.getRelationshipHolder().getAllAssociationsClass()){
			op.forAssociationClass()
			  .createAssociationClass(asr)
			  .build();
		}

		Architecture genereted = givenAArchitecture2("associationClassGerado");
		assertNotNull(genereted);
		assertEquals(3, genereted.getRelationshipHolder().getAllAssociationsClass().get(0).getAllAttributes().size());
	}
	
	@Test
	public void associationClass2() throws Exception {
		DocumentManager doc = givenADocument("associationClass2Gerado");
		
		Architecture a = givenAArchitecture("associationClass/associationClass2");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		
		
		for(AssociationClassRelationship asr : a.getRelationshipHolder().getAllAssociationsClass()){
			op.forAssociationClass()
			  .createAssociationClass(asr)
			  .build();
		}

		Architecture genereted = givenAArchitecture2("associationClass2Gerado");
		assertEquals(1, genereted.getRelationshipHolder().getAllAssociationsClass().size());
		
	}
	
	@Test
	public void associationClass3() throws Exception {
		DocumentManager doc = givenADocument("associationClassComPacoteGerado");
		
		Architecture a = givenAArchitecture("associationClass/associationClassComPacote");
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationClassComPacoteGerado");
	
		Architecture genereted = givenAArchitecture2("associationClassComPacoteGerado");
		assertNotNull(genereted);
	}
	
	@Test
	public void comAttrEMethod() throws Exception{
		Architecture a = givenAArchitecture("associationClass/associationClassWithAttrAndMethod");

		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationClassWithAttrAndMethodGerado");
		
		Architecture ar = givenAArchitecture2("associationClassWithAttrAndMethodGerado");
		
		assertEquals("deve ter somente duas classes", 2, ar.getClasses().size());
		
		assertEquals(1 ,ar.getRelationshipHolder().getAllAssociationsClass().size());
		assertEquals(3, ar.getRelationshipHolder().getAllAssociationsClass().get(0).getAssociationClass().getAllAttributes().size());
		assertEquals(1, ar.getRelationshipHolder().getAllAssociationsClass().get(0).getAssociationClass().getAllMethods().size());
	}

}
