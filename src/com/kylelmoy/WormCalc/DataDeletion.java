package com.kylelmoy.WormCalc;

import java.io.File;
public class DataDeletion {
	public static void main(String[] args) {
		delete(DataSetOperator.remote_root,"/log/");
	}
	public static void delete(String root, String path) {
		File file = new File(root);
		String[] names = file.list();
		for(String name : names)
		    if (new File(root + name).isDirectory()) {
		    	File toDelete = new File(root + name + path);
		    	System.out.println(toDelete.toString());
		    	if (toDelete.isDirectory()) {
		    		deleteFolder(toDelete);
		    	} else {
		    		toDelete.delete();
		    	}
		    }
	}
	public static void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    if(files != null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
}
