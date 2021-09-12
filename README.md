# design-analysis-jodatime


## How to generate data:

You can set to true which script you want to run:

```java
private static boolean EXECUTE_EXTRACT_GRAPH_DATA = false;
private static boolean EXECUTE_GENERATE_LOG_LIST_FILE = false;
private static boolean EXECUTE_EXTRACT_REVISIONS_BY_VERSION = false;
private static boolean EXECUTE_EXTRACT_REVISIONS_BY_YEAR = false;
private static boolean EXECUTE_EXTRACT_ODEM_PROJECTS_CHARACTERISTICS =false;
private static boolean EXECUTE_EXTRACT_JAR_PROJECTS_CHARACTERISTICS =false;
private static boolean EXECUTE_EXTRACT_ODEM_PACKAGE_CHARACTERISTICS =false;

```

Readable data paths, this is the default paths:
```java
//TODO
private static String ODEM_DIRECTORY = new File("").getAbsolutePath() + "\\data\\odem";
```

Output Directories:
```java
//TODO
	private static String GRAPH_OUTPUT_DIRECTORY= new  File("").getAbsolutePath() + "\\data\\results\\graph\\";
```

Other:
```java
private static String PROJECT_NAME = "joda-time";
private static boolean LOG_FILE_FROM_GIT = true;
```

## Execute Extract ODEM Graph Data

The .odem files you want to read must be on the following path:

[`\design-analysis-jodatime\data\odem`](data/odem)

Set `EXECUTE_EXTRACT_GRAPH_DATA` to `true`.

This generates .data files that will be exported to [design-analysis-jodatime\data\results\graph](data/results/graph/GRAPHDIRECTORY.md).


## Execute Generate Log List File

The log list raw file must be on the following path:

[`\design-analysis-jodatime\data\log`](data/log)

Set `EXECUTE_GENERATE_LOG_LIST_FILE` to `true`.

Set `LOG_FILE_FROM_GIT` to `true` if you generate the log file from git and to `false` if you used SVN.

To generate the log file from git in the supported format:
```
git log --name-status --find-renames > <log file name>.txt 
```



This generates .data files that will be exported to [design-analysis-jodatime\data\results\logs](data/results/logs/LOGDIRECTORY.md).


## TODO
- Add how to generate the log in the svn supported format.