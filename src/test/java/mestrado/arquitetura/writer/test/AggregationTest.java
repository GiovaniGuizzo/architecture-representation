package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.exceptions.NotSuppportedOperation;
import arquitetura.representation.Architecture;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

public class AggregationTest extends TestHelper {
	
	@Test
	public void agragacao1() throws Exception {
		DocumentManager doc = givenADocument("agregacaoGerada");
		
		Architecture a = givenAArchitecture("agregacao/agregacao1");
		Operations op = new Operations(doc, a);
		
		generateClasses(a, op);
		for(AssociationRelationship asc : a.getRelationshipHolder().getAllAgragations()){
			generateAggregation(op, asc);
		}
		
		Architecture genereted = givenAArchitecture2("agregacaoGerada");
		
		List<AssociationRelationship> asoci = genereted.getRelationshipHolder().getAllAgragations();
		
		assertEquals("class1_class2_1", asoci.get(0).getName());
		AssociationEnd associationEnd1 = asoci.get(0).getParticipants().get(1);
		AssociationEnd associationEnd2 = asoci.get(0).getParticipants().get(0);
		
		assertBothAssociationEndIsFalse(associationEnd1, associationEnd2);
		
		assertEquals("1..*", associationEnd2.getMultiplicity().toString());
		assertEquals("1", associationEnd1.getMultiplicity().toString());
	}	
	
	
	private static void generateAggregation(Operations op,	AssociationRelationship r) throws NotSuppportedOperation {
		AssociationEnd p1 = r.getParticipants().get(0);
		AssociationEnd p2 = r.getParticipants().get(1);
		
		if(p1.isAggregation()){
			op.forAggregation().createRelation().withName(r.getName()).between(p1).and(p2).build();
		}else if(p2.isAggregation()){
			op.forAggregation().createRelation().withName(r.getName()).between(p2).and(p1).build();
		}
	}
	
//	@Before
//	public void setUp() throws Exception{
//		
//		bar = Mockito.mock(Class.class);
//		Mockito.when(bar.getName()).thenReturn("Bar");
//		Mockito.when(bar.getId()).thenReturn("01010101001001001");
//		
//		foo = Mockito.mock(Class.class);
//		Mockito.when(foo.getName()).thenReturn("Foo");
//		Mockito.when(foo.getId()).thenReturn("199339390");
//	}
//	
//	@Test
//	public void shouldAggregationHaveAName() throws Exception {
//		DocumentManager doc = givenADocument("AggregationWithName");
//		Operations op = new Operations(doc,null);
//		
//		String barKlass =  op.forClass().createClass(foo).build().get("id");
//		String fooKlass = op.forClass().createClass(bar).build().get("id");
//		
//		op.forAggregation().createRelation().withName("My Aggregation").between(barKlass).and(fooKlass).build();
//		
//		Architecture a = givenAArchitecture2("AggregationWithName");
//		AssociationRelationship ag = a.getAllAssociations().get(0);
//		
//		assertNotNull(ag);
//		assertEquals("My Aggregation", ag.getName());
//	}
//	
//	@Test
//	public void shouldAggregationHaveDefaultNameIfBlank() throws Exception{
//		DocumentManager doc = givenADocument("AggregationWithName2");
//		Operations op = new Operations(doc,null);
//		
//		AssociationEnd barKlass =  op.forClass().createClass(foo).build().get("id");
//		String fooKlass = op.forClass().createClass(bar).build().get("id");
//		
//		op.forAggregation().createRelation().withName("").between(barKlass).and(fooKlass).build();
//		
//		Architecture a = givenAArchitecture2("AggregationWithName2");
//		AssociationRelationship ag = a.getAllAssociations().get(0);
//		
//		assertNotNull(ag);
//		assertEquals("", ag.getName());
//	}
//	
//	@Test
//	public void shouldCreateAggregationClassClass() throws Exception{
//		DocumentManager doc = givenADocument("shared");
//		Operations op = new Operations(doc,null);
//		
//		String barKlass =  op.forClass().createClass(foo).build().get("id");
//		String fooKlass = op.forClass().createClass(bar).build().get("id");
//		
//		op.forAggregation().createRelation().withName("name").between(barKlass).withMultiplicy("1..*").and(fooKlass).build();
//		
//		Architecture arch = givenAArchitecture2("shared");
//		
//		assertEquals(1, arch.getAllAssociations().size());
//		AssociationRelationship a = arch.getAllAssociations().get(0);
//		assertEquals("Aggregation", a.getParticipants().get(0).getAggregation());
//		assertEquals("Foo", a.getParticipants().get(0).getCLSClass().getName());
//		assertEquals("1..*", a.getParticipants().get(0).getMultiplicity().toString());
//		
//		assertEquals("none",a.getParticipants().get(1).getAggregation());
//		assertEquals("1", a.getParticipants().get(1).getMultiplicity().toString());
//	}
//	
//	@Test
//	public void shouldCreateAggregationAssociationClassInsidePackageAndClassOutsidePackagee() throws Exception{
//		
//		DocumentManager doc = givenADocument("sharedClassPackageClass");
//		Operations op = new Operations(doc,null);
//		
//		arquitetura.representation.Package models = Mockito.mock(arquitetura.representation.Package.class);
//		Mockito.when(models.getName()).thenReturn("models");
//		Mockito.when(models.getId()).thenReturn("6666666");
//		
//		String barKlass =  op.forClass().createClass(foo).build().get("id");
//
//		op.forPackage().createPacakge(models).withClass(foo.getId()).build();
//		String fooKlass = op.forClass().createClass(bar).build().get("id");
//		
//		op.forAggregation().createRelation().withName("name").between(barKlass).withMultiplicy("1..*").and(fooKlass).build();
//		
//	}
	
}