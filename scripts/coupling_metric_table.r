rm(list = ls())
library(tidyverse)

#
# Configuracao
#
project<-"JEdit";

#baseDirectory <- "D:/Backup/eclipse-workspace";
baseDirectory <- "C:/Users/User/Desktop/Codigos";



#
# Carrega dados
#
data <- read_tsv(paste0(baseDirectory, "/PACOTE/results/ODEMPackageCharacteristics", project, ".data"));


#
# Calcula a tabela com as médias
#
result <- data %>%
  group_by(versions) %>% 
  summarize(
    cbo = mean(cbo, na.rm = TRUE),
    eff = mean(eff, na.rm = TRUE),
    aff = mean(aff, na.rm = TRUE),
    lcom = mean(lcom, na.rm = TRUE)
  ) %>% 
  select(versions, cbo, aff, eff, lcom);


#
# Boxplots
#
ggplot(data, aes(x=versions, y=cbo)) + 
  geom_boxplot() +
  labs(x = NULL, y = "CBO por pacote") + 
  theme_bw();

ggplot(data, aes(x=versions, y=aff)) + 
  geom_boxplot() +
  labs(x = NULL, y = "AFF por pacote") + 
  theme_bw();

ggplot(data, aes(x=versions, y=eff)) + 
  geom_boxplot() +
  labs(x = NULL, y = "EFF por pacote") + 
  theme_bw();

ggplot(data, aes(x=versions, y=lcom)) + 
  geom_boxplot() +
  labs(x = NULL, y = "LCOM por pacote") + 
  theme_bw();


#
# Correlations
#
cor(result %>% select(-versions), method="spearman")
