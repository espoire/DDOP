package file;

import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

/** The writer class simplifies writing an entire file at a time.
 * 
 * @author Espoire
 *
 */
public class Writer {
	/** Writes a new file at the specified <b>filepath</b>, containing the .toString() of the argument(s) appended together.
	 * Will fail and return false if the target file already exists. See Writer.overwrite().
	 *
	 * @param filepath - A full filepath to write to.
	 * @param objects - One or more objects to write to the file.
	 * @return <b>true</b> if successful, <b>false</b> otherwise.
	 */
	public static boolean write(String filepath, Object... objects) {
		return Writer.writeCore(filepath, false, objects);
	}

	/** Writes a new file at the specified <b>filepath</b>, containing the .toString() of the argument(s) appended together.
	 * If the target file already exists, it will be deleted before writing.
	 *
	 * @param filepath - A full filepath to write to.
	 * @param objects - One or more objects to write to the file.
	 * @return <b>true</b> if successful, <b>false</b> otherwise.
	 */
	public static boolean overwrite(String filepath, Object... objects) {
		return Writer.writeCore(filepath, true, objects);
	}

	/** Writes a new file at the specified <b>filepath</b>, containing the .toString() of the argument(s) appended together.
	 *
	 * @param filepath - A full filepath to write to.
	 * @param forceOverwrite - If true and the file exists, the method will delete the file.
	 * @param objects - One or more objects to write to the file.
	 * @return <b>true</b> if successful, <b>false</b> otherwise.
	 */
	private static boolean writeCore(String filepath, boolean forceOverwrite, Object... objects) {
		if(new File(filepath).canRead()) {
			if(forceOverwrite) {
				new File(filepath).delete();
			} else {
				System.err.println("File already exists: " + filepath);
				return false;
			}
		}
		
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new FileWriter(filepath));
		} catch (IOException e) {
			System.err.println("IOException trying to write to: " + filepath);
			e.printStackTrace();
			return false;
		}
		
		try {
			for(Object o : objects) bw.write(o.toString());
		} catch (IOException e) {
			System.err.println("IOException trying to write data to: " + filepath);
			e.printStackTrace();
		} finally {
			try {
			    if(bw != null) bw.close();
			} catch (IOException e) {
				System.err.println("IOException trying to close written file: " + filepath);
			    e.printStackTrace();
			    return false;
			}
		}
		
		return true;
	}
	
	/** Sets the creation, modified, and accessed times of an existing file. Known to silently fail on some unix filesystems. */
	public static void setFileTime(String filepath, FileTime time) {
		Path p = Paths.get(filepath);
		try {
		    Files.setAttribute(p, "creationTime", time);
		    Files.setAttribute(p, "lastModifiedTime", time);
		    Files.setAttribute(p, "lastAccessTime", time);
		} catch (IOException e) {
		    System.err.println("Cannot change the creation time. " + e);
		}
	}

	public static boolean isFirstNewer(String firstFilepath, String secondFilepath) {
		File first = new File(firstFilepath);
		File second = new File(secondFilepath);
		
		if(!second.exists()) return true;
		
		return FileUtils.isFileNewer(first, second);
	}
}