package net.eithon.library.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class FileMisc {
	public static void makeSureParentDirectoryExists(File file){
		File directory = file.getParentFile();
		if (!directory.exists())
		{
			directory.mkdirs();
		}
	}

	public static String [] getFileNames(File folder) {
		return getFileNames(folder, null);
	}

	public static String [] getFileNames(File folder, String extension) {
		File[] files = null;
		if (extension != null) {
			files = folder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(extension);
				}
			});
		} else {
			files = folder.listFiles();
		}
		ArrayList<String> array = new ArrayList<String>();
		for (File file : files) {
			String fileName = file.getName();
			if (extension != null) fileName = fileName.replace(extension,"");
			array.add(fileName);
		}
		return array.toArray(new String[]{""});
	}
}
