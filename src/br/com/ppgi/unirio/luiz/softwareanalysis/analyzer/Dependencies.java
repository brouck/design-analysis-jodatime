package br.com.ppgi.unirio.luiz.softwareanalysis.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectPackage;

/**
 * Class that publishes architectural charts for all versions of Apache Ant
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public class Dependencies
{
	
	private static String ODEM_DIRECTORY = new File("").getAbsolutePath() + "\\data\\odem";
	
	/**
	 * Output directory for architecture charts
	 */
	private static String DIRETORIO_SAIDA = new  File("").getAbsolutePath() + "\\results\\graph\\";
	
	/**
	 * Returns an ordered list of packages
	 */
	private static List<ProjectPackage> getAllPackages(Project project)
	{
		List<ProjectPackage> listPackages = new ArrayList<ProjectPackage>();
		
		for (ProjectPackage p : project.getPackages())
			listPackages.add(p);
		
		Collections.sort(listPackages, new PackageComparator());		
		return listPackages;
	}
	
	private static List<ProjectPackage> getPackageDependees(ProjectPackage sourcePackage, Project project)
	{
		List<ProjectPackage> dependees = new ArrayList<ProjectPackage>();
		
		for (ProjectPackage _package : project.getPackages())
			for (ProjectPackage p : project.getPackageDependencies(_package))
				if (p == sourcePackage)
					dependees.add(_package);		
		
		return dependees;
	}
	
	private static boolean checkDependenciesResolved(ProjectPackage _package, List<ProjectPackage> dependencies, List<ProjectPackage> resolved)
	{
		for (ProjectPackage p : dependencies)
			if (!resolved.contains(p))
				return false;
		
		return true;
	}

	private static List<ProjectPackage> topologicalOrdering(List<ProjectPackage> allPackages, HashMap<ProjectPackage, List<ProjectPackage>> dependees, HashMap<ProjectPackage, List<ProjectPackage>> dependencies)
	{
		List<ProjectPackage> result = new ArrayList<ProjectPackage>();
		
		do
		{
			List<ProjectPackage> partial = new ArrayList<ProjectPackage>();
			
			for (int i = allPackages.size()-1; i >= 0; i--)
			{
				ProjectPackage _package = allPackages.get(i);
				
				if (checkDependenciesResolved(_package, dependencies.get(_package), result))
				{
					partial.add(_package);
					allPackages.remove(_package);
				}
			}
			
			int resolved = partial.size();
			
			while (partial.size() > 0)
			{
				ProjectPackage provider = partial.get(0);
				int maxProvisions = dependees.get(provider).size();
				
				for (int i = 1; i < partial.size(); i++)
				{
					ProjectPackage current = partial.get(i);
					int provisions = dependees.get(current).size();
					
					if (provisions > maxProvisions)
					{
						provider = current;
						maxProvisions = provisions;
					}
				}
				
				result.add(0, provider);
				partial.remove(provider);
			}
			
			if (resolved == 0)
			{
				ProjectPackage provider = allPackages.get(0);
				int maxProvisions = dependees.get(provider).size();
				
				for (int i = 1; i < allPackages.size(); i++)
				{
					ProjectPackage current = allPackages.get(i);
					int provisions = dependees.get(current).size();
					
					if (provisions > maxProvisions)
					{
						provider = current;
						maxProvisions = provisions;
					}
				}
				
				result.add(0, provider);
				allPackages.remove(provider);
			}
			
		} while (allPackages.size() > 0);
		
		return result;
	}

	private static int getLargerPackageNameLength(List<ProjectPackage> allPackages)
	{
		int length = 0;
		
		for (ProjectPackage _package : allPackages)
			if (_package.getName().length() > length)
				length = _package.getName().length();
		
		return length;
	}

	/**
	 * Saves a file conveying dependencies among packages
	 */
	public void saveDependencies(Project project, String outputFilename) throws IOException
	{
		FileWriter fw = new FileWriter(new File(outputFilename));
		BufferedWriter bw = new BufferedWriter(fw);
		
		List<ProjectPackage> allPackages = getAllPackages(project);
		
		HashMap<ProjectPackage, List<ProjectPackage>> dependees = new HashMap<ProjectPackage, List<ProjectPackage>>();
		
		for (ProjectPackage _package : allPackages)
			dependees.put(_package, getPackageDependees(_package, project));

		HashMap<ProjectPackage, List<ProjectPackage>> dependencies = new HashMap<ProjectPackage, List<ProjectPackage>>();
		
		for (ProjectPackage _package : allPackages)
			dependencies.put(_package, project.getPackageDependencies(_package));
		
		allPackages = topologicalOrdering(allPackages, dependees, dependencies);
		
		int nameLength = getLargerPackageNameLength(allPackages) + 1;

		for (ProjectPackage sourcePackage : allPackages)
		{
			for (int i = sourcePackage.getName().length(); i < nameLength; i++)
				bw.write(" ");
			
			bw.write(sourcePackage.getName() + ";");
			
			StringBuilder result = new StringBuilder();
			
			for (int i = 0; i < allPackages.size(); i++)
					result.append("0;");	
			
			List<ProjectPackage> sourceDependees = dependees.get(sourcePackage);
			
			for (int i = 0; i < sourceDependees.size(); i++)
			{
				int index = allPackages.indexOf(sourceDependees.get(i))*2;
				result.setCharAt(index, '1');
			}
			
			/*
			 * printing with a removal of the last character due to the ";" added for each package
			 */
			bw.write(result.toString().substring(0, result.toString().length() -1) + "\r\n");
		}
		
		bw.close();
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


/**
 * Compare the name of different packages
 */
class PackageComparator implements Comparator<ProjectPackage>
{
	@Override
	public int compare(ProjectPackage p1, ProjectPackage p2)
	{
		return p1.getName().compareToIgnoreCase(p2.getName());
	}
}