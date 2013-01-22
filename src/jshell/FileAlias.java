package jshell;

import java.io.Serializable;

/**
 * A JShell FileAlias object that can store text (String) information that is 
 * referenced from a File object.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author Michael In-Jey Hwang <c0hwangi>
 */
@SuppressWarnings("serial")
public class FileAlias extends File implements Serializable {
    /*
     * The File object that this alias points to.
     */
    private File filePointer_;
    
    /**
     * Initializes the name, path, parentDirectory and filePointer of the alias.
     * 
     * @param name the name of the FileAlias.
     * @param path the working directory of FileAlias.
     * @param parentDirectory the parent Directory.
     * @param pointer the File to be linked to.
     */
    public FileAlias(String name, String path, Directory parentDirectory, 
            File pointer) {
        super(name, path, parentDirectory);
        filePointer_ = pointer;
    }
    
    /**
     * Return the content of referenced File object.
     * 
     * @return the String representation of referenced File's contents.
     */
    @Override
    public String getContent() {
        return filePointer_.getContent();
    }
    
    /**
     * Replace the referenced File's content with the parameter text.
     * 
     * @param text the String of characters to be replaced as the new content
     * of the referenced File object.
     */
    @Override
    public void setContent(String text) {
        filePointer_.setContent(text);
    }
}
