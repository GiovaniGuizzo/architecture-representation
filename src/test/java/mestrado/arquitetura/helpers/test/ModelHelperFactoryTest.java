package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.*;

import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.helpers.ModelHelper;
import arquitetura.helpers.ModelHelperFactory;

/**
 * 
 * @author edipofederle
 *
 */
public class ModelHelperFactoryTest {
	
	@Test
	public void shouldBeASingleton() throws ModelNotFoundException, ModelIncompleteException{
		ModelHelper modelHelper1 = ModelHelperFactory.getModelHelper();
		ModelHelper modelHelper2 = ModelHelperFactory.getModelHelper();
		
		assertSame(modelHelper1, modelHelper2);
	}

}