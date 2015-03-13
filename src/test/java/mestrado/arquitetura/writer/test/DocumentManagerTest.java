package mestrado.arquitetura.writer.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.touml.DocumentManager;

public class DocumentManagerTest {
	
	@Test
	public void shouldCreateADocument() throws ModelNotFoundException, ModelIncompleteException{
		String newModelName  = "simplesNew";
		DocumentManager documentManager = new DocumentManager(newModelName);
		
		assertNotNull(documentManager);
		assertNotNull(documentManager.getDocNotation());
		assertNotNull(documentManager.getDocUml());
	}
}