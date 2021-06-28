rm(list = ls())
library(tidyverse)

#
# Configuracao
#
project<-"JUnit";

#baseDirectory <- "D:/Backup/eclipse-workspace";
baseDirectory <- "C:/Users/User/Desktop/Codigos";

#JHotDraw_Exclude <- c("5.4.2");
#JEdit_Exclude <- c("2.3.2", "2.3.3", "2.3.4", "2.3.5", "2.3.6", "2.3.7", "2.3.f", "2.4.2", "2.5.1", "3.0.1", "3.2.1", "4.0.0", "4.0.2", "4.3.0", "4.3.1", "4.3.2","5.4.0");
#JEdit_Exclude <- c("4.8.0");


#
# Carrega dados sobre controle de versoes
#
data <- read_tsv(paste0(baseDirectory, "/PACOTE/results/", project, "_RevisionsByVersion.data"));


#
# Elimina versoes nao utilizadas
#
if (project=="JHotDraw") {
  data <- data %>% 
    mutate(versions = ifelse(versions == "5.4.2", "6.0.1", versions)) %>% 
    mutate(versions = ifelse(versions == "7.2.0", "7.3.0", versions));
}

if (project=="JEdit") {
	data[data$versions == "2.3.2", 1] <- "2.4.1" 
	data[data$versions == "2.3.3", 1] <- "2.4.1" 
	data[data$versions == "2.3.4", 1] <- "2.4.1" 
	data[data$versions == "2.3.5", 1] <- "2.4.1" 
	data[data$versions == "2.3.6", 1] <- "2.4.1" 
	data[data$versions == "2.3.7", 1] <- "2.4.1" 
	data[data$versions == "2.3.f", 1] <- "2.4.1" 
	data[data$versions == "2.4.2", 1] <- "2.4.f" 
	data[data$versions == "2.5.1", 1] <- "2.5.f" 
	data[data$versions == "3.0.1", 1] <- "3.0.2" 
	data[data$versions == "3.2.1", 1] <- "3.2.2" 
	data[data$versions == "4.0.0", 1] <- "4.0.3" 
	data[data$versions == "4.0.2", 1] <- "4.0.3"
	data[data$versions == "4.3.0", 1] <- "4.3.3"
	data[data$versions == "4.3.1", 1] <- "4.3.3"
	data[data$versions == "4.3.2", 1] <- "4.3.3"
}

if (project=="JUnit") {
	data[data$versions == "4.8.0", 1] <- "4.8.1" 
}

#data$versions <- as.factor(data$versions)

result <- 
  data %>%
  mutate(single_class = if_else(classes == 1, 1, 0)) %>% 
  mutate(single_package = if_else(packages == 1, 1, 0)) %>% 
  group_by(versions) %>% 
  summarize(
    commits = n(),
    single_class = sum(single_class),
    single_class_percent = 100 * single_class / commits,
    single_package = sum(single_package),
    single_package_percent = 100 * single_package / commits,
    classes_commit_median = median(classes),
    classes_commit_mean = mean(classes),
    classes_commit_sd = sd(classes),
    packages_commit_median = median(packages),
    packages_commit_mean = mean(packages),
    packages_commit_sd = sd(packages)
  );


#
# Número total de commits
#
sum(result$commits);


#
# Percentual médio de commits de uma classe entre as versões
#
mean(result$single_class_percent);


#
# Média de classes por commit entre todas as versões
#
if (project == "JHowDraw") {
  mean(data %>% filter(versions != "7.0.7") %>% pull(classes));
}

if (project == "JEdit") {
  mean(data %>% filter(versions != "3.2.2") %>% pull(classes));
}

if (project == "JUnit") {
  mean(data %>% pull(classes));
}


#
# Percentual médio de commits de um pacote entre as versões
#
mean(result$single_package_percent);


#
# Média de classes por commit entre todas as versões
#
if (project == "JHowDraw") {
  mean(data %>% filter(versions != "7.0.7") %>% pull(packages));
}

if (project == "JEdit") {
  mean(data %>% filter(versions != "3.2.2") %>% pull(packages));
}


#
# Boxplot de classes por commit
#
p <- ggplot(data, aes(x=versions, y=classes)) + 
  geom_boxplot() +
  labs(x = NULL, y = "Classes por Commit") + 
  theme_bw()

if (project == "JHotDraw") {
  p <- p + coord_cartesian(ylim = c(0, 320))
}

p


#
# Boxplot de pacotes por commit
#
p <- ggplot(data, aes(x=versions, y=packages)) + 
  geom_boxplot() +
  labs(x = NULL, y = "Packages por Commit") + 
  theme_bw()

if (project == "JHotDraw") {
  p + coord_cartesian(ylim = c(0, 27))
}

p

