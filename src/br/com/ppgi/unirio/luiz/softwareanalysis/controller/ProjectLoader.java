package br.com.ppgi.unirio.luiz.softwareanalysis.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FilenameFilter;
import java.util.Vector;
import javax.management.modelmbean.XMLParseException;

import br.com.ppgi.unirio.luiz.softwareanalysis.controller.CDAReader;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectClass;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectPackage;

/**
 * Class that loads real and optimized versions of Apache Ant
 * 
 * @author Marcio
 */
public class ProjectLoader
{
//	private static String OPTIMIZED_DIRECTORY = "\\Users\\Marcio\\Desktop\\Resultados Pesquisa\\Resultados Clustering Apache ANT\\multi run\\v1.9.0\\";
	
	private static String OPTIMIZED_DIRECTORY = "\\results\\";
	
	/**
	 * Input directory for real versions
	 */
	
	private static String ODEM_DIRECTORY = new File("").getAbsolutePath() + "\\data\\ODEMFile";

	private static String JAR_DIRECTORY = new File("").getAbsolutePath() + "\\data\\JARFile";

	private static String ODEMProjectCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\ODEMProjectCharacteristics";
	
	private static String ODEMPackageCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\ODEMPackageCharacteristics";
	
	private static String JARProjectCharacteristics_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\JARProjectCharacteristics";

	private static List<String> instanceFilenames = new ArrayList<String>();
		
	public String[][] PROJECT_INFO; 
	
	public  ProjectLoader(String PROJECT_ODEM_DIRECTORY) throws XMLParseException{
		loadODEMRealVersionInfo(PROJECT_ODEM_DIRECTORY);	
	}
	
	
	/**
	 * Known external dependencies for real versions
	 */
	private static String[] REAL_VERSION_EXTERNAL_DEPENDENCIES = 
	{
			"apple.laf.AquaFileChooserUI",
			"bsh.classpath.ClassManagerImpl",
			"CH.ifa.draw.standard.AlignCommand$1",
			"ch.randelshofer.quaqua.QuaquaManager",
			"com.apple.eawt.Application",
			"com.apple.eawt.ApplicationAdapter",
			"com.apple.eawt.ApplicationEvent",
			"com.apple.eawt.ApplicationListener",
			"com.apple.eio.FileManager",
			"com.apple.mrj.jdirect.Linker",
			"com.apple.mrj.jdirect.MethodClosure",
			"com.apple.mrj.jdirect.MethodClosureUPP",
			"com.apple.mrj.macos.libraries.InterfaceLib",
			"com.apple.mrj.MRJAboutHandler",
			"com.apple.mrj.MRJApplicationUtils",
			"com.apple.mrj.MRJFileUtils",
			"com.apple.mrj.MRJOpenApplicationHandler",
			"com.apple.mrj.MRJOpenDocumentHandler",
			"com.apple.mrj.MRJOSType",
			"com.apple.mrj.MRJPrefsHandler",
			"com.apple.mrj.MRJPrintDocumentHandler",
			"com.apple.mrj.MRJQuitHandler",
			"com.apple.mrj.swing.MacFileChooserUI",
			"com.ibm.uvm.tools.DebugSupport",
			"com.sun.awt.AWTUtilities",
			"com.sun.image.codec.jpeg.JPEGCodec",
			"com.sun.image.codec.jpeg.JPEGDecodeParam",
			"com.sun.image.codec.jpeg.JPEGImageDecoder",
			"com.sun.tools.javac.Main",
			"edu.stanford.ejalbert.BrowserLauncher",
			"edu.umd.cs.findbugs.annotations.Nullable",
			"FirewallPlugin",
			"glguerin.io.FileForker",
			"glguerin.io.Pathname",
			"glguerin.util.MacPlatform",
			"java.awt.BorderLayout",
			"java.awt.Button",
			"java.awt.Canvas",
			"java.awt.Checkbox",
			"java.awt.Color",
			"java.awt.Component",
			"java.awt.Container",
			"java.awt.Cursor",
			"java.awt.Dialog",
			"java.awt.Dimension",
			"java.awt.event.ActionEvent",
			"java.awt.event.ActionListener",
			"java.awt.event.ItemEvent",
			"java.awt.event.ItemListener",
			"java.awt.event.KeyAdapter",
			"java.awt.event.KeyEvent",
			"java.awt.event.KeyListener",
			"java.awt.event.MouseAdapter",
			"java.awt.event.MouseEvent",
			"java.awt.event.MouseListener",
			"java.awt.event.TextEvent",
			"java.awt.event.TextListener",
			"java.awt.event.WindowAdapter",
			"java.awt.event.WindowEvent",
			"java.awt.event.WindowListener",
			"java.awt.FlowLayout",
			"java.awt.Font",
			"java.awt.Frame",
			"java.awt.Graphics",
			"java.awt.GridBagConstraints",
			"java.awt.GridBagLayout",
			"java.awt.GridLayout",
			"java.awt.Image",
			"java.awt.image.ImageObserver",
			"java.awt.image.ImageProducer",
			"java.awt.Insets",
			"java.awt.Label",
			"java.awt.LayoutManager",
			"java.awt.List",
			"java.awt.MediaTracker",
			"java.awt.Menu",
			"java.awt.MenuBar",
			"java.awt.MenuItem",
			"java.awt.Panel",
			"java.awt.Rectangle",
			"java.awt.SystemColor",
			"java.awt.TextArea",
			"java.awt.TextComponent",
			"java.awt.TextField",
			"java.awt.Toolkit",
			"java.awt.Window",
			"java.beans.BeanInfo",
			"java.beans.IntrospectionException",
			"java.beans.Introspector",
			"java.beans.PropertyChangeEvent",
			"java.beans.PropertyChangeListener",
			"java.beans.PropertyChangeSupport",
			"java.beans.PropertyDescriptor",
			"java.beans.PropertyVetoException",
			"java.io.BufferedReader",
			"java.io.BufferedWriter",
			"java.io.ByteArrayOutputStream",
			"java.io.File",
			"java.io.DataInputStream",
			"java.io.DataOutputStream",
			"java.io.InputStreamReader",
			"java.lang.InternalError",
			"javax.swing.JEditorPane",
			"javax.swing.JFileChooser",
			"javax.swing.Box",
			"javax.swing.BoxLayout",
			"javax.swing.border.EmptyBorder",
			"javax.swing.ButtonModel",
			"java.lang.Runtime",
			"java.lang.Process",
			"java.io.BufferedOutputStream",
			"java.io.FileInputStream",
			"java.io.FileNotFoundException",
			"java.io.FileOutputStream",
			"java.io.FileReader",
			"java.io.FileWriter",
			"java.io.InputStream",
			"java.io.IOException",
			"java.io.ObjectInputStream",
			"java.io.ObjectInputStream$GetField",
			"java.io.ObjectOutputStream",
			"java.io.ObjectOutputStream$PutField",
			"java.io.ObjectStreamClass",
			"java.io.ObjectStreamField",
			"java.io.OutputStream",
			"java.io.PrintStream",
			"java.io.PrintWriter",
			"java.io.Reader",
			"java.io.Serializable",
			"java.io.StringReader",
			"java.io.StringWriter",
			"java.io.Writer",
			"java.lang.annotation.Annotation",
			"java.lang.annotation.Documented",
			"java.lang.annotation.ElementType",
			"java.lang.annotation.Inherited",
			"java.lang.annotation.Retention",
			"java.lang.annotation.RetentionPolicy",
			"java.lang.annotation.Target",
			"java.lang.Appendable",
			"java.lang.AssertionError",
			"java.lang.Boolean",
			"java.lang.Byte",
			"java.lang.Character",
			"java.lang.CharSequence",
			"java.lang.Class",
			"java.lang.ClassCastException",
			"java.lang.ClassLoader",
			"java.lang.ClassNotFoundException",
			"java.lang.Deprecated",
			"java.lang.Double",
			"java.lang.Enum",
			"java.lang.Error",
			"java.lang.Exception",
			"java.lang.Float",
			"java.lang.IllegalAccessException",
			"java.lang.IllegalArgumentException",
			"java.lang.IllegalStateException",
			"java.lang.IllegalThreadStateException",
			"java.lang.InstantiationException",
			"java.lang.Integer",
			"java.lang.InterruptedException",
			"java.lang.Iterable",
			"java.lang.Long",
			"java.lang.management.ManagementFactory",
			"java.lang.management.RuntimeMXBean",
			"java.lang.management.ThreadMXBean",
			"java.lang.Math",
			"java.lang.NoClassDefFoundError",
			"java.lang.NoSuchFieldError",
			"java.lang.NoSuchMethodError",
			"java.lang.NoSuchMethodException",
			"java.lang.NullPointerException",
			"java.lang.Number",
			"java.lang.NumberFormatException",
			"java.lang.Object",
			"java.lang.reflect.Array",
			"java.lang.reflect.Constructor",
			"java.lang.reflect.Field",
			"java.lang.reflect.GenericArrayType",
			"java.lang.reflect.InvocationTargetException",
			"java.lang.reflect.Method",
			"java.lang.reflect.Modifier",
			"java.lang.reflect.ParameterizedType",
			"java.lang.reflect.Type",
			"java.lang.reflect.TypeVariable",
			"java.lang.reflect.WildcardType",
			"java.lang.Runnable",
			"java.lang.RuntimeException",
			"java.lang.SecurityException",
			"java.lang.Short",
			"java.lang.StackOverflowError",
			"java.lang.StackTraceElement",
			"java.lang.String",
			"java.lang.StringBuffer",
			"java.lang.StringBuilder",
			"java.lang.System",
			"java.lang.Thread",
			"java.lang.Thread$State",
			"java.lang.ThreadDeath",
			"java.lang.ThreadGroup",
			"java.lang.ThreadLocal",
			"java.lang.Throwable",
			"java.lang.UnsupportedOperationException",
			"java.lang.Void",
			"java.math.BigInteger",
			"java.net.Authenticator",
			"java.net.HttpURLConnection",
			"java.net.InetAddress",
			"java.net.MalformedURLException",
			"java.net.PasswordAuthentication",
			"java.net.ServerSocket",
			"java.net.Socket",
			"java.net.UnknownHostException",
			"java.net.URI",
			"java.net.URISyntaxException",
			"java.net.URL",
			"java.net.URLClassLoader",
			"java.net.URLConnection",
			"java.net.URLDecoder",
			"java.net.URLEncoder",
			"java.net.URLStreamHandler",
			"java.nio.Buffer",
			"java.nio.ByteBuffer",
			"java.nio.channels.FileChannel",
			"java.nio.CharBuffer",
			"java.nio.charset.CharacterCodingException",
			"java.nio.charset.Charset",
			"java.nio.charset.CharsetDecoder",
			"java.nio.charset.CharsetEncoder",
			"java.nio.charset.CodingErrorAction",
			"java.nio.charset.IllegalCharsetNameException",
			"java.nio.charset.MalformedInputException",
			"java.nio.charset.UnsupportedCharsetException",
			"java.nio.file.CopyOption",
			"java.nio.file.Files",
			"java.nio.file.FileSystem",
			"java.nio.file.FileSystems",
			"java.nio.file.Path",
			"java.nio.file.StandardCopyOption",
			"java.security.AccessControlException",
			"java.security.AccessController",
			"java.security.MessageDigest",
			"java.security.NoSuchAlgorithmException",
			"java.security.Permission",
			"java.security.PrivilegedAction",
			"java.text.AttributedCharacterIterator",
			"java.text.AttributedCharacterIterator$Attribute",
			"java.text.AttributedString",
			"java.text.BreakIterator",
			"java.text.CharacterIterator",
			"java.text.CollationKey",
			"java.text.Collator",
			"java.text.DateFormat",
			"java.text.DateFormat$Field",
			"java.text.DecimalFormat",
			"java.text.FieldPosition",
			"java.text.Format",
			"java.text.Format$Field",
			"java.text.MessageFormat",
			"java.text.NumberFormat",
			"java.text.ParseException",
			"java.text.RuleBasedCollator",
			"java.text.SimpleDateFormat",
			"java.util.AbstractList",
			"java.util.ArrayList",
			"java.util.Arrays",
			"java.util.Collection",
			"java.util.Collections",
			"java.util.Comparator",
			"java.util.concurrent.atomic.AtomicInteger",
			"java.util.concurrent.atomic.AtomicLong",
			"java.util.concurrent.Callable",
			"java.util.concurrent.ConcurrentHashMap",
			"java.util.concurrent.ConcurrentLinkedQueue",
			"java.util.concurrent.ConcurrentMap",
			"java.util.concurrent.CopyOnWriteArrayList",
			"java.util.concurrent.CountDownLatch",
			"java.util.concurrent.ExecutionException",
			"java.util.concurrent.Executors",
			"java.util.concurrent.ExecutorService",
			"java.util.concurrent.Future",
			"java.util.concurrent.FutureTask",
			"java.util.concurrent.locks.Lock",
			"java.util.concurrent.locks.ReentrantLock",
			"java.util.concurrent.TimeoutException",
			"java.util.concurrent.TimeUnit",
			"java.util.Enumeration",
			"java.util.HashMap",
			"java.util.HashSet",
			"java.util.Hashtable",
			"java.util.IdentityHashMap",
			"java.util.Iterator",
			"java.util.LinkedHashMap",
			"java.util.LinkedHashSet",
			"java.util.List",
			"java.util.Map",
			"java.util.Map$Entry",
			"java.util.Properties",
			"java.util.Random",
			"java.util.regex.Matcher",
			"java.util.regex.Pattern",
			"java.util.Set",
			"java.util.StringTokenizer",
			"java.util.Vector",
			"java.util.zip.ZipEntry",
			"java.util.zip.ZipFile",
			"javax.accessibility.Accessible",
			"javax.accessibility.AccessibleContext",
			"javax.annotation.concurrent.GuardedBy",
			"javax.annotation.concurrent.ThreadSafe",
			"javax.annotation.Nonnull",
			"javax.annotation.Nullable",
			"javax.imageio.ImageIO",
			"javax.jnlp.ServiceManager",
			"javax.print.attribute.Attribute",
			"javax.print.attribute.AttributeSet",
			"javax.print.attribute.DocAttribute",
			"javax.print.attribute.DocAttributeSet",
			"javax.print.attribute.HashAttributeSet",
			"javax.print.attribute.HashDocAttributeSet",
			"javax.print.attribute.HashPrintRequestAttributeSet",
			"javax.print.attribute.IntegerSyntax",
			"javax.print.attribute.PrintJobAttribute",
			"javax.print.attribute.PrintRequestAttribute",
			"javax.print.attribute.PrintRequestAttributeSet",
			"javax.print.attribute.Size2DSyntax",
			"javax.print.attribute.standard.Chromaticity",
			"javax.print.attribute.standard.Copies",
			"javax.print.attribute.standard.Destination",
			"javax.print.attribute.standard.Finishings",
			"javax.print.attribute.standard.JobHoldUntil",
			"javax.print.attribute.standard.JobName",
			"javax.print.attribute.standard.JobPriority",
			"javax.print.attribute.standard.Media",
			"javax.print.attribute.standard.MediaPrintableArea",
			"javax.print.attribute.standard.MediaSize",
			"javax.print.attribute.standard.MediaSizeName",
			"javax.print.attribute.standard.MediaTray",
			"javax.print.attribute.standard.NumberUp",
			"javax.print.attribute.standard.OrientationRequested",
			"javax.print.attribute.standard.PageRanges",
			"javax.print.attribute.standard.PresentationDirection",
			"javax.print.attribute.standard.PrinterResolution",
			"javax.print.attribute.standard.PrintQuality",
			"javax.print.attribute.standard.SheetCollate",
			"javax.print.attribute.standard.Sides",
			"javax.print.Doc",
			"javax.print.DocFlavor",
			"javax.print.DocFlavor$SERVICE_FORMATTED",
			"javax.print.DocPrintJob",
			"javax.print.event.PrintJobAdapter",
			"javax.print.event.PrintJobEvent",
			"javax.print.event.PrintJobListener",
			"javax.print.PrintException",
			"javax.print.PrintService",
			"javax.print.PrintServiceLookup",
			"javax.print.SimpleDoc",
			"javax.print.StreamPrintService",
			"javax.print.StreamPrintServiceFactory",
			"javax.swing.AbstractButton",
			"javax.swing.AbstractListModel",
			"javax.swing.border.BevelBorder",
			"javax.swing.border.Border",
			"javax.swing.BorderFactory",
			"javax.swing.ComboBoxEditor",
			"javax.swing.DefaultListCellRenderer",
			"javax.swing.DefaultListModel",
			"javax.swing.event.ChangeEvent",
			"javax.swing.event.ChangeListener",
			"javax.swing.event.DocumentEvent",
			"javax.swing.event.DocumentListener",
			"javax.swing.event.ListSelectionEvent",
			"javax.swing.event.ListSelectionListener",
			"javax.swing.event.TreeModelEvent",
			"javax.swing.event.TreeModelListener",
			"javax.swing.event.TreeSelectionEvent",
			"javax.swing.event.TreeSelectionListener",
			"javax.swing.Icon",
			"javax.swing.ImageIcon",
			"javax.swing.JButton",
			"javax.swing.JCheckBox",
			"javax.swing.JComboBox",
			"javax.swing.JComponent",
			"javax.swing.JDialog",
			"javax.swing.JFrame",
			"javax.swing.JLabel",
			"javax.swing.JList",
			"javax.swing.JMenu",
			"javax.swing.JMenuBar",
			"javax.swing.JMenuItem",
			"javax.swing.JOptionPane",
			"javax.swing.JPanel",
			"javax.swing.JProgressBar",
			"javax.swing.JRootPane",
			"javax.swing.JScrollPane",
			"javax.swing.JSeparator",
			"javax.swing.JSplitPane",
			"javax.swing.JTabbedPane",
			"javax.swing.JTextArea",
			"javax.swing.JTextField",
			"javax.swing.JTree",
			"javax.swing.ListCellRenderer",
			"javax.swing.ListModel",
			"javax.swing.ListSelectionModel",
			"javax.swing.ScrollPaneConstants",
			"javax.swing.SwingConstants",
			"javax.swing.SwingUtilities",
			"javax.swing.text.JTextComponent",
			"javax.swing.ToolTipManager",
			"javax.swing.tree.DefaultTreeCellRenderer",
			"javax.swing.tree.TreeCellRenderer",
			"javax.swing.tree.TreeModel",
			"javax.swing.tree.TreePath",
			"javax.swing.UIManager",
			"net.roydesign.app.AboutJMenuItem",
			"net.roydesign.app.Application",
			"net.roydesign.app.QuitJMenuItem",
			"net.roydesign.event.ApplicationEvent",
			"netscape.javascript.JSObject",
			"org.apache.batik.dom.GenericDOMImplementation",
			"org.apache.batik.svggen.SVGGraphics2D",
			"org.hamcrest.BaseMatcher",
			"org.hamcrest.core.CombinableMatcher",
			"org.hamcrest.core.CombinableMatcher$CombinableBothMatcher",
			"org.hamcrest.core.CombinableMatcher$CombinableEitherMatcher",
			"org.hamcrest.CoreMatchers",
			"org.hamcrest.Description",
			"org.hamcrest.Factory",
			"org.hamcrest.Matcher",
			"org.hamcrest.MatcherAssert",
			"org.hamcrest.SelfDescribing",
			"org.hamcrest.StringDescription",
			"org.hamcrest.TypeSafeMatcher",
			"org.jdesktop.layout.Baseline",
			"org.jdesktop.layout.LayoutStyle",
			"org.w3c.dom.Document",
			"org.w3c.dom.DocumentType",
			"org.w3c.dom.DOMImplementation",
			"org.xml.sax.Attributes",
			"org.xml.sax.ContentHandler",
			"org.xml.sax.DTDHandler",
			"org.xml.sax.EntityResolver",
			"org.xml.sax.ErrorHandler",
			"org.xml.sax.helpers.DefaultHandler",
			"org.xml.sax.helpers.XMLReaderFactory",
			"org.xml.sax.InputSource",
			"org.xml.sax.SAXException",
			"org.xml.sax.SAXParseException",
			"org.xml.sax.XMLReader",
			"quicktime.app.view.GraphicsImporterDrawer",
			"quicktime.app.view.QTImageProducer",
			"quicktime.QTSession",
			"quicktime.std.image.GraphicsImporter",
			"quicktime.util.QTHandle",
			"quicktime.util.QTHandleRef",
			"quicktime.util.QTUtils",
			"sun.awt.CausedFocusEvent",
			"sun.awt.CausedFocusEvent$Cause",
			"sun.security.util.SecurityConstants",
			"sun.tools.javac.Main",
			"junit.ui.TestRunner$13"

		};

//	/**
//	 * Returns all real versions
//	 */
//	public String[] getRealVersions()
//	{
//		return REAL_VERSIONS;
//	}
//	
//	/**
//	 * Returns the dates for all real versions
//	 */
//	public String[] getRealVersionsDate()
//	{
//		return VERSION_DATES;
//	}
	
	/**
	 * Loads a real version into a project
	 */
	public Project loadRealVersion(String versao) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		return reader.execute(ODEM_DIRECTORY + "\\"+ versao + ".odem");
	}
	
	
	/**
	 * Loads a real version into a project
	 */
	public static Vector<Project> loadJARVersions(List<String> instanceFilenamesArray) throws XMLParseException
	{
		Vector<Project> instances = new Vector<Project>();
	
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		
		for (String filename : instanceFilenamesArray)
			if (filename.length() > 0)
				instances.add (reader.execute(filename));
		
		return instances;
		
	}
	
	
	/**
	 * Loads a ODEM real version into a project
	 */
	public Project loadODEMRealVersion(String file) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		return reader.execute(file);
	}
	
	/**
	 * Loads a ODEM real version into a project
	 * @return 
	 */
	public void loadODEMRealVersionInfo(String PROJECT_ODEM_DIRECTORY) throws XMLParseException
	{
		List<File> files = new ArrayList<File>();
		
		File dir = new File(PROJECT_ODEM_DIRECTORY);
		
		listFilesOnly(dir,files);
		
		PROJECT_INFO = new String[files.size()][3];
		int count =0;
		for (File file1: files) {
			if(file1.isDirectory() == false && getFileExtension(file1).equals("odem")) {
				String tokens[] = file1.getName().split("-");
				
				PROJECT_INFO[count][0] = tokens[0];
				PROJECT_INFO[count][1] = tokens[1];
				PROJECT_INFO[count][2] = tokens[2].substring(0,8);	
			}
			count++;
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
		 * List all the files from the main directory and its subdirectories
		 * @param inputFile - the main directory
		 * @param files - return a list of files
		 */
		private static List<File>  listFilesOnly(File inputFile) {
			List<File> files = new ArrayList<File>();
			File[] listfiles = inputFile.listFiles();
			for (File file: listfiles) {
				if(file.isDirectory()) {
					listFilesOnly(file, files);
				}
				else {
					files.add(file);
				}
			}
			return files;
		}
	
	
	
	/**
	 * Loads an optimized version into a project
	 */
	private Project loadOptimizedVersion(String solution) throws XMLParseException
	{
		CDAReader reader = new CDAReader();
		reader.setIgnoredClasses(REAL_VERSION_EXTERNAL_DEPENDENCIES);
		Project project = reader.execute("data\\odem\\apache-ant-1.9.0.odem");
		project.clearPackages();

		String[] tokens = solution.split(" ");
		
		for (int i = 0; i < tokens.length; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int packageNumber = Integer.parseInt(tokens[i]);
			
			while (project.getPackageCount() <= packageNumber)
				project.addPackage("" + project.getPackageCount());
			
			ProjectPackage _package = project.getPackageIndex(packageNumber);
			_class.setPackage(_package);
		}
		
		return project;
	}
	
	/**
	 * Loads a set of optimized versions of a project
	 */
	public List<Project> loadOptimizedVersions(String filename) throws XMLParseException
	{
		List<Project> ciclos = new ArrayList<Project>();
		 
		try 
		{
 			BufferedReader br = new BufferedReader(new FileReader(OPTIMIZED_DIRECTORY + filename));
			String line;
 
			while ((line = br.readLine()) != null) 
			{
				int posOpen = line.indexOf('[');
				int posClose = line.indexOf(']');
				
				if (posOpen != -1 && posClose != -1)
					ciclos.add(loadOptimizedVersion(line.substring(posOpen+1, posClose)));
			}
			
			br.close();
 
		} catch (IOException e) 
		{
			e.printStackTrace();
		}

		return ciclos;
	}
	
	/**
	 * Loads the version optimized for EVM
	 */
	public List<Project> loadOptimizedVersionsEVM() throws XMLParseException
	{
		return loadOptimizedVersions("saida evm pseudo.txt");
	}

	/**
	 * Loads the version optimized for MQ
	 */
	public List<Project> loadOptimizedVersionsMQ() throws XMLParseException
	{
		return loadOptimizedVersions("saida mq pseudo.txt");
	}
	
	
	
	public static Vector<Project> runProjectsReading() throws Exception {
		//
		/*setting the folder for app.jar file after the convertion from .apk to .jar */
		File jarFilesFolder = new File(ODEM_DIRECTORY); // current directory
			/*looping throw the files to add the .jar files*/
			for (File file : listFilesOnly(jarFilesFolder)) {
				if (!file.isDirectory()  && getFileExtension(file).equals("odem") ) {
					instanceFilenames.add(file.getCanonicalPath());
				}
			}
		
		
		/*creating a vector of project to load the instance jar file data*/
		Vector<Project> instances = new Vector<Project>();
		instances.addAll(loadJARVersions(instanceFilenames));
		
		return instances;
	}
	
	
}
