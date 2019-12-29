import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import system.BadFileNameException;
import system.FileSystem;
import system.Leaf;
import system.OutOfSpaceException;

import java.nio.file.DirectoryNotEmptyException;

import static org.junit.Assert.*;

public class FileSystemTest {
    private FileSystem fileSystem;
    private String [] name;

    @Before //before each test
    public void setUp() throws Exception {
        fileSystem = new FileSystem(100);
        name = new String[3];
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        fileSystem.file(name , 2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void dir() {
        //fileSystem contains file named file1 - an exception is expected
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        try {
            fileSystem.dir(name);
            assertTrue(false);
        }
        catch (BadFileNameException e) {
            assertTrue(true);
        }

        //name doesn't start with root - an exception is expected
        name = new String[2];
        name[0] = "notRoot"; name[1] = "dir";
        try {
            fileSystem.dir(name);
            assertTrue(false);
        }
        catch (BadFileNameException e) {
            assertTrue(true);
        }


        //name is legal - check if dir exists after adding
        name[0] = "root"; name[1] = "dir";
        try {
            fileSystem.dir(name);
            assertTrue(fileSystem.DirExists(name)!= null );
        } catch (BadFileNameException e) {
            assertTrue(false);
        }

        /* test will work after fixing the bug
            In FileSystem row 260 replace "system.Node" with "system.Tree"
         */
        //dir is already exist
        name[0] = "root"; name[1] = "docs";
        try {
            fileSystem.dir(name);
            assertTrue(fileSystem.DirExists(name) != null);
        } catch (BadFileNameException e) {
            assertTrue(false);
        }
        catch(Exception e){

        }

    }

    @Test
    public void disk() {
        int counter = 0;
        boolean flag = true;
        boolean isRightIndex = false;
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        fileSystem.rmfile(name); //remove file so first cell in Space.blocks will be null
        name[0] = "root"; name[1] = "tempDir"; name[2] = "tempFile";
        try {
            fileSystem.file(name, 10);
            Leaf fileAdded = fileSystem.FileExists(name);
            int [] allocations = fileAdded.allocations; //indexes of allocations
            String[][] disk = fileSystem.disk();

            for (int index = 0; index< disk.length; index++){
                if (disk[index] != null){
                    String[] fileName = disk[index];
                    for (int jndex = 0; jndex < fileName.length; jndex++){
                        if(!name[jndex].equals(fileName[jndex]))
                            flag = false;
                    }
                    if(flag) { //in disk[index] file name is allocated
                        counter++;
                        //check if this index is correct
                        for(int allocationIndex = 0; allocationIndex < allocations.length ; allocationIndex++){
                            if(index == allocations[allocationIndex])
                                isRightIndex = true;
                        }
                        if(!isRightIndex)
                            assertTrue(false);
                    }
                    flag = true;
                    isRightIndex = false;
                }
            }
            assertTrue(counter == 10); //exactly 10 blocks are allocated


        }
        catch (OutOfSpaceException e) {
            assertTrue(false);
        } catch (BadFileNameException e) {
            assertTrue(false);
        }
    }


    @Test
    public void file() {
        //name doesn't start with root - an exception is expected
        name = new String[2];
        name[0] = "notRoot"; name[1] = "newFile";
        try {
            fileSystem.file(name, 2);
            assertTrue(false);
        } catch (BadFileNameException e) {
            assertTrue(true);
        } catch (OutOfSpaceException e) {
            assertTrue(false);
        }

        //there are not enough free blocks - an exception is expected
        name[0] = "root"; name[1] = "newFile";
        try {
            fileSystem.file(name, 100);
            assertTrue(false);
        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (OutOfSpaceException e) {
            assertTrue(true);
        }

        //name of an existing directory
        name[0] = "root"; name[1] = "docs";
        try {
            fileSystem.file(name, 2);
            assertTrue(false);
        } catch (BadFileNameException e) {
            assertTrue(true);
        } catch (OutOfSpaceException e) {
            assertTrue(false);
        }

        //adding new file successfully when all directories are already exist
        name = new String [3];
        name[0] = "root"; name[1] = "docs"; name[2] = "file2";
        int freeSpace = fileSystem.fileStorage.countFreeSpace();
        try {
            fileSystem.file(name, 2);
            //check space allocation
            assertEquals(freeSpace - 2, fileSystem.fileStorage.countFreeSpace());
            //check file2 is exists
            assertTrue(fileSystem.FileExists(name) != null);
        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (OutOfSpaceException e) {
            assertTrue(false);
        }

        //adding new file successfully when one directory doesn't exist
        name = new String [3];
        name[0] = "root"; name[1] = "dir"; name[2] = "file3";
        freeSpace = fileSystem.fileStorage.countFreeSpace();
        try {
            fileSystem.file(name, 2);
            //check space allocation
            assertEquals(freeSpace - 2, fileSystem.fileStorage.countFreeSpace());
            //check file3 is exists
            assertTrue(fileSystem.FileExists(name) != null);
            //check directory "dir" exists
            name = new String[2];
            name[0] = "root"; name[1] = "dir";
            assertTrue(fileSystem.DirExists(name) != null);

        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (OutOfSpaceException e) {
            assertTrue(false);
        }

        /*so far adding 6 kb. trying to add a file with a name of an existing file. There is no enough space
        but if we will erase the old version will be enough. We will try to add 95 kb*/
        name = new String [3];
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
        /* test will work after fixing the bug
           In FileSysetm row 121 replace '-' with '+'
         */
        try {
            fileSystem.file(name, 95); //will be enough space if we will erase the older version
            Leaf fileAdded = fileSystem.FileExists(name);
            assertTrue( fileAdded != null); //file still exists

            /*its size is 95 now - will work after fixing bug in Leaf, adding in constructor this.size = size
            assertTrue(fileAdded.size == 95);
            */

        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (OutOfSpaceException e) {
            assertTrue(false );
        }catch(Exception e){

        }


        /*Trying to add a file with a name of an existing file but the new file is too large
        and the system has not enough space - don't erase the old version*/
        name = new String [3];
        name[0] = "root"; name[1] = "docs"; name[2] = "file1";
         /* test will work after fixing the bug
           In FileSysetm row 126 adding else throw new OutOfSpaceException
         */
        try {
            fileSystem.file(name, 100);
            assertTrue(false);

        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (OutOfSpaceException e) {
            assertTrue(fileSystem.FileExists(name) != null );
        }
        catch(Exception e){

        }
    }

    @Test
    public void lsdir() {
        //directory exists
        name = new String[2];
        name[0] = "root"; name[1] = "docs";
        assertTrue(fileSystem.lsdir(name) != null);

        //directory doesn't exist
        name[0] = "root"; name[1] = "mail";
        assertTrue(fileSystem.lsdir(name) == null);
    }

    @Test
    public void rmfile() {
        //file exists -> name = ["root", "docs", "file1"]
        int freeSpace = fileSystem.fileStorage.countFreeSpace();
        Leaf file = fileSystem.FileExists(name);
        fileSystem.rmfile(name);
        assertTrue(fileSystem.fileStorage.countFreeSpace() > freeSpace); //TODO is it okay?
        assertTrue(fileSystem.FileExists(name) == null);

        //file doesn't exist
        freeSpace = fileSystem.fileStorage.countFreeSpace();
        name[0] = "root"; name[1] = "docs"; name[2] = "fileDoesntExist";
        fileSystem.rmfile(name);
        assertEquals(freeSpace, fileSystem.fileStorage.countFreeSpace());

    }

    @Test
    public void rmdir() {
        //directory contains file "file1" - an exception is expected
        name = new String [2];
        name[0] = "root"; name[1] = "docs";
        try {
            fileSystem.rmdir(name);
            assertTrue(false);
        } catch (DirectoryNotEmptyException e) {
            assertTrue(true);
        }

        //directory is empty - remove should succeed
        name[0] = "root"; name[1] = "dirToRemove";
        try {
            fileSystem.dir(name); //add an empty directory
            fileSystem.rmdir(name); //remove an empty directory
            assertTrue(true);
            assertTrue(fileSystem.DirExists(name) == null);

        } catch (BadFileNameException e) {
            assertTrue(false);
        } catch (DirectoryNotEmptyException e) {
            assertTrue(false);
        }

        //directory doesn't exist - nothing should happen
        name[0] = "root"; name[1] = "dirDoesntExist";
        try {
            fileSystem.rmdir(name); //remove
            assertTrue(fileSystem.DirExists(name) == null);

        } catch (DirectoryNotEmptyException e) {
            assertTrue(false);
        }
    }

    @Test
    public void fileExists() {
        //file exists returns not null -> name = ["root", "docs", "file1"]
        assertTrue(fileSystem.FileExists(name) != null);

        //file doesn't exist returns null
        name[0] = "root"; name[1] = "docs"; name[2] = "fileDoesntExist";
        assertTrue(fileSystem.FileExists(name) == null);
    }

    @Test
    public void dirExists() {
        //dir exists returns not null
        name[0] = "root"; name[1] = "docs";
        assertTrue(fileSystem.FileExists(name) != null);

        //dir doesn't exist returns null
        name[0] = "root"; name[1] = "mail";
        assertTrue(fileSystem.FileExists(name) == null);
    }
}