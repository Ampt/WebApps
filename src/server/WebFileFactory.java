package server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;

import webfiles.HTMLPage;

public class WebFileFactory {
	private static Map<String, Class<? extends WebFile>> knownTypes;

	static {
		Reflections reflections = new Reflections("webfiles");
		Set<Class<? extends WebFile>> subtypes = reflections.getSubTypesOf(WebFile.class);
		knownTypes = new HashMap<String, Class<? extends WebFile>>();
		for(Class<? extends WebFile> possibleMatch : subtypes) {
			if(possibleMatch.isAnnotationPresent(Types.class)) {
				Types t = (Types) possibleMatch.getAnnotation(Types.class);
				for(String fileExt : t.knownTypes()) {
					knownTypes.put(fileExt, possibleMatch);
				}
			}
		}
	}

	public static WebFile getFile(File fileToDisplay) {
		String fileName = fileToDisplay.getName();
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		if(knownTypes.containsKey(extension)) {
			try {
				return knownTypes.get(extension).getDeclaredConstructor(File.class).newInstance(fileToDisplay);
			} catch (Exception e) {
				// 404em!
				return new HTMLPage(new File("webapps/404.html"));
			}
		}
		System.err.println("We don't support the following filetype: " + fileName);
		return new HTMLPage(new File("webapps/404.html"));
	}
}
