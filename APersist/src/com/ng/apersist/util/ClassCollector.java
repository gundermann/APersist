package com.ng.apersist.util;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ClassCollector {

	public static Set<String> collectFromPath(String path, String pkgName) {
		Set<String> classNames = new HashSet<String>();
		File f = new File(path);
		File[] listFiles = f.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (!listFiles[i].isDirectory()) {
				classNames.add(pkgName + "." + listFiles[i].getName().replace(".class", ""));
			} else {
				classNames.addAll(collectFromPath(listFiles[i]
						.getAbsolutePath(), pkgName + "." + listFiles[i].getName()));
			}
		}
		return classNames;
	}

	public static Set<String> collectFromPath(Package pkg) {
		String pkgName = pkg.getName();
	    String relPath = pkgName.replace('.', '/');

	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		return collectFromPath(resource.getPath(), pkgName);
	}

}
