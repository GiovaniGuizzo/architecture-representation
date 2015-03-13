package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Test;

import arquitetura.representation.Architecture;
import arquitetura.representation.Attribute;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Method;
import arquitetura.representation.relationship.AbstractionRelationship;
import arquitetura.representation.relationship.AssociationClassRelationship;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.RealizationRelationship;
import arquitetura.representation.relationship.UsageRelationship;

import com.google.common.collect.Lists;

public class OperationsOverRelationshipsTest extends TestHelper {

	@Test
	public void removeAssociation() throws Exception {
		Architecture a = givenAArchitecture("association");
		
		a.operationsOverRelationship().removeAssociationRelationship(a.getRelationshipHolder().getAllAssociationsRelationships().iterator().next());
		
		GenerateArchitecture g = new GenerateArchitecture();
		
		g.generate(a, "removendoAssociacao");
		
		Architecture genereted = givenAArchitecture2("removendoAssociacao");
		assertEquals(1, genereted.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void moveEntireAssociation() throws Exception{
		Architecture a = givenAArchitecture("association");
		AssociationRelationship association = a.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();
		Class idclass6 = a.findClassByName("Class6").get(0);
		Class idclass8 = a.findClassByName("Class8").get(0);
		
		a.operationsOverRelationship().moveAssociation(association,idclass6, idclass8);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "removendoAssociacaoMove");
		
		Architecture genereted = givenAArchitecture2("removendoAssociacaoMove");
		assertEquals("Agora deve a classe deve ter 2 relacionamentos", 2, genereted.findClassByName("Class6").get(0).getRelationships().size());
	}
	
	@Test
	public void moveMemberEndOfAssociation() throws Exception{
		Architecture a = givenAArchitecture("association");
		AssociationRelationship association = a.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();
		Class idclass8 = a.findClassByName("Class6").get(0);

		a.operationsOverRelationship().moveAssociationEnd(association.getParticipants().get(1), idclass8);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoPartOfAssociation");
		
		Architecture genereted = givenAArchitecture2("movendoPartOfAssociation");
		assertEquals("Agora deve a classe deve ter 2 relacionamentos", 2, genereted.findClassByName("Class6").get(0).getRelationships().size());
	}
	
	
	@Test
	public void shouldCreateANewAssociationWithBothAssociationEndNavigable() throws Exception {
		Architecture a = givenAArchitecture("association");
		
		Class class1 = a.findClassByName("Class1").get(0);
		Class class2 = a.findClassByName("Class8").get(0);
		
		
		a.forAssociation().createAssociationEnd()
					      .withKlass(class1)
					      .withMultiplicity("1..*")
					      .navigable(true)
					      .and()
					      .createAssociationEnd()
					      .withKlass(class2)
					      .withMultiplicity("1..1")
					      .navigable(true).build();
					      
		
		assertEquals(3, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationAddNova");
		
		Architecture genereted = givenAArchitecture2("associationAddNova");
		List<AssociationRelationship> associations = genereted.getRelationshipHolder().getAllAssociationsRelationships();
		
		assertEquals(3, associations.size());
		
		ArrayList<AssociationRelationship> listASsociations = Lists.newArrayList(associations) ;
		
		AssociationRelationship as = getAssociationForTest(listASsociations);
		
		for(AssociationEnd p : as.getParticipants()){
			if(p.getCLSClass().getName().equals("Class1")){
				assertTrue(p.isNavigable());
			}
			if(p.getCLSClass().getName().equals("Class8")){
				assertTrue(p.isNavigable());
			}
		}
		
		
	}

	private AssociationRelationship getAssociationForTest(ArrayList<AssociationRelationship> listASsociations) {
		for(AssociationRelationship r : listASsociations)
			for(AssociationEnd ae : r.getParticipants())
				if((ae.getCLSClass().getName().equals("Class1")) || (ae.getCLSClass().getName().equals("Class8")))
					return r;
		
		return null;
	}
	
	
	/* Fim testes associacao */
	
	
	
	/* Começo testes Dependencia */
	
	@Test
	public void shouldCreateNewDependency() throws Exception{
		Architecture a = givenAArchitecture("association");
		
		Class class1 = a.findClassByName("Class1").get(0);
		Class class2 = a.findClassByName("Class8").get(0);
		
		a.forDependency().create("Dependencia nome")
						 .withClient(class1)
						 .withSupplier(class2).build();
		
		assertEquals(1,a.getRelationshipHolder().getAllDependencies().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "dependenciaNova");
		
		Architecture genereted = givenAArchitecture2("dependenciaNova");
		DependencyRelationship dependency = genereted.getRelationshipHolder().getAllDependencies().iterator().next();
		
		assertEquals("Class1",dependency.getClient().getName());
		assertEquals("Class8",dependency.getSupplier().getName());
		assertEquals("Dependencia nome",dependency.getName());
	}
	
	
	@Test
	public void movendoEntireDependency() throws Exception{
		Architecture a = givenAArchitecture("dependenciaMovendo");
		
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().iterator().next();
		
		Class idclass6 = a.findClassByName("Class4").get(0);
		Class idclass8 = a.findClassByName("Class3").get(0);
		
		a.operationsOverRelationship().moveDependency(dependency, idclass8,idclass6);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoDependencia");
		Architecture genereted = givenAArchitecture2("movendoDependencia");
		
		DependencyRelationship dependency2 = genereted.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Class4", dependency2.getSupplier().getName());
		assertEquals("Class3", dependency2.getClient().getName());
	}
	
	@Test
	public void movendoClientDependency() throws Exception{
		Architecture a = givenAArchitecture("dependenciaMovendo");
		
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Class1",dependency.getClient().getName());
		
		Class newClient = a.findClassByName("Class3").get(0);
		
		a.operationsOverRelationship().moveDependencyClient(dependency, newClient);
		
		assertEquals("Class3",dependency.getClient().getName());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoClientDependencia");
	}
	
	@Test
	public void movendoSupplierDependency() throws Exception{
		Architecture a = givenAArchitecture("dependenciaMovendo");
		
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Class2",dependency.getSupplier().getName());
		
		Class newSupplier = a.findClassByName("Class4").get(0);
		
		a.operationsOverRelationship().moveDependencySupplier(dependency, newSupplier);
		
		assertEquals("Class4",dependency.getSupplier().getName());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoSuppluerDependencia");
	}
	
	
	/* Composicao */
	
	@Test
	public void removendoComposicao() throws Exception{
		Architecture a = givenAArchitecture("compositions/composicao1");
		a.operationsOverRelationship().removeAssociationRelationship(a.getRelationshipHolder().getAllCompositions().iterator().next());  
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "composicao1Removida");
		Architecture genereted = givenAArchitecture2("composicao1Removida");
		
		assertEquals(0, genereted.getRelationshipHolder().getAllCompositions().size());
	}
	
	@Test
	public void addNewComposicao() throws Exception{
		Architecture a = givenAArchitecture("association");
		
		Class class1 = a.findClassByName("Class1").get(0);
		Class class2 = a.findClassByName("Class8").get(0);
		
		a.forAssociation()
		  .createAssociationEnd()
	      .withKlass(class1)
	      .withMultiplicity("1..*")
	      .asComposition()
	      .and()
	      .createAssociationEnd()
	      .withKlass(class2)
	      .withMultiplicity("1..1")
	      .build();
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "composicaoNova");
		Architecture genereted = givenAArchitecture2("composicaoNova");
		assertEquals(2,genereted.getRelationshipHolder().getAllCompositions().size());
	}
	
	@Test
	public void movendoEntireComposicao() throws Exception{
		Architecture a = givenAArchitecture("association");
		
		Class idclass6 = a.findClassByName("Class4").get(0);
		Class idclass8 = a.findClassByName("Class7").get(0);
		
		a.operationsOverRelationship().moveAssociation(a.getRelationshipHolder().getAllCompositions().iterator().next(), idclass6, idclass8);  
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoComposicao");
		Architecture genereted = givenAArchitecture2("movendoComposicao");
		
		assertEquals(1, genereted.getRelationshipHolder().getAllCompositions().size());
		assertEquals("Class7",genereted.getRelationshipHolder().getAllCompositions().iterator().next().getParticipants().get(0).getName());
		assertEquals("Class4",genereted.getRelationshipHolder().getAllCompositions().iterator().next().getParticipants().get(1).getName());
	}
	
	@Test
	public void movendoPartComposicao() throws Exception{
		Architecture a = givenAArchitecture("association");
		
		Class idclass6 = a.findClassByName("Class4").get(0);
		
		a.operationsOverRelationship().moveAssociationEnd(a.getRelationshipHolder().getAllCompositions().iterator().next().getParticipants().get(0), idclass6);  
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "movendoPartOfComposicao");
		Architecture genereted = givenAArchitecture2("movendoPartOfComposicao");
		
		assertEquals(1, genereted.getRelationshipHolder().getAllCompositions().size());
		assertEquals("Class6",genereted.getRelationshipHolder().getAllCompositions().iterator().next().getParticipants().get(0).getName());
		assertEquals("Class4",genereted.getRelationshipHolder().getAllCompositions().iterator().next().getParticipants().get(1).getName());
	}
	
	
	//AssociationClass
	@Test
	public void moverEntireAssociationClass() throws Exception{
		Architecture a = givenAArchitecture("associationClass/movendoAssociationCLass");
		
		AssociationClassRelationship asc = a.getRelationshipHolder().getAllAssociationsClass().iterator().next();
		
		assertEquals("Class1", asc.getMemebersEnd().get(0).getType().getName());
		assertEquals("Class2", asc.getMemebersEnd().get(1).getType().getName());
		assertEquals("Class1", asc.getOwnedEnd().getName());
		
		Class idclass4 = a.findClassByName("Class4").get(0);
		Class idclass3 = a.findClassByName("Class3").get(0);
		
		a.operationsOverRelationship().moveAssociationClass(asc,idclass3, idclass4);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationClassMove");
		
		Architecture genereted = givenAArchitecture2("associationClassMove");
		
		AssociationClassRelationship ascg = genereted.getRelationshipHolder().getAllAssociationsClass().iterator().next();
		assertEquals("Class4", ascg.getMemebersEnd().get(0).getType().getName());
		assertEquals("Class3", ascg.getMemebersEnd().get(1).getType().getName());
		assertEquals("Class3", ascg.getMemebersEnd().get(1).getType().getName());
	}
	
	@Test
	public void moverPartAssociationClass() throws Exception{
		Architecture a = givenAArchitecture("associationClass/movendoAssociationCLass");
		
		AssociationClassRelationship asc = a.getRelationshipHolder().getAllAssociationsClass().iterator().next();
		
		
		Class idclass3 = a.findClassByName("Class3").get(0);
		
		a.operationsOverRelationship().moveMemberEndOf(asc.getMemebersEnd().get(0),idclass3);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationClassMovePart");
		
		Architecture genereted = givenAArchitecture2("associationClassMovePart");
		
		AssociationClassRelationship ascg = genereted.getRelationshipHolder().getAllAssociationsClass().iterator().next();
		assertEquals("Class2", ascg.getMemebersEnd().get(0).getType().getName());
		assertEquals("Class3", ascg.getMemebersEnd().get(1).getType().getName());
		assertEquals("Class3", ascg.getOwnedEnd().getName());
	}
	
	@Test
	public void createNewAssociationClass() throws Exception{
		Architecture a = givenAArchitecture("associationClass/semAssociationClass");
		
		Set<Attribute> listAttrs = new HashSet<Attribute>();
		Set<Method> listMethods = new HashSet<Method>();
		
		Class owner = a.findClassByName("Class1").get(0);
		Class klass = a.findClassByName("Class2").get(0);
		
		assertEquals(0,owner.getRelationships().size());
		assertEquals(0,klass.getRelationships().size());
		
		a.forAssociation().createAssociationClass(listAttrs, listMethods, owner, klass, "ClasseAssociativa");
		
		assertEquals(1, a.getRelationshipHolder().getAllAssociationsClass().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "associationClassNovo");
		
		Architecture generated = givenAArchitecture2("associationClassNovo");
		
		assertEquals("Class2",generated.getRelationshipHolder().getAllAssociationsClass().iterator().next().getMemebersEnd().get(0).getType().getName());
		assertEquals("Class1",generated.getRelationshipHolder().getAllAssociationsClass().iterator().next().getMemebersEnd().get(1).getType().getName());
		
		assertEquals(1, owner.getRelationships().size());
		assertEquals(1, klass.getRelationships().size());
		
		assertEquals("Deve ter 1 associactionClass", 1, klass.getAllAssociationClass().size());
		
	}
	
	
	
	//Generalizacao
	
	@Test
	public void shouldMoveSuperClass() throws Exception{
		Architecture a = givenAArchitecture("generalizacaoMove");
		
		GeneralizationRelationship gene = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		
		assertNotNull(gene);
		assertEquals("Class1",gene.getParent().getName());
		
		a.forGeneralization().moveGeneralizationParent(gene, a.findClassByName("Class3").get(0));
		
		assertEquals("Class3",gene.getParent().getName());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "generalizacaoMoveGerada");
		
		Architecture architecture = givenAArchitecture2("generalizacaoMoveGerada");
		GeneralizationRelationship geneg = architecture.getRelationshipHolder().getAllGeneralizations().iterator().next();
		assertEquals("Class3",geneg.getParent().getName());
	}
	
	@Test
	public void shouldMoveSubClass() throws Exception{
		Architecture a = givenAArchitecture("generalizacaoMove");
		
		GeneralizationRelationship gene = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		
		a.forGeneralization().moveGeneralizationSubClass(gene, a.findClassByName("Class5").get(0));
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "generalizacaoMoveSubGerada");
		
		Architecture architecture = givenAArchitecture2("generalizacaoMoveSubGerada");
		GeneralizationRelationship geneg = architecture.getRelationshipHolder().getAllGeneralizations().iterator().next();
		
		assertEquals("Class5",geneg.getChild().getName());
		assertEquals(1,geneg.getAllChildrenForGeneralClass().size());
	}
	
	@Test
	public void shouldMoveEntiereGeneralization() throws Exception{
		Architecture a = givenAArchitecture("generalizacaoMove");
		
		Class klass5 = a.findClassByName("Class5").get(0);
		Class klass4 = a.findClassByName("Class4").get(0);
		
		GeneralizationRelationship gene = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		
		a.forGeneralization().moveGeneralization(gene, klass5, klass4);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "generalizacaoMovidaInteira");
		
		
		Architecture architecture = givenAArchitecture2("generalizacaoMovidaInteira");
		GeneralizationRelationship geneg = architecture.getRelationshipHolder().getAllGeneralizations().iterator().next();
		assertEquals("Class5",geneg.getParent().getName());
		assertEquals("Class4",geneg.getChild().getName());
	}
	
	@Test
	public void shouldAddNewSubClassToGeneralization() throws Exception{
		Architecture a = givenAArchitecture("generalizacaoMove");
		
		GeneralizationRelationship generalizationRelationship = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		Class klass5 = a.findClassByName("Class5").get(0);
		
		a.forGeneralization().addChildToGeneralization(generalizationRelationship, klass5);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "generalizacaoNovoFilho");
		
		Architecture architecture = givenAArchitecture2("generalizacaoNovoFilho");
		
		assertEquals(2, architecture.getRelationshipHolder().getAllGeneralizations().size());
		
		Set<Element> generalization = architecture.getRelationshipHolder().getAllGeneralizations().iterator().next().getAllChildrenForGeneralClass();
		assertEquals(2, generalization.size());
		
		Iterator<Element> iter = generalization.iterator();
		while (iter.hasNext()) {
			if(!(iter.next().getName().equals("Class5")) || !(iter.next().getName().equals("Class2")))
				Assert.fail("Nao achou Class5 e Class2");
		}
		
	}
	
	@Test
	public void shouldCreateNewGeneralization() throws Exception{
		Architecture a = givenAArchitecture("generalizacaoMove");
		
		Class klass5 = a.findClassByName("Class5").get(0);
		Class klass4= a.findClassByName("Class4").get(0);
		
		a.forGeneralization().createGeneralization(klass5,klass4);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "generalizacaoNovaGeneralizacao");
		Architecture architecture = givenAArchitecture2("generalizacaoNovaGeneralizacao");
		
		assertEquals(2, architecture.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	/* Fim generalizacao */
	
	@Test
	public void shouldMoveRealizationClient() throws Exception{
		Architecture a = givenAArchitecture("realization");
		
		Class klassFooBar = a.createClass("FooBar", false);
		RealizationRelationship realization = a.getRelationshipHolder().getAllRealizations().iterator().next();
		
		a.operationsOverRelationship().moveRealizationClient(realization, klassFooBar);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "realizationMoveClientGerada");
		
		Architecture architecture = givenAArchitecture2("realizationMoveClientGerada");
		assertEquals(1,architecture.getRelationshipHolder().getAllRealizations().size());
		assertEquals("FooBar",architecture.getRelationshipHolder().getAllRealizations().iterator().next().getClient().getName());
	}
	
	@Test
	public void shouldMoveRealizationSupplier() throws Exception{
		Architecture a = givenAArchitecture("realization");
		
		Class klassFooBar = a.createClass("FooBar",false);
		RealizationRelationship realization = a.getRelationshipHolder().getAllRealizations().iterator().next();
		
		a.operationsOverRelationship().moveRealizationSupplier(realization, klassFooBar);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "realizationMoveSupplierGerada");
		
		Architecture architecture = givenAArchitecture2("realizationMoveSupplierGerada");
		assertEquals(1,architecture.getRelationshipHolder().getAllRealizations().size());
		assertEquals("FooBar",architecture.getRelationshipHolder().getAllRealizations().iterator().next().getSupplier().getName());
	}
	
	@Test
	public void shouldMoveEntiereRealization() throws Exception{
		Architecture a = givenAArchitecture("realization");
		
		Class klassFoo = a.createClass("Foo",false);
		Class klassBar = a.createClass("Bar",false);
		
		RealizationRelationship realization = a.getRelationshipHolder().getAllRealizations().iterator().next();
		
		Element client = realization.getClient();
		Element supplier = realization.getSupplier();
		
		a.operationsOverRelationship().moveRealization(realization, klassFoo, klassBar);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "realizationMoveInteiraGerada");
		
		Architecture architecture = givenAArchitecture2("realizationMoveInteiraGerada");
		
		assertEquals(1,architecture.getRelationshipHolder().getAllRealizations().size());
		assertEquals("Bar",architecture.getRelationshipHolder().getAllRealizations().iterator().next().getSupplier().getName());
		assertEquals("Foo",architecture.getRelationshipHolder().getAllRealizations().iterator().next().getClient().getName());
	}
	
	@Test
	public void shouldCreateNewRealization() throws Exception{
		Architecture a = givenAArchitecture("realization");
		
		Class client = a.createClass("Foo",false);
		Class supplier = a.createClass("Bar",false);
		
		a.operationsOverRelationship().createNewRealization(client, supplier);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "realizationNova");
		Architecture architecture = givenAArchitecture2("realizationNova");
		assertEquals(2, architecture.getRelationshipHolder().getAllRealizations().size());
	}
	
	/* Fim Realização testes */
	
	/* Abstração testes */
	
	@Test
	public void shouldRemoveAbstraction() throws Exception {
		Architecture a = givenAArchitecture("abstractionInterElement");
		assertEquals(2, a.getRelationshipHolder().getAllAbstractions().size());
		a.forAbstraction().remove(a.getRelationshipHolder().getAllAbstractions().iterator().next());
		assertEquals(1,a.getRelationshipHolder().getAllAbstractions().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "abstractionInterElementRemoved");
		Architecture architecture = givenAArchitecture2("abstractionInterElementRemoved");
		assertEquals(1, architecture.getRelationshipHolder().getAllAbstractions().size());
	}
	
	@Test
	public void shouldCreateNewAbstraction() throws Exception{
		Architecture a = givenAArchitecture("abstractionInterElement");
		
		Class newClient = a.createClass("NewClient", false);
		Class newSupplier = a.createClass("NewSupplier", false);
		
		a.forAbstraction().create(newClient, newSupplier);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "newAbstraction");
		Architecture architecture = givenAArchitecture2("newAbstraction");
		assertEquals(3, architecture.getRelationshipHolder().getAllAbstractions().size());
	}
	
	@Test
	public void shouldMoveClientAbstraction() throws Exception {
		Architecture a = givenAArchitecture("moveAbstraction");
		AbstractionRelationship abstractionRelationship = a.getRelationshipHolder().getAllAbstractions().iterator().next();
		
		assertEquals("Class2", abstractionRelationship.getClient().getName());
		assertEquals("Class2", abstractionRelationship.getClient().getName());
		
		Class newClient = a.createClass("NewClient",false);
		
		assertEquals(0, newClient.getRelationships().size());
		
		a.forAbstraction().moveClient(abstractionRelationship, newClient);
		
		assertEquals(1, a.getRelationshipHolder().getAllAbstractions().size());
		assertEquals(1,newClient.getRelationships().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "moveAbstractionGerada");
		Architecture architecture = givenAArchitecture2("moveAbstractionGerada");
		
		assertEquals("NewClient", architecture.getRelationshipHolder().getAllAbstractions().iterator().next().getClient().getName());

	}
	
	@Test
	public void shouldMoveSupplierAbstraction() throws Exception {
		Architecture a = givenAArchitecture("moveAbstraction");
		AbstractionRelationship abstractionRelationship = a.getRelationshipHolder().getAllAbstractions().iterator().next();
		assertEquals("Class2", abstractionRelationship.getClient().getName());
		
		Class newSupplier = a.createClass("NewSupplier", false);
		
		assertEquals(0, newSupplier.getRelationships().size());
		
		a.forAbstraction().moveSupplier(abstractionRelationship, newSupplier);
		assertEquals(1, a.getRelationshipHolder().getAllAbstractions().size());
		assertEquals(1, newSupplier.getRelationships().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "moveAbstractionGeradaMoveSupplier");
		Architecture architecture = givenAArchitecture2("moveAbstractionGeradaMoveSupplier");
		
		AbstractionRelationship ab = architecture.getRelationshipHolder().getAllAbstractions().iterator().next();
		assertEquals("NewSupplier", ab.getSupplier().getName());
		
	}
	
	@Test
	public void shouldMoveEntireAbstraction() throws Exception {
		Architecture a = givenAArchitecture("moveAbstraction");
		AbstractionRelationship abstractionRelationship = a.getRelationshipHolder().getAllAbstractions().iterator().next();
		
		Class newSupplier = a.createClass("NewSupplier", false);
		Class newCliente = a.createClass("NewClient", false);
		
		assertEquals(0, newSupplier.getRelationships().size());
		
		a.forAbstraction().move(abstractionRelationship, newSupplier, newCliente);
		assertEquals(1 ,a.getRelationshipHolder().getAllAbstractions().size());
		assertEquals(1,newSupplier.getRelationships().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "moveAbstractionMoveAll");
		Architecture architecture = givenAArchitecture2("moveAbstractionMoveAll");
		
		AbstractionRelationship ab = architecture.getRelationshipHolder().getAllAbstractions().iterator().next();
		assertEquals("NewSupplier", ab.getSupplier().getName());
		assertEquals("NewClient", ab.getClient().getName());
	}
	
	/* Fim testes abstração */
	
	@Test
	public void shouldRemoveUsage() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		
		assertEquals(1,a.getRelationshipHolder().getAllUsage().size());
		a.forUsage().remove(a.getRelationshipHolder().getAllUsage().iterator().next());
		
		assertEquals(0, a.getRelationshipHolder().getAllUsage().size());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "usageRemove");
		Architecture architecture = givenAArchitecture2("usageRemove");
		assertEquals(0, architecture.getRelationshipHolder().getAllUsage().size());
		
		assertEquals(2, architecture.getAllPackages().size());
	}
	
	
	@Test
	public void shouldMoveClientUsage() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		
		Class newClient = a.createClass("newClient",false);
		
		a.forUsage().moveClient(a.getRelationshipHolder().getAllUsage().iterator().next(), newClient);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "usageMoveClient");
		Architecture architecture = givenAArchitecture2("usageMoveClient");
		
		assertEquals("newClient",architecture.getRelationshipHolder().getAllUsage().iterator().next().getClient().getName());
	}
	
	@Test
	public void shouldMoveSupplierUsage() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		
		Class newSupplier = a.createClass("newSupplier",false);
		
		a.forUsage().moveSupplier(a.getRelationshipHolder().getAllUsage().iterator().next(), newSupplier);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "usageMoveSupplier");
		Architecture architecture = givenAArchitecture2("usageMoveSupplier");
		
		assertEquals("newSupplier",architecture.getRelationshipHolder().getAllUsage().iterator().next().getSupplier().getName());
	}
	
	@Test
	public void shouldCreateNewUsage() throws Exception{
		Architecture a = givenAArchitecture("newUsage");
		
		Class newSupplier = a.createClass("newSupplier", false);
		Class newClient = a.createClass("newClient", false);
		
		a.forUsage().create(newClient, newSupplier);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "usageNova");
		Architecture architecture = givenAArchitecture2("usageNova");
		
		
		ArrayList<UsageRelationship> listASsociations = Lists.newArrayList(architecture.getRelationshipHolder().getAllUsage());		
		
		
		assertEquals("newClient", listASsociations.get(0).getClient().getName());
		assertEquals("newSupplier", listASsociations.get(0).getSupplier().getName());
		
	}
	
	@Test
	public void shouldMoveEntireUsage() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		
		Class newSupplier = a.createClass("newSupplier",false);
		Class newClient = a.createClass("newClient",false);
		
		a.forUsage().move(a.getRelationshipHolder().getAllUsage().iterator().next(), newSupplier, newClient);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "usageMovida");
		Architecture architecture = givenAArchitecture2("usageMovida");
		
		assertEquals(1,a.getRelationshipHolder().getAllUsage().size());
		assertEquals("newSupplier", architecture.getRelationshipHolder().getAllUsage().iterator().next().getSupplier().getName());
		assertEquals("newClient", architecture.getRelationshipHolder().getAllUsage().iterator().next().getClient().getName());
		
	}
}


