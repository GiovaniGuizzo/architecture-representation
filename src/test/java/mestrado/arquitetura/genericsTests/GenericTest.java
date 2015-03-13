package mestrado.arquitetura.genericsTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.junit.Test;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.StereotypeHelper;
import arquitetura.helpers.Uml2Helper;
import arquitetura.helpers.Uml2HelperFactory;
import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.UsageRelationship;

/**
 * 
 * @author edipofederle
 *
 */
public class GenericTest extends TestHelper {
	
	
	@Test
	public void shouldApplyStereotypePersistense() throws Exception{
		Package model = givenAModel("interface");
		
		NamedElement klass = modelHelper.getAllClasses(model).get(0);
		Profile profileConcern = (Profile) givenAModel("perfilConcerns.profile");
		model.applyProfile(profileConcern);
		assertNotNull(klass);
		assertEquals("myInterface", klass.getName());
		
		assertFalse("Nao deve possuir concern", StereotypeHelper.hasConcern(klass));
		
		Stereotype concern = profileConcern.getOwnedStereotype("Persistence");
		assertEquals("Persistence", concern.getName());
		assertNotNull(concern);
		
		klass.applyStereotype(concern);
		
		assertTrue("Deve possuir concern", StereotypeHelper.hasConcern(klass));
	}
	
	
	@Test
	public void shouldLoadDependencyInterClassWitoutPackageAndClassWithPackage() throws Exception{
		String uriToArchitecture = getUrlToModel("classPacote");
		Architecture architecture = new ArchitectureBuilder().create(uriToArchitecture);
		
		Set<Class> allClasses = new HashSet<Class>();
		for(arquitetura.representation.Package p : architecture.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		allClasses.addAll(architecture.getClasses());
		
		assertNotNull(architecture);
		assertEquals(2, allClasses.size());
		assertEquals(1, architecture.getRelationshipHolder().getAllRelationships().size());
		
		DependencyRelationship r = architecture.getRelationshipHolder().getAllDependencies().get(0);
		
		assertNotNull(r);
		assertEquals("Class1", r.getClient().getName());
		assertEquals("Class2", r.getSupplier().getName());
		
		assertEquals("model", r.getSupplier().getNamespace());
		assertEquals("model::Package1", r.getClient().getNamespace());
	}
	
	@Test
	public void testAll() throws Exception{
		Architecture a = givenAArchitecture("all");
		assertNotNull(a);
		

		Set<Class> allClasses = new HashSet<Class>();
		for(arquitetura.representation.Package p : a.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		allClasses.addAll(a.getClasses());
		
		assertEquals(3, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllGeneralizations().size());
		assertEquals(1, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
		assertEquals(2, a.getAllPackages().size());
		assertEquals(4, allClasses.size());
		
		GeneralizationRelationship g = a.getRelationshipHolder().getAllGeneralizations().get(0);
		assertEquals("Class2",g.getChild().getName());
		assertEquals("Class1", g.getParent().getName());
		assertNotNull(g.getId());
		
		UsageRelationship usage = a.getRelationshipHolder().getAllUsage().get(0);
		assertNotNull(usage.getId());
		assertEquals("Class2", usage.getClient().getName());
		assertEquals("Class3", usage.getSupplier().getName());
		
		AssociationRelationship association = a.getRelationshipHolder().getAllAssociationsRelationships().get(0);
		assertEquals(2, association.getParticipants().size());
		
		AssociationEnd p1 = association.getParticipants().get(0);
		AssociationEnd p2 = association.getParticipants().get(1);
		assertNotNull(p1);
		assertNotNull(p2);
		assertEquals("Class3", p1.getCLSClass().getName());
		assertEquals("Class1", p2.getCLSClass().getName());
		
		arquitetura.representation.Package server = a.findPackageByName("Server");
		assertNotNull(server);
		assertEquals(3, server.getElements().size());
		//assertContains(server.getClasses(), "Class1", "Class2", "Class3");
		
		arquitetura.representation.Package client = a.findPackageByName("Client");
		assertNotNull(client);
		assertEquals(1, client.getAllClasses().size());
		//assertContains(client.getClasses(), "Class1");
		
	}
	
	@Test
	public void teste() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion, IOException{
		Uml2Helper helper = Uml2HelperFactory.getUml2Helper();
		Package model = helper.load("src/main/java/arquitetura/touml/1/simples.uml");
		assertNotNull(model);
		
		Profile profile = (Profile) helper.getExternalResources(ReaderConfig.getPathToProfileConcerns());
		model.applyProfile(profile);
		URI uri = URI.createFileURI("src/test/java/resources/output.uml");
		helper.saveResources(model, uri);
	}
	
}