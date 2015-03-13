package config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import arquitetura.io.ReaderConfig;

public class ReaderConfigTest2 {
	
	@Before
	public void setUp() {
		ReaderConfig.load();
	}
	
	@Test
	public void comPerfilSetado() {
		assertTrue(ReaderConfig.hasSmartyProfile());
	}
	
	@Test
	public void shouldDirectorySaveModelsExists() {
		File dirSaveModels = new File(ReaderConfig.getDirTarget());
		assertTrue(dirSaveModels.exists());
		assertTrue(dirSaveModels.isDirectory());
	}
	
	@Test
	public void shouldReturnPathToPatternsProfile() {
		String path = ReaderConfig.getPathToProfilePatterns();
		assertNotNull(path);
	}



	@Test
	public void shouldProfileFileExistsOnConfiguredPath() {
		File profile = new File(ReaderConfig.getPathToProfileSMarty());
		assertTrue(profile.exists());
	}
}
