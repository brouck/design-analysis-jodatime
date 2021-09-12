package br.com.ppgi.unirio.luiz.softwareanalysis.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.management.modelmbean.XMLParseException;

import br.com.ppgi.unirio.luiz.softwareanalysis.controller.ProjectLoader;

public class VersionYearLog {
	private static String findVersion(String sdata, String project, String ODEM_DIRECTORY)
			throws XMLParseException, ParseException {
		ProjectLoader pl = new ProjectLoader(ODEM_DIRECTORY + "\\" + project);
		String version = "";
		Date date1 = null;
		Date date2 = null;
		Date date3 = new SimpleDateFormat("yyyy-MM-dd").parse("9999-12-31");
		for (int i = 0; i < pl.PROJECT_INFO.length; i++) {
			if (pl.PROJECT_INFO[i][0].equals(project)) {
//				if (sdata.compareToIgnoreCase(pl.PROJECT_INFO[i][2]) < 0) {
				date1 = new SimpleDateFormat("yyyy-MM-dd").parse(sdata);
				date2 = new SimpleDateFormat("ddMMyyyy").parse(pl.PROJECT_INFO[i][2]);

				if (date1.compareTo(date2) < 0) {
					if (date2.compareTo(date3) < 0) {
						date3 = date2;
						version = pl.PROJECT_INFO[i][1];
					}

				}
			}
		}
		return version;
	}

	private static boolean hasPackage(List<String> packages, String packageName) {
		for (String name : packages)
			if (name.compareToIgnoreCase(packageName) == 0)
				return true;

		return false;
	}

	public void saveRevisionsByVersion(String filename, PrintStream ps, String project, String ODEM_DIRECTORY)
			throws IOException, XMLParseException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		String packageName = "";

		String lastVersion = "";
		String lastRevision = "";
		String lastAuthor = "";
		int lastClasses = 0;
		List<String> lastPackages = new ArrayList<String>();
		ps.println("versions\tauthor\tclasses\tpackages");
		br.readLine(); // jumping the title of the file.
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if (line.length() > 0) {
				String tokens[] = line.split(" ");

				String sdata = tokens[0];
				String revision = tokens[1];
				String author = tokens[2];
				String clazz = tokens[3];

				clazz = clazz.substring(0, clazz.lastIndexOf('.'));
				// String className = clazz.substring(clazz.lastIndexOf('.') + 1);
//				String packageName = clazz.substring(0, clazz.lastIndexOf('/'));

				if (clazz.lastIndexOf("/") == -1) {
					packageName = "NONAME";
				} else {
					packageName = clazz.substring(0, clazz.lastIndexOf('/'));
				}

				if (revision.compareToIgnoreCase(lastRevision) != 0) {
					if (lastVersion.length() > 0)
						ps.println(lastVersion + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

					lastVersion = findVersion(sdata, project, ODEM_DIRECTORY);
					lastRevision = revision;
					lastAuthor = author;
					lastClasses = 0;
					lastPackages.clear();
				}

				if (!hasPackage(lastPackages, packageName))
					lastPackages.add(packageName);

				lastClasses++;
			}
		}

		if (lastVersion.length() > 0)
			ps.println(lastVersion + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

		br.close();
	}

	public void saveRevisionsByYear(String filename, PrintStream ps) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;

		int lastYear = 0;
		String lastRevision = "";
		String lastAuthor = "";
		int lastClasses = 0;
		List<String> lastPackages = new ArrayList<String>();
		ps.println("year\tauthor\tclasses\tpackages");
		br.readLine(); // jumping the title of the file.
		while ((line = br.readLine()) != null) {
			if (line.length() > 0) {
				String tokens[] = line.split(" ");

				String sdata = tokens[0];
				String revision = tokens[1];
				String author = tokens[2];
				String clazz = tokens[3];

				clazz = clazz.substring(0, clazz.lastIndexOf('.'));
				// String className = clazz.substring(clazz.lastIndexOf('.') + 1);
				String packageName = clazz.substring(0, clazz.lastIndexOf('/'));

				if (revision.compareToIgnoreCase(lastRevision) != 0) {
					if (lastYear > 0)
						ps.println(lastYear + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

					lastYear = Integer.parseInt(sdata.substring(0, 4));
					lastRevision = revision;
					lastAuthor = author;
					lastClasses = 0;
					lastPackages.clear();
				}

				if (!hasPackage(lastPackages, packageName))
					lastPackages.add(packageName);

				lastClasses++;
			}
		}

		if (lastYear > 0)
			ps.println(lastYear + "\t" + lastAuthor + "\t" + lastClasses + "\t" + lastPackages.size());

		br.close();
	}

	/**
	 * Generate the data file based on the commit logs
	 * @param filename Name of the file that will be loaded
	 * @param ps The prinstream
	 * @param isGit Boolean that represents if the log file is from git
	 * @throws IOException
	 * @throws ParseException
	 */
	public void loadLogFile(String filename, PrintStream ps, boolean isGit) throws IOException, ParseException
	{
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		String revision = "";
		String author = "";
		String date = "";
		String type = "";
		
		new ArrayList<String>();
		ps.println("version\tauthor\tclasses\tpackages");
		
		while ((line = br.readLine()) != null) 
		{

			
			try {
				if (line.length() > 0) {
					String tokens[] = line.split(":");
					String tokensTab[] = line.split("\t");
					String tokensSpace[] = line.split(" ");

					
					/* If log file is generated from git log we'll use the commit hash to replace the revision */
					if(isGit && tokensSpace.length > 1) {					
						if(tokensSpace[0].equals("commit")) {
							if (tokensSpace.length>1) {
								revision = tokensSpace[1];	
							}
						}
					} else {
						if(tokens[0].equals("Revision")) {
							if (tokens.length>1) {
								revision = tokens[1];	
							}
						}
					}
				
				
					if(tokens[0].equals("Author")) {
						if (tokens.length>1) {
							author = tokens[1].trim().length()==0 ? "N/A" : tokens[1];	
						}
					
					}
				
					else if(isGit && tokens[0].equals("Date")) {
						String str = tokens[1].substring(1, tokens[1].length() -2);
						if (tokens.length>1) {
							//Fri Dec 19 03:00:43 2003 +0000
							Date date1=new SimpleDateFormat("EEE MMM d", Locale.ENGLISH).parse(str.trim());
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
							date = dateFormat.format(date1);	
						}	
					
					}
					
					else if(!isGit && tokens[0].equals("Date")) {
						String str = tokens[1].substring(1, tokens[1].length() -2);
						if (tokens.length>1) {
							Date date1=new SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).parse(str.trim());
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
							date = dateFormat.format(date1);	
						}	
					
					}
					
					/* Git uses D to represent deletions*/
					else if(isGit && (tokensTab[0].equals("M") || tokensTab[0].equals("A") || tokensTab[0].equals("D"))) {
						if (tokensTab.length>1) {
							if (tokensTab[1].endsWith(".java")) {
								type = tokensTab[1];
								ps.println(date + " "+ revision.trim() + " " + author.trim().replace(' ', '-') + " " + type.trim());
							}
					}
						
					else if(!isGit && (tokensTab[0].equals("M") || tokensTab[0].equals("A") || tokensTab[0].equals("R"))) {
						if (tokensTab.length>1) {
							if (tokensTab[1].endsWith(".java")) {
								type = tokensTab[1];
								ps.println(date + " R" + revision.trim() + " " + author.trim() + " " + type.trim());
							}	
						}
					}
						
				}
				}
			}
			catch(Exception  e) {
				System.out.println(e);
			}
		}
		br.close();
	}
}