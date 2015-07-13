package net.eithon.library.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.bukkit.plugin.java.JavaPlugin;

public class FileMisc {
	public static void makeSureParentDirectoryExists(File file){
		File directory = file.getParentFile();
		if (!directory.exists())
		{
			directory.mkdirs();
		}
	}
	
	public static void makeSureDirectoriesExists(File file){
		if (file.exists()) return;
		if (file.isDirectory()) {
			file.mkdirs();
			return;
		}
		makeSureParentDirectoryExists(file);
	}

	public static File getPluginDataFile(JavaPlugin plugin, String fileName) {
		return new File(plugin.getDataFolder(), fileName);
	}

	public static String [] getFileNames(File folder) {
		return getFileNames(folder, null);
	}

	public static String [] getFileNames(File folder, String extension) {
		File[] files = getFiles(folder, extension);
		return filesToFileNames(extension, files);
	}

	private static String[] filesToFileNames(String extension, File[] files) {
		if (files == null) return null;
		ArrayList<String> array = new ArrayList<String>();
		for (File file : files) {
			String fileName = file.getName();
			if (extension != null) fileName = fileName.replace(extension,"");
			array.add(fileName);
		}
		return array.toArray(new String[]{""});
	}

	public static File[] getFiles(File folder, String extension) {
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
		return files;
	}

	public static File[] getFilesOrderByLastModified(File folder, String extension, boolean ascending) {
		File[] files = getFiles(folder, extension);
		if ((files == null) || (files.length < 2)) return files;
		int factor = ascending ? 1 : -1;

		Arrays.sort(files, new Comparator<File>(){
			public int compare(File f1, File f2)
			{
				return factor*Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			} });
		return files;
	}
}
