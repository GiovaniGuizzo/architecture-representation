	package mestrado.arquitetura.builders.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.List;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.relationship.DependencyRelationship;


/**
 * @see <a href="https://dl.dropbox.com/u/6730822/d3.png"> Modelo (arch) usado nos testes</a>
 */
public class DependenciesTest extends TestHelper {

	private Architecture arch;

	@Before
	public void setUp() throws Exception{
		arch = givenAArchitecture("dependency");
	}

	@Test
	public void shouldLoadDependency() throws Exception {
		Architecture arch = givenAArchitecture("dependency2");
		DependencyRelationship dependency = arch.getRelationshipHolder().getAllDependencies().iterator().next();

		assertNotNull(dependency);
		assertEquals("Supplier Should be Class1", "Class1", dependency.getSupplier().getName());
		assertEquals("Dependency name should be Dependency1", "Dependency1", dependency.getName());
	}
	
	/**
	 * @see <a href="http://d.pr/i/q4QO">Modelo usado no teste (Imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadDependencyClassInsidePackageToClassOutsidePackage() throws Exception {
		String uriToArchitecture2 = getUrlToModel("classPacote");
		Architecture a = new ArchitectureBuilder().create(uriToArchitecture2);

		assertNotNull(a);
		Element class1 = a.getRelationshipHolder().getAllDependencies().iterator().next().getClient();
		Element class2 = a.getRelationshipHolder().getAllDependencies().iterator().next().getSupplier();
		assertEquals("Class1", class1.getName());
		assertEquals("Class2", class2.getName());
	}

	/**
	 * @see <a href="http://d.pr/i/uVOY">Modelo usado no teste (Imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadDependencyClassOutsidePackageToClassInsidePackage()	throws Exception {
		Architecture a =givenAArchitecture("dependencyClassOutsidePackageToClassInside");

		assertNotNull(a);

		assertEquals(1, a.getRelationshipHolder().getAllDependencies().size());
		assertEquals("Class1", a.getRelationshipHolder().getAllDependencies().iterator().next().getClient().getName());
		assertEquals("model", a.getRelationshipHolder().getAllDependencies().iterator().next().getClient().getNamespace());
		assertEquals("Class2", a.getRelationshipHolder().getAllDependencies().iterator().next().getSupplier().getName());
		assertEquals("model::Package1", a.getRelationshipHolder().getAllDependencies().iterator().next().getSupplier().getNamespace());

	}

	@Test
	public void shouldDependencyNameBeEmptyWhenNull() throws Exception {

		DependencyRelationship dependency = arch.getRelationshipHolder().getAllDependencies().iterator().next();
		dependency.setName(null);
		assertEquals("Dependency name should be empty", "",	dependency.getName());
	}
	
	
	@Test
	public void shouldLoadOnlyDependency() throws Exception{
		arch = givenAArchitecture("dependencia2");
		assertNotNull(arch);
	}

	@Test @Ignore
	public void shouldDependencyContainsTwoSuppliers() throws Exception {
		assertNotNull(findDependency("Dependency4"));
		assertNotNull(findDependency("Dependency5"));
	}

	private DependencyRelationship findDependency(String name) {
		Iterator<DependencyRelationship> iter = arch.getRelationshipHolder().getAllDependencies().iterator();
		while (iter.hasNext()) {
		  DependencyRelationship d = iter.next();
		  if(d.getName().equals(name))
			  return d;
		}
		return null;
	}

	@Test
	public void shouldReplaceClientDependency() throws Exception {
		Architecture a = givenAArchitecture("ReplaceClientDependency");

		Class klass = a.findClassByName("Class2").get(0);
		
		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Class3", dependency.getClient().getName());

		dependency.replaceClient(klass);

		assertEquals("Class2", dependency.getClient().getName());
	}

	@Test
	public void shouldReplaceSupplierDependency() throws Exception {
		
		Architecture a = givenAArchitecture("ReplaceClientDependency");

		Class klass = a.findClassByName("Class2").get(0);

		DependencyRelationship dependency = a.getRelationshipHolder().getAllDependencies().iterator().next();
		assertEquals("Class1", dependency.getSupplier().getName());

		dependency.replaceSupplier(klass);

		assertEquals("Class2", dependency.getSupplier().getName());
	}
	
	
	/**
	 * @see <a href="https://dl.dropbox.com/u/6730822/dependencyPcakgeClass.png">Modelo usado no teste (Imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadInterElementDependency() throws Exception {
		String uriToArchitecture8 = getUrlToModel("dependency2");
		Architecture architecture8 = new ArchitectureBuilder().create(uriToArchitecture8);

		DependencyRelationship dependencyInterElement = architecture8.getRelationshipHolder().getAllDependencies().iterator().next();

		assertNotNull(dependencyInterElement);

		assertEquals("Class1", dependencyInterElement.getSupplier().getName());
		Element intefacee = dependencyInterElement.getSupplier();
		assertNotNull(dependencyInterElement.getClient());
		assertEquals("Package1", dependencyInterElement.getClient().getName());
		assertNotNull(intefacee);
	}

	
	/**
	 * @see <a href="https://dl.dropbox.com/u/6730822/d2.png>Modelo usado no teste (Imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadDependencyClassPackage() throws Exception{
		Architecture a = givenAArchitecture("classPackageDependency");
		
		DependencyRelationship dependencyInterElement = a.getRelationshipHolder().getAllDependencies().iterator().next();

		assertNotNull(dependencyInterElement);
		assertEquals("Class2", dependencyInterElement.getClient().getName());
		assertEquals("Package1", dependencyInterElement.getSupplier().getName());
	}
	
	@Test
	public void shouldLoadDependencyPackagePackage() throws Exception{
		Architecture a = givenAArchitecture("dependencyPackagePackage");
		
		List<DependencyRelationship> dependency = a.getRelationshipHolder().getAllDependencies();

		assertNotNull(dependency);
		assertEquals(1, dependency.size());
		assertEquals("Package1", dependency.iterator().next().getClient().getName());
	}
	
	
	/**
	 * @see <a href="https://dl.dropboxusercontent.com/u/6730822/de/dependency3.png">Modelo usado no teste (Imagem)</a>
	 * @throws Exception
	 */
	@Test
	public void shouldLoadDependencyPackageClass() throws Exception{
		Architecture a = givenAArchitecture("dependencyPackageClass");
		
		List<DependencyRelationship> dependency = a.getRelationshipHolder().getAllDependencies();

		assertNotNull(dependency);
		assertEquals(1, dependency.size());
		
		assertEquals("Package1", dependency.iterator().next().getClient().getName());
		assertEquals("Class1", dependency.iterator().next().getSupplier().getName());
		
	}

}