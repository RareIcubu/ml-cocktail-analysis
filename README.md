# Cocktail Clustering Analysis :cocktail:

Machine learning pipeline for exploratory data analysis (EDA) and clustering of cocktail recipes from TheCocktailDB.  
**Java 17 | Maven | Smile Machine Learning**

[![Java Version](https://img.shields.io/badge/Java-17-007396?logo=openjdk)](https://adoptium.net/)
[![Maven](https://img.shields.io/badge/Maven-3.9.6-C71A36?logo=apachemaven)](https://maven.apache.org)

## 📥 Instalacja

### Wymagania wstępne
- minimum Java 17 JDK ([Temurin Distribution](https://adoptium.net/))
- Maven 3.9.6+

```bash
# Clone repository
git clone https://github.com/RareIcubu/ml-cocktail-analysis.git
cd cocktail-clustering

# Install Maven dependencies
mvn clean install
```

## 🏃♂️ How to Run

Uruchamianie projektu za pomocą Maven:
```bash
# Windows
mvn exec:java -Dexec.mainClass="mlcocktail.MainPipeline"

# Linux/macOS
mvn exec:java@run
```


## 📂 Struktura projektu
```
.
├── data
│   └── cocktail_dataset.json
├── dependency-reduced-pom.xml
├── docs
├── pom.xml
├── src
│   └── main
│       └── java
│           └── mlcocktail
│               ├── Archive                                 # Stare pliki
│               │   ├── DataExplorer.java_old
│               │   └── DataQuality.java_old                
│               ├── clustering
│               │   ├── ClustererFactory.java               # Fabryka klastrowania
│               │   ├── Clusterer.java                      # Klasa abstrakcyjna klastrowania
│               │   ├── DBSCANClusterer.java                # Klastrowanie DBSCAN
│               │   ├── GridSearchClustering.java           # szukanie optymalnych parametrów
│               │   ├── KMeansClusterer.java                # Klastrowanie K-Means
│               │   ├── OptParams.java                      # Optymalizacja parametrów
│               │   └── XMeansClusterer.java                # klastrowania X-Means
│               ├── config
│               │   ├── ClustererConfig.java                # Konfiguracja klastrowania
│               │   └── ReducerConfig.java                  # Konfiguracja redukcji wymiarów
│               ├── data
│               │   ├── Cocktail.java                       # Klasa koktajli
│               │   ├── DataLoader.java                     # Wczytywanie danych
│               │   ├── DataPreprocessor.java               # Preprocessing danych
│               │   ├── Ingredient.java                     # Klasa składników
│               │   └── OutlierRemoval.java                 # Usuwanie wartości odstających
│               ├── evaluation
│               │   ├── ClusteringResult.java               # Wyniki klastrowania
│               │   ├── Evaluator.java                      # Silhouette, Davies-Boulding, Dunn itp.
│               │   └── Visualization.java                  # Wizualizacja wyników
│               ├── features
│               │   ├── DimensionReducer.java               # PCA/UMAP
│               │   ├── EnhancedFeatureExtractor.java       # TF-IDF
│               │   ├── PCAReducer.Java                     # Metoda PCA
│               │   ├── ReducerFactory.Java                 # Fabryka reduktorów
│               │   └── UMAPReducer.Java                    # Metoda UMAP
│               └── MainPipeline.java                       # Klasa main
```

## ⚙️ Konfiguracja
| Komponent          | Opcje                          |
|--------------------|----------------------------------|
| **Clustering**     | K-Means, DBSCAN, X-Means         |
| **Dimensionality** | PCA (5-20 components), UMAP      |
| **Metrics**        | Silhouette, Davies-Bouldin, Dunn |

## 📊 Wizualizacja wyników

```

Zawiera:
- 2D wykresy klastrów
- Silhouette diagrams

## 📦 Biblioteki
Wszystkie zależności są dostępne w pliku [pom.xml](./pom.xml):
```xml
<dependencies>
  <dependency>
    <groupId>com.github.haifengl</groupId>
    <artifactId>smile-core</artifactId>
    <version>3.0.1</version>
  </dependency>
  <dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
  </dependency>
</dependencies>
```

📬 **Kontakt**: [Jakub Jasiński](mailto:280109@student.pwr.edu.pl)  
🔖 **Licencja**: GPL 3.0 license (see [LICENSE](LICENSE))
``` 
