package br.com.ppgi.unirio.luiz.softwareanalysis.controller;

import br.com.ppgi.unirio.luiz.softwareanalysis.model.Project;
import br.com.ppgi.unirio.luiz.softwareanalysis.model.ProjectClass;

/**
 * DEFINIC�ES:
 * 
 * - acoplamento = n�mero de depend�ncias que as classes de um pacote possuem com classes de fora
 *   do pacote. Deve ser minimizado.
 *   
 * - coes�o = n�mero de depend�ncias que as classes de um pacote possuem com outras classes do
 *   mesmo pacote. Deve ser maximizado (ou seja, minimizamos seu valor com sinal invertido)
 *   
 * - spread = partindo de zero e percorrendo cada pacote, acumula o quadrado da diferen�a entre
 *   o n�mero de classes do pacote e o n�mero de classes do menor pacote
 *   
 * - diferenca = diferen�a entre o n�mero m�ximo de classes em um pacote e o n�mero m�nimo de
 *   classes em um pacote
 * 
 * @author Marcio Barros
 */
public class ClusteringCalculator
{
	private int classCount;
	private int packageCount;

	private int[][] dependencies;
	private int[] originalPackage;
	private int[] newPackage;
	private int[] originalClasses;
	private int[] newClasses;

	private int minClasses;
	private int maxClasses;
	private double[] classProbability;
	
	private double sum;
	private double sumSquare;
	private int count;

	/**
	 * Inicializa o calculador de acoplamento
	 */
	public ClusteringCalculator(Project project, int packageCount) throws Exception
	{
		this.classCount = project.getClassCount();
		this.packageCount = packageCount;
		this.minClasses = this.maxClasses = 0;
		this.classProbability = null;
		prepareClasses(project);
	}
	
	/**
	 * Prepara as classes para serem processadas pelo programa
	 */
	public void prepareClasses(Project project) throws Exception
	{
		this.dependencies = new int[classCount][classCount];
		
		this.originalPackage = new int[classCount];
		this.newPackage = new int[classCount];
		
		this.originalClasses = new int[packageCount];
		this.newClasses = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			ProjectClass _class = project.getClassIndex(i);
			int sourcePackageIndex = project.getIndexForPackage(_class.getPackage());
			
			this.originalPackage[i] = sourcePackageIndex;
			this.newPackage[i] = sourcePackageIndex;
			this.originalClasses[sourcePackageIndex]++;
			this.newClasses[sourcePackageIndex]++; 

			for (int j = 0; j < _class.getDependencyCount(); j++)
			{
				String targetName = _class.getDependencyIndex(j).getElementName();
				int classIndex = project.getClassIndex(targetName);
				
				if (classIndex == -1)
//					throw new Exception ("Class not registered in project: " + targetName);
					System.out.println("Class not registered in project: " + targetName);
				else {dependencies[i][classIndex]++;}
			}
		}
	}

	/**
	 * Estima as probabilidades de distribui��o de classes 
	 */
	public void setClassDistributionProbabilities(int min, int expected, int max)
	{
		// guarda os par�metros de distribui��o de classes
		this.minClasses = min;
		this.maxClasses = max;
		this.classProbability = new double[max-min];
		
		// calcula a altura da distribui��o triangular
		double height = 2.0 / (max - min); 
		
		// calcula a equa��o da reta da esquerda
		double al = height / (expected - min);
		double bl = -min * al;
		
		// calcula a equa��o da reta da direita
		double ar = -height / (max - expected);
		double br = -max * ar;
		
		// estima as probabilidades
		for (int i = min; i < expected; i++)
			this.classProbability[i-min] = al / 2 * (i + 1) * (i + 1) + bl * (i + 1) - al / 2 * i * i - bl * i;
		
		for (int i = expected; i < max; i++)
			this.classProbability[i-min] = ar / 2 * (i + 1) * (i + 1) + br * (i + 1) - ar / 2 * i * i - br * i;
	}

	/**
	 * Estima as probabilidades de distribui��o de classes 
	 */
	private double calculateClassCountProbabilities(int count)
	{
		if (count < this.minClasses || count >= this.maxClasses)
			return 0.0;
		else
			return classProbability[count - this.minClasses]; 
	}

	/**
	 * Inicializa o processo de c�lculo
	 */
	public void reset()
	{
		for (int i = 0; i < classCount; i++)
			newPackage[i] = originalPackage[i];

		for (int i = 0; i < packageCount; i++)
			newClasses[i] = originalClasses[i];
	}

	/**
	 * Move uma classe para um pacote
	 */
	public void moveClass(int classIndex, int packageIndex)
	{
		int actualPackage = newPackage[classIndex];
		
		if (actualPackage != packageIndex)
		{
			newClasses[actualPackage]--;
			newPackage[classIndex] = packageIndex;
			newClasses[packageIndex]++;
		}
	}
	
	/**
	 * Returns the package of a given class
	 */
	public int getPackage(int classIndex)
	{
		return newPackage[classIndex];
	}

	/**
	 * Retorna o n�mero de classes de um pacote
	 */
	public int getClassCount(int packageIndex)
	{
		return newClasses[packageIndex];
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	public int getMinimumClassCount()
	{
		int min = Integer.MAX_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count < min)
				min = count;
		}

		return min;
	}

	/**
	 * Retorna o n�mero de movimentos de classes
	 */
	public int getMaximumClassCount()
	{
		int max = Integer.MIN_VALUE;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];

			if (count > max)
				max = count;
		}

		return max;
	}

	/**
	 * Retorna o n�mero de pacotes com mais de uma classe
	 */
	public int getPackageCount()
	{
		int packages = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (newClasses[i] > 0)
				packages++;

		return packages;
	}

	/**
	 * Calcula o numero de depend�ncias com origem em um dado pacote e t�rmino em outro de um pacote
	 */
	public int countOutboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					edges++;
		}

		return edges;
	}

	/**
	 * 
	 */
	public int calculateEfferentCoupling(int packageIndex)
	{
		boolean[] efferentClasses = new boolean[classCount];

		for (int i = 0; i < classCount; i++)
			efferentClasses[i] = false;
		
		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					efferentClasses[j] = true;
		}

		int count = 0;
		
		for (int i = 0; i < classCount; i++)
			if (efferentClasses[i])
				count++;

		return count;
	}

	/**
	 * 
	 */
	public double calculateEfferentCoupling()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 0)
			{
				sum += calculateEfferentCoupling(i);
				count++;
			}
		
		return sum / count;
	}

	/**
	 * Calcula o numero de depend�ncias com um pacote e t�rmino em um dado pacote
	 */
	public int countInboundEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == packageIndex)
					edges++;
		}

		return edges;
	}

	/**
	 * Calcula o numero de depend�ncias de classes externas a um pacote que dependem dele
	 */
	public int calculateAfferentCoupling(int packageIndex)
	{
		int afferentClasses = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				continue;
			
			boolean dependsOnPackage = false;
			
			for (int j = 0; j < classCount && !dependsOnPackage; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == packageIndex)
					dependsOnPackage = true;
			
			if (dependsOnPackage)
				afferentClasses++;
		}

		return afferentClasses;
	}

	/**
	 * Calcula o numero de depend�ncias de classes externas a um pacote que dependem dele
	 */
	public double calculateAfferentCoupling()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 0)
			{
				sum += calculateAfferentCoupling(i);
				count++;
			}
		
		return sum / count;
	}
	
	/**
	 * Calcula o n�mero de depend�ncias internas de um pacote
	 */
	public int countIntraEdges(int packageIndex)
	{
		int edges = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage != packageIndex)
				continue;
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					edges++;
		}

		return edges;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public int calculateDifference()
	{
		int min = getMinimumClassCount();
		int max = getMaximumClassCount();
		return max - min;
	}

	/**
	 * Retorna a dispers�o da distribui��o de classes em pacotes
	 */
	public double calculateSpread()
	{
		int min = getMinimumClassCount();
		double spread = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int count = newClasses[i];
			spread += Math.pow(count - (double)min, 2);
		}

		return spread;
	}

	/**
	 * Calcula o acoplamento do projeto
	 */
	public int calculateCoupling()
	{
		int coupling = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] != currentPackage)
					coupling += 2;
		}

		return coupling;
	}

	/**
	 * Calcula a coes�o do projeto
	 */
	public int calculateCohesion()
	{
		int cohesion = 0;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
				if (dependencies[i][j] > 0 && newPackage[j] == currentPackage)
					cohesion++;
		}

		return cohesion;
	}

	/**
	 * Calcula o coeficiente de modularidade do projeto
	 */
	public double calculateModularizationFactor(int packageIndex)
	{
		int[] inboundEdges = new int[packageCount];
		int[] outboundEdges = new int[packageCount];
		int[] intraEdges = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			int sourcePackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
			{
				if (dependencies[i][j] > 0)
				{
					int targetPackage = newPackage[j];
					
					if (targetPackage != sourcePackage)
					{
						outboundEdges[sourcePackage]++;
						inboundEdges[targetPackage]++;
					}
					else
						intraEdges[sourcePackage]++;
				}
			}
		}
		
		int inter = inboundEdges[packageIndex] + outboundEdges[packageIndex];
		int intra = intraEdges[packageIndex];
		
		if (intra != 0 && inter != 0)
			return intra / (intra + 0.5 * inter);

		return 0.0;
	}

	/**
	 * Calcula o coeficiente de modularidade do projeto
	 */
	public double calculateModularizationQuality()
	{
		int[] inboundEdges = new int[packageCount];
		int[] outboundEdges = new int[packageCount];
		int[] intraEdges = new int[packageCount];

		for (int i = 0; i < classCount; i++)
		{
			int sourcePackage = newPackage[i];
			
			for (int j = 0; j < classCount; j++)
			{
				if (dependencies[i][j] > 0)
				{
					int targetPackage = newPackage[j];
					
					if (targetPackage != sourcePackage)
					{
						outboundEdges[sourcePackage]++;
						inboundEdges[targetPackage]++;
					}
					else
						intraEdges[sourcePackage]++;
				}
			}
		}
		
		double mq = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int inter = inboundEdges[i] + outboundEdges[i];
			int intra = intraEdges[i];
			
			if (intra != 0 && inter != 0)
			{
				double mf = intra / (intra + 0.5 * inter);
				mq += mf;
			}
		}

		return mq;
	}

	/**
	 * Calcula o cluster score de um pacote
	 */
	public int calculateClusterScore(int packageIndex)
	{
		int score = 0;

		for (int i = 0; i < classCount-1; i++)
		{
			int sourcePackage = newPackage[i];

			if (sourcePackage == packageIndex)
			{
				for (int j = i+1; j < classCount; j++)
				{
					int targetPackage = newPackage[j];
	
					if (targetPackage == sourcePackage)
						if (dependencies[i][j] > 0 || dependencies[j][i] > 0) 
							score++;
						else
							score--; 
				}
			}
		}

		return score;
	}

	/**
	 * Calcula o EVM do projeto
	 */
	public int calculateEVM()
	{
		int evm = 0;

		for (int i = 0; i < classCount-1; i++)
		{
			int sourcePackage = newPackage[i];
			
			for (int j = i+1; j < classCount; j++)
			{
				int targetPackage = newPackage[j];

				if (targetPackage == sourcePackage)
					if (dependencies[i][j] > 0 || dependencies[j][i] > 0) 
						evm++;
					else
						evm--; 
			}
		}

		return evm;
	}

	/**
	 * Calcula o fator de distribui��o de classes 
	 */
	public double calculateClassDistributionFactor()
	{
		double cdf = 0.0;

		for (int i = 0; i < packageCount; i++)
		{
			int count = getClassCount(i);
			cdf += calculateClassCountProbabilities(count);
		}

		return cdf;
	}
	
	/**
	 * Calcula o desvio padr�o do n�mero de dependencias internas de um pacote
	 */
	public double calculateCohesionElegance()
	{		
		sum=0;
		sumSquare=0;
		count=0;

		for (int i = 0; i < packageCount; i++)
			add(countInboundEdges(i));		

		return standardDeviation();
	}
	
	/**
	 * calcula o desvio padr�o do n�mero de depend�ncias externas de cada pacote 
	 */
	public double calculateCouplingElegance()
	{
		sum=0;
		sumSquare=0;
		count=0;

		for (int i = 0; i < packageCount; i++)
			add(countOutboundEdges(i));

		return standardDeviation();
	}
		
	/**
	 * Calcula o desvio padr�o do n�mero de classes por pacote
	 */
	public double calculateClassElegance()
	{
		sum=0;
		sumSquare=0;
		count=0;
		
		for (int i = 0; i < packageCount; i++)
			add(getClassCount(i));

		return standardDeviation();
	}

	/**
	 * Retorna as classes que pertencem a um pacote, dado seu �ndice
	 */
	private int[] getPackageClasses(int packageIndex)
	{
		int classesOnPackage = getClassCount(packageIndex);
		int[] _classes = new int[classesOnPackage];
		int walker = 0;
		
		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
				_classes[walker++] = i;
		}

		return _classes;
	}
	
	/**
	 * Calcula o LCOM5 de um pacote, dado seu �ndice
	 */
	public double calculateLCOM5(int packageIndex)
	{
		int classesOnPackage = getClassCount(packageIndex);
		int[] _classes = getPackageClasses(packageIndex);
		int[] usage = new int[classesOnPackage];
		
		for (int i = 0; i < classesOnPackage; i++)
		{
			int classI = _classes[i];
			
			for (int j = 0; j < classesOnPackage; j++)
			{
				int classJ = _classes[j];
				
				if (dependencies[classI][classJ] > 0)
					usage[i]++;
			}
		}
		
		double sum = 0.0;
		
		for (int i = 0; i < classesOnPackage; i++)
			sum += usage[i] + 1;
		
		double classSquare = (double)classesOnPackage * (double)classesOnPackage;
		return (sum - classSquare) / (classesOnPackage - classSquare);
	}
	
	/**
	 * Calcula o LCOM5 do projeto
	 */
	public double calculateLCOM5()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 1)
			{
				sum += calculateLCOM5(i);
				count++;
			}
		
		return sum / count;
	}

	/**
	 * Calcula o CBO de um pacote, dado seu �ndice
	 */
	public int calculateCBO(int packageIndex)
	{
		boolean[] knownDependencies = new boolean[packageCount];
		int totalDependencies = 0;

		for (int i = 0; i < packageCount; i++)
			knownDependencies[i] = false;

		for (int i = 0; i < classCount; i++)
		{
			int currentPackage = newPackage[i];
			
			if (currentPackage == packageIndex)
			{
				for (int j = 0; j < classCount; j++)
				{
					int myPackage = newPackage[j];
			
					if (dependencies[i][j] > 0 && myPackage != currentPackage && !knownDependencies[myPackage])
					{
						totalDependencies++;
						knownDependencies[myPackage] = true;
					}
				}
			}
		}

		return totalDependencies;
	}

	/**
	 * Calcula o CBO de um projeto
	 */
	public double calculateCBO()
	{
		double sum = 0.0;
		int count = 0;
		
		for (int i = 0; i < packageCount; i++)
			if (getClassCount(i) > 0)
			{
				sum += calculateCBO(i);
				count++;
			}
		
		return sum / count;
	}

	/**
	 * Conta o n�mero de pacotes com apenas uma classe
	 */
	public int countSingleClassPackages()
	{
		int count = 0;

		for (int i = 0; i < packageCount; i++)
			if (newClasses[i] == 1)
				count++;

		return count;
	}
	
	public void add(int number)
	{
		sum += number;
		sumSquare += number * number;
		count++;
	}
	
	/**
	 * Returns the standard deviation of previously added numbers
	 */
	public double standardDeviation() 
	{
		return (count > 2) ? Math.sqrt(sumSquare / count - sum / count * sum / count) : 0.0;
	}

	/**
	 * Returns the average value of previously added numbers
	 */
	public double average() 
	{
		return (count > 0) ? sum / count : 0.0;
	}
}