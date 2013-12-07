package server;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import webfiles.*;

public class WebFileFactory {
	
	Map<String, Class<? extends WebFile>> webFiles;
	
	public WebFileFactory(){
		webFiles = getWebFileClasses();
	}

	private static Map<String, Class<? extends WebFile>> getWebFileClasses(){
	    Set<URL> urls = ClasspathHelper.forPackage("webfiles");
	    
	    Reflections reflections = new Reflections("webfiles");

	    Set<Class<? extends WebFile>> subTypes = reflections.getSubTypesOf(WebFile.class);
	    
	    Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Types.class);
	    
	    System.out.println("Here are the annotated classes:");
	    for(Class<?> annotatedClass : annotated){
	    	System.out.println(annotatedClass);
	    	annotatedClass.getDeclaredAnnotations();
	    }
	    		
	    Map<String, Class<? extends WebFile>> webFiles = new HashMap<String, Class<? extends WebFile>>(); 
	    
	    for(Class<? extends WebFile> possibleMatch : subTypes){
	    	System.out.println(possibleMatch);
	    	if (annotated.contains(possibleMatch)){
	    		String[] acceptedFiles = possibleMatch.getAnnotation(Types.class).knownTypes();
	    		for(String s: acceptedFiles){
	    			webFiles.put(s, possibleMatch);
	    		}
	    	}
	    }
	    
	    return webFiles;
	}
	
}
