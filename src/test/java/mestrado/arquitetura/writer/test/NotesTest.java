package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import main.GenerateArchitecture;
import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Element;
import arquitetura.representation.Package;
import arquitetura.representation.Variability;
import arquitetura.representation.Variant;
import arquitetura.touml.BindingTime;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;
import arquitetura.touml.VariabilityStereotype;

/**
 * Uma nota é um comentário da UML.
 * 
 * @author edipofederle
 *
 */
public class NotesTest extends TestHelper {
	
	private Element employee;
	private Element person;

	@Before
	public void setUp(){
		
		employee = Mockito.mock(Class.class);
		Mockito.when(employee.getName()).thenReturn("Employee");
		Mockito.when(employee.getId()).thenReturn("199339390");
		
		person = Mockito.mock(Class.class);
		Mockito.when(person.getName()).thenReturn("Person");
		Mockito.when(person.getId()).thenReturn("1993393123");
	}
	
	@Test
	public void shouldGenerateVariabilityIntoPackage() throws Exception{
		Architecture a = givenAArchitecture("varpacote");
		
		Set<Class> allClasses = new HashSet<Class>();
		for(Package p : a.getAllPackages())
			allClasses.addAll(p.getAllClasses());
		
		allClasses.addAll(a.getClasses());
		
		GenerateArchitecture g = new GenerateArchitecture();
		g.generate(a, "varpacote_4");
		
		Architecture varpacote_3 = givenAArchitecture2("varpacote_4");
		assertNotNull(varpacote_3);
		assertEquals(1, varpacote_3.getAllVariabilities().size());
		assertEquals(1, varpacote_3.findPackageByName("Package1").getAllClasses().size());
		Variability variability = varpacote_3.getAllVariabilities().get(0);
		assertEquals(1,variability.getVariants().size());
	}
	
	@Test
	public void shouldCreateANote() throws Exception{
		DocumentManager doc = givenADocument("note");
		Operations op = new Operations(doc, null);
		
		
		String idNote = op.forNote().createNote().build();
		
		Variability v = Mockito.mock(Variability.class);
		Mockito.when(v.getName()).thenReturn("Variabilidade1");
		Mockito.when(v.getMinSelection()).thenReturn("1");
		Mockito.when(v.getMaxSelection()).thenReturn("2");
		Mockito.when(v.allowAddingVar()).thenReturn(false);
		Mockito.when(v.getBindingTime()).thenReturn(BindingTime.DESIGN_TIME);
		
		Variant variant = Mockito.mock(Variant.class);
		Mockito.when(variant.getName()).thenReturn("class1");
		
		Mockito.when(v.getVariants()).thenReturn(Arrays.asList(variant));
		
		VariabilityStereotype a = new VariabilityStereotype(v);
		
		op.forNote().addVariability(idNote, a ).build();
		
		op.forClass().createClass(employee).linkToNote(idNote);
		
		Architecture arq = givenAArchitecture2("note");
		
		assertEquals(1, arq.getAllVariabilities().size());
	}


}