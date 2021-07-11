package br.com.ppgi.unirio.luiz.softwareanalysis.analyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.management.modelmbean.XMLParseException;

import br.com.ppgi.unirio.luiz.softwareanalysis.controller.ClusteringCalculator;
import br.com.ppgi.unirio.luiz.softwareanalysis.controller.ProjectLoader;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;

public class MainDashboard {
	/**
	 * Executions variables
	 * TODO: Export to a "env"like file  
	 */
	private static boolean EXECUTE_EXTRACT_GRAPH_DATA = false;
	private static boolean EXECUTE_GENERATE_LOG_LIST_FILE = false;
	private static boolean EXECUTE_EXTRACT_REVISIONS_BY_VERSION = false;
	private static boolean EXECUTE_EXTRACT_REVISIONS_BY_YEAR = false;
	private static boolean EXECUTE_EXTRACT_ODEM_PROJECTS_CHARACTERISTICS = false;
	private static boolean EXECUTE_EXTRACT_JAR_PROJECTS_CHARACTERISTICS = false;
	private static boolean EXECUTE_EXTRACT_ODEM_PACKAGE_CHARACTERISTICS = false;

	/**
	 * Input directories
	 */
	private static String ODEM_DIRECTORY = new File("").getAbsolutePath() + "\\data\\odem";
	private static String JAR_DIRECTORY = new File("").getAbsolutePath() + "\\data\\JARFile";
	private static String ODEMProjectCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\SoftwareAnalysis\\ODEMProjectCharacteristics";
	private static String ODEMPackageCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\SoftwareAnalysis\\ODEMPackageCharacteristics";
	private static String JARProjectCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\SoftwareAnalysis\\JARProjectCharacteristics";
	
	/**
	 * Output directory
	 */
	private static String GRAPH_OUTPUT_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\results\\graph\\";
	
	
	public static final void main(String[] args) throws Throwable
	{
		if(EXECUTE_EXTRACT_GRAPH_DATA) {
			extractGraphData();			
		}

		if(EXECUTE_GENERATE_LOG_LIST_FILE) {
			generateLogListFile("joda-time");			
		}

		if(EXECUTE_EXTRACT_REVISIONS_BY_VERSION) {
			extractRevisionsByVersion("joda-time");			
		}

		if(EXECUTE_EXTRACT_REVISIONS_BY_YEAR) {
			extractRevisionsByYear("junit");			
		}

		if(EXECUTE_EXTRACT_ODEM_PROJECTS_CHARACTERISTICS) {
			extractODEMProjectCharacteristics();			
		}
		
		if(EXECUTE_EXTRACT_JAR_PROJECTS_CHARACTERISTICS) {
			extractJARProjectCharacteristics();		
		}
		
		if(EXECUTE_EXTRACT_ODEM_PACKAGE_CHARACTERISTICS) {
			extractODEMPackageCharacteristics();			
		}		
	}
	
	/**
	 * generate a list of years by commit, including the developer, the number of classes and packages
	 */
	private static void extractODEMPackageCharacteristics() throws Exception {
		PrintStream ps =null;
		try{
			ps = new PrintStream(new FileOutputStream(ODEMPackageCharacteristics_DIRECTORY + "\\ODEMPackageCharacteristics.data"));
		
			ps.println("version\tpackage\tcbo\taff\teff\tlcom\tmf\tcs");
			
			ProjectLoader loader = new ProjectLoader(ODEM_DIRECTORY);
			
			List<File> files = new ArrayList<File>();
			
			File dir = new File(ODEM_DIRECTORY);
			
			listFilesOnly(dir,files);
			
			for (File file: files) {
				if(file.isDirectory() == false && getFileExtension(file).equals("odem")) {
					Project project = loader.loadODEMRealVersion(file.getAbsolutePath());
					ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
					
					for (int i = 0; i < project.getPackageCount(); i++) {
						String str = file.getName().substring(0, file.getName().length() -5) + "\t" + (project.getPackageIndex(i).getName());
						System.out.println(str);
						ps.println(file.getName().substring(0, file.getName().length() -5) + "\t" + (project.getPackageIndex(i).getName().equals("") != true ? project.getPackageIndex(i).getName() : "NONAME") + 
									"\t" + cc.calculateCBO(i) + 
									"\t" + cc.calculateAfferentCoupling(i) + 
									"\t" + cc.calculateEfferentCoupling(i) + 
									"\t" + cc.calculateLCOM5(i) +
									"\t" + cc.calculateModularizationFactor(i) +
									"\t" + cc.calculateClusterScore(i));	
				}	
			}
		}
		}
		catch (Exception e) {
			
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}	
		}
		
		System.out.println("extractODEMPackageCharacteristics Finished!");		
	}

	/**
	 * generate a list of years by commit, including the developer, the number of classes and packages
	 */
	private static void extractJARProjectCharacteristics() throws IOException, ClassNotFoundException {
		PrintStream ps =null;
		try{		
			FileOutputStream out = new FileOutputStream(JARProjectCharacteristics_DIRECTORY + "\\JARProjectCharacteristics.data"); 
			ps = new PrintStream(out);
			ps.println("versions\tpackages\tclasses\tattrs\tmeths\tpmeths");
					
			List<File> files = new ArrayList<File>();
			
			File dir = new File(JAR_DIRECTORY);
			
			listFilesOnly(dir,files);
			
			for (File file: files) {
				if(file.isDirectory() == false && getFileExtension(file).equals("jar")) {
					
					JarFile jar = new JarFile(file);
					Enumeration<JarEntry> e = jar.entries();
	
					while (e.hasMoreElements())
					{
						JarEntry jarEntry = e.nextElement();
						String fileName = jarEntry.getName();
						
//						if (fileName.endsWith(".class") && !fileName.equals("install.class") && !fileName.equals("Install.class"))
						if (fileName.endsWith(".class"))
						{
							InputStream is = jar.getInputStream(jarEntry);
							ProjectCharacteristics mainCharacteristics = new ProjectCharacteristics();
							mainCharacteristics.extractJARProjectCharacteristics(is, file.getName(), fileName, ps);
							is.close();
						}
					}
	
					jar.close();
				}			
			}
		}
		catch (Exception e) {
			
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}	
		}
		
		System.out.println("extractJARCharacteristics Finished!");	
	}


	
	/**
	 * generate a list of years by commit, including the developer, the number of classes and packages
	 */
	private static void extractODEMProjectCharacteristics() throws Exception {
		PrintStream ps =null;
		try{
			FileOutputStream out = new FileOutputStream(ODEMProjectCharacteristics_DIRECTORY + "\\ODEMProjectCharacteristics.data");
			ps = new PrintStream(out);
			
			ps.println("versions\tpackageCount\tclassCount\telegance\tsingleClassPackages\tmaximumClassConcentration\tdependencyCount\tcbo\taff\teff\tmq\tevm\tlcom5");
			
			
	//		ProjectCharacteristics mc = new ProjectCharacteristics();
			ProjectLoader loader = new ProjectLoader(ODEM_DIRECTORY);
			
			List<File> files = new ArrayList<File>();
			
			File dir = new File(ODEM_DIRECTORY);
			
			listFilesOnly(dir,files);
			
			for (File file: files) {
				if(file.isDirectory() == false && getFileExtension(file).equals("odem")) {
					Project project = loader.loadODEMRealVersion(file.getAbsolutePath());
					ProjectCharacteristics mainCharacteristics = new ProjectCharacteristics();
					mainCharacteristics.extractODEMProjectCharacteristics(file.getName(), project, ps);
	//				System.out.println(file);
				}			
			}
			
			ps.close();
			
		}
		catch (Exception e) {
			
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}	
		}
		
		System.out.println("extractCharacteristics Finished!");
	}
	
	/**
	 * generate a list of years by commit, including the developer, the number of classes and packages
	 */
	private static void extractRevisionsByYear(String projectName) throws IOException {
		PrintStream ps = null;
		try{
			FileOutputStream out = new FileOutputStream("results\\" + projectName + "_RevisionsByYear.data");
			ps = new PrintStream(out);
			VersionYearLog mainVersionYearLog = new VersionYearLog();
			mainVersionYearLog.saveRevisionsByYear("results\\" + projectName + "_LogListFile.data", ps);
		}
		catch (Exception e) {
			
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}	
		}
		
		System.out.println("generateRevisionsByYear Finished!");
	}


	/**
	 * generate a list of versions by commit, including the developer, the number of classes and packages
	 */
	private static void extractRevisionsByVersion(String projectName) throws IOException, XMLParseException, ParseException {
		PrintStream ps = null;
		try{
			FileOutputStream out = new FileOutputStream("results\\" + projectName + "_RevisionsByVersion.data");
			ps = new PrintStream(out);
			VersionYearLog mainVersionLog = new VersionYearLog();
			mainVersionLog.saveRevisionsByVersion("results\\" + projectName + "_LogListFile.data", ps, projectName, ODEM_DIRECTORY);
		}
		catch (Exception e) {
			
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}	
		}
		
		System.out.println("generateRevisionsByVersion Finished!");
	}

	/**
	 * Interpret and generate a list of log file containing the commits.
	 */
	private static void generateLogListFile(String projectName ) throws IOException, Throwable {
		PrintStream ps = null;
		try{
			String path = "data\\results\\logs\\" + projectName + "_LogListFile.data";
			String loadPath = "data\\log\\" + projectName + "_log_raw.data";
			FileOutputStream out = new FileOutputStream(path);
			ps = new PrintStream(out);
			VersionYearLog mainVersionLog = new VersionYearLog();
			File file = new File(loadPath);
			mainVersionLog.loadLogFile(loadPath, ps);
		}
		catch (Exception e) {
			System.out.println(e);
		}
		
		finally {
			if(!Objects.isNull(ps)) {
				ps.close();	
			}
		}
			
			System.out.println("generateLogListFile Finished!");		
	}

	public static final void extractGraphData() throws IOException, Exception
	{
		ProjectLoader loader = new ProjectLoader(ODEM_DIRECTORY);
		
		List<File> files = new ArrayList<File>();
		
		File dir = new File(ODEM_DIRECTORY);
		
		listFilesOnly(dir,files);
		
		for (File file: files) {
			if(file.isDirectory() == false && getFileExtension(file).equals("odem")) {
				Project project = loader.loadODEMRealVersion(file.getAbsolutePath());
				Dependencies mainArchitectureDSM = new Dependencies();
				mainArchitectureDSM.saveDependencies(project, GRAPH_OUTPUT_DIRECTORY + file.getName() + "_GraphData.data");
				System.out.println (GRAPH_OUTPUT_DIRECTORY + file.getName() + ".graph.txt");
			}			
		}
		
		System.out.println("generateGraphData Finished!");	
	}
	
	/**
	 * Get the file extension of a file
	 * @param file under analysis
	 * @return the extension without the dot
	 */
	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".")+1);
		else return "";
	}
	
	/**
	 * List all the files from the main directory and its subdirectories
	 * @param inputFile - the main directory
	 * @param files - return a list of files
	 */
	private static void listFilesOnly(File inputFile, List<File> files) {
		File[] listfiles = inputFile.listFiles();
		for (File file: listfiles) {
			if(file.isDirectory()) {
				listFilesOnly(file, files);
			}
			else {
				files.add(file);
			}
		}
	}
}