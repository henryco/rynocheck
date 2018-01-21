package net.henryco.rynocheck.utils;

import com.github.henryco.injector.meta.resolver.IClassFinder;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.val;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor @Log
public class PluginClassFinder implements IClassFinder{

	private final ClassLoader classLoader;
	public PluginClassFinder() {
		this(null);
	}

	@Override
	public ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {

		List<ClassLoader> classLoadersList = new LinkedList<>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		if (classLoader != null) classLoadersList.add(classLoader);

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(false), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(pckgname))));

		val set = reflections.getSubTypesOf(Object.class);

		return new ArrayList<>(set);
	}
}