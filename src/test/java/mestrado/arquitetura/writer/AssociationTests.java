package mestrado.arquitetura.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import mestrado.arquitetura.exceptions.InvalidMultiplictyForAssociationException;
import mestrado.arquitetura.helpers.test.TestHelper;
import mestrado.arquitetura.parser.AssociationOperations;
import mestrado.arquitetura.parser.ClassOperations;
import mestrado.arquitetura.parser.DocumentManager;
import mestrado.arquitetura.representation.Architecture;
import mestrado.arquitetura.representation.relationship.AssociationRelationship;

import org.junit.Test;

public class AssociationTests  extends TestHelper {
	
		
	@Test(expected=InvalidMultiplictyForAssociationException.class)
	public void shouldNotCreateAssociationBetweenClassesWhenLowerIsMany() throws Exception{
		DocumentManager doc = givenADocument("testeNovaAssociacaoInvlida", "simples");
		AssociationOperations associationOperations = new AssociationOperations(doc);
		ClassOperations classOperations = new ClassOperations(doc);
		
		Map<String, String> employee = classOperations.createClass("Employee").build();
		Map<String, String> manager = classOperations.createClass("Casa").build();
		
		associationOperations.createAssociation()
							 .betweenClass(employee.get("classId")).withMultiplicy("*..1")
							 .andClass(manager.get("classId"))
							 .build();
	}
	
	@Test
	public void shouldCreateAssociation() throws Exception{
		DocumentManager doc = givenADocument("testeNovaAssociacao", "simples");
		AssociationOperations associationOperations = new AssociationOperations(doc);
		ClassOperations classOperations = new ClassOperations(doc);
		
		Map<String, String> employee = classOperations.createClass("Employee").build();
		Map<String, String> casa = classOperations.createClass("Casa").build();
		
		associationOperations.createAssociation()
							 .betweenClass(employee.get("classId")).withMultiplicy("1..*")
							 .andClass(casa.get("classId")).withMultiplicy("0..1")
							 .build();
		
		Architecture a = givenAArchitecture2("testeNovaAssociacao");
		
		assertEquals(1,a.getAllAssociations().size());
		AssociationRelationship association = a.getAllAssociations().get(0);
		assertNotNull(association);
		assertEquals(2, association.getParticipants().size());
		
		assertEquals("Casa", association.getParticipants().get(0).getCLSClass().getName());
		assertEquals("0..1", association.getParticipants().get(0).getMultiplicity().toString());
		
		assertEquals("Employee", association.getParticipants().get(1).getCLSClass().getName());
		assertEquals("1..*", association.getParticipants().get(1).getMultiplicity().toString());
	}
}