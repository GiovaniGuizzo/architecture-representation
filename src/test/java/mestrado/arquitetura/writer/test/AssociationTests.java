package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class AssociationTests  extends TestHelper {
	
	

	@Test
	public void shouldGenerateAssociationWithNevegabilityFalse() throws Exception {
		DocumentManager doc = givenADocument("nevegabilityGerado");
		
		Architecture a = givenAArchitecture("associations/nevegability");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		
		
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("nevegabilityGerado");
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		assertFalse(asoci.get(0).getParticipants().get(0).isNavigable());
		assertTrue(asoci.get(0).getParticipants().get(1).isNavigable());
	}

	private void generateAssociations(Architecture a, Operations op) {
		for(AssociationRelationship as : a.getRelationshipHolder().getAllAssociationsRelationships()){
			op.forAssociation().createAssociation()
							   .withName(as.getName())
							   .betweenClass(as.getParticipants().get(0))
							   .andClass(as.getParticipants().get(1)).build();
		}
	}

	
	@Test
	public void shouldGenerateAssociationWithBothNevegability() throws Exception {
		DocumentManager doc = givenADocument("nomeAssociacaoBothDirectionGerada");
		
		Architecture a = givenAArchitecture("associations/nomeAssociacaoBothDirection");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("nomeAssociacaoBothDirectionGerada");
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		
		assertEquals("class1_class2_1", asoci.get(0).getName());
		AssociationEnd associationEnd1 = asoci.get(0).getParticipants().get(1);
		AssociationEnd associationEnd2 = asoci.get(0).getParticipants().get(0);
		assertBothAssociationEndIsTrue(associationEnd1, associationEnd2);
	}
	
	@Test
	public void shouldGenerateAssociationWithNevegabilityTrueBothDirection() throws Exception {
		DocumentManager doc = givenADocument("nevegability_one_directionGerada");
		
		Architecture a = givenAArchitecture("associations/nevegability_one_direction");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("nevegability_one_directionGerada");
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		assertEquals("Edipo", asoci.get(0).getName());
		assertTrue(asoci.get(0).getParticipants().get(1).isNavigable());
		assertFalse(asoci.get(0).getParticipants().get(0).isNavigable());
	}
	
	@Test
	public void withMultiplicity() throws Exception{
		DocumentManager doc = givenADocument("associationWithMultiplicityGerada");
		
		Architecture a = givenAArchitecture("associations/associationWithMultiplicity");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("associationWithMultiplicityGerada");
		
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		
		assertEquals("class1_class2_1", asoci.get(0).getName());
		AssociationEnd associationEnd1 = asoci.get(0).getParticipants().get(1);
		AssociationEnd associationEnd2 = asoci.get(0).getParticipants().get(0);
		
		assertBothAssociationEndIsFalse(associationEnd1,associationEnd2);
		
		assertEquals("1..*", associationEnd1.getMultiplicity().toString());
		assertEquals("1..*", associationEnd2.getMultiplicity().toString());
	}
	
	@Test
	public void withMultiplicity2() throws Exception{
		DocumentManager doc = givenADocument("associationWithMultiplicityGerada2");
		
		Architecture a = givenAArchitecture("associations/associationWithMultiplicity2");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("associationWithMultiplicityGerada2");
		
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		
		assertEquals("class1_class2_1", asoci.get(0).getName());
		AssociationEnd associationEnd1 = asoci.get(0).getParticipants().get(1);
		AssociationEnd associationEnd2 = asoci.get(0).getParticipants().get(0);
		
		assertEquals("1..*", associationEnd2.getMultiplicity().toString());
		assertEquals("1", associationEnd1.getMultiplicity().toString());
	}
	
	@Test
	public void withMultiplicity3() throws Exception{
		DocumentManager doc = givenADocument("associationWithMultiplicity3Gerado");
		
		Architecture a = givenAArchitecture("associations/associationWithMultiplicity3");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateAssociations(a, op);
		
		Architecture genereted = givenAArchitecture2("associationWithMultiplicity3Gerado");
		
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		
		assertEquals("class1_class2_1", asoci.get(0).getName());
		AssociationEnd associationEnd1 = asoci.get(0).getParticipants().get(1);
		AssociationEnd associationEnd2 = asoci.get(0).getParticipants().get(0);
		
		assertBothAssociationEndIsTrue(associationEnd1, associationEnd2);
		
		assertEquals("1..*", associationEnd2.getMultiplicity().toString());
		assertEquals("0..*", associationEnd1.getMultiplicity().toString());
	}

}