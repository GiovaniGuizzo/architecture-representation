package io.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import arquitetura.io.CopyFile;

public class CopyFileTest {
	
	private File target;
	private File f;
	
	@Before
	public void setUp() throws IOException{
		File source = new File("source");
		source.mkdir();
		
		f = new File("source/godisdead.txt");
		f.createNewFile();
		target = new File("dest");
		target.mkdir();
	}
	
	@Test
	public void shouldCopyFileFromSourceToTarget() throws FileNotFoundException{
		
		assertNotNull(f);
		File newFile = new File("dest/godisdead.txt");
		CopyFile.copyFile(f, newFile);
		
		assertTrue(new File("dest/godisdead.txt").exists());
		assertTrue(new File("source/godisdead.txt").exists());
	}
	
	@After
	public void tearDown(){
		deleteDirectory(new File("source"));
		deleteDirectory(new File("dest"));
	}
	
	//TODO Mover
	/**
	 * Delets a dir recursively deleting anything inside it.
	 * @param dir The dir to delete
	 * @return true if the dir was successfully deleted
	 */
	private static boolean deleteDirectory(File dir) {
	    if(! dir.exists() || !dir.isDirectory())    {
	        return false;
	    }

	    String[] files = dir.list();
	    for(int i = 0, len = files.length; i < len; i++)    {
	        File f = new File(dir, files[i]);
	        if(f.isDirectory()) {
	            deleteDirectory(f);
	        }else   {
	            f.delete();
	        }
	    }
	    return dir.delete();
	}
	
}
