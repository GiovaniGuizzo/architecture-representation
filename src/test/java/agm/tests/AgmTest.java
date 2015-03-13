package agm.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import mestrado.arquitetura.helpers.test.TestHelper;

import org.junit.Before;
import org.junit.Test;

import arquitetura.builders.ArchitectureBuilder;
import arquitetura.exceptions.ClassNotFound;
import arquitetura.exceptions.InterfaceNotFound;
import arquitetura.exceptions.PackageNotFound;
import arquitetura.representation.Architecture;
import arquitetura.representation.Attribute;
import arquitetura.representation.Class;
import arquitetura.representation.Interface;
import arquitetura.representation.Method;
import arquitetura.representation.Package;
import arquitetura.representation.Variability;

public class AgmTest extends TestHelper {
	
	private Architecture architecture;
	
	@Before
	public void setUp() throws Exception{
		String uriToArchitecture = getUrlToModel("agmfinal/agm");
		architecture = new ArchitectureBuilder().create(uriToArchitecture);
	}
	
	@Test
	public void deveCarregarTodasAsClasses(){
		Set<Class> all = new HashSet<Class>();
		for(Package p : architecture.getAllPackages())
			all.addAll(p.getAllClasses());
		assertEquals("Deve ter 31 classes", 31, all.size());
	}
	
	/* Classes Stereotypes */
	
	@Test
	public void testClasseBowlingGameStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("BowlingGame").get(0);
		
		assertEquals("BowlingGame",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "bowling");
	}
	
	@Test
	public void testClassePongGameStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("PongGame").get(0);
		
		assertEquals("PongGame",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "pong");
	}
	
	@Test
	public void testClasseBricklesGameStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("BricklesGame").get(0);
		
		assertEquals("BricklesGame",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "brickles");
	}
	
	@Test
	public void testClasseGameStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("Game").get(0);
		assertEquals("Game",klass.getName());
		assertEquals("mandatory",klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "play");
		assertTrue(klass.isVariationPoint());
	}
	
	@Test
	public void testClasseGameMgrStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("GameMgrTeste").get(0);
		assertEquals("GameMgrTeste",klass.getName());
	}
	
	@Test
	public void testClasseMovableSpritesStereotypes()throws ClassNotFound{
		Class klass = architecture.findClassByName("MovableSprites").get(0);
		
		assertEquals("MovableSprites",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "collision", "movement");
		assertTrue(klass.isVariationPoint());
	}


	@Test
	public void testClasseVelocityStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("Velocity").get(0);
		
		assertEquals("Velocity",klass.getName());
		assertNull(klass.getVariantType());
		
		assertContainsConcern(klass.getOwnConcerns(), "movement");
	}
	
	@Test
	public void testClassePuckStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("Puck").get(0);
		
		assertEquals("Puck",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		assertFalse(klass.isVariationPoint());
		
		assertContainsConcern(klass.getOwnConcerns(), "play");
	}
	
	@Test
	public void testClasseWallStereotypes()throws ClassNotFound{
		Class klass = architecture.findClassByName("Wall").get(0);
		
		assertEquals("Wall",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		assertFalse(klass.isVariationPoint());
		
		assertContainsConcern(klass.getOwnConcerns(), "brickles");
	}
	
	@Test
	public void testClassePaddleStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("Paddle").get(0);
		
		assertEquals("Paddle",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		assertFalse(klass.isVariationPoint());
		
		assertContainsConcern(klass.getOwnConcerns(), "play");
	}
	
	@Test
	public void testClasseFloorStereotypes() throws ClassNotFound{
		Class klass = architecture.findClassByName("Floor").get(0);
		
		assertEquals("Floor",klass.getName());
		assertEquals("alternative_OR",klass.getVariantType());
		assertFalse(klass.isVariationPoint());
		
		assertContainsConcern(klass.getOwnConcerns(), "brickles");
	}
	
	
	/* Classes Stereotypes */
	
	@Test
	public void deveCarregarTodasAsInterfaces(){
		Set<Interface> all = new HashSet<Interface>();
		for(Package p : architecture.getAllPackages())
			all.addAll(p.getAllInterfaces());
		assertEquals("Deve ter 15 interfaces", 15, all.size());
	}
	
	@Test
	public void deveCarregarTodasAsVariabilidades(){
		assertEquals("Deve ter 5 variabilidades", 5, architecture.getAllVariabilities().size());
	}
	
	@Test
	public void deveCarregarTodosOsPacotes(){
		assertEquals(9, architecture.getAllPackages().size());
	}
	
	@Test
	public void deveCarregarTodosOsInteresses(){
		assertEquals("Deve ter 11 interesses", 11, architecture.getAllConcerns().size());
	}
	
	@Test
	public void classeMovableSpritesDeveTerDoisInteresses() throws ClassNotFound{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		assertEquals(2,movableSprites.getOwnConcerns().size());
		
		assertContainsConcern(movableSprites.getOwnConcerns(), "movement", "collision");
	}
	
	@Test
	public void classeMovableSpritesDeveSerUmPontoDeVariacao() throws ClassNotFound{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		assertTrue(movableSprites.isVariationPoint());
	}
	
	@Test
	public void classeMovableSpritesDeveSerAlternativeOr() throws ClassNotFound{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		assertEquals("alternative_OR", movableSprites.getVariantType());
	}
	
	@Test
	public void classeMovableSpritesDeveTerTresAttributos() throws ClassNotFound{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		assertEquals(3,movableSprites.getAllAttributes().size());		
	}
	
	@Test
	public void classeMovableSpritesIsMovingAttributeCorrectProps() throws Exception{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		Attribute isMoving = movableSprites.findAttributeByName("isMoving");
		
		assertNotNull(isMoving);
		assertEquals("isMoving", isMoving.getName());
		assertEquals("Boolean", isMoving.getType());
		
		assertEquals("Atributo deve ter 1 interesse", 1, isMoving.getOwnConcerns().size());
		assertContainsConcern(isMoving.getOwnConcerns(), "movement");
		
	}
	
	@Test
	public void classeMovableSpritesDeveTerSeteMetodos() throws Exception{ 
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		
		assertEquals("Deve ter 7 metodos", 7, movableSprites.getAllMethods().size());
	}
	
	@Test
	public void classeMovableSpritesMovingMetodoCorrectProps() throws Exception{
		Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		Method moving = movableSprites.findMethodByName("moving");
		
		assertNotNull(moving);
		assertEquals("moving", moving.getName());
		assertEquals("Boolean", moving.getReturnType());
		
		assertEquals(1, moving.getOwnConcerns().size());
		assertContainsConcern(moving.getOwnConcerns(), "movement");
	}
	
	/* Interfaces */
	
	@Test
	public void interfaceIGameBoardData() throws InterfaceNotFound{
		Interface iGameBoardData = architecture.findInterfaceByName("IGameBoardData");
		
		assertNotNull(iGameBoardData);
		assertEquals(1, iGameBoardData.getOperations().size());
		
		Method operation = iGameBoardData.getOperations().iterator().next();
		assertEquals("getGameBoard", operation.getName());
		
		//Params
		assertEquals(1, operation.getParameters().size());
		assertEquals("gameCode",operation.getParameters().get(0).getName());
		assertEquals("String", operation.getParameters().get(0).getType());
		assertEquals("", operation.getReturnType());
		
		//Concerns
		assertEquals(1, operation.getOwnConcerns().size());
		assertContainsConcern(operation.getOwnConcerns(), "play");
	}
	
	/* Interfaces */
	
	
	/* Variabilidades */
	
	@Test
	public void movableSpriteVariability(){
		Variability var1 = architecture.getAllVariabilities().get(0);
		assertEquals("movable_sprite", var1.getName());
		
		assertEquals("1", var1.getMinSelection());
		assertEquals("2", var1.getMaxSelection());
		assertEquals("DESIGN_TIME", var1.getBindingTime());
		assertNotNull(var1.getVariationPoint());
		for(arquitetura.representation.Variant v : var1.getVariants()){
			System.out.println(v.getName());
		}
		assertEquals(2, var1.getVariants().size());
		
		assertEquals("Puck", var1.getVariants().get(0).getName());
		assertEquals("Paddle", var1.getVariants().get(1).getName());
		
		assertEquals("MovableSprites", var1.getVariationPoint().getVariationPointElement().getName());
	}
	
	@Test
	public void gameVariability(){
		Variability var2 = architecture.getAllVariabilities().get(1);
		assertEquals("game", var2.getName());
		
		assertEquals("1", var2.getMinSelection());
		assertEquals("3", var2.getMaxSelection());

		assertEquals("DESIGN_TIME", var2.getBindingTime());
		
		assertNotNull(var2.getVariationPoint());
		assertEquals(3, var2.getVariants().size());
		assertEquals("PongGame", var2.getVariants().get(0).getName());
		assertEquals("BricklesGame", var2.getVariants().get(1).getName());
		
		assertEquals("Game", var2.getVariationPoint().getVariationPointElement().getName());
	}
	
	@Test
	public void pacoteTeste() throws PackageNotFound{
		arquitetura.representation.Package p = architecture.findPackageByName("GameMgr");
		System.out.println(p.getOwnConcerns());
	}
	
	@Test
	public void SpritVariability(){
		Variability var = architecture.getAllVariabilities().get(2);
		assertEquals("sprit", var.getName());
		
		assertEquals("1", var.getMinSelection());
		assertEquals("2", var.getMaxSelection());
		assertEquals("DESIGN_TIME", var.getBindingTime());
		assertNotNull(var.getVariationPoint());
		
		
		assertTrue(var.getVariationPoint().getVariationPointElement().getVariant().getVariabilities().isEmpty());
		
		assertEquals(2, var.getVariants().size());
		
		assertTrue(var.getVariants().get(0).getVariabilities().contains(var));
		
		assertEquals("MovableSprites", var.getVariants().get(1).getName());
		assertEquals("StationarySprite", var.getVariants().get(0).getName());
		
		assertEquals("Sprit", var.getVariationPoint().getVariationPointElement().getName());
	}
	
	@Test
	public void rankingVariability(){
		Variability var = architecture.getAllVariabilities().get(3);
		assertEquals("ranking", var.getName());
		
		assertEquals("0", var.getMinSelection());
		assertEquals("1", var.getMaxSelection());

		assertEquals("DESIGN_TIME", var.getBindingTime());
		
		assertNull(var.getVariationPoint());
		assertEquals(1, var.getVariants().size());
		assertEquals("Player", var.getVariants().get(0).getName());
	}
	
	@Test
	public void stationaySpritVariability(){
		Variability var = architecture.getAllVariabilities().get(4);
		assertEquals("stationay_sprit", var.getName());
		
		assertEquals("1", var.getMinSelection());
		assertEquals("4", var.getMaxSelection());

		assertEquals("DESIGN_TIME", var.getBindingTime());
		
		assertNotNull(var.getVariationPoint());
		assertEquals(5, var.getVariants().size());
		assertEquals("Ceiling", var.getVariants().get(0).getName());
		assertEquals("Floor", var.getVariants().get(1).getName());
		assertEquals("Wall", var.getVariants().get(2).getName());
		assertEquals("Brick", var.getVariants().get(3).getName());
		assertEquals("BrickPile", var.getVariants().get(4).getName());
	}
	
	/* Variabilidades */
	
	
	@Test
	public void teste_1() throws Exception{

		
//		System.out.println("Interfaces:"+ architecture.getAllInterfaces());
//		System.out.println("Classes:"+architecture.getAllClasses());
//		System.out.println(architecture.getAllConcerns());
//		System.out.println(architecture.getAllPackages());
		
		arquitetura.representation.Class movableSprites = architecture.findClassByName("MovableSprites").get(0);
		//arquitetura.representation.Class player = architecture.findClassByName("Player").get(0);
		
		assertNotNull(movableSprites);
		assertTrue(movableSprites.isVariationPoint());
		
		//System.out.println(movableSprites.getVariationPoint().getVariabilities().get(0).getName());
		
//		for (Variability v : VariabilityFlyweight.getInstance().getVariabilities()) {
//			System.out.println("Nome:"+v.getName() + "| ClassOwner:"+v.getOwnerClass());
//			for(Variant variant : v.getVariants()){
//				System.out.println("\t"+variant.getName());
//			}
//			 
//		}
	
	}

}
