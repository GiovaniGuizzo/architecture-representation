package mestrado.arquitetura.helpers.test;

import static org.junit.Assert.*;

import org.junit.Test;

import arquitetura.exceptions.ModelIncompleteException;
import arquitetura.exceptions.ModelNotFoundException;
import arquitetura.helpers.Uml2Helper;
import arquitetura.helpers.Uml2HelperFactory;

/**
 * 
 * @author edipofederle
 *
 */
public class Uml2HelperFactoryTest {
	
	@Test
	public void shouldBeASingleton() throws ModelNotFoundException, ModelIncompleteException{
		Uml2Helper uml2Helper1 = Uml2HelperFactory.getUml2Helper();
		Uml2Helper uml2Helper2 = Uml2HelperFactory.getUml2Helper();
		
		assertSame(uml2Helper1, uml2Helper2);
	}
	
	@Test
	public void shouldHaveSMartyProfileWhenGetUml2Helper() throws ModelNotFoundException, ModelIncompleteException{
		Uml2Helper uml2Helper1 = Uml2HelperFactory.getUml2Helper();
		uml2Helper1.getSMartyProfile();
	}

}