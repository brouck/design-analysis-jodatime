# design-analysis-jodatime


## How to generate data:

You can set to true which script you want to run:

```java
	private static boolean EXECUTE_EXTRACT_GRAPH_DATA = false;
	private static boolean EXECUTE_GENERATE_LOG_LIST_FILE = false;
	private static boolean EXECUTE_EXTRACT_REVISIONS_BY_VERSION = false;
	private static boolean EXECUTE_EXTRACT_REVISIONS_BY_YEAR = false;
	private static boolean EXECUTE_EXTRACT_ODEM_PROJECTS_CHARACTERISTICS = false;
	private static boolean EXECUTE_EXTRACT_JAR_PROJECTS_CHARACTERISTICS = false;
	private static boolean EXECUTE_EXTRACT_ODEM_PACKAGE_CHARACTERISTICS = false;

```

## Extract ODEM Graph Data

The .odem files you want to read must be on the following path:

[`\design-analysis-jodatime\data\odem`](data/odem)

And you need to set `EXECUTE_EXTRACT_GRAPH_DATA` to `true`.

This generates .data files that will be exported to [design-analysis-jodatime\data\results\graph](data/results/graph/GRAPHDIRECTORY.md).