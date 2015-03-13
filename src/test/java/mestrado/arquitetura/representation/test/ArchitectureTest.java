package mestrado.arquitetura.representation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.exceptions.ClassNotFound;
import arquitetura.exceptions.InterfaceNotFound;
import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.PackageNotFound;
import arquitetura.helpers.UtilResources;
import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Interface;
import arquitetura.representation.Package;
import arquitetura.representation.relationship.AbstractionRelationship;
import arquitetura.representation.relationship.AssociationClassRelationship;
import arquitetura.representation.relationship.AssociationRelationship;
import arquitetura.representation.relationship.DependencyRelationship;
import arquitetura.representation.relationship.GeneralizationRelationship;
import arquitetura.representation.relationship.RealizationRelationship;
import arquitetura.representation.relationship.Relationship;
import arquitetura.representation.relationship.UsageRelationship;

/**
 * @author elf
 *
 */
public class ArchitectureTest extends TestHelper {
	
	private Architecture arch;
	private Architecture architecture;
	private GenerateArchitecture generate ;
	
	/**
	 * @see <a href="https://dl.dropbox.com/u/6730822/generico.png">Modelo usado para architecture (Imagem)</a>
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		arch = new Architecture("arquitetura");
		generate = new GenerateArchitecture();
		String uriToArchitecture = getUrlToModel("generico");
		architecture = new ArchitectureBuilder().create(uriToArchitecture);
	}
	
	@Test
	public void shouldReturnConcernsForClass() throws Exception{
		String uriToArchitecture = getUrlToModel("f/Needless");
		Architecture architecture = new ArchitectureBuilder().create(uriToArchitecture);
		
		Set<Concern> concernsClass1 = architecture.findClassByName("Class1").get(0).getOwnConcerns();
		assertEquals(2, concernsClass1.size());
		
		assertContainsConcern(concernsClass1, "action", "bowling");
	}
	@Test
	public void shouldArchitectureHaveAName(){
		assertEquals("arquitetura", arch.getName());
	}
	
	@Test
	public void shouldArchitectureHaveAEmptyNameWhenNull(){
		arch.setName(null);
		assertEquals("", arch.getName());
	}
	
	@Test
	public void shouldReturnAllPackages(){
		arch.createPackage("Pacote");
		
		assertEquals(1, arch.getAllPackages().size());
		assertEquals("Pacote", arch.getAllPackages().iterator().next().getName());
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoPackages(){
		assertTrue(arch.getAllPackages().isEmpty());
	}
	
	@Test
	public void shouldReturnAllClasses() throws ClassNotFound{
		arch.createClass("Klass", false);
		
		assertEquals(1, arch.getClasses().size());
		assertEquals("Klass", arch.findClassByName("Klass").get(0).getName());
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoClasses(){
		assertTrue(arch.getClasses().isEmpty());
	}

	@Test
	public void shouldReturnEmptyListWhenNoVariabilities(){
		assertEquals(Collections.emptyList(), arch.getAllVariabilities());
	}
	
	@Test
	public void shouldReturnNullWhenElementNotFound(){
		assertNull(arch.findElementByName("KlassNotFound","class"));
	}
	
	@Test
	public void shouldReturnElementClassByName(){
		arch.createClass("Klass", false);
		Element klass = arch.findElementByName("klass","class");
		
		assertNotNull(klass);
		assertEquals("Klass", klass.getName());
	}
	
	@Test
	public void shouldReturnElementPackageByName(){
		assertEquals(0, arch.getAllPackages().size());
		
		arch.createPackage("Pacote");
		Element pkg = arch.findElementByName("Pacote", "package");
		
		assertNotNull(pkg);
		assertEquals("Pacote", pkg.getName());
		assertEquals(1, arch.getAllPackages().size());
	}
	
	@Test
	public void shouldReturnAllInterfaces() throws InterfaceNotFound{
		
		arch.createClass("Klass1", false);
		arch.createInterface("Interface1");
		arch.createInterface("Interface2");
		
		assertEquals(2, arch.getInterfaces().size());
		assertEquals(1, arch.getClasses().size());
		assertNotNull(arch.findInterfaceByName("Interface1"));
		assertNotNull(arch.findInterfaceByName("Interface2"));
	}
	
	@Test
	public void shouldReturnEmptyListWhenNoInterfaces(){
		assertTrue(arch.getInterfaces().isEmpty());
	}
	
	@Test
	public void shouldReturnAllGeneralizations() throws Exception{
		assertEquals(3, architecture.getRelationshipHolder().getAllRelationships().size());
		assertNotNull(architecture.getRelationshipHolder().getAllGeneralizations());
		assertEquals(1, architecture.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	
	@Test
	public void shouldReturnAllAssociations(){
		assertEquals(1, architecture.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void shouldReturnAllUsageClassPackage(){
		assertEquals(1, architecture.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void shouldReturnAllDependency() throws Exception{
		String uriToArchitecture = getUrlToModel("dependency");
		Architecture a = new ArchitectureBuilder().create(uriToArchitecture);
		assertEquals(5, a.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void shouldReturnVisibilityForAttribute() throws Exception{
		String uriToArchitecture =  getUrlToModel("testeCreateClassWithAttribute");
		Architecture a = new ArchitectureBuilder().create(uriToArchitecture);
		Class klass = a.findClassByName("Class43").get(0);
		assertEquals("public",klass.findAttributeByName("age").getVisibility());
	}
	
	@Test
	public void shouldReturnAllDependencyClassPackage() throws Exception{
		String uriToArchitecture = getUrlToModel("dependency");
		Architecture a = new ArchitectureBuilder().create(uriToArchitecture);
		assertEquals(5, a.getRelationshipHolder().getAllDependencies().size());
	}
	
	/**
	 * @see <a href="https://dl.dropbox.com/u/6730822/d5.png"> Modelo usado no teste (imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadDependencyPackageClass() throws Exception{
		String uriToArchitecture = getUrlToModel("dependency2");
		Architecture a = new ArchitectureBuilder().create(uriToArchitecture);
		assertEquals(1, a.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void shouldFindClassByName() throws Exception{
		assertNotNull(architecture.findClassByName("Class3"));
		assertEquals("Class3", architecture.findElementByName("CLass3","class").getName());
	}
	
	@Test
	public void shouldFindClassById() throws Exception{
		Architecture a = givenAArchitecture("classesComMesmoNome");
		Class c = a.findClassById("_Tln8YMlrEeKhSNDKJvPCfQ");
		
		assertNotNull(c);
		assertEquals("model::Package1",c.getNamespace());
		
		Class c2 = a.findClassById("_Q4UBkMlrEeKhSNDKJvPCfQ");
		assertNotNull(c2);
		assertEquals("model",c2.getNamespace());
	}
	
	@Test
	public void shouldReturnsListOfClassesWhenModelContainsClassesWithSameName() throws Exception{
		targetDirExport =  "src/test/java/resources/";
		Architecture a = givenAArchitecture2("classesComMesmoNome");
		List<Class> classes = a.findClassByName("Class1");
		
		assertEquals(2,classes.size());
		assertEquals("Class1", classes.get(0).getName());
		assertEquals("Class1", classes.get(1).getName());
		targetDirExport = ReaderConfig.getDirExportTarget();
	}
	
	public void shouldReturnsNullWhenClassNotFound() throws ClassNotFound{
		assertNull(architecture.findClassByName("noClass"));
	}
	
	@Test
	public void shouldFindInterfaceByName() throws Exception{
		Architecture a = givenAArchitecture("classes");
		assertNotNull(a.findInterfaceByName("Interface"));
		assertEquals("Interface", a.findInterfaceByName("Interface").getName());
	}
	
	public void shouldReturnsNullWhenInterfaceNotFound() throws InterfaceNotFound{
		assertNull(architecture.findInterfaceByName("noInterface"));
	}
	
	@Test
	public void shouldFindPackageByName() throws PackageNotFound{
		Package pkg = architecture.findPackageByName("Package1");
		assertNotNull(pkg);
		assertEquals("Package1", pkg.getName());
	}
	
	public void shouldRaisePackageNotFoundExceptionWhenPackageNotFound() throws PackageNotFound{
		assertNull(architecture.findPackageByName("noPackage"));
	}
	
	@Test
	public void shouldMoveClassToPackage() throws Exception{
		Architecture a = givenAArchitecture("classPackage");
		Class klass1 = a.findClassByName("Class1").get(0);
		Package package1 = a.findPackageByName("Package1");
		
		assertEquals(0, package1.getAllClasses().size());
		
		assertEquals("model", klass1.getNamespace());
		
		a.moveElementToPackage(klass1, package1);
		assertEquals(1, a.getAllClasses().size());
		assertEquals(1, package1.getAllClasses().size());
		
		assertEquals("classPackage::Package1", klass1.getNamespace());
	}
	
	@Test
	public void shouldRemoveAssociationRelationship() throws Exception{
		Architecture a = givenAArchitecture("associations/associationWithMultiplicity");
		AssociationRelationship as = a.getRelationshipHolder().getAllAssociationsRelationships().iterator().next();
		assertEquals("Architecture should contain 2 associations", 1, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		assertEquals(2, a.getClasses().size());
		a.removeRelationship(as);
		assertEquals("Architecture should contain 0 associations", 0, a.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveAssociationRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("associations/associationWithMultiplicity");
		assertEquals("Architecture should contain 2 associations", 1,	a.getRelationshipHolder().getAllAssociationsRelationships().size());
		assertEquals("Architecture should contain 2 associations", 1,	a.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void shouldRemoveDependecyRelationship() throws Exception{
		Architecture a = givenAArchitecture("dependency");
		DependencyRelationship dp = a.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Architecture should contain 5 dependency", 5,	a.getRelationshipHolder().getAllDependencies().size());
		a.operationsOverRelationship().removeDependencyRelationship(dp);
		assertEquals("Architecture should contain 4 dependency", 4,	a.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveDependencyRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("dependency");
		assertEquals("Architecture should contain 5 dependency", 5,	a.getRelationshipHolder().getAllDependencies().size());
		a.operationsOverRelationship().removeDependencyRelationship(null);
		assertEquals("Architecture should contain 4 dependency", 5,	a.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void shouldRemoveUsageRelationship() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		UsageRelationship usage = a.getRelationshipHolder().getAllUsage().iterator().next();
		assertEquals("Architecture should contain 1 usage", 1,	a.getRelationshipHolder().getAllUsage().size());
		a.forUsage().remove(usage);
		assertEquals("Architecture should contain 0 usage", 0,	a.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveUsageRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("usage3");
		assertEquals("Architecture should contain 1 usage", 1,	a.getRelationshipHolder().getAllUsage().size());
		a.forUsage().remove(null);
		assertEquals("Architecture should contain 0 usage", 1,	a.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void shouldRemoveAssociationClassRelationship() throws Exception{
		Architecture a = givenAArchitecture("associationClass");
		AssociationClassRelationship associationClass = a.getRelationshipHolder().getAllAssociationsClass().iterator().next();
		assertEquals("Architecture should contain 1 AssociationClass", 1,	a.getRelationshipHolder().getAllAssociationsClass().size());
		a.operationsOverRelationship().removeAssociationClass(associationClass);
		assertEquals("Architecture should contain 0 AssociationClass", 0,	a.getRelationshipHolder().getAllAssociationsClass().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveAssociationClassRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("associationClass");
		assertEquals("Architecture should contain 1 AssociationClass", 1,	a.getRelationshipHolder().getAllAssociationsClass().size());
		assertEquals("Architecture should contain 0 AssociationClass", 1,	a.getRelationshipHolder().getAllAssociationsClass().size());
	}
	
	@Test
	public void shouldRemoveGeneralizationRelationship() throws Exception{
		Architecture a = givenAArchitecture("generalizationArch");
		GeneralizationRelationship g = a.getRelationshipHolder().getAllGeneralizations().iterator().next();
		assertEquals("Architecture should contain 3 generalization", 3,	a.getRelationshipHolder().getAllGeneralizations().size());
		a.operationsOverRelationship().removeGeneralizationRelationship(g);
		assertEquals("Architecture should contain 2 generalization", 2,	a.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveGeneralizationRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("generalizationArch");
		assertEquals("Architecture should contain 3 generalization", 3,	a.getRelationshipHolder().getAllGeneralizations().size());
		a.operationsOverRelationship().removeGeneralizationRelationship(null);
		assertEquals("Architecture should contain 2 generalization", 3,	a.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	@Test
	public void shouldRemoveAbstractionRelationship() throws Exception{
		Architecture a = givenAArchitecture("abstractionInterElement");
		AbstractionRelationship ab = a.getRelationshipHolder().getAllAbstractions().iterator().next();
		assertEquals("Architecture should contain 1 Abstraction", 2,	a.getRelationshipHolder().getAllAbstractions().size());
		a.forAbstraction().remove(ab);
		assertEquals("Architecture should contain 0 Abstraction", 1,	a.getRelationshipHolder().getAllAbstractions().size());
	}
	
	@Test
	public void shouldNotChangeListWhenTryRemoveAbstractionRelationshipNotExist() throws Exception{
		Architecture a = givenAArchitecture("abstractionInterElement");
		assertEquals("Architecture should contain 1 Abstraction", 2, a.getRelationshipHolder().getAllAbstractions().size());
		a.forAbstraction().remove(null);
		assertEquals("Architecture should contain 1 Abstraction", 2, a.getRelationshipHolder().getAllAbstractions().size());
	}
	
	@Test
	public void shouldCreateAPackage() throws PackageNotFound{
		Package packageTest = architecture.createPackage("myPackage");
		assertEquals("generico::myPackage", packageTest.getNamespace());
		
		assertNotNull(packageTest.getId());
		assertEquals(2, architecture.getAllPackages().size());
		assertSame(packageTest, architecture.findPackageByName("myPackage"));
	}
	
	@Test
	public void shouldRemoveAPackage() throws PackageNotFound{
		Package p = architecture.findPackageByName("Package1");
		assertNotNull(p);
		architecture.removePackage(p);
		assertEquals(0,architecture.getAllPackages().size());
	}
	
	@Test
	public void shouldRemoveAllElementRelatedWithPackageWhenPackageRemove1() throws Exception{
		Architecture a = givenAArchitecture("testePackageRemove");
		
		Set<Class> allClasses = new HashSet<Class>();
		for(Package p : a.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		Set<Interface> allInterfaces = new HashSet<Interface>();
		for(Package p : a.getAllPackages())
			allInterfaces.addAll(p.getAllInterfaces());
		
		assertEquals(1, allClasses.size());
		assertEquals(0, allInterfaces.size());
		
		
		a.removePackage(a.getAllPackages().iterator().next());
		
		assertEquals(0, a.getClasses().size());
		assertEquals(0, a.getClasses().size());
		assertTrue(a.getAllPackages().isEmpty());
	}
	
	@Test
	public void shouldRemoveAllElementRelatedWithPackageWhenPackageRemove2() throws Exception{
		Architecture a = givenAArchitecture("removePacoteTeste2");
		
		Set<Class> allClasses = new HashSet<Class>();
		for(Package p : a.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		allClasses.addAll(a.getClasses());
		
		
		assertEquals(2, allClasses.size());
		assertEquals(1, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		
		a.removePackage(a.getAllPackages().iterator().next());
		
		assertEquals(1, a.getClasses().size());
		assertEquals(0, a.getRelationshipHolder().getAllAssociationsRelationships().size());
	}
	
	@Test
	public void shouldRemoveAllElementRelatedWithPackageWhenPackageRemove3() throws Exception{
		Architecture a = givenAArchitecture("removePacote3");
		
		Set<Class> allClasses = new HashSet<Class>();
		for(Package p : a.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		allClasses.addAll(a.getClasses());
		
		assertEquals("Deve ter tres clases", 3, allClasses.size());
		assertEquals("Deve ter 2 associações",2, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		
		for(AssociationRelationship as : a.getRelationshipHolder().getAllAssociationsRelationships())
			assertEquals(2, as.getParticipants().size());
		
		a.removePackage(a.getAllPackages().iterator().next());
		
		Set<Class> allClasses2 = new HashSet<Class>();
		for(Package p : a.getAllPackages())
			allClasses2.addAll(p.getAllClasses());
		
		allClasses2.addAll(a.getClasses());
		
		assertEquals("Deve ter uma classe", 1,allClasses2.size());
		assertEquals(0, a.getRelationshipHolder().getAllAssociationsRelationships().size());
		
	}
	
	
	@Test
	public void shouldCreateInterface() throws InterfaceNotFound{
		assertEquals(0, architecture.getInterfaces().size());
		Interface interfacee = architecture.createInterface("myInterface");

		assertEquals("generico::myInterface", interfacee.getNamespace());
		
		assertNotNull(interfacee);
		assertNotNull(interfacee.getId());
		assertEquals(1, architecture.getInterfaces().size());
		assertEquals(interfacee, architecture.findInterfaceByName("myInterface"));
	}
	
	@Test
	public void shouldRemoveInterface(){
		Interface interfacee = architecture.createInterface("myInterface");
		
		assertEquals(1, architecture.getInterfaces().size());
		
		architecture.removeInterface(interfacee); 
		
		assertEquals(0, architecture.getInterfaces().size());
	}
	
	@Test
	public void shouldCreateClass() throws ClassNotFound{
		assertEquals(3, architecture.getClasses().size());
		
		
		Class klass = architecture.createClass("Bar", true);
		assertEquals("generico::Bar", klass.getNamespace());
		
		assertEquals(4, architecture.getClasses().size());
		
		assertEquals(klass, architecture.findClassByName("Bar").get(0));
		assertTrue(architecture.findClassByName("Bar").get(0).isAbstract());
	}
	
	@Test
	public void shouldRemoveClass() throws ClassNotFound{
		assertEquals(3, architecture.getClasses().size());
		Class klass = architecture.findClassByName("Class1").get(0);
		
		architecture.removeClass(klass);
		assertEquals(2, architecture.getClasses().size());
	}
	
	@Test
	public void shouldFindPackageOfClass() throws Exception{
		Architecture arch = givenAArchitecture("classPacote");
		Class klass = arch.findClassByName("Class1").get(0);
		
		assertEquals("Class1",klass.getName());
		
		assertEquals("Package1", arch.findPackageOfClass(klass).getName());
	}
	
	public void shouldThrowsPackageNotFound() throws Exception{
		Architecture arch = givenAArchitecture("classPacote");
		
		Class klass = Mockito.mock(Class.class);
		Mockito.when(klass.getName()).thenReturn("XPTO");
		
		assertNull(arch.findPackageOfClass(klass));
	}
	
	@Test
	public void shouldReturnAllRelationshipsOfPackage() throws Exception{
		Architecture a = givenAArchitecture("PackageClassUsage");
		
		Package p = a.getAllPackages().iterator().next();
		Set<Relationship> relationships = p.getRelationships();
		
		assertEquals(1, relationships.size());
		assertTrue(relationships.iterator().next() instanceof UsageRelationship);
	}
	
	@Test
	public void shouldReturnEmptyListWhenNotFoundRelationships() throws Exception{
		Architecture a = givenAArchitecture("package");
		
		assertTrue(a.getAllPackages().iterator().next().getRelationships().isEmpty());
	}
	
	@Test
	public void testClone() throws Exception{
		Architecture a = givenAArchitecture("agmfinal/agm");
		Architecture cloneA = a.deepClone();
		
		assertNotNull(cloneA);
		assertNotSame(a, cloneA);
	}
	
	@Test
	public void testGetAllElements() throws Exception{
		Architecture a = givenAArchitecture("recurPackages");
		
		assertEquals(7, a.getElements().size());
		assertEquals("Deve ter 3 pacotes", 3, a.getAllPackages().size());
		assertContains(a.getAllPackages(), "Package1", "Package2", "Package3");
		
		assertEquals("Deve ter 4 classes", 4, a.getAllClasses().size());
	}
	
	
	@Test
	public void testAddImplementedInterfaceToClass() throws Exception {
		Architecture gerada = implementedInterfaceByClass();
		
		assertEquals(1, gerada.getAllClasses().size());
		assertEquals(1, gerada.getAllInterfaces().size());
		assertEquals(1, gerada.getRelationshipHolder().getAllRealizations().size());
		assertEquals(1, gerada.findClassByName("Foo").get(0).getImplementedInterfaces().size());
	}

	private Architecture implementedInterfaceByClass() throws ModelNotFoundException, ModelIncompleteException, Exception {
		givenADocument("implementedInterface");
		GenerateArchitecture g = new GenerateArchitecture();
		
		Architecture a = givenAArchitecture2("implementedInterface");
		
		Class client = a.createClass("Foo", false);
		Interface supplier = a.createInterface("Bar");
		assertTrue(a.addImplementedInterface(supplier, client));
		
		g.generate(a, "implementedInterfaceGerada");
		Architecture gerada = givenAArchitecture2("implementedInterfaceGerada");
		return gerada;
	}
	
	@Test
	public void testAddImplementedInterfaceToPackage() throws Exception {
		Architecture gerada = implementedInterfaceByPakage();
		
		assertEquals(1, gerada.getAllPackages().size());
		assertEquals(1, gerada.getAllInterfaces().size());
		assertEquals(1, gerada.getRelationshipHolder().getAllRealizations().size());
		assertEquals(1, gerada.findPackageByName("Package1").getImplementedInterfaces().size());
	}
	
	@Test
	public void removeImplementedInteraceFromClass() throws Exception {
		Architecture arch = implementedInterfaceByClass();
		
		Class foo = arch.findClassByName("Foo").get(0);
		Interface inter = arch.findInterfaceByName("Bar");
		
		arch.removeImplementedInterface(foo, inter);
		
		generate.generate(arch, "implementedInterfaceRemovedFromClass");
		Architecture gerada = givenAArchitecture2("implementedInterfaceRemovedFromClass");
		assertEquals(1, gerada.getAllClasses().size());
		assertEquals(1, gerada.getAllInterfaces().size());
		assertEquals(0, gerada.getRelationshipHolder().getAllRealizations().size());
		assertEquals(0, gerada.findClassByName("Foo").get(0).getImplementedInterfaces().size());
	}

	@Test
	public void removeImplementedInteraceFromPackage() throws Exception {
		Architecture arch = implementedInterfaceByPakage();
		Package pacote = arch.findPackageByName("Package1");
		Interface inter = arch.findInterfaceByName("Bar");
		
		arch.removeImplementedInterface(inter, pacote);
		
		generate.generate(arch, "implementedInterfaceRemovedFromPackage");
		Architecture gerada = givenAArchitecture2("implementedInterfaceRemovedFromPackage");
		assertEquals(0, gerada.findPackageByName("Package1").getImplementedInterfaces().size());
		assertEquals(0, gerada.getRelationshipHolder().getAllRealizations().size());
	}
	
	/**
	 * @return
	 * @throws ModelNotFoundException
	 * @throws ModelIncompleteException
	 * @throws Exception
	 */
	private Architecture implementedInterfaceByPakage()	throws ModelNotFoundException, ModelIncompleteException, Exception {
		givenADocument("implementedInterfacePackage");
		
		Architecture a = givenAArchitecture2("implementedInterfacePackage");
		
		Package client = a.createPackage("Package1");
		Interface supplier = a.createInterface("Bar");
		assertTrue(a.addImplementedInterface(supplier, client));
		
		generate.generate(a, "implementedInterfacePackageGerada");
		Architecture gerada = givenAArchitecture2("implementedInterfacePackageGerada");
		return gerada;
	}
	
	
	
	@Test
	public void testAddRequiredInterfaceToClass() throws Exception {
		givenADocument("requiredInterfaceClass");
		GenerateArchitecture g = new GenerateArchitecture();
		
		Architecture a = givenAArchitecture2("requiredInterfaceClass");
		
		Class client = a.createClass("Foo", false);
		Interface supplier = a.createInterface("Bar");
		a.addRequiredInterface(supplier, client);
		
		g.generate(a, "requiredInterfaceClassGerada");
		Architecture gerada = givenAArchitecture2("requiredInterfaceClassGerada");
		assertEquals(1,gerada.findClassByName("Foo").get(0).getRequiredInterfaces().size());
	}
	
	@Test
	public void testAddRequiredInterfaceToPackage() throws Exception {
		givenADocument("requiredInterfacePackage");
		GenerateArchitecture g = new GenerateArchitecture();
		Architecture a = givenAArchitecture2("requiredInterfacePackage");
		Package client = a.createPackage("Pacote1");
		Interface supplier = a.createInterface("Bar");
		a.addRequiredInterface(supplier, client);
		
		g.generate(a, "requiredInterfacePackageGerada");
		Architecture gerada = givenAArchitecture2("requiredInterfacePackageGerada");
		assertEquals(1, gerada.findPackageByName("Pacote1").getRequiredInterfaces().size());
	}
	
	@Test
	public void removeRequiredInterfaceFromPackage() throws Exception {
		givenADocument("requiredInterfacePackageRemove");
		GenerateArchitecture g = new GenerateArchitecture();
		Architecture a = givenAArchitecture2("requiredInterfacePackageRemove");
		Package client = a.createPackage("Pacote1");
		Interface supplier = a.createInterface("Bar");
		a.addRequiredInterface(supplier, client);
		assertEquals(1, a.getRelationshipHolder().getAllDependencies().size());
		
		a.removeRequiredInterface(supplier, client);
		
		g.generate(a, "requiredInterfacePackageRemoveGerada");
		Architecture gerada = givenAArchitecture2("requiredInterfacePackageRemoveGerada");
		
		assertEquals(0 ,gerada.findPackageByName("Pacote1").getRequiredInterfaces().size());
		assertEquals(0 ,gerada.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void removeRequiredInterfaceFromClass() throws Exception {
		givenADocument("requiredInterfaceClassRemove");
		GenerateArchitecture g = new GenerateArchitecture();
		
		Architecture a = givenAArchitecture2("requiredInterfaceClassRemove");
		
		Class client = a.createClass("Foo", false);
		Interface supplier = a.createInterface("Bar");
		a.addRequiredInterface(supplier, client);
		
		a.removeRequiredInterface(supplier, client);
		
		g.generate(a, "requiredInterfaceClassRemoveGerada");
		Architecture gerada = givenAArchitecture2("requiredInterfaceClassRemoveGerada");

		
		g.generate(a, "requiredInterfacePackageRemoveGerada");
		
		assertEquals(0 ,gerada.findClassByName("Foo").get(0).getRequiredInterfaces().size());
		assertEquals(0 ,gerada.getRelationshipHolder().getAllDependencies().size());
		
	}
	
	@Test
	public void onlyAddAssociationIfDontExists() throws Exception{
		Architecture a = givenAArchitecture("testeee");
		
		Class c1 = a.findClassByName("Class1").get(0);
		Class c2 =a.findClassByName("Class2").get(0);
		
		Class c3 = a.createClass("Foo", false);
		
		AssociationRelationship newRelationship = new AssociationRelationship(c1, c3);
		AssociationRelationship newRelationship2 = new AssociationRelationship(c1, c2);
		
		assertTrue(a.addRelationship(newRelationship));
		assertFalse(a.addRelationship(newRelationship2));
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
	}
	
	@Test
	public void onlyAddDependencyIfDontExists() throws Exception {
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		Class supplier2 = a.createClass("Teste", false);
		
		DependencyRelationship d = new DependencyRelationship(supplier, client, "");
		DependencyRelationship d2 = new DependencyRelationship(supplier2, client, "");
		
		DependencyRelationship dsame = new DependencyRelationship(supplier, client, "");
		
		
		assertTrue(a.addRelationship(d));
		assertTrue(a.addRelationship(d2));
		
		assertEquals(2, a.getRelationshipHolder().getAllDependencies().size());
		assertFalse(a.addRelationship(dsame));
		assertEquals(2, a.getRelationshipHolder().getAllDependencies().size());
	}
	
	@Test
	public void onlyAddRealizationIfDontExist() throws Exception {
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		RealizationRelationship r = new RealizationRelationship(client, supplier, "", UtilResources.getRandonUUID());
		
		assertTrue(a.addRelationship(r));
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
	}
	
	@Test
	public void onlyAddGeneralizationIfDontExistWay1() throws Exception {
		Architecture a = givenAArchitecture("testeee");
		
		Class parent = a.createClass("Foo", false);
		Class child = a.createClass("Bar", false);
		
		GeneralizationRelationship r = a.forGeneralization().createGeneralization(parent, child);
		
		assertFalse(a.addRelationship(r));
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	@Test
	public void onlyAddGeneralizationIfDontExistWay2() throws Exception {
		Architecture a = givenAArchitecture("testeee");
		
		Class parent = a.createClass("Foo", false);
		Class child = a.createClass("Bar", false);
		
		GeneralizationRelationship r = new GeneralizationRelationship(parent, child, a.getRelationshipHolder());
		
		assertTrue(a.addRelationship(r));
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllGeneralizations().size());
	}
	
	@Test
	public void onlyAddUsageIfDontExistWay1() throws Exception{
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		UsageRelationship r = a.forUsage().create(client, supplier);
		
		assertFalse(a.addRelationship(r));
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void onlyAddUsageIfDontExistWay2() throws Exception{
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		UsageRelationship r = new UsageRelationship("", supplier, client);
		
		assertTrue(a.addRelationship(r));
		assertFalse(a.addRelationship(r));
		
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllUsage().size());
	}
	
	@Test
	public void onlyAddAbstractionIfDontExistWay1() throws Exception{
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		AbstractionRelationship r =  new AbstractionRelationship(client, supplier, UtilResources.getRandonUUID());
		assertTrue(a.addRelationship(r));
		assertFalse(a.addRelationship(r));
		
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllAbstractions().size());
	}
	
	@Test
	public void onlyAddAbstractionIfDontExistWay2() throws Exception{
		Architecture a = givenAArchitecture("testeee");
		
		Class client = a.createClass("Foo", false);
		Class supplier = a.createClass("Bar", false);
		
		AbstractionRelationship r = a.forAbstraction().create(client, supplier);
		assertFalse(a.addRelationship(r));
		
		assertEquals(2, a.getRelationshipHolder().getAllRelationships().size());
		assertEquals(1, a.getRelationshipHolder().getAllAbstractions().size());
	}
	
	
	@Test
	public void whenRemoveAInterfaceShouldRemoveFromPackageImplementedInterfacesToo() throws Exception {
		Architecture a = givenAArchitecture("removeInterface");
		Package p1 = a.findPackageByName("Package1");
		
		assertEquals(1, p1.getImplementedInterfaces().size());
		
		Interface inter = a.findInterfaceByName("Interface");
		a.removeInterface(inter);
		
		assertEquals(0, p1.getImplementedInterfaces().size());
	}
	
	@Test
	public void whenRemoveAInterfaceShouldRemoveFromClassImplementedInterfacesToo() throws Exception {
		Architecture a = givenAArchitecture("removeInterface");
		Class c1 = a.findClassByName("Class1").get(0);
		
		assertEquals(1, c1.getImplementedInterfaces().size());
		
		Interface inter = a.findInterfaceByName("Interface3");
		a.removeInterface(inter);
		
		assertEquals(0, c1.getImplementedInterfaces().size());
	}
	
	@Test
	public void whenRemoveAInterfaceShouldRemoveFromPackageRequeiredInterfacesToo() throws Exception {
		Architecture a = givenAArchitecture("removeInterface");
		Package p1 = a.findPackageByName("Package2");
		
		assertEquals(1, p1.getRequiredInterfaces().size());
		
		Interface inter = a.findInterfaceByName("Interface2");
		a.removeInterface(inter);
		
		assertEquals(0, p1.getRequiredInterfaces().size());
	}
	
	@Test
	public void whenRemoveAInterfaceShouldRemoveFromClassRequeiredInterfacesToo() throws Exception {
		Architecture a = givenAArchitecture("removeInterface");
		Class c1 = a.findClassByName("Class2").get(0);
		
		assertEquals(1, c1.getRequiredInterfaces().size());
		
		Interface inter = a.findInterfaceByName("Interface4");
		a.removeInterface(inter);
		
		assertEquals(0, c1.getRequiredInterfaces().size());
	}
	
	@Test
	public void whenRemoveAClassShouldRemoveRelatedRelationshiopsAssociation() throws Exception{
		Architecture a = givenAArchitecture("removeClass");
		Class c1 = a.findClassByName("Class1").get(0);
		
		assertEquals(4, a.getAllClasses().size());
		assertEquals(1, a.getRelationshipHolder().getAllAssociations().size());
		
		a.removeClass(c1);
		
		assertEquals(0, a.getRelationshipHolder().getAllAssociations().size());
		assertEquals(3, a.getAllClasses().size());
	}
	
	@Test
	public void whenRemoveAClassShouldRemoveRelatedRelationshiopsDependency() throws Exception{
		Architecture a = givenAArchitecture("removeClass");
		Class c1 = a.findClassByName("Foo").get(0);
		
		assertEquals(4, a.getAllClasses().size());
		assertEquals(1, a.getRelationshipHolder().getAllDependencies().size());
		
		a.removeClass(c1);
		
		assertEquals(0, a.getRelationshipHolder().getAllDependencies().size());
		assertEquals(3, a.getAllClasses().size());
		assertEquals(1, a.getAllPackages().size());
	}
	
	@Test
	public void testRemovePackage() throws Exception {
		Architecture a = givenAArchitecture("removePackage");
		Package p = a.findPackageByName("Package1");
		a.removePackage(p);
		
		assertEquals(0, a.getAllPackages().size());
		assertEquals(0, a.getRelationshipHolder().getAllDependencies().size());
		assertEquals(0, a.getRelationshipHolder().getAllAssociations().size());
		assertNotNull(a.findClassByName("Class3"));
		assertNotNull(a.findClassByName("Class2"));
		assertNull(a.findClassByName("Class1"));
	}
	
	/**
	 * para o operador crossover
	 * 
	 */
	@Test
	public void testUpdateClassRelationships() throws Exception{
		
		Architecture parent = givenAArchitecture("parent");
		Architecture offspring = givenAArchitecture("offspring");
		
		for(Class klass : parent.getAllClasses())
			updateClassRelationship(klass, offspring);
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(offspring, "offspring");
		
	}
	
	@Test
	public void testFindElementByName() throws Exception{
		Architecture a = givenAArchitecture("pacotesClasses");
		assertNotNull(a.findElementByName("Foo"));
		assertNotNull(a.findElementByName("Class1"));
		assertNotNull(a.findElementByName("Bar"));
		assertNotNull(a.findElementByName("Teste"));
		assertNotNull(a.findElementByName("Interface"));
		assertNotNull(a.findElementByName("Interface2"));
		assertNotNull(a.findElementByName("Package2"));
	}

	private void updateClassRelationship(Class classComp, Architecture offspring ) throws ClassNotFound {
		
		Collection<Relationship> parentRelationships = classComp.getRelationships();
		for (Relationship relationship : parentRelationships){
			if (relationship instanceof AssociationClassRelationship){
				
				AssociationClassRelationship asc = (AssociationClassRelationship)relationship;
				
				Class offspringMember1 = offspring.findClassByName(asc.getMemebersEnd().get(0).getType().getName()).get(0);
				Class offspringMember2 = offspring.findClassByName(asc.getMemebersEnd().get(1).getType().getName()).get(0);
				
				offspring.forAssociation().createAssociationClass(asc.getAllAttributes(), asc.getAllMethods(), offspringMember1, offspringMember2, asc.getAssociationClass().getName());
			}
		}
	}

	
}