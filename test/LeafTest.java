import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import system.FileSystem;
import system.Leaf;

import static org.junit.Assert.*;

public class LeafTest {
    private FileSystem fileSystem;
    private String [] name;

    @Before //before each test
    public void setUp() throws Exception {
        fileSystem = new FileSystem(100);
        name = new String[3];
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        fileSystem.file(name , 2);
    }

    @Test
    public void getPath(){
        Leaf leaf = fileSystem.FileExists(name);
        String[] output = leaf.getPath();
        for (int index = 0; index < output.length; index++)
            assertTrue(output[index].equals(name[index]));
    }
}
