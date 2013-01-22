package jshell;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * A JUnit test suite for JShell class. This test suite is responsible for
 * verifying the JShell commands and determines if they behave in their expected
 * ways.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author In-Jey Hwang <c0hwangi>
 */
public class JUnitTest {
    // A JShell instance for testing

    private jshell.JShell newShell_;

    /**
     * Test the mkdir command of JShell class.
     *
     * Test case 1: try mkdir with one parameter Test case 2: try mkdir on
     * multiple parameters Test case 3: try mkdir with full paths Test case 4:
     * try creating
     */
    @Test
    public void testMkdir() {
        try {
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            List<String> paramList = new ArrayList<String>();

            //Test mkdir with one parameter
            paramList.add("home");
            newShell_.mkdir(paramList);
            assert (root.contains("home"));
            root.removeItem(newShell_.getItemAtPath("/home", 0));
            paramList.clear();

            //Test mkdir with multiple parameters
            paramList.add("home");
            paramList.add("boot");
            paramList.add("etc");
            paramList.add("tmp");
            newShell_.mkdir(paramList);

            for (String dir : paramList) {
                assert (root.getSubDirectories().containsKey(dir));
            }

            //Check the created folders for correct initialization
            for (String dir : paramList) {
                JShellItem cur = root.getItem(dir);
                assertEquals(cur.getPath(), "/" + dir + "/");
                assertEquals(cur.getName(), dir);
                assertEquals(cur.getParentDirectory(), root);
                assert ((Directory) cur).getContents().isEmpty();
            }
            paramList.clear();

            //Test mkdir with full paths
            paramList.add("home/khodeir");
            paramList.add("home/in-jey");
            paramList.add("home/michael");
            paramList.add("home/joseph");
            newShell_.mkdir(paramList);
            Directory home = (Directory) root.getItem("home");

            for (String dir : paramList) {
                assert (home.contains(dir.substring(5)));
            }

            //Check the created folders for correct initialization
            for (String dir : paramList) {
                JShellItem cur = home.getItem(dir.substring(5));
                assertEquals(cur.getPath(), "/" + dir + "/");
                assertEquals(cur.getName(), dir.substring(5));
                assertEquals(cur.getParentDirectory(), home);
                assert ((Directory) cur).getContents().isEmpty();
            }
            paramList.clear();

            //test mkdir on a path that includes a non existing directory
            paramList.add("falseDirectory/dir1");
            try {
                newShell_.mkdir(paramList);
            } catch (NullPointerException e) {
                assert e.getMessage().equals(
                        "The path specified is incorrect.") :
                        "Unexpected error in mkdir: " + e.getMessage();
            }
            paramList.clear();

            //test case insensitivity (Should not create dir "Home" because
            //"home" exists.)
            paramList.add("Home");
            try {
                newShell_.mkdir(paramList);
            } catch (Exception e) {
                assert e.getMessage().equals(
                        "A directory already exists at that path.") :
                        "Unexpected error in mkdir: " + e.getMessage();
                assert (!newShell_.getRootDirectory().contains("Home"));
            }
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in mkdir: "
                    + e.getMessage();
        }
    }

    /**
     * Test the cd command of JShell class.
     *
     * Test Case 1: Full path. Test Case 2: Relative path. Test Case 3: Relative
     * path. Test Case 4: Full path.
     */
    @Test
    public void testCd() {
        try {
            // Initialize and reference the variables.
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            Directory homeDir = new Directory("home", "/home/", root);
            Directory bootDir = new Directory("boot", "/boot/", root);
            Directory etcDir = new Directory("etc", "/etc/", root);
            Directory tmpDir = new Directory("tmp", "/tmp/", root);
            Directory documentsDir =
                    new Directory("Documents", "/home/Documents/", homeDir);

            // Create a simple file system.
            newShell_.getRootDirectory().addItem(homeDir);
            newShell_.getRootDirectory().addItem(bootDir);
            newShell_.getRootDirectory().addItem(etcDir);
            newShell_.getRootDirectory().addItem(tmpDir);
            homeDir.addItem(documentsDir);

            // Test Case 1: Changing directory from '/' to '/home/', via 
            // full path.

            // Change directory to '/home/'.
            newShell_.cd("/home/");

            assertEquals("should return: /home/", "/home/",
                    newShell_.getCurrentDirectory().getPath());

            // Test Case 2: Change directory to parent directory, via 
            // relative path.

            // Change directory to parent directory.
            newShell_.cd("/../");

            assertEquals("should return: /", "/",
                    newShell_.getCurrentDirectory().getPath());

            // Test Case 3: Change directory from '/' to './home/Documents' via 
            // relative pathing.

            // Change directory from '/' to '/home/Documents/'.
            newShell_.cd("./home/Documents");

            assertEquals("should return: /home/Documents/", "/home/Documents/",
                    newShell_.getCurrentDirectory().getPath());

            // Test Case 4: Change directory from '/home/Documents/' to '/etc/'.

            // Change directory from '/home/Documents/' to '/etc/'.
            newShell_.cd("/../../etc/");

            assertEquals("should return: /etc/", "/etc/",
                    newShell_.getCurrentDirectory().getPath());
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in Cd: "
                    + e.getMessage();
        }
    }

    /**
     * Test the ls command of JShell class.
     *
     * Test case 1: Try ls with no parameters Test case 2: Try ls with "/" as a
     * parameter Test case 3: Try ls with a path relative to the current
     * directory Test case 4: Try ls with a full path Test case 5: Try ls with a
     * non existing directory Test case 6: Try ls with multiple parameters
     */
    @Test
    public void testLs() {
        try {
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            List<String> paramList = new ArrayList<String>();
            newShell_.setCurrentOption("");

            //Test on empty root directory
            assertEquals("", newShell_.ls(paramList));

            //Create folders and files
            Directory dir1 = new Directory("dir1", "/dir1/", root);
            root.addItem(dir1);
            Directory dir2 = new Directory("dir2", "/dir2/", root);
            root.addItem(dir2);
            File file1 = new File("file1", "/file1", root);
            root.addItem(file1);
            Directory subDir1 =
                    new Directory("subDir1", "/dir1/subDir1/", dir1);
            dir1.addItem(subDir1);
            Directory subDir2 =
                    new Directory("subDir2", "/dir1/subDir2/", dir1);
            dir1.addItem(subDir2);

            //Try calling ls on the root directory without specifying path
            assertEquals("dir1\ndir2\nfile1", newShell_.ls(paramList));

            //Test ls on the root directory with "/" as a parameter
            paramList.add("/");
            assertEquals("/:\ndir1\ndir2\nfile1", newShell_.ls(paramList));
            paramList.clear();

            //Test on a subdirectory without specifying full path
            paramList.add("dir1");
            assertEquals("dir1:\nsubDir1\nsubDir2", newShell_.ls(paramList));
            paramList.clear();

            //Test on subdirectory using full path
            paramList.add("/dir1");
            assertEquals("/dir1:\nsubDir1\nsubDir2", newShell_.ls(paramList));
            paramList.clear();

            //try printing the contents of a nonexisting directory
            paramList.add("falseDir");
            assertEquals("falseDir: No such file or directory",
                    newShell_.ls(paramList));
            paramList.clear();

            //try calling ls with multiple JShellItem parameters 
            //(including a file)
            paramList.add("dir1");
            paramList.add("dir2");
            paramList.add("file1");
            assertEquals("dir1:\nsubDir1\nsubDir2\n\ndir2:\n\nfile1",
                    newShell_.ls(paramList));
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in ls: "
                    + e.getMessage();
        }
    }

    /**
     * Test the pwd command of JShell class.
     *
     * Test Case 1: Print working directory at root. Test Case 2: Print working
     * directory at another location.
     */
    @Test
    public void testPwd() {
        try {
            // Initialize and reference the variables.
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            Directory homeDir = new Directory("home", "/home/", root);
            Directory bootDir = new Directory("boot", "/boot/", root);
            Directory etcDir = new Directory("etc", "/etc/", root);
            Directory tmpDir = new Directory("tmp", "/tmp/", root);
            Directory documentsDir =
                    new Directory("Documents", "/home/Documents/", homeDir);

            // Create a simple file system.
            newShell_.getRootDirectory().addItem(homeDir);
            newShell_.getRootDirectory().addItem(bootDir);
            newShell_.getRootDirectory().addItem(etcDir);
            newShell_.getRootDirectory().addItem(tmpDir);
            homeDir.addItem(documentsDir);

            // Test Case 1: Print working directory at root.

            assertEquals("should return: /", "/", newShell_.pwd());

            // Test Case 2: Print working directory at '/home/Documents/'.

            // Change directory to '/home/Documents/'
            newShell_.cd("/home/Documents");

            assertEquals("should return: /home/Documents/", "/home/Documents/",
                    newShell_.pwd());

        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in Pwd: "
                    + e.getMessage();
        }
    }

    /**
     * Test the redirection method.
     *
     * Test case 1: test redirection with only a > first redirection paramter
     * Test case 2: test redirection with only a >> first redirection paramter
     * Test case 3: test redirection with only a second redirection paramter
     * Test case 4: test redirection with both redirection paramters
     */
    @Test
    public void testRedirection() {
        try {
            newShell_ = new JShell();
            List<String> paramList = new ArrayList<String>();
            List<String> emptyparamList = new ArrayList<String>();

            //Testing redirection with only a > first redirection paramter
            paramList.add(">");
            newShell_.handleRedirection(paramList);
            assertEquals("No changes should have been made to the parameters",
                    paramList, newShell_.handleRedirection(paramList));

            //Testing redirection with only a >> first redirection paramter
            paramList.clear();
            paramList.add(">>");
            newShell_.handleRedirection(paramList);
            assertEquals("No changes should have been made to the parameters",
                    paramList, newShell_.handleRedirection(paramList));

            //Testing redirection with only a second redirection paramter
            paramList.clear();
            paramList.add("cat.txt");
            newShell_.handleRedirection(paramList);
            assertEquals("No changes should have been made to the parameters",
                    paramList, newShell_.handleRedirection(paramList));

            //Testing redirection with both redirection paramters
            paramList.clear();
            paramList.add(">");
            paramList.add("cat.txt");
            newShell_.handleRedirection(paramList);
            assertEquals("Those two redirection elements should have been removed",
                    emptyparamList, newShell_.handleRedirection(paramList));

        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in Cat: "
                    + e.getMessage();
        }
    }

    /**
     * Test the cat command of JShell class.
     *
     * Test case 1: call cat on a non existing file Test case 2: call cat on an
     * existing file Test case 3: call cat on a file whose contents were
     * modified Test case 4: call cat on a file in a directory Test case 5: call
     * cat on a Directory instead of a file
     */
    @Test
    public void testCat() {
        try {
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            List<String> paramList = new ArrayList<String>();
            Directory dir1 = new Directory("dir1", "/dir1/", root);
            root.addItem(dir1);
            File file1 = new File("test.txt", "/test.txt", root);
            root.addItem(file1);
            File subFile1 = new File("test1.txt", "/dir1/test1.txt", dir1);
            dir1.addItem(subFile1);

            //Try calling cat on a non existant file.
            assertEquals("This file shoutld not exist",
                    "The file was not found.", newShell_.cat("test0.txt"));

            //Try calling cat on an existing fie;
            assertEquals("This file shoutld be empty", "",
                    newShell_.cat("test.txt"));

            //Try calling cat after the new file has been altered.;
            file1.setContent("Hello World");
            assertEquals("This file should have Hello World in it",
                    "Hello World", newShell_.cat("test.txt"));

            //Try a file within a directory;
            paramList.clear();
            subFile1.setContent("This is a file located in dir1");
            assertEquals("Should print the contents of test1.txt",
                    "This is a file located in dir1",
                    newShell_.cat("/dir1/test1.txt"));

            //try cat on a directory
            assertEquals("dir1: is not a file.", newShell_.cat("/dir1"));

        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in Cat: "
                    + e.getMessage();
        }
    }

    /**
     * Test the echo command of JShell class.
     *
     * Test case 1: Pass an incorrectly formatted parameter Test case 2: Try
     * echo without > or >> Test case 3: Try echo > with a non existing file
     * Test case 4: Try echo > on an existing file Test case 5: Try echo >> with
     * an existing file Test case 6: Try echo >> with a non existing file
     */
    @Test
    public void testEcho() {
        newShell_ = new JShell();
        try {
            List<String> paramList = new ArrayList<String>();

            //Try calling echo without a proper string >.
            paramList.add("Hello");
            paramList.add("World");
            assertEquals("This should return the original statement.",
                    "Echo requires a string with \" & \" surrounding the words\n",
                    newShell_.echo(paramList));
            paramList.clear();

            //Try calling echo without any >.
            paramList.add("\"Hello");
            paramList.add("World\"");
            paramList.add("test.txt");
            assertEquals("This should return the original statement.",
                    "Hello World\n", newShell_.echo(paramList));
            paramList.clear();

            //Try calling echo > on a non existing file.
            JShellItem targetFile;
            paramList.add("\"Kittens Rock\"");
            paramList.add(">");
            paramList.add("test.txt");
            newShell_.echo(paramList);
            targetFile = newShell_.getItemAtPath("test.txt", 0);
            assertEquals("This file should have Kittens Rock in it",
                    "Kittens Rock", ((File) targetFile).getContent());
            paramList.clear();

            //Try calling echo > on an existing file.
            paramList.add("\"Kittens Rock\"");
            paramList.add(">");
            paramList.add("test.txt");
            newShell_.echo(paramList);
            targetFile = newShell_.getItemAtPath("test.txt", 0);
            assertEquals("This file should have Kittens Rock in it",
                    "Kittens Rock", ((File) targetFile).getContent());
            paramList.clear();

            //Try calling echo >> on an existant file.
            paramList.add("\"LOL\"");
            paramList.add(">>");
            paramList.add("test.txt");
            newShell_.echo(paramList);
            assertEquals("This file should append LOL to Kittens Rock",
                    "Kittens RockLOL", ((File) targetFile).getContent());
            paramList.clear();

            //Try calling echo >> on an non existant file.
            paramList.add("\"Appendix\"");
            paramList.add(">>");
            paramList.add("test1.txt");
            newShell_.echo(paramList);
            targetFile = newShell_.getItemAtPath("test1.txt", 0);
            assertEquals("Should create test1.txt with content Appendix",
                    "Appendix", ((File) targetFile).getContent());
            paramList.clear();
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in Echo: "
                    + e.getMessage();
        }
    }

    /**
     * Test the getItemAtPath function of JShell class.
     *
     * Test case 1: Try the root directory Test case 2: Try a nested directory
     * Test case 3: Try getItemAtPath with a non-zero numFoldersUp parameter
     * Test case 4: Try getItemAtPath with a large numFoldersUp parameter
     */
    @Test
    public void testGetItemAtPath() {
        try {
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            Directory dir1 = new Directory("dir1", "/dir1/", root);
            root.addItem(dir1);
            Directory dir2 = new Directory("dir2", "/dir1/dir2/", dir1);
            dir1.addItem(dir2);

            //Test on root directory
            assertEquals(root, newShell_.getItemAtPath("/", 0));

            //Test on nested directory
            assertEquals(dir2,
                    newShell_.getItemAtPath("/dir1/dir2/", 0));

            //Test on a non-zero parent value
            assertEquals(dir1, newShell_.getItemAtPath("/dir1/dir2/", 1));

            //Test on a large parent value
            assertEquals(root, newShell_.getItemAtPath("/dir1/dir2/", 100));
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in getItemAtPath: "
                    + e.getMessage();
        }
    }

    /**
     * Test the ln command from the JShell class.
     *
     * Test Case 1: Linking File object with FileAlias object in a Directory.
     * Test Case 2: Linking Directory object with Directory object in a
     * Directory.
     */
    @Test
    public void testln() {
        try {
            // Initialize and reference the variables.
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            Directory homeDir = new Directory("home", "/home/", root);
            Directory bootDir = new Directory("boot", "/boot/", root);
            Directory etcDir = new Directory("etc", "/etc/", root);
            Directory tmpDir = new Directory("tmp", "/tmp/", root);
            Directory documentsDir = new Directory("Documents",
                    "/home/Documents/", homeDir);
            Directory desktopDir = new Directory("Desktop",
                    "/home/Desktop/", homeDir);
            File aTxtFile = new File("readme.txt", "/home/Desktop/",
                    desktopDir, "Hello World.");

            // Create a simple file system.
            root.addItem(homeDir);
            root.addItem(bootDir);
            root.addItem(etcDir);
            root.addItem(tmpDir);
            homeDir.addItem(documentsDir);
            homeDir.addItem(desktopDir);
            desktopDir.addItem(aTxtFile);

            // Test Case 1: Link 'home/Desktop/readme.txt' to '/home/Documents'.
            newShell_.ln("/home/Desktop/readme.txt/", "/home/Documents/");

            // Reference the File object and FileAlias object to be linked.
            File fileLink1 = ((File) newShell_.getItemAtPath(
                    "/home/Desktop/readme.txt/", 0));
            FileAlias fileAlias1 = ((FileAlias) newShell_.getItemAtPath(
                    "/home/Documents/readme.txt/", 0));

            assertEquals("should return: Hello World.", fileLink1.getContent(),
                    fileAlias1.getContent());

            // Test Case 2: Link '/home/Documents' to '/home/Desktop/'.

            newShell_.ln("/home/Documents/", "/home/Desktop/");

            // Reference the two Directories to be linked.
            Directory dirLink2 = ((Directory) newShell_.getItemAtPath("/home/Documents/", 0));
            Directory dirAlias2 = ((Directory) newShell_.getItemAtPath(
                    "/home/Desktop/Documents/", 0));

            assertEquals("should return: readme.txt", dirLink2.getContents(),
                    dirAlias2.getContents());
        } catch (Exception e) {
            assert e == null : "Caught unexpected exception in ln: "
                    + e.getMessage();
        }
    }

    /**
     * Test the man function of JShell class.
     *
     * Test case 1: Try man on an existing command Test case 2: Try man on a non
     * existing command Test case 3: Try man without parameters
     */
    @Test
    public void testMan() {
        newShell_ = new JShell();

        //Test documentation for existing command
        assertEquals("ln PATH1 PATH2\nMake PATH1 a symbolic link to "
                + "PATH2. Both PATH1 and PATH2 may\nbe relative "
                + "to the current directory or may be full paths."
                + " PATH1 is a\nsynonym for PATH2, If PATH2 is "
                + "deleted or moved, then PATH1 will still\nexist "
                + "but is invalid.", newShell_.man("ln"));

        //Test documentation for non-existing command
        assertEquals("There is no manual for this command.", newShell_.man("badParam"));

        //Test documentation for empty paramter
        assertEquals("What manual page do you want?", newShell_.man(""));
    }

    /**
     * Test the get command from the JShell class.
     *
     * Test Case 1: Try with a valid URL Test Case 2: Try with an invalid URl
     */
    @Test
    public void testGet() {
        newShell_ = new JShell();

        //Test with a text file at a valid URL
        assertEquals("students like beer, not homework",
                newShell_.get("http://uf6.info/txt/1751781.txt"));

        //Test with invalid URL
        assertEquals("The file was not found.", newShell_.get("bolloxURL"));
    }

    /**
     * Test the mv command from the JShell class.
     *
     * Test Case 1: Try moving one directory in the root to another directory
     * Test Case 2: Try moving a directory at a full path to another directory
     * Test Case 4: Try moving a directory into another directory that contains
     * a directory with the same name Test Case 4: Try moving a directory into
     * an non existing directory
     */
    @Test
    public void testMv() {
        try {
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            newShell_.setCurrentOption("");

            //create dir2 and dir1 with subdirectory subdir 1
            Directory dir1 = new Directory("dir1", "/dir1/", root);
            root.addItem(dir1);
            Directory subDir1 = new Directory("subDir1", "/dir1/subDir1", dir1);
            dir1.addItem(subDir1);
            Directory dir2 = new Directory("dir2", "/dir2/", root);
            root.addItem(dir2);

            //Test: move subdir1 from dir1 to dir 2
            newShell_.mv("dir1/subDir1", "/dir2/");
            assertEquals(dir2, subDir1.getParentDirectory());
            assert (!dir1.contains("subDir1"));

            //Try moving a directory a into another directory that 
            //contains a directory with the same name
            Directory sameNameDir = new Directory("subDir1", "/dir1/subDir1",
                    dir1);
            dir1.addItem(sameNameDir);
            try {
                newShell_.mv("/dir2/subDir1/", "/dir1/subDir1");
            } catch (Exception e) {
                assert e.getMessage().equals(
                        "/dir1/subDir1: already exists.") :
                        "Unexpected error in mkdir: " + e.getMessage();
            }
            dir1.removeItem(sameNameDir);

            //Try moving a directory into an unexisting directory
            try {
                newShell_.mv("/dir2/subDir1/", "/falseDir/subDir1");
            } catch (Exception e) {
                assert e.getMessage().equals(
                        "/falseDir/subDir1: invalid destination path.") :
                        "Unexpected error in mkdir: " + e.getMessage();
            }
        } catch (Exception e) {
            System.out.println("Unexpected Error in mv: " + e);
        }
    }

    /**
     * Test the cp command of JShell class.
     *
     * Test1: Copy a directory from the root to another directory Test2: Check
     * that the contents of the folder were copied Test3: Check that the copied
     * items are independent from the original copies. Test4: Duplicate a folder
     * and give it a different name
     */
    @Test
    public void testCp() {
        //Initialize JShell and create directories /dir1 /dir2 /dir/subDir1
        newShell_ = new JShell();
        Directory root = newShell_.getRootDirectory();
        Directory dir1 = new Directory("dir1", "/dir1/", root);
        Directory subDir1 = new Directory("subDir1", "/dir1/subDir1/", dir1);
        dir1.addItem(subDir1);
        root.addItem(dir1);
        Directory dir2 = new Directory("dir2", "/dir2/", root);
        root.addItem(dir2);

        //Try copying a directory into other directory(without changing name)
        try {
            newShell_.cp("/dir1", "/dir2/");
        } catch (Exception e) {
            assert e == null : "Caught Unexpected Exception running "
                    + "cp('/dir1','/dir2/'): " + e.getMessage();
        }
        //Test that there exists a copy of dir1 in dir2. /dir2/dir1 
        dir2 = (Directory) root.getItem("dir2");
        assert dir2.contains("dir1") : "/dir2/ should contain directory dir1";

        //dir1 was successfully copied to /dir2/
        //Now check that /dir2/dir1 contains subDir1/
        Directory dir1Copy = (Directory) dir2.getItem("dir1");
        assert dir1Copy.contains("subDir1") :
                "/dir2/dir1/ should contain directory subDir1"
                + dir1.getContents().keySet().toString();

        //dir1 was successfully copied recursively to /dir2/
        //New check that they are different objects.
        assert dir1Copy != dir1 :
                "/dir2/dir1 is only a symbollic link for /dir1.";
        //further check by modifying one and not the other.
        dir1.addItem(new Directory("subDir2", "/dir1/SubDir2/", dir1));
        assert dir1.contains("subDir2") && !dir1Copy.contains("subDir2") :
                "Modifications in /dir1 should not be reflected in /dir2/dir1";

        //Directories in the shell now are root, dir1, dir2, dir1/subDir1,
        //dir1/subDir2, dir2/dir1, and dir2/dir1/subDir1
        try {
            //test copying directory into same directory by changing name
            newShell_.cp("dir1", "dir3");
        } catch (Exception e) {
            assert e == null :
                    "Caught Unexpected Exception running cp('dir1','dir3'): "
                    + e.getMessage();
        }
        //check that dir3 was created in currentDirectory (root)
        assert newShell_.getCurrentDirectory().contains("dir3") :
                "Should have created directory dir3 in current directory.";
        Directory dir3 =
                (Directory) newShell_.getCurrentDirectory().getItem("dir3");
        //check that the contents of dir3 are identical to the contents of dir1
        assert dir1.getContents().keySet().equals(dir3.getContents().keySet()) :
                "Contents of newly created dir3 should have been identical"
                + " to those of dir1.";

        //check that they are different objects and modifications in 
        //one will not reflect in the other.
        assert dir1 != dir3 : "/dir3 is only a symbollic link for /dir1.";
        dir1.addItem(new File("file1.txt", "/dir1/file.txt", dir1));
        assert dir1.contains("file1.txt") && !dir3.contains("file1.txt") :
                "Modifications in /dir1 should not be reflected in /dir3.";
    }

    /**
     * Test the rm command of the JShell class.
     *
     * Test Case 1: force remove a Directory object. Test Case 2: force remove a
     * File object and a Directory object. Test Case 3: force remove a FileAlias
     * object.
     */
    @Test
    public void testRm() {
        try {
            // Initialize and reference the variables.
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            jshell.Directory homeDir = new Directory("home", "/home/", root);
            Directory bootDir = new Directory("boot", "/boot/", root);
            Directory etcDir = new Directory("etc", "/etc/", root);
            Directory tmpDir = new Directory("tmp", "/tmp/", root);
            Directory documentsDir = new Directory("Documents",
                    "/home/Documents/", homeDir);
            Directory downloadsDir = new Directory("Downloads",
                    "/home/Downloads/", homeDir);
            Directory desktopDir = new Directory("Desktop",
                    "/home/Desktop/", homeDir);
            File txtFileOne = new File("readme.txt",
                    "/home/Desktop/readme.txt", desktopDir, "Hello World.");
            File txtFileTwo = new File("testlog.txt",
                    "/home/Documents/testlog.txt", documentsDir, "TEST ALL THE THINGS!");
            List<String> paramList = new ArrayList<String>();

            // Create a simple file system.
            root.addItem(homeDir);
            root.addItem(bootDir);
            root.addItem(etcDir);
            root.addItem(tmpDir);
            homeDir.addItem(documentsDir);
            homeDir.addItem(desktopDir);
            homeDir.addItem(downloadsDir);
            desktopDir.addItem(txtFileOne);
            documentsDir.addItem(txtFileTwo);
            newShell_.ln("/home/Desktop/readme.txt/", "/home/Documents/");
            newShell_.ln("/home/Documents/testlog.txt/", "/home/Desktop/");

            // Test Case 1: Force remove Downloads from home.
            newShell_.setCurrentOption("f");
            paramList.add("/home/Downloads/");
            newShell_.rm(paramList);
            assert !(homeDir.contains("Downloads")) :
                    "Downloads shouldn't exist in /home/.";
            paramList.clear();

            // Test Case 2: Force remove readme.txt from Desktop and etc 
            // from root.
            paramList.add("/home/Desktop/readme.txt/");
            paramList.add("/etc/");
            newShell_.rm(paramList);
            assert !(root.contains("etc")) : "etc shouldn't exist in /.";
            assert !(desktopDir.contains("readme.txt")) :
                    "readme.txt shouldn't exist in /home/Desktop/.";
            paramList.clear();

            // Test Case 3: Force remove FileAlias object: readme.txt from 
            // Documents.
            paramList.add("/home/Documents/readme.txt/");
            newShell_.rm(paramList);
            assert !(documentsDir.contains("readme.txt")) :
                    "readme.txt shouldn't exist in /home/Documents/";
            paramList.clear();
            newShell_.setCurrentOption("");
        } catch (Exception e) {
            assert e == null :
                    "Caught unexpected exception in rm: " + e.getMessage();
        }
    }

    /**
     * Test the find command of JShell class.
     *
     * Test Case 1: find with empty parameters at root. 
     * Test Case 2: find with regex from /home/. 
     * Test Case 3: find with regex and existing path at root. 
     * Test Case 4: find from /home/ with a non existent path.
     */
    @Test
    public void testFind() {
        try {
            // Initialize and reference the variables.
            newShell_ = new JShell();
            Directory root = newShell_.getRootDirectory();
            Directory homeDir = new Directory("home", "/home/", root);
            Directory bootDir = new Directory("boot", "/boot/", root);
            Directory etcDir = new Directory("etc", "/etc/", root);
            Directory tmpDir = new Directory("tmp", "/tmp/", root);
            Directory documentsDir = new Directory("Documents", 
                    "/home/Documents/", homeDir);
            Directory desktopDir = new Directory("Desktop", 
                    "/home/Desktop/", homeDir);
            File txtFileOne = new File("readme.txt", 
                    "/home/Desktop/readme.txt", desktopDir, "Hello World.");
            List<String> paramList = new ArrayList<String>();

            // Create a simple file system.
            root.addItem(homeDir);
            root.addItem(bootDir);
            root.addItem(etcDir);
            root.addItem(tmpDir);
            homeDir.addItem(documentsDir);
            homeDir.addItem(desktopDir);
            desktopDir.addItem(txtFileOne);

            // Test Case 1: Test find at root with empty parameters.
            paramList.add("");
            assertEquals("/tmp/\n"
                    + "/etc/\n"
                    + "/boot/\n"
                    + "/home/Desktop/readme.txt\n"
                    + "/home/Desktop/\n"
                    + "/home/Documents/\n"
                    + "/home/\n"
                    + "/", newShell_.find(paramList.get(0), 
                    paramList.subList(1, paramList.size())));
            paramList.clear();

            // Test Case 2: Test find at /home/ with regex: "h".
            paramList.add("h");
            paramList.add("/home/");
            assertEquals("/home/Desktop/readme.txt\n"
                    + "/home/Desktop/\n"
                    + "/home/Documents/\n"
                    + "/home/", newShell_.find(paramList.get(0), 
                    paramList.subList(1, paramList.size())));
            paramList.clear();

            // Test Case 3: Test find at /home/ with regex: "".
            newShell_.cd("home");
            paramList.add("re");
            assertEquals("/home/Desktop/readme.txt", 
                    newShell_.find(paramList.get(0), 
                    paramList.subList(1, paramList.size())));
            paramList.clear();

            // Test Case 4: Test find at /home/ for a JShellItem that 
            // doesn't exist.
            paramList.add("troll");
            assertEquals("find: no such file or directory.", 
                    newShell_.find(paramList.get(0), 
                    paramList.subList(1, paramList.size())));
            paramList.clear();
        } catch (Exception e) {
            assert e == null : 
                    "Caught unexpected exception in find: " + e.getMessage();
        }
    }
    /**
     * Test the grel command of JShell class.
     *
     * Test Case 1: Call grep on a directory without -R
     * Test Case 2: Search for text in an empty file without -R 
     * Test Case 3: Search for text in a file with one line. 
     * Test Case 4: Search for text in a file with multiple lines.
     * Test Case 5: Search for text recursively in a folder containing 
     * multiple files
     */
    @Test
    public void testGrep() {
        try{
            newShell_ = new JShell();
            newShell_.setCurrentOption("");
            Directory root = newShell_.getRootDirectory();
            Directory dir1 = new Directory("dir1", "/dir1/", root);
            Directory subDir1 = new Directory("subDir1","/dir1/subDir1/", dir1);
            dir1.addItem(subDir1);
            root.addItem(dir1);
            List<String> paramList = new ArrayList<String>();
        
            //Try grep on a directory without the -R option
            paramList.add(dir1.getPath());
            assertEquals("Didn't display an error when calling grep on a directory.", 
            "Cannot call grep on a directory without -R.", 
                newShell_.grep("some string", paramList));
            
            //Search for text in an empty file without the -R option
            paramList.clear();
            File file1 = new File("file1", "/file1", root);
            root.addItem(file1);
            paramList.add(file1.getPath());
            assertEquals("Found something that wasn't there", 
                "", newShell_.grep("some string", paramList));
            
            //Search for text in a file with one line
            file1.setContent("test line");
            assertEquals("Didn't print the line", 
                "/file1:\ntest line", newShell_.grep("test", paramList));
            
            //Search for text in a file with multiple lines
            file1.setContent("test line1\ntest line2\ntest line1 copy");
            assertEquals("Didn't print the right lines", 
                "/file1:\ntest line1\ntest line1 copy", newShell_.grep("line1", 
                                                               paramList));
            
            //Search for text recursively in a folder containing multiple files
            File file2 = new File("file2", "dir1/file2", dir1);
            dir1.addItem(file2);
            file2.setContent("test line2\ntest line1\ntest line2 copy");
            File file3 = new File("file3", "dir1/file3", dir1);
            dir1.addItem(file3);
            file3.setContent("test line1\ntest line2\ntest line1 copy");
            paramList.clear();
            paramList.add(dir1.getPath());
            newShell_.setCurrentOption("R");
            assertEquals("Didn't print the right lines", 
                "dir1/file3:\ntest line2\ndir1/file2:\ntest line2\ntest "
                    + "line2 copy",
                newShell_.grep("line2", paramList));
        }
        catch (Exception e) {
            assert e == null : 
                    "Caught unexpected exception in grep: " + e.getMessage();
        }  
    }
}
