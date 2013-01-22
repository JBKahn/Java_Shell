package jshell;

import java.util.HashMap;
import java.io.Serializable;

/**
 * A JShell Directory object that can contain File objects as well as other 
 * Directory objects.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author In-Jey Hwang <c0hwangi>
 */
public class Directory extends JShellItem implements Serializable{
    /**
     * Contents of the directory. The names are keys and JShellItems are values.
     */
    private HashMap<String, JShellItem> contents_;

    /**
     * Initializes the name, path and parentDirectory of the directory.
     *
     * @param name name of the Directory.
     * @param path the path of the Directory where the Directory is located.
     * @param parentDirectory the parent Directory.
     */
    public Directory(String name, String path, Directory parentDirectory) {
        super(name, path, parentDirectory);
        contents_ = new HashMap<String, JShellItem>();
    }

    /**
     * Returns an ArrayList of contents of the directory.
     * 
     * @return an arrayList of contents of the directory.
     */
    public HashMap<String, JShellItem> getContents() {
        return contents_;
    }
    
    /**
     * Replaces the contents of the directory with the contents in cont.
     * 
     * @param cont a hashMap with String names for keys, and JShellItem values.
     */
    public void setContents(HashMap<String, JShellItem> cont) {
        contents_ = cont;
    }
    
    /**
     * Returns an ArrayList of Directories contained in the directory.
     * 
     * @return a hashmap of subdirectories of the directory. The keys are the 
     * names of the subdirectories.
     */
    public HashMap<String, Directory> getSubDirectories() {
        HashMap<String, Directory> subdirs = new HashMap<String, Directory>();
        for (JShellItem i : contents_.values()) {
            if (i instanceof Directory) {
                subdirs.put(i.getName(), (Directory) i);
            }
        }
        return subdirs;
    }

    /**
     * Returns an ArrayList of Files contained in the directory.
     * 
     * @return an ArrayList of files contained in the directory.
     */
    public HashMap<String, File> getFiles() {
        HashMap<String, File> files = new HashMap<String, File>();
        for (JShellItem i : contents_.values()) {
            if (i instanceof File) {
                files.put(i.getName(), (File) i);
            }
        }
        return files;
    }

    /**
     * Returns the JShellItem in contents_ that matches the String name.
     * 
     * @return the JShellItem in contents_ that matches the String name.
     */
    public JShellItem getItem(String name) {
        return contents_.get(name.toLowerCase());
    }

    /**
     * Removes item from the directory.
     * 
     * @param item the JShellItem to be removed from the directory.
     */
    public void removeItem(JShellItem item) {
        contents_.remove(item.getName().toLowerCase());
    }

    /**
     * Adds item to the directory.
     * 
     * @param item the JShellItem to be added to the directory.
     */
    public void addItem(JShellItem item) {
        contents_.put(item.getName().toLowerCase(), item);
    }

    /**
     * Checks whether the directory contains the JShellItem item.
     * 
     * @param item the name of the item.
     * @return True or False depending on whether the directory contains the
     * JShellItem item.
     */
    public boolean contains(String item) {
        return contents_.containsKey(item.toLowerCase());
    }

    /**
     * Returns a list of items in the directory separated by new lines.
     * 
     * @return itemList a a semi-colon, a new line, and a list of contents of
     * the directory separated by new lines.
     */
    @Override
    public String ls() {
        HashMap<String, JShellItem> contents = getContents();
        String itemList = ":";
        for (JShellItem item : contents.values()) {
            itemList += "\n" + item.getName();
        }
        return itemList;
    }
    
    /**
     * Return the number of children of this Directory.
     * 
     * @return a String representation of an error message.
     */
    @Override
    public int getSize() {
    	return contents_.size();
    }
    
    /**
     * Return the number of children of this Directory.
     * 
     * @return a String representation of an error message.
     */
    public int getNumDirectories() {
    	return getSubDirectories().size();
    }
}
