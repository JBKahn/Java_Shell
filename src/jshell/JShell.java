package jshell;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A JShell file system.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author In-Jey Hwang <c0hwangi>
 */
@SuppressWarnings("serial")
public class JShell implements Serializable {
    /*
     * The current working directory.
     */
    private Directory currentDirectory_;
    /*
     * The map of commands available in JShell.
     */
    private HashMap<String, Integer> commands_;
    /*
     * The root directory of JShell.
     */
    private final Directory rootDirectory_;
    /*
     * The status of whether the console should not display output.
     */
    private String currentOptions_;
    /*
     * Stores the redirection information.
     */
    private List<String> currentRedirection_ = new ArrayList<String>();
    /**
     * JShell constructor. Initialize instance variables and map each command to
     * a number.
     */
    public JShell() {
        currentDirectory_ = new Directory("", "/", null);
        rootDirectory_ = currentDirectory_;
        rootDirectory_.setParentDirectory(rootDirectory_);
        commands_ = new HashMap<String, Integer>();
        commands_.put("exit", 0);
        commands_.put("mkdir", 1);
        commands_.put("cd", 2);
        commands_.put("ls", 3);
        commands_.put("pwd", 4);
        commands_.put("mv", 5);
        commands_.put("cp", 6);
        commands_.put("cat", 7);
        commands_.put("get", 8);
        commands_.put("echo", 9);
        commands_.put("rm", 10);
        commands_.put("ln", 11);
        commands_.put("man", 12);
        commands_.put("find", 13);
        commands_.put("grep", 14);
    }

    /**
     * Prints the prompt in the path + # format.
     */
    public void printPrompt() {
        System.out.print(currentDirectory_.getPath() + "# ");
    }

    /**
     * Reads user input from the console.
     *
     * @return An ArrayList of words typed by user.
     */
    public ArrayList<String> readInput() {
        currentOptions_ = new String();
        String arg;
        ArrayList<String> input = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        String inputText = scanner.nextLine();
        Scanner tokenizer = new Scanner(inputText);
        while (tokenizer.hasNext()) {
            arg = tokenizer.next();
            if (!arg.startsWith("-")) {
                input.add(arg);
            } else {
                currentOptions_ += arg.substring(1);
            }
        }
        return input;
    }

    /**
     * Call the appropriate command function and notify user of incorrect
     * command input.
     *
     * @param command is the operation to do.
     * @param params is a list of parameters for the command.
     */
    public void executeCommand(String command, List<String> params) {
        if (commands_.containsKey(command)) {
            int commandNum = commands_.get(command);
            try {
                switch (commandNum) {
                    // mkdir command.
                    case 1:
                        if (params.size() > 0) {
                            mkdir(params);
                        } else {
                            System.out.println("mkdir: missing operand");
                        }
                        break;

                    // cd command.
                    case 2:
                        if (params.size() == 1) {
                            cd(params.get(0).toString());
                        } else if (params.size() > 1) {
                            System.out.println("cd: Too many arguments.");
                        } else {
                            cd("/");
                        }
                        break;

                    // ls command.
                    case 3:
                        String lsContent = ("\"" + ls(params) + "\"");
                        currentRedirection_.add(0, lsContent);
                        System.out.print(echo(currentRedirection_));
                        break;

                    // pwd command.
                    case 4:
                        if (params.size() < 1) {
                            String pwdContent = ("\"" + pwd() + "\"");
                            currentRedirection_.add(0, pwdContent);
                            System.out.print(echo(currentRedirection_));
                        } else {
                            System.out.println(
                                    "pwd: ignoring non-option arguments");
                            System.out.println(pwd());
                        }
                        break;

                    // mv command.
                    case 5:
                        if (params.size() == 2) {
                            mv(params.get(0).toString(), 
                                    params.get(1).toString());
                        } else {
                            System.out.println("mv: missing file operand"
                                    + "\nSpecify OLDFILE and NEWFILE");
                        }
                        break;

                    // cp command.
                    case 6:
                        if (params.size() == 2) {
                            cp(params.get(0).toString(), 
                                    params.get(1).toString());
                        } else {
                            System.out.println("cp: missing file operand"
                                    + "\nSpecify OLDFILE and NEWFILE");
                        }
                        break;

                    // cat command.
                    case 7:
                        if (params.size() == 1) {
                            String catContent = ("\""
                                    + cat(params.get(0).toString()) + "\"");
                            currentRedirection_.add(0, catContent);
                            System.out.print(echo(currentRedirection_));
                        } else {
                            System.out.println("cat: missing file operand"
                                    + "\nInvalid filename");
                        }
                        break;

                    // get command.
                    case 8:
                        if (params.size() == 1) {
                            String getContent = ("\""
                                    + get(params.get(0).toString()) + "\"");
                            currentRedirection_.add(0, getContent);
                            System.out.print(echo(currentRedirection_));
                        } else {
                            System.out.println(
                                    "get: missing operand\nInvalid URL");
                        }
                        break;

                    // echo command.
                    case 9:
                        if (params.size() > 0) {
                            if (currentRedirection_.size() > 0) {
                                params.add(currentRedirection_.get(0));
                                params.add(currentRedirection_.get(1));
                            }
                            System.out.print(echo(params));
                        } else {
                            System.out.print("\n");
                        }
                        break;

                    // rm command.
                    case 10:
                        if (params.size() > 0) {
                            rm(params);
                        } else {
                            System.out.println("mkdir: missing operand");
                        }
                        break;

                    // ln command.
                    case 11:
                        if (params.size() == 2) {
                            ln(params.get(0).toString(), 
                                    params.get(1).toString());
                        } else {
                            System.out.println("ln: missing file operand"
                                    + "\nSpecify PATH1 and PATH2");
                        }
                        break;

                    // man command.
                    case 12:
                        if (params.isEmpty()) {
                            params.add("");
                        }
                        String manContent = (
                                "\"" + man(params.get(0).toString()) + "\"");
                        currentRedirection_.add(0, manContent);
                        System.out.print(echo(currentRedirection_));
                        break;

                    // find command.
                    case 13:
                        if (params.isEmpty()) {
                            params.add("");
                        }
                        // Add regex stuff here.
                        String findContent = find(params.get(0),
                                params.subList(1, params.size()));
                        assert findContent.equals("");
                        findContent = ("\"" + findContent + "\"");
                        currentRedirection_.add(0, findContent);
                        System.out.print(echo(currentRedirection_));
                        break;

                    // grep command.
                    case 14:
                        if (params.size()<2) {
                            System.out.println("Usage: grep [OPTION]... PATTERN [FILE]...");
                        }else{
                        String grepContent = ("\""
                                + grep(params.get(0),
                                params.subList(1, params.size())) + "\"");
                        currentRedirection_.add(0, grepContent);
                        System.out.print(echo(currentRedirection_));
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("-JShell: " + command + ": command not found");
        }
    }

    /**
     * Creates Directories in the paths provided. The path is considered to be
     * invalid if (1) There is already an item with the same path. (2) The
     * immediate parent of the specified path does not exist.
     *
     * @throws Exception if a provided path is invalid.
     * @param paths is the list of paths. Paths can be relative to the current
     * directory or the full path.
     */
    public void mkdir(List<String> paths) throws Exception {
        for (String dir : paths) {
            if (!dir.startsWith("/")) {
                dir = currentDirectory_.getPath().concat(dir);
            }
            while (dir.endsWith("/")) {
                dir = dir.substring(0, dir.length() - 1);
            }
            String[] dirs = dir.split("/");
            String dirName = dirs[dirs.length - 1];
            dir = dir.substring(0, dir.length() - dirName.length());
            Directory parent = (Directory) getItemAtPath(dir, 0);

            if (parent == null) {
                throw new NullPointerException("The path specified is "
                        + "incorrect.");
            }
            if (parent.getContents().keySet().contains(dirName)) {
                throw new Exception("A directory already exists at that "
                        + "path.");
            }
            parent.addItem(new Directory(dirName, parent.getPath() + dirName
                    + "/", parent));
        }
    }

    /**
     * Creates a file at the path passed in the parameter.
     *
     * @param path The path at which the file is to be created. The path has to
     * contain the file name as well.
     */
    public void mkfile(String path) throws Exception {
        Directory target = currentDirectory_;
        if (path.startsWith("/")) {
            String targetPath = path.substring(0, path.lastIndexOf("/"));
            target = (Directory) getItemAtPath(targetPath, 0);
        }
        if (target != null) {
            String fileName = path.substring(path.lastIndexOf("/") + 1);
            if (path.indexOf("/") != -1) {
                target = (Directory) getItemAtPath(
                        path.substring(0, path.lastIndexOf("/")), 0);
            }
            target.addItem(new File(fileName, target.getPath() + fileName,
                    target));
        }
    }

    /**
     * Changes current working directory to dir.
     *
     * @param dir name of a directory in the current directory or may be a full
     * path; '..' means a parent directory and '.' means the current directory;
     * The directory separator must be '/', the forward slash.
     */
    public void cd(String dir) throws Exception {
        // Initialize the destination directory.
        JShellItem destination;
        String path = dir;

        // If user's input path doesn't begin with "/", use relative path
        // with the current directory.
        if (!dir.startsWith("/")) {
            dir = currentDirectory_.getPath().concat(dir);
        }

        // If user's input path doesn't end with "/", concatenate it to
        // complete a full path.
        if (!dir.endsWith("/")) {
            dir = dir.concat("/");
        }

        // Find the directory location.
        destination = getItemAtPath(dir, 0);

        // Check to see if user is trying to change directory into a File
        // object.
        if (destination instanceof File) {
            System.out.println(path + ": Not a directory.");

            // destination is a Directory Object.
        } else {
            // Use try to catch NullPointerException for directories that
            // doesn't exist.

            // If path exists, location of the directory is found.
            if (destination != null) {
                currentDirectory_ = (Directory) destination;
            } else {
                // The directory doesn't exist.
                throw new NullPointerException(path + ": no such directory.");
            }
        }
    }

    /**
     * Return the names of the contained items, if path leads to a Directory.
     * Or, return the path provided, if it leads to a File.
     *
     * @param paths a list of Strings containing the desired paths.
     * @return a String containing the relevant contents.
     */
    public String ls(List<String> paths) throws Exception {
        // If ls is called without parameters.
        if (paths.isEmpty()) {
            if (!currentOptions_.equals("R")) {
                if (currentDirectory_.getSize() == 0) {
                    return "";
                } else {
                    return currentDirectory_.ls().substring(2);
                }
            }
            paths.add(currentDirectory_.getPath());
            return ls(paths);

            // If ls is called with paramters.
        } else {
            String lsOutput = "";
            for (int i = 0; i < paths.size(); i++) {
                JShellItem target = getItemAtPath(paths.get(i), 0);
                if (!lsOutput.isEmpty()) {
                    lsOutput += "\n";
                }
                lsOutput += paths.get(i);
                if (target == null) {
                    lsOutput += ": No such file or directory";
                } else {

                    if (target instanceof Directory
                            && currentOptions_.equals("R")
                            && ((Directory) target).getNumDirectories() > 0 && !(target instanceof DirectoryAlias)) {
                        List<String> recursivePath = recurseOnPath(
                                paths.get(i), false);
                        Collections.reverse(recursivePath);
                        lsOutput += target.ls();
                        lsOutput += "\n";
                        lsOutput += ls(recursivePath.subList(1,
                                recursivePath.size()));
                    } else {
                        // list paths separately
                        lsOutput += target.ls();

                        // if there are more paths, print a blank line
                        if (i < paths.size() - 1) {
                            lsOutput += "\n";
                        }
                        // notify user if there is an error in the path
                        // specified
                    }
                }
            }
            return lsOutput;
        }
    }

    /**
     * Return the current working directory as a full path.
     *
     * @return the String representation of current working directory's full
     * path.
     */
    public String pwd() {
        // Return the current working directory.
        return currentDirectory_.getPath();
    }

    /**
     * Move file or directory oldFile to newFile;
     *
     * @param oldFile a full path to a JShellItem or the name of a JShellItem in
     * the current directory which needs to be moved.
     * @param newFile a full path to a directory or the name of a directory in
     * the current directory to which oldFile needs to be moved.
     */
    public void mv(String oldFile, String newFile) throws Exception {
        JShellItem source = getItemAtPath(oldFile, 0);
        if (source == null) {
            throw new Exception(oldFile + ": doesn't exist.");
        }
        Directory sourceParent = source.getParentDirectory();
        Directory destinationParent;
        String newName;

        if (newFile.endsWith("/")) {
            destinationParent = (Directory) getItemAtPath(newFile, 0);
            newName = source.getName();

        } else {
            destinationParent = (Directory) getItemAtPath(newFile, 1);
            newName = newFile.substring(newFile.lastIndexOf("/") + 1);
        }

        if (destinationParent == null) {
            throw new Exception(newFile + ": invalid destination path.");
        }
        if (destinationParent.contains(newName)) {
            throw new Exception(newFile + ": already exists.");
        }
        sourceParent.removeItem(source);
        source.setName(newName);
        if (source instanceof Directory) {
            if (destinationParent.isChildOf((Directory) source)) {
                throw new Exception(String.format(
                        "mv: cannot move '%s' to a subdirectory of itself, '%s'"
                        , oldFile, newFile));
            }
            newName.concat("/");
        }
        source.setPath(destinationParent.getPath() + newName);
        source.setParentDirectory(destinationParent);
        destinationParent.addItem(source);

    }

    /**
     * Copy file or directory oldFile to newFile;
     *
     * @param oldFile a full path to a JShellItem or the name of a JShellItem in
     * the current directory which needs to be copied.
     * @param newFile a full path to a directory or the name of a directory in
     * the current directory to which oldFile needs to be copied.
     */
    public void cp(String oldPath, String newPath) throws Exception {
        Directory destinationParent;
        String newName;
        if (oldPath.equals(newPath)) {
            return;
        }
        JShellItem oldItem = getItemAtPath(oldPath, 0);
        if (oldItem == null) {
            throw new Exception(oldItem + " does not exist.");
        }
        if (newPath.endsWith("/")) {
            destinationParent = (Directory) getItemAtPath(newPath, 0);
            newName = oldItem.getName();
        } else {
            destinationParent = (Directory) getItemAtPath(newPath, 1);
            newName = newPath.substring(newPath.lastIndexOf("/") + 1);
        }

        if (destinationParent == null) {
            throw new Exception(newPath + " is an invalid destination path.");
        }
        if (destinationParent.contains(newName)) {
            throw new Exception(destinationParent.getPath() + newName 
                    + " already exists.");
        }
        if (oldItem instanceof File) {
            File newItem = new File(newName, destinationParent.getPath() + "/"
                    + newName, destinationParent, ((File) oldItem).getContent());
            destinationParent.addItem(newItem);
        } else {
            if (destinationParent.isChildOf((Directory) oldItem)) {
                throw new Exception(String.format(
                        "cp: cannot copy '%s' into itself, '%s'", 
                        oldPath, newPath));
            }
            Directory newItem = new Directory(newName,
                    destinationParent.getPath() + newName + "/",
                    destinationParent);
            destinationParent.addItem(newItem);
            for (JShellItem item : 
                    ((Directory) oldItem).getContents().values()) {
                cp(item.getPath(), newItem.getPath());
            }

        }
    }

    /**
     * Display the content of File in the shell.
     *
     * @param file the full path or the name of a File in current directory.
     * @return the contents of the specified File object.
     */
    public String cat(String file) throws Exception {
        // The file name given by the parameter is used to grab the file.
        JShellItem targetFile = getItemAtPath(file, 0);
        // If the file exists, then it returns the getContent method which
        // contains all of the files contents.
        if (targetFile != null) {
            return targetFile.getContent();
        } else {
            return "The file was not found.";
        }
    }

    /**
     * Saves the contents of the file located at URL to a file in the current
     * directory
     *
     * @param params
     * @return
     */
    public String get(String URL) {
        try {
            // The url is read by a buffered reader
            URL url = new URL(URL);
            BufferedReader page = new BufferedReader(new InputStreamReader(
                    url.openStream()));
            String pageInfo = "";
            String str;
            // The loop reads the lines of the page and concatenates them into a
            // single String.
            while ((str = page.readLine()) != null) {
                pageInfo = pageInfo + " " + str;
            }
            // The string containing the page's text is returned.
            return pageInfo.substring(1);
        } catch (IOException e) {
            return "The file was not found.";
        }
    }

    /**
     * Returns the string in the parameters defined by the existence of ' " ' at
     * the beginning and end of two of the parameters.
     *
     * @param params
     * @return
     */
    public String getContent(List<String> params) {
        String content = "";
        for (String x : params) {
        	while (x.indexOf("\\n") != -1) 
        		x = x.substring(0,x.indexOf("\\n")) + "\n" + x.substring(x.indexOf("\\n") + 2);
            if (content.isEmpty()
                    | !(content.endsWith("\" ") && content.length() > 2)) {
                content = content + x + " ";
            }
        }
        return content.substring(0, content.length() - 1);
    }

    /**
     * Returns the last_index, if it exists, of the string defined by the
     * existence of an ' " ' at the end of one of the parameters.
     *
     * @param params
     * @return
     */
    public int getLastIndex(List<String> params) {
        int curr_index = 0;
        int last_index = 0;
        for (String x : params) {
            if (x.endsWith("\"") & (!(curr_index == 0 & x.length() == 1))) {
                last_index = curr_index;
            }
            curr_index++;
        }
        return last_index;
    }

    /**
     * If there's an operator and filename in the list of parameters, then if operator 
     * is '>', overwrites the contents of outFile with the string text; if the operator 
     * is '>>', appends text to the contents of outFile; If outFile doesn't exist, 
     * creates a new file outFile and appends text to its contents.
     *
     * @param params A list of parameters which must include a string and may optionally
     * include an operator and a filename.
     */
    public String echo(List<String> params) throws Exception {
        // The content to be output in retrieved
        String content = getContent(params);
        // The last index of the string, if it exists, in the List params.
        int last_index = getLastIndex(params);
        // If content is a valid string and the correct syntax is used to write
        // to a file, then this code is executed.
        if (params.size() > last_index + 2 && content.startsWith("\"")
                && content.endsWith("\"") && content.startsWith("\"")
                && params.get(last_index + 1).startsWith(">")) {

            // The file name and parameter for writing is retrieved.
            String file_name = params.get(last_index + 2);
            String param = params.get(last_index + 1);
            // The quotations are striped from the string to be used.
            content = content.substring(1, content.length() - 1);

            // The file is retrieved if it exists.
            JShellItem targetFile = getItemAtPath(file_name, 0);

            // The file is deleted if it exists and is to be overwritten.
            // Otherwise the file is created.
            if (!(param.equals(">>") & targetFile != null)) {
                if (targetFile != null) {
                    targetFile.getParentDirectory().removeItem(targetFile);
                }
                mkfile(file_name);
                targetFile = getItemAtPath(file_name, 0);
            }

            // The file's contents are appended to either a blank (new) file
            // or an old file, depending on the user's input.
            ((File) targetFile).setContent(((File) targetFile).getContent()
                    + content);

            return "";
            // If the string is valid but the syntax is incorrect, then this
            // code is
            // executed.
        } else if (content.endsWith("\"") & content.startsWith("\"")) {
            if (content.substring(1, content.length() - 1).isEmpty()) {
                return content.substring(1, content.length() - 1);
            }
            return content.substring(1, content.length() - 1) + "\n";
            // If the string is not valid, this code is executed.
        } else {
            return "Echo requires a string with \" & \" surrounding the words"
                    + "\n";
        }
    }

    /**
     * Prompt to confirm that the user wants to delete the JShellItem from the
     * file system. If so, remove it. If -f is supplied, do not prompt and
     * confirm. If the root directory is specified, its contents but not itself
     * will be subject to deletion.
     *
     * @param paths the JShellItem to be removed.
     */
    public void rm(List<String> paths) throws Exception {
        // Loop through the user inputs.
        for (String path : paths) {
            // Initialize the current JShellItem.
            JShellItem item = getItemAtPath(path, 0);
            if (item == null) {
                System.out.printf("%s: does not exist.\n", path);
            } else {
                // Keep track of the size of that JShellItem.
                int size = item.getSize();

                if (size > 0) {
                    List<String> recursiveList = recurseOnPath(path, true);
                    rm(recursiveList.subList(0, size + 1));

                    if (item.getSize() == 0) {
                        rm(recursiveList.subList(size + 1, size + 2));
                    }

                } else {
                    if (item.getPath().equals("/")) {
                        return;
                    }
                    // Check the -f option.
                    if (!currentOptions_.equals("f")) {
                        System.out.printf("Really remove %s from %s? (y/n) ",
                                item.getName(), 
                                item.getParentDirectory().getPath());

                        // Read the user input.
                        ArrayList<String> in = readInput();

                        // If 'y'
                        if (in.toString().equals("[y]")) {
                            item.getParentDirectory().removeItem(item);
                            // if not 'n', ask again.
                        } else if (!in.toString().equals("[n]")) {
                            List<String> tempStore = new ArrayList<String>();
                            tempStore.add(path);
                            rm(tempStore);
                        }

                        // If -f is specified.
                    } else {
                        item.getParentDirectory().removeItem(item);
                    }
                }

            }
        }
    }

    /**
     * Create a shortcut of firstPath at secondPath.
     *
     * @param firstPath the target JShellItem to be linked.
     * @param secondPath the destination of the shortcut.
     */
    // TODO: Make ln fit the 30 line
    public void ln(String firstPath, String secondPath) throws Exception {
        // Initialize the variables.
        JShellItem targetItem;
        JShellItem aliasItem;
        String newName;
        Directory destinationDirectory;

        // Find and reference the target item. Throw Exception if the path
        // doesn't exist.
        targetItem = getItemAtPath(firstPath, 0);
        if (targetItem == null) {
            throw new Exception(firstPath + ": doesn't exist.");
        }

        // Find and reference the destination Directory.
        if (secondPath.endsWith("/")) {
            destinationDirectory = (Directory) getItemAtPath(secondPath, 0);
            newName = targetItem.getName();
        } else {
            destinationDirectory = (Directory) getItemAtPath(secondPath, 1);
            newName = secondPath.substring(secondPath.lastIndexOf("/") + 1);
        }

        // If the Directory destination is not found.
        if (destinationDirectory == null) {
            throw new Exception(secondPath 
                    + " is an invalid destination path.");
        }

        // Throw Exception when there is a JShellItem that already exist within
        // the destination Directory.
        if (destinationDirectory.contains(newName)) {
            throw new Exception(secondPath + ": already exists.");
        }

        // If the target item is a Directory object.
        if (targetItem instanceof Directory) 
            // Create a new Directory object that will references it's contents
            // from the target item.
            aliasItem = new DirectoryAlias(newName, destinationDirectory.getPath()
                    + newName + "/", destinationDirectory, (Directory)targetItem);



            // If the target item is a File object.
         else 
            // Create a new FileAlias object that will reference it's contents
            // from the target item.
            aliasItem = new FileAlias(newName, destinationDirectory.getPath()
                    + newName, destinationDirectory, (File) targetItem);

         
        
        // Add the alias object to the destination Directory.
        destinationDirectory.addItem(aliasItem);
    }

    /**
     * Returns the contents of a text file located at path.
     *
     * @param path the path to the text file.
     * @return the contents of the file.
     */
    public static String readTextFile(String path) {
        try {
            StringBuilder sb = new StringBuilder(1024);
            BufferedReader reader = new BufferedReader(new FileReader(path));

            char[] chars = new char[1024];
            int numRead;
            while ((numRead = reader.read(chars)) > -1) {
                sb.append(String.valueOf(chars, 0, numRead));
            }

            reader.close();

            return sb.toString();
        } catch (IOException e) {
            return "There is no manual for this command.";
        }
    }

    /**
     * Returns the manual for the desired command. If no command is specified,
     * or the specified command does not exist, an error message is returned.
     *
     * @param command a string specifying the desired command.
     * @return a string containing command's manual or an error message.
     */
    public String man(String command) {
        if (command.isEmpty()) {
            return "What manual page do you want?";
        } else {
            String path = "./manuals/" + command + ".txt";
            return readTextFile(path);
        }
    }

    /**
     * Print the path of the files specified by the paths argument whose path
     * contains regex.
     *
     * @param regex is a String regular expression
     * @param paths is a List of paths
     */
    public String find(String regex, List<String> paths) throws Exception {
        String results = "";
        regex = regex.replace("?", ".?").replace("*", ".*?");
        Pattern regexPattern = Pattern.compile(regex);
        Matcher regexMatcher;

        // If the user input was empty, call find on the current directory.
        if (paths.isEmpty()) {
            paths.add(currentDirectory_.getPath());
        }
        // Loop through the paths.
        for (String path : paths) {
            List<String> recursiveListing = recurseOnPath(path, true);
            // Loop through the list of JShellItems found recursively.
            for (String name : recursiveListing) {
                regexMatcher = regexPattern.matcher(name);
                // Append to results if match is found.
                if (regexMatcher.find()) {
                    results = results + name + "\n";
                }
            }
        }
        if (results.endsWith("\n")) {
            results = results.substring(0, results.length() - 1);
        } else {
            results = "find: no such file or directory.";
        }
        // Return the completed output.
        return results;
    }

    /**
     * Prints the path of the files specified by the paths argument that contain
     * a string that matches regex, followed by the particular line.
     *
     * @param regex is a String regular expression.
     * @param paths is a List of paths.
     *
     */
    public String grep(String regex, List<String> paths) throws Exception {
    	if (regex.startsWith("\"")){
    		int i = 0;
    		for(i = 0; i<paths.size() && !regex.endsWith("\""); i++)
    			regex +=" " + paths.get(i);
    		regex = regex.substring(1,regex.length()-1);
    		paths = paths.subList(i, paths.size());
    	}
    	if(paths.isEmpty())
    		throw new Exception("Usage: grep [OPTION]... PATTERN [FILE]...");
        String results = "";

        for (String path : paths) {
        	
            JShellItem item = getItemAtPath(path, 0);
            if (item.getSize()>0 && currentOptions_.equals("R") ) {

                results += grep(regex,
                        recurseOnPath(path, true).subList(0, item.getSize()));
            } else if (item instanceof File) {
                String content = ((File) item).getContent();
                if (content.contains(regex)) {
                	if (!results.isEmpty())
                		results += "\n";
                    results += item.getPath() + ":\n";
                    for (String line : content.split("\\n")) {
                        if (line.contains(regex)) {
                            results += line + "\n";
                        }
                    }
                    results = results.substring(0, results.length() - 1);
                }
            } else if (item instanceof Directory && !currentOptions_.equals("R")){
                results += "Cannot call grep on a directory without -R.";
            }
            else{
            	results += "";
            }
           
        }
        return results;
    }

    /**
     * Gets the JShellItem located numFoldersUp numbers of folders up from the
     * item located at path; path can be a full path or a path relative to the
     * current directory; if there is no such item, returns null.
     *
     * Call JShellItem(path, 0) to get the item located at path.
     *
     * @param path is a string that represents the full path to the JShellItem.
     * @param numFoldersUp
     * @return the JShellItem found at path or null if the item doesn't exist.
     */
    public JShellItem getItemAtPath(String path, int numFoldersUp)
            throws Exception {
        JShellItem item = currentDirectory_;
        int startIndex = 0;
        if (path.startsWith("/")) {
            startIndex = 1;
            item = rootDirectory_;
        }
        String[] directories = path.split("/");
        while (startIndex < directories.length - numFoldersUp) {
            if (directories[startIndex].equals("..")) {
                item = item.getParentDirectory();
                startIndex++;
            } else if (((Directory) item).contains(directories[startIndex])) {
                if (item instanceof File) {
                    throw new ClassCastException(item.getPath()
                            + "is not a Directory");
                }
                item = ((Directory) item).getItem(directories[startIndex]);
                startIndex++;
            } else if (!directories[startIndex].equals(".")) {
                return null;
            } else {
                startIndex++;
            }
        }
        return item;
    }

    /**
     * Return the root directory of JShell.
     *
     * @return Directory.
     */
    public Directory getRootDirectory() {
        return rootDirectory_;
    }

    /**
     * Return the current working directory of JShell.
     *
     * @return Directory.
     */
    public Directory getCurrentDirectory() {
        return currentDirectory_;
    }

    /**
     * Return the current option for commands.
     *
     * @return String representation of the current options.
     */
    public String getCurrentOption() {
        return currentOptions_;
    }

    /**
     * Set the current option for commands. [For testing purposes]
     *
     * @param newOption String the option to be set.
     */
    public void setCurrentOption(String newOption) {
        currentOptions_ = newOption;
    }

    /**
     * Removes redirection symbols from the parameter list params. 
     * Stores redirection information in currentRedirection_.
     *
     * @return params without redirection symbols
     */
    public List<String> handleRedirection(List<String> params) {
        // Clear the data for redirection for each new case.
        currentRedirection_.clear();
        // If the correct syntax is found, then the if statement is executed.
        if (params.size() >= 2 && params.get(params.size() - 2).equals(">")
                | params.get(params.size() - 2).equals(">>")) {
            // The redirection data is sent to a different private ArrayList.
            currentRedirection_.add(params.get(params.size() - 2));
            currentRedirection_.add(params.get(params.size() - 1));
            params.remove(params.size() - 1);
            params.remove(params.size() - 1);
        }
        return params;
    }

    /**
     * @param path
     * @param includeFiles
     * @return
     * @throws Exception
     */
    public List<String> recurseOnPath(String path, boolean includeFiles)
            throws Exception {

        List<String> paths = new ArrayList<String>();
        JShellItem item = getItemAtPath(path, 0);
        if (!(item instanceof DirectoryAlias) &&(includeFiles || item instanceof Directory)) {
            paths.add(item.getPath());
        }
        if (item instanceof File) {
            return paths;
        } else {
            for (JShellItem cont : ((Directory) item).getContents().values()) {
                paths.addAll(0, recurseOnPath(cont.getPath(), includeFiles));
            }
        }
        return paths;
    }

    /**
     * Loads the JShell from the specified file; returns null if file is
     * invalid.
     *
     * @param serFile is the path to a system file containing serialization
     * information for JShell.
     * @return a JShell item loaded from serFile or null if serFile contains
     * invalid data or doesn't exist.
     */
    public static JShell loadJShell(String serFile) {
        JShell loadedShell;
        FileInputStream fis;
        ObjectInputStream in;
        try {
            fis = new FileInputStream(serFile);
            in = new ObjectInputStream(fis);
            loadedShell = (JShell) in.readObject();
            in.close();
        } catch (Exception ex) {
            loadedShell = null;
        }
        return loadedShell;
    }

    /**
     * Saves the JShell into file at the specified path;
     *
     * @param serFile is the path to a system file in which serialization data
     * will be stored.
     * @return a JShell item loaded from serFile or null if serFile contains
     * invalid data or doesn't exist.
     */
    public void saveJShell(String serFile) {
        FileOutputStream fos;
        ObjectOutputStream out;
        try {
            fos = new FileOutputStream(serFile);
            out = new ObjectOutputStream(fos);
            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            System.out.println("Cannot save serialization data");
        }
    }

    /**
     * Creates new JShell instance and runs the Shell.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        String filename = "JShell.ser";
        JShell newShell = loadJShell(filename);
        
        if (newShell == null) {
            newShell = new JShell();
        }
        newShell.printPrompt();
        ArrayList<String> input = newShell.readInput();
        while (input.isEmpty() || !"exit".equals(input.get(0))) {
            if (!input.isEmpty()) {
                List<String> params = newShell.handleRedirection(input.subList(1,
                        input.size()));
                newShell.executeCommand(input.get(0).toString(), params);
            }
            newShell.printPrompt();
            input = newShell.readInput();
        }
        filename = "JShell.ser";
        newShell.saveJShell(filename);
    }
}
