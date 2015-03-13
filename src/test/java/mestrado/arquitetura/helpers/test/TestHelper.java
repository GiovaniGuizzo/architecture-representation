package mestrado.arquitetura.helpers.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import junitx.framework.Assert;

import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.exceptions.SMartyProfileNotAppliedToModelExcepetion;
import arquitetura.helpers.ModelHelper;
import arquitetura.helpers.ModelHelperFactory;
import arquitetura.helpers.Uml2Helper;
import arquitetura.helpers.Uml2HelperFactory;
import arquitetura.io.ReaderConfig;
import arquitetura.representation.Architecture;
import arquitetura.representation.Class;
import arquitetura.representation.Concern;
import arquitetura.representation.Element;
import arquitetura.representation.Variant;
import arquitetura.representation.relationship.AssociationEnd;
import arquitetura.touml.DocumentManager;
import arquitetura.touml.Operations;

/**
 * 
 * @author edipofederle
 *
 */
public abstract class TestHelper {

	protected static Uml2Helper uml2Helper;
	protected static ModelHelper modelHelper;
	protected static String targetDir;
	protected static String targetDirExport;
	
	static{
	    	ReaderConfig.load();
		uml2Helper = Uml2HelperFactory.getUml2Helper();
		
		targetDir = ReaderConfig.getDirTarget();
		targetDirExport = ReaderConfig.getDirExportTarget();
			
		modelHelper = ModelHelperFactory.getModelHelper();
	}

	public static Package givenAModel(String modelName) throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion {
		String uri = getUrlToModel(modelName);
		String absolutePath = new File(uri).getAbsolutePath();
		Package epo2Model = uml2Helper.load(absolutePath);
		return epo2Model;
	}

	public static NamedElement givenAClass() throws ModelNotFoundException  , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package content = givenAModel("ExtendedPO2");
		List<org.eclipse.uml2.uml.Class> elementsClass = modelHelper.getAllClasses(content);
		return elementsClass.get(0);
	}
	/**
	 * Retorna URI para um resource dentro de "src/test/java/resources/"
	 * @param modelName. ex: "meuModel" ( sem a extensao do arquivo ).
	 * @return uri String
	 */
	protected static String getUrlToModel(String modelName) {
		return new File("src/test/java/resources/" + modelName + ".uml").getAbsolutePath(); 
	}

	protected static String getUriToResource(String resourcesNamee) {
		return URI.createFileURI(getUrlToModel(resourcesNamee)).toString();
	}
	
	public static Stereotype getStereotypeByName(String name) throws ModelNotFoundException , ModelIncompleteException , SMartyProfileNotAppliedToModelExcepetion{
		Package perfil = givenAModel("smartyProfile");
		return  perfil.getOwnedStereotype(name);
	}
	
	protected Architecture givenAArchitecture(String name) throws Exception {
		String uriToArchitecture6 = getUrlToModel(name);
		return  new ArchitectureBuilder().create(uriToArchitecture6);
	}
	
	protected Architecture givenAArchitecture2(String name) throws Exception {
		String uriToArchitecture6 = getUrlToModelManipulation(name);
		return new ArchitectureBuilder().create(uriToArchitecture6);
	}
	
	protected static String getUrlToModelManipulation(String modelName) {
		return targetDirExport+modelName+".uml";
	}

	protected <T extends Element> void assertContains(Set<T> list, String... expected) {
		requiredElements: for (String requiredElementName : expected)  {
				for (Element element : list)
					if (requiredElementName.equalsIgnoreCase(element.getName())) continue requiredElements;
				Assert.fail("list there is no element called " + requiredElementName);
			}
		}
	
	protected <T extends Element> void assertContains(List<T> list, String... expected) {
		requiredElements: for (String requiredElementName : expected)  {
				for (Element element : list)
					if (requiredElementName.equalsIgnoreCase(element.getName())) continue requiredElements;
				Assert.fail("list there is no element called " + requiredElementName);
			}
	}
	
	public void assertContainsConcern(Set<Concern> ownConcerns, String... expected) {
		requiredElements: for (String requiredElementName : expected)  {
			for (Concern element : ownConcerns)
				if (requiredElementName.equalsIgnoreCase(element.getName())) continue requiredElements;
			Assert.fail("list there is no concern called " + requiredElementName);
		}
	}
	
	
	
	protected boolean modelContainId(String modelName, String id){
		File file = new File(getUrlToModelManipulation(modelName));
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
			
			while (scanner.hasNextLine()) {
			    String line = scanner.nextLine();
			    if(line != null)
				    if(line.contains(id))
				        return true;
			}
			scanner.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			scanner.close();
		}
		
		return false;
	}
	
	public DocumentManager givenADocument(String outputModelName) throws ModelNotFoundException, ModelIncompleteException {
		DocumentManager documentManager = new DocumentManager(outputModelName);
		return documentManager;
	}

	protected String generateRandomWord(int wordLength) throws IOException {
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader("src/test/java/resources/dictionary.txt"));
		List<String> lines = new ArrayList<String>();
		String line = reader.readLine();

		while( line != null ) {
		    lines.add(line);
		    line = reader.readLine();
		}

		Random r = new Random();
		String randomString = lines.get(r.nextInt(lines.size()));
		
		return randomString;
	}
	
	/**
	 * 
	 * @param name
	 * @param idRootVpClass
	 * @return
	 */
	protected Variant givenAVariant(String name, String rootVpName, String variantName) {
		Variant mandatory = Variant.createVariant().withName(name)
				                   .andRootVp(rootVpName)
				                   .withVariantType(variantName)
				                   .build();
		return mandatory;
	}
	
	protected void generateClasses(Architecture a, Operations op) {
		for (Class klass : a.getClasses()) {
			op.forClass().createClass(klass).build();
		}
	}
	
	
	protected void assertBothAssociationEndIsFalse(AssociationEnd associationEnd1, AssociationEnd associationEnd2) {
		if((associationEnd1.isNavigable()) || (associationEnd2.isNavigable())){
			Assert.fail("Expect false but returns true");
		}
	}
	
	protected void assertBothAssociationEndIsTrue(AssociationEnd associationEnd1, AssociationEnd associationEnd2) {
		if((!associationEnd1.isNavigable()) || (!associationEnd2.isNavigable())){
			Assert.fail("Expect true but returns false");
		}
	}
		
}