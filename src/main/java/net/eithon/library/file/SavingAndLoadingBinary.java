package net.eithon.library.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SavingAndLoadingBinary {
	public static <T extends Object> void save(T obj,File file) throws Exception
	{
		FileMisc.makeSureParentDirectoryExists(file);
		
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);

		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	
	public static <T extends Object> T load(File file) throws Exception
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		@SuppressWarnings("unchecked")
		T result = (T)ois.readObject();
		ois.close();
		return result;
	}
}
