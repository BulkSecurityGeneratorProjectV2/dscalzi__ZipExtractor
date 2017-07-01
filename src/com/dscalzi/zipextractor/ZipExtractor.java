/*
 * ZipExtractor
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.zipextractor;

import java.io.File;

import org.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import com.dscalzi.zipextractor.managers.ConfigManager;
import com.dscalzi.zipextractor.managers.MessageManager;
import com.dscalzi.zipextractor.util.ZServicer;

public class ZipExtractor extends JavaPlugin{ 

	@SuppressWarnings("unused")
	private Metrics metrics;
	
    @Override
    public void onEnable(){
    	ConfigManager.initialize(this);
    	MessageManager.initialize(this);
    	ZServicer.initalize(ConfigManager.getInstance().getMaxQueueSize(), ConfigManager.getInstance().getMaxPoolSize());
    	this.getCommand("zipextractor").setExecutor(new MainExecutor(this));
    	metrics = new Metrics(this);
    }
    
    @Override
    public void onDisable(){
    	boolean wait = ConfigManager.getInstance().waitForTasksOnShutdown();
    	ZServicer.getInstance().terminate(!wait, wait);
    }
    
    public String formatPath(String path, boolean forStorage){
    	
    	if(path == null) return null;
    	
    	if(path.contains("*plugindir*")) path = path.replace("*plugindir*", this.getDataFolder().getAbsolutePath());
    	
    	path = path.replaceAll("/|\\\\\\\\|\\\\", "/");
    	
    	String[] cleaner = path.split("\\/");
    	path = "";
    	for(int i=0; i<cleaner.length; ++i){
    		cleaner[i] = cleaner[i].trim();
    		path += cleaner[i] + "/";
    	}
    	path = path.substring(0, (path.lastIndexOf("/") != -1) ? path.lastIndexOf("/") : path.length());
    		
    	
    	if(!forStorage)
    		path = path.replace("/", File.separator);
    	
    	return path;
    }
    
    public String getFileExtension(File f){
    	String fileExtension = "";
    	String path = f.getAbsolutePath();
		if(path.lastIndexOf(".") != -1 && !f.isDirectory()) 
			fileExtension = path.substring(path.lastIndexOf(".")).toLowerCase();
		return fileExtension;
    }
    
}