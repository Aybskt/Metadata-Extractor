<div align="center">
  <img src="https://raw.githubusercontent.com/Aybskt/Metadata-Extractor/main/assets/logo.png" width="120px" />
  <h1 align="center">🖼️ Metadata Extractor & Steganography</h1>
  <p align="center">
    <strong>Application de bureau en Java permettant d’explorer des images, d’en extraire les métadonnées et de manipuler des messages cachés par stéganographie, le tout via une interface graphique moderne et un mode ligne de commande.</strong>
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
  <img src="https://img.shields.io/github/license/Aybskt/Metadata-Extractor?style=for-the-badge&color=green&v=1" />
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

**Metadata Extractor & Steganography** est une application Java conçue pour analyser des images de manière pratique et visuelle. Elle permet d’explorer un dossier d’images, de prévisualiser une image sélectionnée, d’extraire ses métadonnées et d’utiliser un système de **stéganographie** pour encoder ou décoder un message texte dans une image.

Le projet suit une architecture **MVC** claire et propose deux modes d’utilisation complémentaires : une interface graphique pour l’usage interactif, et une interface en ligne de commande pour les traitements rapides ou automatisés. L’interface Swing est enrichie par **FlatLaf**, offrant un thème sombre moderne, cohérent et agréable à utiliser.

---

## ✨ Fonctionnalités

- 🖥️ **Double interface** : une interface graphique conviviale et une interface console (CLI) pour les usages scriptés ou automatisés.
- 📂 **Exploration récursive des dossiers** : analyse automatique d’une arborescence pour détecter les images compatibles.
- 🖼️ **Prévisualisation d’image** : affichage centré de l’image en conservant les proportions et la zone utile.
- 🧾 **Extraction de métadonnées** : lecture et affichage des tags d’image (EXIF, IPTC, etc.) via la bibliothèque `metadata-extractor`.
- 🧪 **Validation des fichiers image** : contrôle du type MIME avant traitement pour éviter les formats non pris en charge.
- 🔐 **Encodage stéganographique** : insertion d’un message dans les bits de poids faible du canal bleu d’une image.
- 🔓 **Décodage de message** : récupération d’un texte précédemment caché dans une image encodée.
- 🎨 **Interface moderne** : composants personnalisés, panneaux arrondis, panneau d’aperçu dédié et thème sombre FlatLaf.

---

## 🧰 Technologies utilisées

| Technologie | Rôle |
|------------|------|
| **Java 17** | Langage principal du projet. |
| **Java Swing** | Construction de l’interface graphique. |
| **FlatLaf** | Modernisation et theming de l’interface (dark theme). |
| **metadata-extractor** | Lecture et parsing des métadonnées d’images. |
| **Architecture MVC** | Séparation claire entre vue, contrôleur et services métier. |

---

## 🧱 Structure du projet

Le projet est organisé autour de plusieurs packages principaux, ce qui facilite la lisibilité, les tests et la maintenance du code. L’application sépare clairement la logique d’interface, les contrôleurs et les services métier.

```bash
src
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

- `App.java` : point d’entrée principal, lance soit le mode GUI, soit le mode CLI selon les arguments fournis.
- `MainFrame.java` : fenêtre principale de l’application graphique (layout, panneaux, événements UI).
- `CliController.java` : gestion des options et commandes utilisateur en ligne de commande.
- `MetadataService.java` : validation, lecture et formatage des métadonnées d’une image.
- `DirectoryService.java` : exploration récursive des dossiers et recherche des images compatibles.
- `SteganographyService.java` : encodage et décodage des messages cachés dans les images.
- `ImageMimeUtils.java` : utilitaires de validation et description des types d’images pris en charge.

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

- Java Runtime Environment (**JRE 11** ou supérieur).
- Vérification de la version :

```sh
java --version
```

### Installation

1. Ouvrir la page **[Releases](https://github.com/Aybskt/Metadata-Extractor/releases)**.
2. Télécharger la dernière version disponible (fichier JAR).
3. Lancer l’application avec le fichier JAR correspondant au mode souhaité.

---

## 💻 Utilisation

### Interface Graphique (GUI)

Le mode graphique est idéal pour parcourir visuellement des images, consulter leurs métadonnées et utiliser les boîtes de dialogue d’encodage ou de décodage. L’interface principale contient une barre de saisie de chemin, une arborescence d’images, un panneau d’aperçu et une zone d’affichage des métadonnées.

```sh
java -jar app.jar
```

Fonctionnalités principales accessibles via la GUI :

- ouverture d’un fichier image ou d’un dossier ;
- affichage des images détectées dans une arborescence ;
- aperçu visuel de l’image sélectionnée ;
- extraction immédiate des métadonnées ;
- encodage d’un message dans une image ;
- décodage d’un message caché.

### Ligne de Commande (CLI)

Le mode CLI permet d’utiliser rapidement l’application dans un terminal ou dans des scripts. Les options disponibles sont gérées par `CliController` et couvrent l’aide, l’analyse d’image, l’exploration de dossier et la stéganographie.

#### Afficher l’aide

```sh
java -jar app.jar -h
```

#### Exemples d’utilisation

- **Afficher les métadonnées d’une image**
```sh
java -jar app.jar -f /chemin/vers/image.jpg
```

- **Explorer un dossier d’images**
```sh
java -jar app.jar -d /chemin/vers/dossier
```

- **Encoder un message dans une image**
```sh
java -jar app.jar -s /chemin/vers/image.png "Bonjour" /chemin/vers/image_encodee.png
```

- **Décoder un message depuis une image encodée**
```sh
java -jar app.jar -e /chemin/vers/image_encodee.png
```

#### Options disponibles

| Option | Description |
|--------|-------------|
| `-h`, `--help` | Affiche l’aide. |
| `-f <image>` | Affiche les métadonnées d’une image. |
| `-d <dossier>` | Explore récursivement un dossier et liste les images détectées. |
| `-s <image> <message> [sortie.png]` | Encode un message dans une image. |
| `-e <image>` | Décode un message caché dans une image. |

---

## 📚 Documentation

La documentation technique complète du projet est générée avec **Javadoc** et publiée via GitHub Pages.

➡️ **[Accéder à la Javadoc](https://aybskt.github.io/Metadata-Extractor/)**

---

## 📜 Licence

Ce projet est distribué sous la Licence **MIT**. Voir le fichier `LICENSE` pour plus de détails.

Copyright (c) 2025 - Ayoub. A (Aybskt)
