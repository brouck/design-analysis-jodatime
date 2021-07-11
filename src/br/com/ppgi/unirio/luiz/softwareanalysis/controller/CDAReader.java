package br.com.ppgi.unirio.luiz.softwareanalysis.controller;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javax.management.modelmbean.XMLParseException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.ppgi.unirio.luiz.softwareanalysis.model.DependencyType;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ElementType;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ElementVisibility;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectClass;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectPackage;

public class CDAReader
{
	private String[] ignoredClasses = null;
	
	/**
	 * Indica a lista de classes ignoradas
	 */
	public void setIgnoredClasses(String[] classes)
	{
		this.ignoredClasses = classes;
	}
	
	/**
	 * Verifica se uma classe deve ser ignorada
	 */
	private boolean isIgnored(String class_)
	{
		if (ignoredClasses == null)
			return false;
		
		for (String s : ignoredClasses)
			if (s.compareToIgnoreCase(class_) == 0)
				return true;
		
		return false;
	}
	
	/**
	 * Carrega um arquivo XML para a mem�ria
	 */
	private Document loadDocument(String filename) throws XMLParseException
	{
		File file = new File(filename);
 		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

 		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
 		dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			if(!Objects.isNull(doc)) {
				doc.getDocumentElement().normalize();
				return doc;
			}
			else {
				throw new XMLParseException ("unable to load file '" + filename + "'");
			}			
		}
		catch (ParserConfigurationException e)
		{
			throw new XMLParseException ("invalid XML content in file '" + filename + "'");
		}
		catch (SAXException e)
		{
			throw new XMLParseException ("invalid XML content in file '" + filename + "'");
		}
		catch (IOException e)
		{
			throw new XMLParseException ("unable to load file '" + filename + "'");
		}
	}

	/**
	 * Retorna o valor de um atributo de um elemento
	 */
	private String getElementAttribute(Element element, String name) throws XMLParseException
	{
		String value = element.getAttribute(name);
		
		if (value == null)
			throw new XMLParseException("missing attribute '" + name + "' for element '" + element.getNodeName() + "'");
		
		return value;
	}

	/**
	 * Retorna o valor de um atributo de um elemento, usando um valor default em sua aus�ncia
	 */
	private String getElementAttribute(Element element, String name, String defvalue)
	{
		String value = element.getAttribute(name);
		
		if (value == null)
			return defvalue;
		
		return value;
	}
	
	/**
	 * Carrega o primeiro filho de um elemento com uma determinada tag
	 */
	private Element getFirstElement(Element element, String tag) throws XMLParseException
	{
		NodeList nodeList = element.getElementsByTagName(tag);

		if (nodeList.getLength() == 0)
			throw new XMLParseException("missing child tag '" + tag + "' under '" + element.getNodeName() + "'");

		return (Element) nodeList.item(0);		
	}
	
	/**
	 * Carrega as depend�ncias de uma classe
	 */
	private void loadDependencies(ProjectClass aClass, Element element) throws XMLParseException
	{
		Element dependencyRoot = getFirstElement(element, "dependencies");
		NodeList nodeList = dependencyRoot.getElementsByTagName("depends-on");

		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Element child = (Element)nodeList.item(i);
			String name = getElementAttribute(child, "name");
			String sClassification = getElementAttribute(child, "classification");
			
			DependencyType classification = DependencyType.fromIdentifier(sClassification);
			
			if (classification == null)
				throw new XMLParseException("invalid classification '" + sClassification + "' for dependency from '" + aClass.getName() + "' to '" + name + "'");

			aClass.addDependency(name, classification);
		}
	}

	/**
	 * Carrega as classes de um pacote
	 */
	private void loadClasses(Project project, ProjectPackage apackage, Element element) throws XMLParseException
	{
		NodeList nodeList = element.getElementsByTagName("type");

		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Element child = (Element)nodeList.item(i);
			String name = getElementAttribute(child, "name");
			String sClassification = getElementAttribute(child, "classification");
			String sVisibility = getElementAttribute(child, "visibility");
			String sAbstract = getElementAttribute(child, "isAbstract", "false");
			
			ElementType classification = ElementType.fromIdentifier(sClassification);
			
			if (classification == null)
				throw new XMLParseException("invalid classification '" + sClassification + "' for type '" + name + "'");
			
			ElementVisibility visibility = ElementVisibility.fromIdentifier(sVisibility);
			
			if (visibility == null)
				throw new XMLParseException("invalid visibility '" + sVisibility + "' for type '" + name + "'");
			
		
			ProjectClass aClass = new ProjectClass(name, classification, visibility, (sAbstract.equals("yes")?true:false));
			aClass.setPackage(apackage);
			project.addClass(aClass);
			loadDependencies(aClass, child);
		}
	}

	/**
	 * Carrega os pacotes da aplica��o
	 */
	private void loadNamespaces(Project project, Element element) throws XMLParseException
	{
		NodeList nodeList = element.getElementsByTagName("namespace");

		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Element child = (Element)nodeList.item(i);
			String name = getElementAttribute(child, "name");

			ProjectPackage apackage = project.addPackage(name);
			loadClasses(project, apackage, child);
		}
	}

	/**
	 * Carrega os containers da aplica��o
	 */
	private void loadContainers(Project project, Element element) throws XMLParseException
	{
		NodeList nodeList = element.getElementsByTagName("container");

		for (int i = 0; i < nodeList.getLength(); i++)
			loadNamespaces(project, (Element)nodeList.item(i));
	}

	/**
	 * Carrega uma aplica��o a partir do elemento raiz do arquivo
	 */
	private Project loadApplication(Element root) throws XMLParseException
	{
		Element element = getFirstElement(root, "context");		
		String name = getElementAttribute(element, "name");
		Project application = new Project(name);
		loadContainers(application, element);
		return application;
	}

	/**
	 * Verifica as depend�ncias de um projeto
	 */
	private void checkDependencies (Project project) throws XMLParseException
	{
		int classCount = project.getClassCount();
		
		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
	
			for (int j = _class.getDependencyCount()-1; j >= 0; j--)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex == -1)
				{
					if (!isIgnored(targetName))
						System.out.println ( "Class not registered in project: " + targetName);
//						throw new XMLParseException ("Class not registered in project: " + targetName);
						_class.removeDependency(j);
//						break;
				}
			}
		}
	}

	/**
	 * Carrega uma aplica��o a partir de um arquivo no formato XML ODEM
	 */
	public Project execute(String filename) throws XMLParseException
	{
		Document doc = loadDocument(filename);

		if (doc == null)
			return null;

		Project project = loadApplication(doc.getDocumentElement());

		if (project == null)
			return null;

		checkDependencies(project);
		return project;
	}
}