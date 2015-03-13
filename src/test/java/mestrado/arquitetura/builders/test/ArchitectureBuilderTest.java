package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.exceptions.ClassNotFound;
import arquitetura.exceptions.MethodNotFoundException;
import arquitetura.exceptions.PackageNotFound;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.ConcernHolder;
import arquitetura.representation.Method;
import arquitetura.representation.Package;
import arquitetura.representation.Variability;
import arquitetura.representation.relationship.AssociationClassRelationship;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;

/**
 * 
 * @author edipofederle
 * 
 */
public class ArchitectureBuilderTest extends TestHelper {

	private Architecture architecture;

	private Class klass, klassClass2;

	@Before
	public void setUp() throws Exception {
		 architecture = givenAArchitecture("testArch");
		 klass =  architecture.findClassByName("Class1").get(0);
		 klassClass2 = architecture.findClassByName("Class2").get(0);
	}
	
	/**
	 * Baseado no arquivo de profile informado no arquivo de configuração.
	 * 
	 */
	@Test
	public void shouldHaveAListOfAllowedConcerns() throws Exception{
		givenAArchitecture("concerns/completeClass");
		assertNotNull(ConcernHolder.INSTANCE.allowedConcerns());
	}

	@Test
	public void shouldHaveTwoConcerns() throws Exception {
		Architecture architecture2 = givenAArchitecture("testArch");
		assertEquals("Architecture should have two conecern", 2, architecture2.getAllConcerns().size());
	}

	@Test
	public void shouldNotBeNull() throws Exception {
		assertNotNull("Architecture should NOT be null", architecture);
	}

	@Test
	public void shouldHaveAName() throws Exception {
		assertEquals("Architecture name should be 'modelVariability",
				"testArch", architecture.getName());
	}

	@Test
	public void shouldHaveLoadPackages() throws Exception {
		assertNotNull(architecture.getAllPackages());
		assertEquals("Architecture should have two package", 4, architecture.getAllPackages().size());
	}

	@Test
	public void shouldHaveCorrectNameForPackage() throws PackageNotFound {
		assertNotNull(architecture.findPackageByName("Package1"));
	}

	@Test
	public void shouldHaveMandatoryStereotype() {
		assertNotNull(architecture.getAllVariationPoints());
		System.out.println(architecture.getAllVariationPoints().get(0));
	}

	@Test
	public void shouldClassHaveNamespace() throws ClassNotFound {
		assertNotNull(klass.getNamespace());
	}

	@Test
	public void shouldHaveOneMethod() throws MethodNotFoundException {

		assertEquals("Class1", klass.getName());
		assertEquals(1, klass.getAllMethods().size());
		
		Method fooMethod = klass.findMethodByName("foo");
		
		assertNotNull(fooMethod.getName());
		assertEquals("String", fooMethod.getReturnType());
		assertEquals(3, fooMethod.getParameters().size());
		assertEquals("name", fooMethod.getParameters().get(1).getName());
		assertEquals("String", fooMethod.getParameters().get(1).getType());
		assertEquals("Description", fooMethod.getParameters().get(2).getName());
		assertEquals("String", fooMethod.getParameters().get(2).getType());
	}

	@Test
	public void shouldHaveAEmptyStringTypeWhenNotTypeFoundForAttribute() {
		assertEquals("", klassClass2.getAllAttributes().iterator().next().getType());
		assertEquals("age", klassClass2.getAllAttributes().iterator().next().getName());
	}

	@Test
	public void shouldClass2HaveTwoAttributes() {
		assertEquals(2, klassClass2.getAllAttributes().size());
	}

	@Test
	public void shoulClassdBeAbastract() throws ClassNotFound {
		assertTrue("class should be abstract", architecture.findClassByName("Bar").get(0).isAbstract());
	}

	@Test
	public void shouldClassNotBeAbstract() {
		assertFalse("class should not be abstract", klass.isAbstract());
	}

	@Test
	public void shouldContainsAClassWithConcern() {
		Set<Concern> concerns = klass.getOwnConcerns();
		assertFalse(concerns.isEmpty());
		assertContainsConcern(concerns, "pong");
	}

	@Test
	public void shouldContainTwoConcerns() {
		Set<Concern> concerns = klass.getOwnConcerns();
		assertEquals(2, concerns.size());
		
		assertContainsConcern(concerns, "pong", "play");
	}

	@Test
	public void testWithoutPackages() throws Exception {
		Architecture architecture = givenAArchitecture("semPacote");
		assertNotNull(architecture);
		assertEquals(2, architecture.getAllClasses().size());

		assertEquals(1, architecture.getAllPackages().size());
		assertEquals("pacote1", architecture.getAllPackages().iterator().next().getName());
	}


	@Test
	public void shouldLoadPackagesInsidePackage() throws PackageNotFound {
		Package package2 = architecture.findPackageByName("Pacote2");
		assertEquals("Pacote2 should contains two packges", 2, package2.getNestedPackages().size());
		assertEquals("Pacote2DentroDoPacote2", package2.getNestedPackages().iterator().next().getName());
	}

	@Test
	public void shouldNestedPackagesHaveTwoClasses() throws PackageNotFound {
		Package pkg = architecture.findPackageByName("Pacote1DentroDoPacote2");
		assertNotNull(pkg);
		assertEquals(2, pkg.getAllClasses().size());
	}

	@Test
	public void shouldNEstedPackage2HaveOneClass() throws PackageNotFound {
		Package pkg = architecture.findPackageByName("Pacote2DentroDoPacote2");
		assertNotNull(pkg);
		assertEquals(1, pkg.getAllClasses().size());
	}

	@Test
	public void shouldHaveOneVariability() {
		assertEquals(1, architecture.getAllVariabilities().size());
	}


	@Test
	public void shouldVariabilityBelongToClass1() {
		Variability variability = architecture.getAllVariabilities().get(0);
		assertNotNull(variability);

		assertEquals("Class1", variability.getOwnerClass());
	}

	@Test
	public void shouldLoadInterElementDependency() throws Exception {
		Architecture architecture8 = givenAArchitecture("dependency2");

		DependencyRelationship dependencyInterElement = architecture8.getRelationshipHolder().getAllDependencies().get(0);

		assertNotNull(dependencyInterElement);
		
		assertEquals("Package1", dependencyInterElement.getClient().getName());

	}

	// AssociationClass

	@Test
	public void shouldLoadAssociationClassAssociation() throws Exception {
		String uriToArchitecture8 = getUrlToModel("associationClass");
		Architecture architecture8 = new ArchitectureBuilder().create(uriToArchitecture8);

		List<AssociationClassRelationship> associationClass = architecture8.getRelationshipHolder().getAllAssociationsClass();

		assertEquals(2, associationClass.iterator().next().getMemebersEnd().size());
		assertEquals("Employee", associationClass.iterator().next().getMemebersEnd().get(0).getType().getName());
		assertEquals("Class1", associationClass.iterator().next().getMemebersEnd().get(1).getType().getName());
		assertEquals("AssociationClass1", associationClass.iterator().next().getName());
	}

	/**
	 * @see <a href="http://d.pr/i/DLKa">Modelo usado no teste</a>
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldLoadSimpleAssociationOnModelWithAssociationClassRelationship() throws Exception {

		String uriToArchitecture8 = getUrlToModel("associationClassAndAssociations");
		Architecture architecture8 = new ArchitectureBuilder().create(uriToArchitecture8);

		assertEquals(1, architecture8.getRelationshipHolder().getAllAssociationsRelationships().size());
		assertEquals(1, architecture8.getRelationshipHolder().getAllAssociationsClass().size());

		AssociationRelationship association =  architecture8.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();

		assertNotNull(association);
		assertEquals(2, association.getParticipants().size());
		assertEquals("Employee", association.getParticipants().get(0).getCLSClass().getName());
		assertEquals("Class2", association.getParticipants().get(1).getCLSClass().getName());

	}


}