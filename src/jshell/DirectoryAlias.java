package jshell;

import java.util.HashMap;

/**
 * A JShell DirectoryAlias object that acts as a shortcut to a directory
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author Michael In-Jey Hwang <c0hwangi>
 */
public class DirectoryAlias extends Directory {

    /**
     * A pointer to the directory
     */
    private Directory pointer_;
    
    /**
     * Initializes instance variables
     * @param name
     * @param path
     * @param parentDirectory
     * @param link 
     */
    public DirectoryAlias(String name, String path,
                Directory parentDirectory, Directory link) {
		super(name, path, parentDirectory);
		pointer_ = link;
	}
    /**
     * Returns an ArrayList of contents of the directory.
     * 
     * @return an arrayList of contents of the directory.
     */
    @Override
    public HashMap<String, JShellItem> getContents() {
    	if (this.isChildOf(pointer_))
    		return super.getContents();
        return pointer_.getContents();
    }
    
    /**
     * Replaces the contents of the directory with the contents in cont.
     * 
     * @param cont a hashMap with String names for keys, and JShellItem values.
     */
    @Override
    public void setContents(HashMap<String, JShellItem> cont) {
        pointer_.setContents(cont);
    }
    
    /**
     * Returns an ArrayList of Directories contained in the directory.
     * 
     * @return a hashmap of subdirectories of the directory. The keys are the 
     * names of the subdirectories.
     */
    @Override
    public HashMap<String, Directory> getSubDirectories() {
        return pointer_.getSubDirectories();
    }

    /**
     * Returns an ArrayList of Files contained in the directory.
     * 
     * @return an ArrayList of files contained in the directory.
     */
    @Override
    public HashMap<String, File> getFiles() {
        return pointer_.getFiles();
    }

    /**
     * Returns the JShellItem in contents_ that matches the String name.
     * 
     * @return the JShellItem in contents_ that matches the String name.
     */
    @Override
    public JShellItem getItem(String name) {
        return pointer_.getItem(name);
    }

    /**
     * Removes item from the directory.
     * 
     * @param item the JShellItem to be removed from the directory.
     */
    @Override
    public void removeItem(JShellItem item) {
        pointer_.removeItem(item);
    }

    /**
     * Adds item to the directory.
     * 
     * @param item the JShellItem to be added to the directory.
     */
    @Override
    public void addItem(JShellItem item) {
        pointer_.addItem(item);
    }

    /**
     * Checks whether the directory contains the JShellItem item.
     * 
     * @param item the name of the item.
     * @return True or False depending on whether the directory contains the
     * JShellItem item.
     */
    @Override
    public boolean contains(String item) {
        return pointer_.contains(item);
    }

    /**
     * Returns a list of items in the directory separated by new lines.
     * 
     * @return itemList a a semi-colon, a new line, and a list of contents of
     * the directory separated by new lines.
     */
    @Override
    public String ls() {
    	if (this.isChildOf(pointer_))
    		return "";
        return pointer_.ls();
    }
    
    /**
     * Return the number of children of this Directory.
     * 
     * @return a String representation of an error message.
     */
    @Override
    public int getSize() {
    	if (this.isChildOf(pointer_))
    		return super.getSize();
    	return pointer_.getSize();
    }
    
    /**
     * Return the number of children of this Directory.
     * 
     * @return a String representation of an error message.
     */
    @Override
    public int getNumDirectories() {
    	if (this.isChildOf(pointer_))
    		return super.getNumDirectories();
    	return pointer_.getNumDirectories();
    }

}
