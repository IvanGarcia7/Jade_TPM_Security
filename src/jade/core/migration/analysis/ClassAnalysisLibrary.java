/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
 
package jade.core.migration.analysis;

import java.util.Vector;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

//import org.apache.bcel.classfile.ClassParser;
//import org.apache.bcel.classfile.JavaClass;
//import org.apache.bcel.classfile.Field;
//import org.apache.bcel.classfile.Method;
//import org.apache.bcel.Repository;
//import org.apache.bcel.util.ClassLoaderRepository;
//import org.apache.bcel.classfile.Constant;
//import org.apache.bcel.classfile.ConstantPool;
//import org.apache.bcel.classfile.ConstantClass;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;

/**
 * Library to recursively analize class dependencies and construct jar packets
 * with theirs.
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 */
public class ClassAnalysisLibrary {

	private static final int SIZE_CREATE_JAR_BUFFER = 4096;
	
	String className = null;
	Vector filters = null;
	Class c = null;
	
	public ClassAnalysisLibrary(String className) throws ClassNotFoundException {
		this.className = className;
		filters = new Vector();
	}
	
	public ClassAnalysisLibrary(Class c) throws ClassNotFoundException {
		this.className = c.getName();
		this.c = c;
		filters = new Vector();
	}
	
	/**
	 * Method to add a filter to the class dependence search.
	 * @param filter String with filter to add.
	 */
	public void addFilter(String filter) {
		filters.add(filter);
	}
	
	/**
	 * Clear all inserted filters.
	 *
	 */
	public void resetFilter() {
		filters.clear();
	}
	
	/**
	 * Return an Enumeration of Strings of all added filters.
	 * @return An Enumeration of strings with filters.
	 */
	public Enumeration getFilters() {
		return (filters.elements());
	}
	
	/**
	 * Return bytes from the loaded class.
	 * @return
	 */
//	public byte[] getClassBytes() {
//		return clazz.getBytes();
//	}

	
	/**
	 * Method that return a JAR byte array with passed class and his
	 * dependences.
	 * @param c Main class to start search.
	 * @param recursiveSearch Indicates whether search dependences of the first dependences of main class.
	 * @return Return byte array of the resulting JAR.
	 */
	public byte[] createJar(boolean recursiveSearch) throws ClassNotFoundException, IOException, Exception {
		
		ClassAnalysisLibrary cal;
		Enumeration dependantClasses;
		String dependantClass;
		ByteArrayOutputStream baos;
		DataInputStream dis;
		JarOutputStream jos;
		JarEntry je;
		ClassLoader cl;
		byte[] buffer = new byte[SIZE_CREATE_JAR_BUFFER];
		int readed = 0;
		
		
		//Get an String enumeration of agent dependant classes.
      	dependantClasses = getDependantClasses(recursiveSearch);

      	//Acquire class c ClassLoader.
      	cl = c.getClassLoader();
      	
      	//Construct JAR agent.
      	baos = new ByteArrayOutputStream();
      	jos = new JarOutputStream(baos);
      	while (dependantClasses.hasMoreElements()) {
      		dependantClass = (String) dependantClasses.nextElement();
      		je = new JarEntry(dependantClass + ".class");
      		jos.putNextEntry(je);

      		dis = new DataInputStream(cl.getResourceAsStream(dependantClass + ".class")); 
          
      		while ((readed=dis.read(buffer))>=0)      			
      			jos.write(buffer,0,readed);
			
      		dis.close();
			jos.closeEntry();
      	}
      	    
      	//Close streams.
      	jos.close();
	
		return baos.toByteArray();
	}
	
	/**
	 * Search dependant classes. If is selected it searches recursively, i.e.
	 * it search dependant classes of the dependant classes.
	 * @param recursiveSearch Activate or not recursive search.
	 * @return Enumeration of strings with dependant classes.
	 */
	public Enumeration getDependantClasses(boolean recursiveSearch) throws ClassNotFoundException {
		
		Vector result = new Vector();
		scanDependantClasses(recursiveSearch, result, className);
		
		return result.elements();
	}

	/**
	 * Recursive method that search for dependant classes and do recursive
	 * calls to theirs to find another level of dependant classes.
	 * @param recursiveSearch Activate or not recursive search.
	 * @param result Last results.
	 * @param className Class to explore.
	 * @throws ClassNotFoundException If the class is not found.
	 */
	private void scanDependantClasses(boolean recursiveSearch, Vector result, String className) throws ClassNotFoundException {

		ConstantPool cp = null;
		boolean removed = false;
		String tempConstant;
		String[] arrayString;
		Vector subresult = new Vector();
	
		try {
			if (c!=null) cp = new ConstantPool(className,c.getClassLoader());
			else cp = new ConstantPool(className);
		} catch (IOException ioe) {
			throw new ClassNotFoundException();
		}
		
		if (cp==null) throw new ClassNotFoundException();

		//Scan constant pool class searching Classes, Fields, Methods and Interfaces.
		for(int i=1;i<cp.length();i++){
			ConstantElement ce = cp.getElement(i);
			if (ce instanceof ConstantClass){
				tempConstant = ((ConstantUtf8String)cp.getElement(((ConstantClass)ce).getNameIndex())).getString();
				if (acceptConstant(tempConstant)) subresult.add(tempConstant);

			}
			else if(ce instanceof ConstantField){
				//ConstantClass cc = 
					//(ConstantClass)cp.getElement(((ConstantField)ce).getClassIndex());
				ConstantNameAndType cne = 
					(ConstantNameAndType)cp.getElement(((ConstantField)ce).getNameIndex());
				
				tempConstant = cleanSignature(((ConstantUtf8String)cp.getElement(cne.getDescIndex())).getString());
				if (tempConstant!=null)
					if (acceptConstant(tempConstant)) subresult.add(tempConstant);

				//Print field name (debug purpose).
				//System.out.println(((ConstantUtf8String)cp.getElement(cne.getNameIndex())).getString());
			}
			else if(ce instanceof ConstantMethod){
				//ConstantClass cc = 
					//(ConstantClass)cp.getElement(((ConstantMethod)ce).getClassIndex());
				ConstantNameAndType cne = 
					(ConstantNameAndType)cp.getElement(((ConstantMethod)ce).getNameIndex());
				
				arrayString = 
					unmarshallMethodClasses(((ConstantUtf8String)cp.getElement(cne.getDescIndex())).getString());
				for (int j = 0; j < arrayString.length; j++) {
					if ((arrayString[j] != null)&&(!arrayString[j].trim().equals(""))) {
						if (acceptConstant(arrayString[j])) subresult.add(arrayString[j]);
					}
				}

				//Print method name (debug purpose).
				//System.out.println(((ConstantUtf8String)cp.getElement(cne.getNameIndex())).getString());
			}

		}
		
		//Look what subresult are in the result vector and delete 
		//them from subresult vector.
		for (int i = (subresult.size()-1); i >= 0; i--) {
			removed = false;
			for (int j = 0; j < result.size(); j++) {
				if (subresult.get(i).equals(result.get(j))) {
					subresult.remove(i);
					removed = true;
					break;
				}
			}
			if (!removed) result.add(subresult.get(i));
		}
		
		//Do the recursive search if needed.
		if (recursiveSearch) {
			for (int i = 0; i < subresult.size(); i++) {
				scanDependantClasses(true,result,(String) subresult.get(i));
			}
		}
	}
	
	/**
	 * Method that unmarshall classes contained into a method signature.
	 * @param signature Signature to unmarshall.
	 * @return Array of strings with classes referenced.
	 */
	private String[] unmarshallMethodClasses(String signature) {
		
		String arguments[], returnClass;
		int first, last;
		
		first = signature.indexOf('(');
		last = signature.lastIndexOf(')');



		//Take the arguments and return classes.
		returnClass = signature.substring(last+1,signature.length());
		arguments = (signature.substring(first+1,last).concat(returnClass)).split(";");
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].length()>0) {
				if (arguments[i].charAt(0) != 'L') {
					arguments[i] = null;
				} else {
					arguments[i] = arguments[i].substring(1);
				}
			}
		}
			
		return arguments;
	}
	
	/**
	 * Clean signature string from control characters (such as [, L, ;).
	 * @param signature String signature to clean.
	 * @return Cleaned string or null if the string is not a reference to a class.
	 */
	private String cleanSignature(String signature) {
		
		int counter = 0;

		//Filter constants that begin with "[".
		while (signature.charAt(counter) == '[') {
			counter++;
		}
		
		//Check for control character L that indicate a reference to a class.
		if (signature.charAt(counter) != 'L') return null;
		
		//Delete first characters pointed out by counter and last semicolon character.
		return signature.substring(counter+1,signature.length()-1);
		
	}
	
	/**
	 * Method that decides whether accept a constant or not according to
	 * the filters added by user.
	 * @param constant Constant to process.
	 * @return 	true  --> Constant is accepted.
	 * 			false --> Constant is not accepted.
	 */
	private boolean acceptConstant(String constant) {
	
		//Filter constants that begin with "[".
		if (constant.charAt(0) == '[') return false;
		
		//Filter constants that contain character "$".
		//if (constant.indexOf('$') != -1) return false;
		
		//constant.startsWith();
		for (int i = 0; i < filters.size(); i++) {
			if (constant.startsWith((String) filters.get(i))) return false;
		}
		
		return true;
	}
	
}
