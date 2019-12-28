import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import system.FileSystem;
import system.Leaf;
import system.OutOfSpaceException;
import system.Tree;

import static org.junit.Assert.*;

public class SpaceTest {
    private FileSystem fileSystem;
    private String [] name;
    Leaf leaf;

    @Before //before each test
    public void setUp() throws Exception {
        fileSystem = new FileSystem(100);
        name = new String[1];
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        leaf = new Leaf("temp", 10);
    }

    @Test
    public void Alloc(){
        try {
            FileSystem.fileStorage.Alloc(10, this.leaf);
        }
        catch (OutOfSpaceException exception) {
            assertTrue(false);
        }
        assertTrue(FileSystem.fileStorage.countFreeSpace() == 90);
        assertFalse(FileSystem.fileStorage.countFreeSpace() > 90);
        for (int index = 0; index < 10; index++){
            assertTrue(FileSystem.fileStorage.getAlloc()[index] == this.leaf);
        }
    }

    @Test
    public void Dealloc(){
        FileSystem.fileStorage.Dealloc(this.leaf);
        assertTrue(FileSystem.fileStorage.countFreeSpace() == 100);
        assertFalse(FileSystem.fileStorage.countFreeSpace() < 100);
        for (int index = 0; index < 10; index++){
            assertFalse(FileSystem.fileStorage.getAlloc()[index] == this.leaf);
        }
    }
}
