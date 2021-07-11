package br.com.ppgi.unirio.luiz.softwareanalysis.analyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.bcel.Const;
//import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import br.com.ppgi.unirio.luiz.softwareanalysis.controller.ClusteringCalculator;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;

/**
 * Publishes metrics and characteristics for a set of projects
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public class ProjectCharacteristics
{
//	private static String JAR_DIRECTORY = "..\\Users\\Marcio\\Desktop\\Resultados Pesquisa\\Resultados Clustering Apache ANT\\versoes";

	private static String JAR_DIRECTORY = new File("").getAbsolutePath() + "\\data\\jar";
	
	private static String ODEM_DIRECTORY = new File("").getAbsolutePath() + "\\data\\odem";
	
		/**
	 * Publishes information about a JAva class
	 */
    public void extractJARProjectCharacteristics(InputStream is, String version, String filename, PrintStream ps) throws ClassNotFoundException, IOException
	{
		ClassParser cp = new ClassParser(is, filename);
		JavaClass clazz = cp.parse();
		String packageName = "";
		String className = "";
		
		int countAttributes = clazz.getAttributes().length;
		int countMethods = clazz.getMethods().length;
		int countPublicMethods = 0;
		
		for (Method method : clazz.getMethods())
			if ((method.getAccessFlags() & Const.ACC_PUBLIC) != 0)
				countPublicMethods++;
		
		if (filename.lastIndexOf("/")== -1) {
			packageName = "NONAME";
			className = filename;
		} else {
			packageName = filename.substring(0, filename.lastIndexOf("/")).replace('/', '.');
			className = filename.substring(filename.lastIndexOf("/")+1, filename.lastIndexOf("."));
		}
			
		ps.println(version + "\t" + packageName + "\t" + className + "\t" + countAttributes + "\t" + countMethods + "\t" + countPublicMethods);
	}
	

	/**
	 * Extract the characteristics and metrics for the size and coupling of a given project
	 */
	public void extractODEMProjectCharacteristics(String version, Project project, PrintStream ps) throws Exception
	{
		ClusteringCalculator cc = new ClusteringCalculator(project, project.getPackageCount());
		int dependencyCount = project.getDependencyCount();
		double mq = cc.calculateModularizationQuality();
		int evm = cc.calculateEVM();
		double aff = cc.calculateAfferentCoupling();
		double eff = cc.calculateEfferentCoupling();
		double lcom5 = cc.calculateLCOM5();
		double cbo = cc.calculateCBO();
		int packageCount = cc.getPackageCount();
		int classCount = project.getClassCount();
		int singleClassPackages = cc.countSingleClassPackages();
		int maximumClassConcentration = cc.getMaximumClassCount();
		double elegance = cc.calculateClassElegance();
		
//		System.out.println(version + "; P: " + packageCount + "; ELG: " + elegance + "; SCP: " + singleClassPackages + "; CONC: " + maximumClassConcentration + "; D: " + dependencyCount + "; CBO: " + cbo + "; AFF: " + aff + "; EFF: " + eff + "; MQ: " + mq + "; EVM: " + evm + "; LCOM: " + lcom5););
//		ps.println("version\tpackageCount\tclassCount\telegance\tsingleClassPackages\tmaximumClassConcentration\tdependencyCount\tcbo\taff\teff\tmq\tevm\tlcom5");
		
		ps.println(version.substring(0, version.length() -5) + "\t" + packageCount + "\t" + classCount +"\t" + elegance + "\t" + singleClassPackages + "\t" + maximumClassConcentration+ "\t" + dependencyCount + "\t" + cbo + "\t" + aff + "\t" + eff + "\t" + mq + "\t" + evm + "\t" + lcom5);
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

}