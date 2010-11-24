package com.ecmdeveloper.plugin.scripting.classloader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import com.ecmdeveloper.plugin.scripting.util.PluginLog;

/**
 * The ExecuteMethodActionDelegate uses ProjectClassLoader to load the selected
 * class into the Favorites plug-in to be executed. This ClassLoader locates the
 * class file using the project’s Java build path, reads the class file using
 * standard java.io, and creates the class in memory using the superclass’
 * defineClass() method. It is not complete as it only loads source-based
 * classes; loading classes from a JAR file or reference project is left as an
 * exercise for the reader.
 */
public class ProjectClassLoader extends ClassLoader
{
   /**
    * The project for which classes are loaded
    */
   private IJavaProject project;

   /**
    * Construct a new instance for loading classes for a specific project.
    * 
    * @param project
    *           the project for which classes are loaded
    */
   public ProjectClassLoader(IJavaProject project) {
      if (project == null || !project.exists() || !project.isOpen())
         throw new IllegalArgumentException("Invalid project");
      this.project = project;
   }

   /**
    * Finds the class with the specified binary name. We override this method to
    * find the class in the associated project.
    * 
    * @param name
    *           The binary name of the class
    * @return The resulting Class object
    * @throws ClassNotFoundException
    *            If the class could not be found
    */
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      byte[] buf = readBytes(name);
      if (buf == null)
         throw new ClassNotFoundException(name);
      return defineClass(name, buf, 0, buf.length);
   }

   /**
    * Locate a class with the specified name and load the bytes from its
    * classfile.
    * 
    * @param name
    *           the fully qualified name of the class to be located.
    * @return the bytes or <code>null</code> if not found
    */
   private byte[] readBytes(String name) {

	   Path relativePathToClassFile = new Path(name.replace('.', '/') + ".class");

	   try
	   {
		   IClasspathEntry[] entries = project.getResolvedClasspath(true);
		   byte[] buffer = null;

		   for (IClasspathEntry entry : entries) {

			   switch (entry.getEntryKind()) {

			   case IClasspathEntry.CPE_SOURCE:
				   buffer = getBytesFromSource(entry, relativePathToClassFile);
				   break;
			   case IClasspathEntry.CPE_LIBRARY:
				   buffer = readBytesFromLibrary(entry, relativePathToClassFile);
				   break;
			   case IClasspathEntry.CPE_PROJECT:
				   // Handle other entry types here.
				   break;

			   default:
				   break;
			   }

			   if (buffer != null) {
				   return buffer;
			   }
		   }
	   }
	   catch (JavaModelException e) {
		   PluginLog.error(e);
		   return null;
	   }
	   return null;
   }

   private byte[] getBytesFromSource(IClasspathEntry entry, Path relativePathToClassFile)
   throws JavaModelException {
	   IPath path = getOutputPath(entry, relativePathToClassFile);
	   return readBytesFromFile(path.toFile());
   }

   private IPath getOutputPath(IClasspathEntry entry, Path relativePathToClassFile)
   throws JavaModelException {
	   IPath path = entry.getOutputLocation();
	   IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
	   if (path != null) {
		   path = rootLoc.append(path);
	   } else {
		   path = rootLoc.append(project.getOutputLocation());
	   }
	   path = path.append(relativePathToClassFile);
	   return path;
   }

   private byte[] readBytesFromLibrary(IClasspathEntry entry, Path relativePathToClassFile) {

	   IPath libraryPath = entry.getPath();
	   String jarFileName = libraryPath.toOSString();
	   //System.out.println( jarFileName );
	   try {
		   JarFile jarFile = new JarFile( jarFileName );
		   JarEntry jarEntry = jarFile.getJarEntry(relativePathToClassFile.toString() );
		   if ( jarEntry != null ) {
			   InputStream inputStream = jarFile.getInputStream(jarEntry);
			   
			   ByteArrayOutputStream byteStream = new ByteArrayOutputStream();   
			   int nextValue = inputStream.read();   
			   while (-1 != nextValue) {   
			   byteStream.write(nextValue);   
			   nextValue = inputStream.read();   
			   }   
			   return byteStream.toByteArray();   
		   
//			   return readBytesFromInputStream(inputStream);
		   }
	   } catch (IOException e) {
		   PluginLog.error(e);
	   }
	   return null;
   }

   private static byte[] readBytesFromFile(File file) {

	   if (file == null || !file.exists()) {
		   return null;
	   }
	   try {
		   InputStream stream = new BufferedInputStream(new FileInputStream(file));
		   return readBytesFromInputStream(stream);
	   } catch (FileNotFoundException e) {
		   PluginLog.error(e);
	   }
	   return null;
   }

   	private static byte[] readBytesFromInputStream(InputStream stream) {
   		try {
   			int size = 0;
   			byte[] buf = new byte[10];
   			while (true) {
   				int count = stream.read(buf, size, buf.length - size);
   				if (count < 0)
   					break;
   				size += count;
   				if (size < buf.length)
   					break;
   				byte[] newBuf = new byte[size + 10];
   				System.arraycopy(buf, 0, newBuf, 0, size);
   				buf = newBuf;
   			}
   			byte[] result = new byte[size];
   			System.arraycopy(buf, 0, result, 0, size);
   			return result;
   		} catch (Exception e) {
   			PluginLog.error(e);
   			return null;
   		} finally {
   			try {
   				if (stream != null)
   					stream.close();
   			} catch (IOException e) {
   				PluginLog.error(e);
   				return null;
   			}
   		}
	}
}
