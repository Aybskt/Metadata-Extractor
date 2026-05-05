package com.projectd10.model;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.projectd10.util.ImageMimeUtils;

import java.nio.file.Path;

/**
 * Service d'extraction des metadonnees d'image.
 * <p>
 * Cette classe sert a valider une image, lire ses metadonnees avec la bibliotheque metadata-extractor
 * et retourner un rapport texte exploitable dans l'interface graphique ou dans le mode CLI.
 * </p>
 *
 * @author Ayoub ABDELLI
 * @version 1.0
 * @since 20-09-2025
 */
public class MetadataService {

    /**
     * Extrait les metadonnees d'une image compatible.
     *
     * @param imagePath chemin de l'image a analyser.
     * @return texte contenant le chemin, le type MIME et les metadonnees trouvees.
     * @throws IllegalArgumentException si l'image est invalide ou si l'extraction echoue.
     */
    public String extract(Path imagePath) {
        // On refuse les fichiers non supportes avant de lancer l'extraction.
        ImageMimeUtils.validateSupportedImage(imagePath);

        StringBuilder builder = new StringBuilder();
        builder.append("Fichier : ").append(imagePath.toAbsolutePath()).append(System.lineSeparator());
        builder.append("Type MIME : ").append(ImageMimeUtils.describe(imagePath)).append(System.lineSeparator());
        builder.append(System.lineSeparator());

        try {
            // La bibliotheque metadata-extractor lit les repertoires et tags internes de l'image.
            Metadata metadata = ImageMetadataReader.readMetadata(imagePath.toFile());
            boolean hasMetadata = false;

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    hasMetadata = true;
                    builder.append("[")
                            .append(directory.getName())
                            .append("] ")
                            .append(tag.getTagName())
                            .append(" = ")
                            .append(tag.getDescription())
                            .append(System.lineSeparator());
                }

                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        builder.append("[ERREUR ")
                                .append(directory.getName())
                                .append("] ")
                                .append(error)
                                .append(System.lineSeparator());
                    }
                }
            }

            if (!hasMetadata) {
                builder.append("Aucune metadonnee lisible n'a ete trouvee.")
                        .append(System.lineSeparator());
            }

            return builder.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Impossible d'extraire les metadonnees : " + e.getMessage(), e);
        }
    }

    /**
     * Verifie qu'un chemin pointe vers une image PNG, JPG ou JPEG valide.
     *
     * @param imagePath chemin de l'image a verifier.
     * @throws IllegalArgumentException si le fichier n'est pas une image acceptee.
     */
    public void validateSupportedImage(Path imagePath) {
        ImageMimeUtils.validateSupportedImage(imagePath);
    }
}
