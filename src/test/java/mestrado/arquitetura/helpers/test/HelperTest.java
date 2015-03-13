package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;


import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.Uml2Helper;
import arquitetura.helpers.Uml2HelperFactory;

/**
 * 
 * @author edipofederle
 *
 */
public class HelperTest extends TestHelper {
	
	private Uml2Helper uml2Helper;
	
	@Before
	public void setUp() throws ModelNotFoundException, ModelIncompleteException{
		uml2Helper = Uml2HelperFactory.getUml2Helper();
	}
	
	@After
	public void tearDown(){
		File profileFile = new File("src/test/java/resources/modelo12.uml");
		if (profileFile != null)
			profileFile.delete();
	}

	@Test
	public void shouldCreateStereotype(){
		Profile profile = givenAProfile("myProfile");
		assertNotNull(profile);
		Stereotype s = uml2Helper.createStereotype(profile, "stereotypeTest", false);
		assertNotNull(s);
		assertEquals(s.getName(), "stereotypeTest");
	}

	private Profile givenAProfile(String name) {
		Profile profile = uml2Helper.createProfile(name);
		return profile;
	}
	
	@Test
	public void shouldSaveProfile() throws IOException{
		Profile profile = givenAProfile("myProfile");
		URI profileURI = URI.createFileURI("arch/src/test/java/resources/"+profile.getName());
		uml2Helper.saveResources(profile, profileURI);
		File profileFile = new File("arch/src/test/java/resources/" + profile.getName() + ".uml");
		assertTrue(profileFile.exists());
	}
	
	@Test
	public void testCreateEnumeration(){
		Profile prof = givenAProfile("ecore");
		uml2Helper.createEnumeration(prof, "VisibilityKind");
		assertEquals(1, prof.getOwnedElements().size());
		assertEquals("VisibilityKind", prof.getPackagedElements().get(0).getName());
	}

	@Test
	public void testCreateLiteralEnumeration(){
		Profile prof = givenAProfile("ecore");
		Enumeration vb = uml2Helper.createEnumeration(prof, "VisibilityKind");
		uml2Helper.createEnumerationLiteral(vb, "Unspecified");
		assertEquals("Unspecified", vb.getOwnedLiterals().get(0).getName());
	}
	
	@Test
	public void testCreateGeneralizationStereotype(){
		Profile prof = givenAProfile("ecore");
		Stereotype eAttributeStereotype =uml2Helper.createStereotype(prof, "eAttribute", true);
		Stereotype eStructuralFeatureStereotype = uml2Helper.createStereotype(prof, "EStructuralFeature", true);
		uml2Helper.createGeneralization(eAttributeStereotype, eStructuralFeatureStereotype);
	}
	
	@Test
	public void testCreateAttr() throws ModelNotFoundException{
		Profile prof = givenAProfile("ecore");
		Stereotype eStructuralFeatureStereotype = uml2Helper.createStereotype(prof, "EStructuralFeature", true);
		Type booleanPrimitiveType = uml2Helper.getPrimitiveType("Boolean");
		Property isTransientProperty = uml2Helper.createAttribute(eStructuralFeatureStereotype, "isTransient", booleanPrimitiveType, 0, 1);
		assertNotNull(isTransientProperty);
	}
	
	@Test
	public void testReference() throws ModelNotFoundException{
		Profile prof = givenAProfile("ecore");
		uml2Helper.referenceMetaclass(prof, UMLPackage.Literals.PROPERTY.getName());
	}
	
	@Test
	public void testCreateExtension() throws ModelNotFoundException{
		Profile prof = givenAProfile("ecore");
		Class propertyMetaclass = uml2Helper.referenceMetaclass(prof, UMLPackage.Literals.PROPERTY.getName());
		Stereotype eAttributeStereotype = uml2Helper.createStereotype(prof, "eAttribute", true);
		uml2Helper.createExtension(propertyMetaclass, eAttributeStereotype, false);
		assertEquals(1, prof.getMetaclassReferences().size());
	}
	
	@Test
	public void testApplyProfile() throws IOException, ModelNotFoundException, ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Profile profile = givenAProfile("ecore");
		profile.define();
		Package epo2Model =  uml2Helper.load(getUrlToModel("smarty.profile").toString());
		uml2Helper.applyProfile(epo2Model, profile);
		assertNotNull(epo2Model.getAllAppliedProfiles());
		assertNotNull(epo2Model.getAllAppliedProfiles().size());
		assertEquals("Model name should be ecore", "ecore", epo2Model.getAllAppliedProfiles().get(0).getName());
	}
	
}