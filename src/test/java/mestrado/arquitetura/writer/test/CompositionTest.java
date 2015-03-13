package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.exceptions.CustonTypeNotFound;
import arquitetura.exceptions.InvalidMultiplictyForAssociationException;
import arquitetura.exceptions.NodeNotFound;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class CompositionTest extends TestHelper {
	
	private Element bar;
	private Element foo;
	private Element classeDois;
	private Element classeUm;
	
	@Before
	public void setUp() throws Exception{
		
		bar = Mockito.mock(Class.class);
		Mockito.when(bar.getName()).thenReturn("Bar");
		Mockito.when(bar.getId()).thenReturn("01010101001001001");
		
		foo = Mockito.mock(Class.class);
		Mockito.when(foo.getName()).thenReturn("Foo");
		Mockito.when(foo.getId()).thenReturn("199339390");
		
		
		classeUm = Mockito.mock(Class.class);
		Mockito.when(classeUm.getName()).thenReturn("ClasseUm");
		Mockito.when(classeUm.getId()).thenReturn("0101010101231201001001");
		
		classeDois = Mockito.mock(Class.class);
		Mockito.when(classeDois.getName()).thenReturn("ClasseDois");
		Mockito.when(classeDois.getId()).thenReturn("19933939023232");
	}
	
	
	
	@Test
	public void compositionGenerate1() throws Exception{
		DocumentManager doc = givenADocument("composicao1Gerada");
		
		Architecture a = givenAArchitecture("compositions/composicao1");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateComposition(a, op);
	
		Architecture saida = givenAArchitecture2("composicao1Gerada");
		assertEquals(1,saida.getRelationshipHolder().getAllCompositions().size());
		
		AssociationRelationship comp = saida.getRelationshipHolder().getAllCompositions().get(0);
		assertTrue(comp.getParticipants().get(0).isNavigable());
		assertFalse(comp.getParticipants().get(1).isNavigable());
	}
	
	@Test
	public void compositionGenerateComMultiplicidade() throws Exception{
		DocumentManager doc = givenADocument("composicaoComMultiplicidade1Gerada");
		
		Architecture a = givenAArchitecture("compositions/composicaoComMultiplicidade1");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		generateComposition(a, op);
	
		Architecture saida = givenAArchitecture2("composicaoComMultiplicidade1Gerada");
		assertEquals(1,saida.getRelationshipHolder().getAllCompositions().size());
		
		AssociationRelationship comp = saida.getRelationshipHolder().getAllCompositions().get(0);
		assertFalse(comp.getParticipants().get(0).isNavigable());
		assertFalse(comp.getParticipants().get(1).isNavigable());
	}



	private void generateComposition(Architecture a, Operations op) throws CustonTypeNotFound, NodeNotFound, InvalidMultiplictyForAssociationException {
		for(AssociationRelationship r : a.getRelationshipHolder().getAllCompositions()){
			AssociationEnd p1 = r.getParticipants().get(0);
			AssociationEnd p2 = r.getParticipants().get(1);
			if(p1.isComposite()){
				op.forComposition().createComposition()
								   .between(p1)
								   .and(p2)
								   .build();
			}else if(p2.isComposite()){
				op.forComposition().createComposition()
				   .between(p2)
				   .and(p1)
				  .build();
			}
		}
	}
	
	
	
	
	
	
	
	
//	@Test
//	public void shouldCreateComposition() throws Exception{
//		DocumentManager doc = givenADocument("Composicao");
//		
//		Operations op = new Operations(doc,null);
//		
//		String barKlass =  op.forClass().createClass(foo).build().get("id");
//		String fooKlass = op.forClass().createClass(bar).build().get("id");
//		
//		op.forComposition().createComposition().between(barKlass).and(fooKlass).build();
//		Architecture a = givenAArchitecture2("Composicao");
//		
//		assertEquals(1,a.getAllAssociations().size());
//		
//		AssociationRelationship composition = a.getAllAssociations().get(0);
//		assertEquals(2,composition.getParticipants().size());
//		
//		assertEquals("Composition",composition.getParticipants().get(0).getAggregation());
//		assertEquals("1",composition.getParticipants().get(0).getMultiplicity().toString());
//	}
	
//	@Test
//	public void shouldCreateCompositionWithMultiplicy() throws Exception{
//		DocumentManager doc = givenADocument("ComposicaoComMultiplicidade");
//		Operations op = new Operations(doc,null);
//		
//		String classUm =  op.forClass().createClass(classeUm).build().get("id");
//		String classDois = op.forClass().createClass(classeDois).build().get("id");
//		
//		op.forComposition().createComposition().between(classUm).withMultiplicy("1..*").and(classDois).withMultiplicy("0..1").build();
//		
//		Architecture a = givenAArchitecture2("ComposicaoComMultiplicidade");
//		AssociationRelationship decom = a.getAllAssociations().get(0);
//		assertNotNull(decom);
//		
//		assertEquals(2,decom.getParticipants().size());
//		assertEquals("ClasseUm",decom.getParticipants().get(0).getCLSClass().getName());
//		assertEquals("1..*",decom.getParticipants().get(0).getMultiplicity().toString());
//		assertEquals("ClasseDois",decom.getParticipants().get(1).getCLSClass().getName());
//		assertEquals("0..1",decom.getParticipants().get(1).getMultiplicity().toString());
//	}
	
//	@Test
//	public void shouldCreateDependencyClassPackageClass() throws Exception{
//		DocumentManager doc = givenADocument("composicaoPacote");
//		Operations op = new Operations(doc, null);
//		
//		String classUm =  op.forClass().createClass(classeUm).build().get("id");
//		String classDois = op.forClass().createClass(classeDois).build().get("id");
//		
//		Package pacote = Mockito.mock(Package.class);
//		Mockito.when(pacote.getName()).thenReturn("meu.com.pacote");
//		Mockito.when(pacote.getId()).thenReturn("12312312312");
//		
//		op.forPackage().createPacakge(pacote).withClass(classUm).build();
//		op.forComposition().createComposition().between(classDois).and(classUm).build();
//		
//		Architecture a = givenAArchitecture2("composicaoPacote");
//		AssociationRelationship decom = a.getAllAssociations().get(0);
//		assertNotNull(decom);
//		
//		assertEquals(2, decom.getParticipants().size());
//		
	//}
}
