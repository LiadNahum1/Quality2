import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class FileSystemTest {
    private FileSystem fileSystem;
    private String [] name;
    private ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        fileSystem = new FileSystem(100);
        name = new String[2];
        name[0] = "root"; name[1] = "file1";
        fileSystem.file(name, 2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void dir() {
        //name doesn't start with root - an exception is expected
        name[0] = "not root"; name[1] = "dir";
        try {
            fileSystem.dir(name);
            assertTrue(false);
        }
        catch (BadFileNameException e) {
            assertTrue(true);
        }

        //fileSystem contains file named file1 - an exception is expected
        name[0] = "root"; name[1] = "file1";
        try {
            fileSystem.dir(name);
            assertTrue(false);
        }
        catch (BadFileNameException e) {
            assertTrue(true);
        }

        //name is legal - check is dir exists after adding
        name[0] = "root"; name[1]="dir";
        try {
            fileSystem.dir(name);
            assertTrue(fileSystem.DirExists(name) != null );
        } catch (BadFileNameException e) {
            assertTrue(false);
        }
    }

    @Test
    public void disk() {
    }

    @Test
    public void file() {
    }

    @Test
    public void lsdir() {
    }

    @Test
    public void rmfile() {
        //file exists
        int freeSpace = fileSystem.fileStorage.countFreeSpace();
        name[0] = "root"; name[1]="file1";
        Leaf file = fileSystem.FileExists(name);
        fileSystem.rmfile(name);
        assertTrue(fileSystem.fileStorage.countFreeSpace() > freeSpace); //TODO is it okay?

        //file doen't exist
        freeSpace = fileSystem.fileStorage.countFreeSpace();
        name[0] = "root"; name[1]="file doesnt exist";
        fileSystem.rmfile(name);
        assertEquals(freeSpace, fileSystem.fileStorage.countFreeSpace());

    }

    @Test
    public void rmdir() {
    }

    @Test
    public void fileExists() {
    }

    @Test
    public void dirExists() {
    }
}