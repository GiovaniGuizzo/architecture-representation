package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.Before;
import org.junit.Test;

import arquitetura.exceptions.EnumerationNotFoundException;
import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.ModelHelperFactory;
import arquitetura.helpers.Uml2Helper;
import arquitetura.helpers.Uml2HelperFactory;
/**
 * 
 * @author edipofederle
 *
 */
public class Uml2HelperTest  extends TestHelper{
	
	private static Uml2Helper uml2Helper;
	
	@Before
	public void setUp() throws ModelNotFoundException, ModelIncompleteException{
		uml2Helper = Uml2HelperFactory.getUml2Helper();
		modelHelper = ModelHelperFactory.getModelHelper();
	}
	
	@Test
	public void shouldBeSameInstance() throws ModelNotFoundException, ModelIncompleteException{
		uml2Helper = Uml2HelperFactory.getUml2Helper();
	}
	
	@Test(expected=ModelNotFoundException.class)
	public void shouldRaiseExecptioWhenModelNotFound() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		URI uri = URI.createFileURI("modeloNaoExiste.uml");
		uml2Helper.load(uri.toString());
	}
	
	
	@Test
	public void shouldLoadAModelWithTwoProfiles() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("testArch");
		Package model = uml2Helper.load(uri.toString());
		assertEquals("should have two profiles applied", 2, model.getAppliedProfiles().size());
	}
	
	@Test(expected=ModelIncompleteException.class)
	public  void shouldRaiseModelNotFoundExceptionWhenModelIncomplete() throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		String uri = getUrlToModel("modelIncompleto");
		uml2Helper.load(uri);
	}
	
	@Test
	public void shouldGetEnumByName() throws ModelNotFoundException, ModelIncompleteException, EnumerationNotFoundException , SMartyProfileNotAppliedToModelExcepetion{
		Profile profile = (Profile) uml2Helper.load(getUrlToModel("smarty.profile"));
		PackageableElement enumm = uml2Helper.getEnumerationByName(profile, "BindingTime");
		assertTrue(enumm.eClass().equals(UMLPackage.Literals.ENUMERATION));
	}
	
	@Test
	public void shouldGetEnumLiteralByName() throws ModelNotFoundException, ModelIncompleteException, EnumerationNotFoundException{
		 EnumerationLiteral a = uml2Helper.getLiteralEnumeration("DESIGN_TIME");
		 assertNotNull(a);
		 assertEquals("DESIGN_TIME", a.getName());
	}
	
	@Test
	public void whenProfileIsntDefinedDefineIt() throws ModelNotFoundException, ModelIncompleteException, SMartyProfileNotAppliedToModelExcepetion{
		Profile profile = (Profile) uml2Helper.load(getUrlToModel("profileNotdefined.profile"));
		assertTrue(profile.isDefined());
	}
	
	@Test
	public void shouldGetPrimitiveType() throws ModelNotFoundException{
		Type str = uml2Helper.getPrimitiveType("String");
		assertNotNull(str);
		assertEquals("String", str.getName());
	}
	
	@Test
	public void shouldGetPrimitiveTypeWithSpace() throws ModelNotFoundException{
		Type str = uml2Helper.getPrimitiveType("String  ");
		assertNotNull(str);
		assertEquals("String", str.getName());
		
		Type integer = uml2Helper.getPrimitiveType(" Integer");
		assertNotNull(integer);
		assertEquals("Integer", integer.getName());
	}
	
	@Test
	public void shouldGetPrimitiveTypeIgnoringCase() throws ModelNotFoundException{
		Type str = uml2Helper.getPrimitiveType("string");
		assertNotNull(str);
		assertEquals("String", str.getName());
		
		Type integer = uml2Helper.getPrimitiveType("integer");
		assertNotNull(integer);
		assertEquals("Integer", integer.getName());
		
		Type integer2 = uml2Helper.getPrimitiveType(" InTeger");
		assertNotNull(integer2);
		assertEquals("Integer", integer2.getName());
	}
	
	@Test
	public void givenAModelshouldReturnUriToPerfil() throws ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package model = givenAModel("variability");
		EList<Profile> profiles = model.getAppliedProfiles();
		assertEquals(1, profiles.size());
		assertEquals("smartyProfile", profiles.get(0).getName());
	}
	
}