//package operationsOverElementsTests;
//
//import static org.junit.Assert.assertEquals;
//import main.GenerateArchitecture;
//import mestrado.arquitetura.helpers.test.TestHelper;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import arquitetura.builders.ArchitectureBuilder;
//import arquitetura.representation.Architecture;
//import arquitetura.representation.Attribute;
//import arquitetura.representation.Class;
//import arquitetura.representation.Method;
//
//
///**
// * ALERTA: Estes testes são LENTOS. Então não rode eles enquanto desenvolve. Recomenda-se rodar essa classe 
// * quando feita alterações na manipulações de elementos (ex: mover um atributo de uma classe para outra).
// * 
// * 
// * @author edipofederle<edipofederle@gmail.com>
// *
// */
//public class OperationOnClassesTest extends TestHelper {
//	
//	private Architecture architecture;
//	private GenerateArchitecture generate;
//
//	private Class bowlingGame;
//	private Class pongGame;
//	
//	@Before
//	public void setUp() throws Exception{
//		String uriToArchitecture = getUrlToModel("agmfinal/agm");
//		architecture = new ArchitectureBuilder().create(uriToArchitecture);
//		
//		 bowlingGame = architecture.findClassByName("BowlingGame").get(0);
//		 pongGame = architecture.findClassByName("PongGame").get(0);
//		
//		generate = new GenerateArchitecture();
//	}
//	
//	
//	@Test
//	public void removeAttributeFromClass() throws Exception {
//		assertEquals(2, bowlingGame.getAllAttributes().size());
//		
//		Attribute attrGameCode = bowlingGame.findAttributeByName("gameCode");
//		bowlingGame.removeAttribute(attrGameCode);
//		
//		assertEquals(1, bowlingGame.getAllAttributes().size());
//		
//		
//		generate.generate(architecture, "saidaTeste1");
//		
//		Architecture genereted = givenAArchitecture2("saidaTeste1");
//		assertEquals(1, genereted.findClassByName("BowlingGame").get(0).getAllAttributes().size());
//		
//	}
//	
//	@Test
//	public void moveAttributeFromClass() throws Exception {
//		
//		assertEquals(2, bowlingGame.getAllAttributes().size());
//		assertEquals(3, pongGame.getAllAttributes().size());
//		
//		Attribute attrGameCode = bowlingGame.findAttributeByName("gameCode");
//		bowlingGame.moveAttributeToClass(attrGameCode, pongGame);
//		
//		assertEquals(1, bowlingGame.getAllAttributes().size());
//		
//		
//		generate.generate(architecture, "saidaTeste2");
//		
//		Architecture genereted = givenAArchitecture2("saidaTeste2");
//		assertEquals(1, genereted.findClassByName("BowlingGame").get(0).getAllAttributes().size());
//		assertEquals(4, genereted.findClassByName("PongGame").get(0).getAllAttributes().size());
//	}
//	
//	
//	@Test
//	public void removeMethodFromClass() throws Exception {
//		
//		assertEquals(4, bowlingGame.getAllMethods().size());
//		
//		Method loadGameMethod = bowlingGame.findMethodByName("loadGame");
//		bowlingGame.removeMethod(loadGameMethod);
//		
//		assertEquals(3, bowlingGame.getAllMethods().size());
//		
//		generate.generate(architecture, "saidaTeste3");
//		
//		Architecture genereted = givenAArchitecture2("saidaTeste3");
//		assertEquals(3, genereted.findClassByName("bowlingGame").get(0).getAllMethods().size());
//	}
//	
//	@Test
//	public void moveMethodFromClass() throws Exception{
//		assertEquals(4, bowlingGame.getAllMethods().size());
//		
//		Method loadGameMethod = bowlingGame.findMethodByName("loadGame");
//		bowlingGame.moveMethodToClass(loadGameMethod, pongGame);
//		
//		assertEquals(3, bowlingGame.getAllMethods().size());
//		
//		generate.generate(architecture, "saidaTeste4");
//		
//		Architecture genereted = givenAArchitecture2("saidaTeste4");
//		assertEquals(3, genereted.findClassByName("bowlingGame").get(0).getAllMethods().size());
//		assertEquals(5, genereted.findClassByName("pongGame").get(0).getAllMethods().size());
//	}
//
//}
