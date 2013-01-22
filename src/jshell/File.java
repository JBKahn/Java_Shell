package jshell;

import java.io.Serializable;

/**
 * A JShell File object that can store text (String) information.
 *
 * @author Michael Kozakov <c1kozako>
 * @author Mohamed Khodeir <c1khodei>
 * @author Joseph Ben-Zion Kahn <g1boggyp>
 * @author Michael In-Jey Hwang <c0hwangi>
 */
@SuppressWarnings("serial")
public class File extends JShellItem implements Serializable{
    /*
     * The contents of the file, of the type String.
     */
    private String content_;

    /**
     * Initializes the name path and parentDirectory of the file
     * 
     * @param name the name of the File.
     * @param path the working directory of File.
     * @param parentDirectory the parent Directory.
     */
    public File(String name, String path, Directory parentDirectory) {
        super(name, path, parentDirectory);
        content_ = "";
    }
    public File(String name, String path, Directory parentDirectory,
            String content) {
        super(name, path, parentDirectory);
        content_ = content;
    }
    /**
     * Replace the File's content with text.
     * 
     * @param text a String of characters to be replaced as the new content of 
     * the File object.
     */
    public void setContent(String text) {
        content_ = text;
    }

    /**
     * Return the content of the File.
     * 
     * @return the String representation of File's contents.
     */
    @Override
    public String getContent() {
        return content_;
    }       
}
