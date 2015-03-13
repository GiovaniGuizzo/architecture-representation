package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import arquitetura.io.ReaderConfig;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReaderConfig.class)
public class ReaderConfigTest {
    
    @Before
    public void setUp(){
	ReaderConfig.load();
    }

    @Test
    public void shouldReturnPathToDirectorySaveModels() {
	String dir = ReaderConfig.getDirTarget();
	assertEquals("manipulation/", dir);
    }

    @Test
    public void shouldDirectorySaveModelsExists() {
	File dirSaveModels = new File(ReaderConfig.getDirTarget());
	assertTrue(dirSaveModels.exists());
	assertTrue(dirSaveModels.isDirectory());
    }

    @Test
    public void shouldReturnPathToDirectoryExportModels() {
	String dir = ReaderConfig.getDirExportTarget();
	assertNotNull(dir);
    }

    @Test
    public void shouldDirectoryExportModelsExists() {
	File exportModels = new File(ReaderConfig.getDirExportTarget());
	assertTrue(exportModels.exists());
	assertTrue(exportModels.isDirectory());
    }

    @Test
    public void shouldReturnPathToProfile() {
	String path = ReaderConfig.getPathToProfileSMarty();
	assertNotNull(path);
    }

    @Test
    public void shouldReturnPathToRelationshipProfile() {
	String path = ReaderConfig.getPathToProfileRelationships();
	assertNotNull(path);
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

    @Test
    public void dontReturnConfigFileConfIfISetOne6() {
	ReaderConfig.setPathProfileRelationship("new/path/");
	assertEquals("new/path/", ReaderConfig.getPathToProfileRelationships());
    }

    @Test
    public void semPerfilSetado() {
	PowerMockito.mockStatic(ReaderConfig.class);
	PowerMockito.when(ReaderConfig.getPathToProfileSMarty()).thenReturn(null);
	PowerMockito.when(ReaderConfig.getPathToProfileConcerns()).thenReturn(null);
	assertFalse(ReaderConfig.hasSmartyProfile());
    }

    @Test
    public void comPerfilSetado() {
	assertTrue(ReaderConfig.hasSmartyProfile());
    }

    @Test
    public void getPathToTemplateModelsDirectory() {
	String path = ReaderConfig.getPathToTemplateModelsDirectory();
	assertNotNull(path);
    }

    @Test
    public void dontReturnConfigFileConfIfISetOne1() {
	ReaderConfig.setPathToTemplateModelsDirectory("new/path/");
	assertEquals("new/path/", ReaderConfig.getPathToTemplateModelsDirectory());
    }

    @Test
    public void dontReturnConfigFileConfIfISetOne2() {
	ReaderConfig.setPathToProfileConcerns("new/path/");
	assertEquals("new/path/", ReaderConfig.getPathToProfileConcerns());
    }

    @Test
    public void dontReturnConfigFileConfIfISetOne3() {
	ReaderConfig.setPathToProfileSMarty("new/path/");
	assertEquals("new/path/", ReaderConfig.getPathToProfileSMarty());
    }

    @Test
    public void dontReturnConfigFileConfIfISetOne4() {
	ReaderConfig.setDirExportTarget("new/path/");
	assertEquals("new/path/", ReaderConfig.getDirExportTarget());
    }

    @Test
    public void dontReturnConfigFileConfIfISetOne5() {
	ReaderConfig.setDirTarget("new/path/");
	assertEquals("new/path/", ReaderConfig.getDirTarget());
    }

    @Test
    public void shouldReturnsTrueIfHasSmartyProfile() {
	ReaderConfig.setPathToProfileSMarty("/new/path");
	assertTrue(ReaderConfig.hasSmartyProfile());
    }

    @Test
    public void shouldReturnsFalseIfHasSmartyProfile() {
	ReaderConfig.setPathToProfileSMarty("");
	assertFalse(ReaderConfig.hasSmartyProfile());
    }

    @Test
    public void shouldChancePathToConfigureFile() {
	ReaderConfig.setPathToConfigurationFile("src/test/java/resources/application.yaml");
	ReaderConfig.load();
	assertEquals("src/test/java/resources/application.yaml", ReaderConfig.getNewPathToConfigurationFile());
    }

}