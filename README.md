<div align="center">
  <img src="https://raw.githubusercontent.com/Aybskt/Metadata-Extractor/main/assets/logo.png" width="120px" />
  <h1 align="center">🖼️ Metadata Extractor & Steganography</h1>
  <p align="center">
    <strong>Application de bureau développée en Java pour explorer des images, extraire leurs métadonnées et manipuler des messages cachés grâce à la stéganographie, avec une interface graphique moderne et un mode ligne de commande.</strong>
    <br />
    <br />
    <a href="https://aybskt.github.io/Metadata-Extractor/"><strong>🎓 Consulter la Javadoc</strong></a>
    ·
    <a href="https://github.com/Aybskt/Metadata-Extractor/releases"><strong>🚀 Télécharger la dernière version</strong></a>
    ·
    <a href="https://github.com/Aybskt/Metadata-Extractor/issues"><strong>🐞 Signaler un bug</strong></a>
  </p>
</div>

<div align="center">
  <img src="https://img.shields.io/github/last-commit/Aybskt/Metadata-Extractor?style=for-the-badge&logo=github&color=blue" />
  <img src="https://img.shields.io/github/repo-size/Aybskt/Metadata-Extractor?style=for-the-badge&logo=github" />
  <img src="https://img.shields.io/github/languages/top/Aybskt/Metadata-Extractor?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/github/license/Aybskt/Metadata-Extractor?style=for-the-badge&color=green" />
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/UI-Swing%20%2B%20FlatLaf-6C63FF?style=for-the-badge" />
</div>

<br>

---

## Table des matières

1. [🌟 À propos du projet](#-à-propos-du-projet)
2. [✨ Fonctionnalités](#-fonctionnalités)
3. [🧰 Technologies utilisées](#-technologies-utilisées)
4. [🧱 Structure du projet](#-structure-du-projet)
5. [📸 Aperçu de l'application](#-aperçu-de-lapplication)
6. [🚀 Démarrage rapide](#-démarrage-rapide)
7. [💻 Utilisation](#-utilisation)
   - [Interface Graphique (GUI)](#interface-graphique-gui)
   - [Ligne de Commande (CLI)](#ligne-de-commande-cli)
8. [📚 Documentation](#-documentation)
9. [📜 Licence](#-licence)

---

## 🌟 À propos du projet

**Metadata Extractor & Steganography** est une application Java conçue pour analyser des images de manière pratique et visuelle. Elle permet d’explorer un dossier d’images, de prévisualiser une image sélectionnée, d’extraire ses métadonnées et d’utiliser un système de stéganographie pour encoder ou décoder un message texte dans une image. [file:32][file:35][file:36]

Le projet suit une architecture **MVC** claire et propose deux modes d’utilisation complémentaires : une interface graphique pour l’usage interactif, et une interface en ligne de commande pour les traitements rapides ou automatisés. L’interface Swing est enrichie par **FlatLaf** afin de proposer un rendu sombre, moderne et cohérent. [file:32][file:33][file:34]

---

## ✨ Fonctionnalités

- 🖥️ **Double interface** : une interface graphique conviviale et une interface CLI pour l’automatisation. [file:32][file:33][file:34]
- 📂 **Exploration récursive des dossiers** : recherche automatique des images compatibles dans une arborescence. [file:32][file:34]
- 🖼️ **Prévisualisation d’image** : affichage centré de l’image en conservant ses proportions. [file:31][file:32]
- 🧾 **Extraction de métadonnées** : lecture et affichage des tags d’image grâce à la bibliothèque `metadata-extractor`. [file:36]
- 🧪 **Validation des fichiers image** : contrôle du type MIME avant traitement. [file:32][file:36]
- 🔐 **Encodage stéganographique** : insertion d’un message dans les bits de poids faible du canal bleu d’une image. [file:35]
- 🔓 **Décodage de message** : récupération d’un texte précédemment caché dans une image encodée. [file:35]
- 🎨 **Interface moderne** : composants personnalisés, panneaux arrondis, aperçu visuel et thème sombre FlatLaf. [file:30][file:31][file:32][file:33]

---

## 🧰 Technologies utilisées

| Technologie | Rôle |
|---------|------|
| **Java 17** | Langage principal du projet. [file:33] |
| **Java Swing** | Construction de l’interface graphique. [file:32][file:33] |
| **FlatLaf** | Modernisation de l’apparence de l’interface. [file:32][file:33] |
| **metadata-extractor** | Lecture des métadonnées d’images. [file:36] |
| **Architecture MVC** | Organisation du code entre vue, contrôleur et services métier. [file:32][file:34][file:35][file:36] |

---

## 🧱 Structure du projet

Le projet est organisé autour de plusieurs packages principaux, ce qui facilite la lisibilité et la maintenance du code. L’application sépare clairement la logique d’interface, les contrôleurs et les services métier. [file:32][file:33][file:34][file:35][file:36]

```bash
src/main/java/com/projectd10
├── App.java
├── controller
│   └── CliController.java
├── model
│   ├── DirectoryService.java
│   ├── MetadataService.java
│   └── SteganographyService.java
├── util
│   └── ImageMimeUtils.java
└── view
    ├── MainFrame.java
    └── components
        ├── ImagePreviewPanel.java
        └── RoundedPanel.java
```

### Rôle des classes principales

- `App.java` : point d’entrée principal, lance soit le mode GUI, soit le mode CLI selon les arguments. [file:33]
- `MainFrame.java` : fenêtre principale de l’application graphique. [file:32]
- `CliController.java` : gestion des commandes utilisateur en ligne de commande. [file:34]
- `MetadataService.java` : validation et extraction des métadonnées d’une image. [file:36]
- `DirectoryService.java` : exploration des dossiers et recherche des images. [file:32][file:34]
- `SteganographyService.java` : encodage et décodage des messages cachés. [file:35]
- `ImageMimeUtils.java` : utilitaires de validation et description des images compatibles. [file:32][file:36]

---

## 📸 Aperçu de l'application

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/Aybskt/Metadata-Extractor/main/assets/img1.png" alt="Vue principale de l'application"></td>
    <td><img src="https://raw.githubusercontent.com/Aybskt/Metadata-Extractor/main/assets/img2.png" alt="Exploration et analyse d'une image"></td>
  </tr>
  <tr>
    <td align="center"><em>Fenêtre principale de l’application</em></td>
    <td align="center"><em>Prévisualisation, métadonnées et actions de stéganographie</em></td>
  </tr>
</table>

---

## 🚀 Démarrage rapide

Assurez-vous que Java est installé sur votre machine avant d’exécuter l’application.

### Prérequis

- Java Runtime Environment (**JRE 11** ou supérieur)
- Vérification de la version :

```sh
java --version
```

### Installation

1. Ouvrir la page **[Releases](https://github.com/Aybskt/Metadata-Extractor/releases)**.
2. Télécharger la dernière version disponible.
3. Lancer l’application avec le fichier JAR correspondant au mode souhaité.

---

## 💻 Utilisation

### Interface Graphique (GUI)

Le mode graphique est idéal pour parcourir visuellement des images, consulter leurs métadonnées et utiliser les boîtes de dialogue d’encodage ou de décodage. L’interface principale contient une barre de saisie de chemin, une arborescence d’images, un panneau d’aperçu et une zone d’affichage des métadonnées. [file:32]

```sh
java -jar gui.jar
```

Fonctions accessibles via la GUI :

- ouverture d’un fichier image ou d’un dossier ;
- affichage des images détectées dans une arborescence ;
- aperçu visuel de l’image sélectionnée ;
- extraction immédiate des métadonnées ;
- encodage d’un message dans une image ;
- décodage d’un message caché. [file:32]

### Ligne de Commande (CLI)

Le mode CLI permet d’utiliser rapidement l’application dans un terminal ou dans des scripts. Les options disponibles sont gérées par `CliController` et couvrent l’aide, l’analyse d’image, l’exploration de dossier et la stéganographie. [file:34]

#### Afficher l’aide

```sh
java -jar cli.jar -h
```

#### Exemples d’utilisation

- **Afficher les métadonnées d’une image**
```sh
java -jar cli.jar -f /chemin/vers/image.jpg
```

- **Explorer un dossier d’images**
```sh
java -jar cli.jar -d /chemin/vers/dossier
```

- **Encoder un message dans une image**
```sh
java -jar cli.jar -s /chemin/vers/image.png "Bonjour Ayoub" /chemin/vers/image_encodee.png
```

- **Décoder un message depuis une image encodée**
```sh
java -jar cli.jar -e /chemin/vers/image_encodee.png
```

#### Options disponibles

| Option | Description |
|--------|-------------|
| `-h`, `--help` | Affiche l’aide. [file:34] |
| `-f <image>` | Affiche les métadonnées d’une image. [file:34] |
| `-d <dossier>` | Explore récursivement un dossier et liste les images détectées. [file:34] |
| `-s <image> <message> [sortie.png]` | Encode un message dans une image. [file:34][file:35] |
| `-e <image>` | Décode un message caché dans une image. [file:34][file:35] |

---

## 📚 Documentation

La documentation technique complète du projet est générée avec **Javadoc** et peut être publiée via GitHub Pages.

➡️ **[Accéder à la Javadoc](https://aybskt.github.io/Metadata-Extractor/)**

---

## 📜 Licence

Ce projet est distribué selon la licence définie dans le dépôt GitHub. Consulter le fichier `LICENSE` pour plus d’informations.

Copyright (c) 2025 - Ayoub. A (Aybskt)