package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Element;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.Relationship;

public class AssociationsTest extends TestHelper {
	
	@Test
	public void shouldHaveTwoAssociations() throws Exception {
		Architecture a = givenAArchitecture("association");
		assertEquals("Architecture should contain 2 associations", 2,	a.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void testAssociationEndNavegability() throws Exception{
		Architecture a = givenAArchitecture("associations/nevegability");
		
		List<AssociationRelationship> associations = a.getRelationshipHolder().getAllAssociationsRelationships();
		AssociationRelationship association = associations.iterator().next();
		List<AssociationEnd> parts = association.getParticipants();
		
		assertFalse(parts.get(0).isNavigable());
		assertTrue(parts.get(1).isNavigable());
	}
	
	@Test
	public void testAssociations() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");

		List<AssociationRelationship> r = architecture2.getRelationshipHolder().getAllAssociationsRelationships();
		assertNotNull(r);

		AssociationRelationship association = r.iterator().next();
		List<AssociationEnd> participants = association.getParticipants();

		assertEquals(2, participants.size());

		assertNotNull(association.getParticipants());
		assertEquals("none", association.getParticipants().get(0).getAggregation());
		assertFalse(association.getParticipants().get(0).isNavigable());
		assertEquals("Class2", participants.get(0).getCLSClass().getName());

		assertEquals("none", association.getParticipants().get(1).getAggregation());
		assertTrue(association.getParticipants().get(1).isNavigable());
		assertEquals("Class1", participants.get(1).getCLSClass().getName());
	}
	
	@Test
	public void testAssociation2() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");
		
		
		
		AssociationRelationship association = null;
		
		Iterator<AssociationRelationship> iter = architecture2.getRelationshipHolder().getAllAssociationsRelationships().iterator();
		while (iter.hasNext()) {
			iter.next();
			association = iter.next();
		}
		List<AssociationEnd> participants = association.getParticipants();

		assertNotNull(association);

		assertEquals(2, participants.size());

		assertEquals("none", association.getParticipants().get(0).getAggregation());
		assertTrue(association.getParticipants().get(0).isNavigable());
		assertEquals("Class3", participants.get(0).getCLSClass().getName());

		assertEquals("none", association.getParticipants().get(1).getAggregation());
		assertFalse(association.getParticipants().get(1).isNavigable());
		assertEquals("Class4", participants.get(1).getCLSClass().getName());
	}
	

	@Test
	public void testMultiplicityAssociationRelationship() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");
		AssociationRelationship association = null;
		
		Iterator<AssociationRelationship> iter = architecture2.getRelationshipHolder().getAllAssociationsRelationships().iterator();
		while (iter.hasNext()) {
			iter.next();
			association = iter.next();
		}
		assertEquals("1", association.getParticipants().get(1).getMultiplicity().getLowerValue());
		assertEquals("1", association.getParticipants().get(1).getMultiplicity().getUpperValue());
		assertEquals("1", association.getParticipants().get(1).getMultiplicity().toString());
	}

	@Test
	public void testMultiplicityAssociationRelationship2() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");
		AssociationRelationship association = architecture2.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();

		assertEquals("1", association.getParticipants().get(0).getMultiplicity().getLowerValue());
		assertEquals("1", association.getParticipants().get(0).getMultiplicity().getUpperValue());
		assertEquals("1", association.getParticipants().get(0).getMultiplicity().toString());
	}
	
	@Test
	public void shouldContainCompositeAssociation() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");
		AssociationRelationship associationComposite = architecture2.getRelationshipHolder().getAllCompositions().iterator().next();
		List<AssociationEnd> participants = associationComposite.getParticipants();

		assertFalse(associationComposite.getParticipants().get(0).isNavigable());
		assertEquals("Class5", participants.get(0).getCLSClass().getName());

		assertEquals("composite", associationComposite.getParticipants().get(1).getAggregation());
		assertFalse(associationComposite.getParticipants().get(1).isNavigable());
		assertEquals("Class6", participants.get(1).getCLSClass().getName());
		assertEquals("none", associationComposite.getParticipants().get(0).getAggregation());

		assertEquals("0..*", associationComposite.getParticipants().get(0).getMultiplicity().toString());
		assertEquals("1", associationComposite.getParticipants().get(1).getMultiplicity().toString());
	}
	
	@Test
	public void shouldContainAggregationAssociation() throws Exception {
		Architecture architecture2 = givenAArchitecture("association");
		AssociationRelationship aggregation = architecture2.getRelationshipHolder().getAllAgragations().iterator().next();
		List<AssociationEnd> participants = aggregation.getParticipants();

		assertFalse(aggregation.getParticipants().get(0).isNavigable());
		assertEquals("Class7", participants.get(0).getCLSClass().getName());

		assertFalse(aggregation.getParticipants().get(1).isNavigable());
		assertEquals("Class8", participants.get(1).getCLSClass().getName());

		assertEquals("shared", aggregation.getParticipants().get(0).getAggregation());
		assertFalse(aggregation.getParticipants().get(1).isNavigable());

		assertEquals("1", aggregation.getParticipants().get(1).getMultiplicity().toString());
		assertEquals("1..*", aggregation.getParticipants().get(0).getMultiplicity().toString());
	}
	
	@Test
	public void testAssociationWithThreeClasses() throws Exception {
		Architecture architecture3 = givenAArchitecture("complexAssociation");
		Set<Relationship> r = architecture3.getRelationshipHolder().getAllRelationships();

		assertNotNull(architecture3);
		assertEquals("Should Contains Two Relationships", 2, r.size());
		assertEquals("Should Contains Three Classes", 3, architecture3.getClasses().size());

		AssociationRelationship association1 = null;
		AssociationRelationship association2 = null;
		
		architecture3.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();
		
		Iterator<AssociationRelationship> iter = architecture3.getRelationshipHolder().getAllAssociationsRelationships().iterator();
		while (iter.hasNext()) {
		  association1 = iter.next();
		  association2 = iter.next();
		}

		assertNotNull(association1);
		assertNotNull(association2);

		assertEquals(2, association1.getParticipants().size());
		assertEquals(2, association2.getParticipants().size());

		List<AssociationEnd> a = association1.getParticipants();
		List<AssociationEnd> b = association2.getParticipants();
		Element klass3 = a.get(0).getCLSClass();
		Element klass2 = a.get(1).getCLSClass();

		Element klass1 = b.get(1).getCLSClass();
		Element kllass2a = b.get(0).getCLSClass();

		assertEquals("Class2", klass1.getName());
		assertEquals("Class3", kllass2a.getName());
		assertEquals("Class2", klass3.getName());
		assertEquals("Class1", klass2.getName());
		assertTrue(a.get(1).isNavigable());
		assertFalse(a.get(0).isNavigable());
	}
	
	
	@Test
	public void test() throws Exception{
		Architecture a = givenAArchitecture("associacoa1");
		
		assertEquals(1, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		
		AssociationRelationship as = a.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();
		
		assertEquals(2, as.getParticipants().size());
		assertEquals("Class2", as.getParticipants().get(0).getCLSClass().getName());
		assertEquals("1..*", as.getParticipants().get(0).getMultiplicity().toString());
		assertEquals("0..1", as.getParticipants().get(1).getMultiplicity().toString());
	}

}
