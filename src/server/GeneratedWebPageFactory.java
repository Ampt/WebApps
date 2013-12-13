package server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;

import webfiles.HTMLPage;

public class GeneratedWebPageFactory {
	private static Map<String, Class<? extends WebFile>> knownGeneratedWebFiles;

	static {
		knownGeneratedWebFiles = new HashMap<String, Class<? extends WebFile>>();
		Reflections reflections = new Reflections("generatedWebFile");
		Set<Class<? extends WebFile>> generatedWebFiles = reflections.getSubTypesOf(WebFile.class);
		for(Class<? extends WebFile> webFileClass: generatedWebFiles){
			knownGeneratedWebFiles.put(webFileClass.getCanonicalName().replace("generatedWebFiles.", ""), webFileClass);
		}

	}
	/**
	private static void regenerateWebFileList(){
		knownGeneratedWebFiles.clear();
		Reflections reflections = new Reflections("generatedWebFile");
		Set<Class<? extends WebFile>> generatedWebFiles = reflections.getSubTypesOf(WebFile.class);
		for(Class<? extends WebFile> webFileClass: generatedWebFiles){
			
			knownGeneratedWebFiles.put(webFileClass.getCanonicalName(), webFileClass);
		}
	}
	*/

	public static WebFile getGeneratedFile(String fileName) {
		// Make sure we don't have any pesky paths in the name.
		String strippedName = new File(fileName).getName();
		String withoutExtension = strippedName.substring(0, strippedName.lastIndexOf('.'));
		if(knownGeneratedWebFiles.containsKey(withoutExtension)) {
			try {
				return knownGeneratedWebFiles.get(withoutExtension).getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				/** We don't support dynamic class loading... yet.
				// We don't have a matching generated web file. Let's try again just to make sure.
				regenerateWebFileList();
				try {
					return knownGeneratedWebFiles.get(absoluteName).getDeclaredConstructor().newInstance();
				} catch (Exception e2Brute) {
					// We really don't have what they want. Let's give 'em the old 404
					return new HTMLPage(new File("webapps/404.html"));
				}
				*/ 
				// This will return a 404 page.
				return new HTMLPage(new File("webapps/404.html"));
			}
		}
		// No matching file. 404!
		return new HTMLPage(new File("webapps/404.html"));
	}
}
