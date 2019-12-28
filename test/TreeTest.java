import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import system.FileSystem;
import system.Tree;

import static org.junit.Assert.*;


public class TreeTest {
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
    public void GetChildByName(){
        String[] directory = {"root"};
        Tree tree = fileSystem.DirExists(directory);
        Tree output = tree.GetChildByName("docs");
        assertTrue(output.children.containsKey("file1"));
        assertFalse(output.children.containsKey("notExistFile"));
        output = tree.GetChildByName("directoryThatIsNotExist");
        assertTrue(output.children != null);
        assertTrue(output.children.isEmpty());

    }


}
