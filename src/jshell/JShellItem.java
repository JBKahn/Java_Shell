package jshell;

import java.io.Serializable;

/**
 * A JShell Item object that can be stored within a JShell Directory.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author In-Jey Hwang <c0hwangi>
 */
@SuppressWarnings("serial")
public abstract class JShellItem implements Serializable{
	/*
	 * Path of the JShellItem.
	 */
    private String path_;
    
    /*
     * Name of the JShellItem.
     */
    private String name_;
    
    /*
     * The parent directory of the JShellItem.
     */
    private Directory parentDirectory_;

    /**
     * Constructor for creating a new JShellItem object.
     * 
     * @param name the name of the JShellItem.
     * @param path the path of the Directory where the JShellItem is located.
     * @param parentDirectory the parent Directory.
     */
    public JShellItem(String name, String path, Directory parentDirectory) {
        name_ = name;
        path_ = path;
        parentDirectory_ = parentDirectory;
    }

    /**
     * Replace the path to the JShellItem to the one specified in 
     * the parameter path.
     * 
     * @param path the full path to the JShellItem's directory.
     */
    public void setPath(String path) {
        path_ = path;
    }

    /** 
     * Return a {@link String} representing the full path 
     * to the JShellItem's directory.
     * 
     * @return a String representing the full path of the JShellItem's 
     * Directory.
     */
    public String getPath() {
        return path_;
    }

    /**
     * Replace the current name of the JShellItem to param name.
     * 
     * @param name a String representing the new name of the JShellItem.
     */
    public void setName(String name) {
        name_ = name;
    }

    /**
     * Return a {@link String} representing the name of the JShellItem.
     * 
     * @return a String representation of JShellItem's name.
     */
    public String getName() {
        return name_;
    }
    
     /**
     * Return a {@link Directory} object that represents the parent Directory.
     * 
     * @return the parent Directory.
     */
    public Directory getParentDirectory() {
        return parentDirectory_;
    }

    /**
     * Assign parentDirectory as the new parent Directory.
     * 
     * @param parentDirectory a Directory object that represents the new 
     * parent Directory.
     */
    public void setParentDirectory(Directory parentDirectory) {
        parentDirectory_ = parentDirectory;
    }
    
    /**
     * Return an empty string.
     * 
     * @return an empty string.
     */
    public String ls() {
        return "";
    }
    
    /**
     * Notify the User that this JShellItem is not a File, and therefore
     * the cat command cannot be used.
     * 
     * @return a String representation of an error message.
     */
    public String getContent() {
        return name_ + ": is not a file.";
    }
    /**
     * Notify the User that this JShellItem is not a Directory, and therefore
     * the size attribute is not defined.
     * 
     * @return Integer.
     */
    public int getSize() {
    	return -1;
    	
    }
    /**
     * Checks whether the directory is a subdirectory of ancestor
     * 
     * @param ancestor directory
     * @return True if the directory is a subdirectory of ancestor
     */
    public boolean isChildOf(Directory ancestor){
    	return path_.contains(ancestor.getPath());
    }
}
