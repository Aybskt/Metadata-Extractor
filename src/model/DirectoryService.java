package com.projectd10.model;

import com.projectd10.util.ImageMimeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service d'exploration des dossiers.
 * <p>
 * Cette classe sert a rechercher les fichiers image compatibles dans un repertoire, a les trier
 * et a produire un rapport contenant eventuellement les metadonnees de chaque image detectee.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class DirectoryService {
    private final MetadataService metadataService;

    /**
     * Cree le service d'exploration avec le service d'extraction des metadonnees.
     *
     * @param metadataService service utilise pour extraire les metadonnees des images trouvees.
     */
    public DirectoryService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    /**
     * Liste recursivement les images PNG, JPG et JPEG valides dans un dossier.
     *
     * @param rootDirectory dossier racine a explorer.
     * @return liste triee des chemins d'images compatibles.
     * @throws IllegalArgumentException si le chemin n'est pas un dossier valide.
     * @throws IllegalStateException si le dossier ne peut pas etre parcouru.
     */
    public List<Path> listImageFiles(Path rootDirectory) {
        // Avant de parcourir le dossier, on verifie que le chemin est correct.
        validateDirectory(rootDirectory);

        // Files.walk permet de parcourir recursivement tous les sous-dossiers.
        try (Stream<Path> stream = Files.walk(rootDirectory)) {
            return stream.filter(Files::isRegularFile)
                    .filter(ImageMimeUtils::isSupportedImage)
                    .sorted(Comparator.comparing(path -> path.toAbsolutePath().toString().toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'explorer le dossier : " + e.getMessage(), e);
        }
    }

    /**
     * Construit un rapport texte pour les images detectees dans un dossier.
     *
     * @param rootDirectory dossier racine a analyser.
     * @param includeMetadata {@code true} pour ajouter les metadonnees de chaque image au rapport.
     * @return rapport lisible contenant le nombre d'images et leurs informations.
     */
    public String buildDirectoryReport(Path rootDirectory, boolean includeMetadata) {
        validateDirectory(rootDirectory);
        List<Path> images = listImageFiles(rootDirectory);

        StringBuilder builder = new StringBuilder();
        builder.append("Dossier explore : ").append(rootDirectory.toAbsolutePath()).append(System.lineSeparator());
        builder.append("Nombre d'images trouvees : ").append(images.size()).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        // Si aucune image compatible n'est trouvee, le rapport reste explicite.
        if (images.isEmpty()) {
            builder.append("Aucune image PNG/JPG/JPEG detectee par MIME.")
                    .append(System.lineSeparator());
            return builder.toString();
        }

        for (Path image : images) {
            builder.append("- ").append(image.toAbsolutePath())
                    .append(" [").append(ImageMimeUtils.describe(image)).append("]")
                    .append(System.lineSeparator());

            if (includeMetadata) {
                builder.append(metadataService.extract(image)).append(System.lineSeparator());
            }
        }

        return builder.toString();
    }

    /**
     * Verifie que le chemin fourni correspond a un dossier existant.
     *
     * @param rootDirectory chemin a valider.
     * @throws IllegalArgumentException si le chemin est nul, inexistant ou n'est pas un dossier.
     */
    private void validateDirectory(Path rootDirectory) {
        if (rootDirectory == null) {
            throw new IllegalArgumentException("Le chemin du dossier est requis.");
        }
        if (!Files.exists(rootDirectory)) {
            throw new IllegalArgumentException("Le dossier n'existe pas : " + rootDirectory);
        }
        if (!Files.isDirectory(rootDirectory)) {
            throw new IllegalArgumentException("Le chemin fourni n'est pas un dossier : " + rootDirectory);
        }
    }
}
