package me.shansen.EggCatcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//Created 11/11/2016 2:21 AM
public class EggCatcherLogger{
	private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private File file;

	public EggCatcherLogger(File file){
		this.file = file;
	}

	public void logToFile(String message){
		if (!file.exists()){
			try{
				file.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileWriter(file, true));
		}catch(IOException e){
			e.printStackTrace();
		}
		pw.println(dateFormat.format(System.currentTimeMillis()) + " " + message);
		pw.flush();
		pw.close();
	}

	public File getFile(){
		return file;
	}

	public void setFile(File file){
		this.file = file;
	}
}
