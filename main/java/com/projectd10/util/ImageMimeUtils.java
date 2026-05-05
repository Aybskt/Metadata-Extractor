package com.projectd10.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * Utilitaire de detection et de validation des types MIME d'images.
 * <p>
 * Cette classe sert a identifier de facon fiable les images PNG, JPG et JPEG, a valider les fichiers
 * fournis par l'utilisateur et a construire le filtre utilise par les selecteurs de fichiers Swing.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public final class ImageMimeUtils {
    private static final Set<String> SUPPORTED_MIME_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/pjpeg"
    );

    /**
     * Constructeur prive pour empecher l'instanciation de cette classe utilitaire.
     */
    private ImageMimeUtils() {
    }

    /**
     * Detecte le type MIME d'un fichier image.
     * <p>
     * La methode tente d'abord la detection systeme, puis la detection via ImageIO, puis une
     * verification par extension en dernier recours.
     * </p>
     *
     * @param path chemin du fichier a analyser.
     * @return type MIME normalise, ou {@code null} si le fichier n'est pas reconnu.
     */
    public static String probeMimeType(Path path) {
        if (path == null || !Files.exists(path) || Files.isDirectory(path)) {
            return null;
        }

        try {
            // Premier essai : detection MIME fournie par le systeme.
            String mime = Files.probeContentType(path);
            if (isSupportedMime(mime)) {
                return normalizeMime(mime);
            }
        } catch (IOException ignored) {
        }

        // Deuxieme essai : detection du format par ImageIO.
        try (ImageInputStream stream = ImageIO.createImageInputStream(path.toFile())) {
            if (stream != null) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
                if (readers.hasNext()) {
                    String format = readers.next().getFormatName();
                    String normalized = format == null ? "" : format.toLowerCase(Locale.ROOT).trim();
                    if ("png".equals(normalized)) {
                        return "image/png";
                    }
                    if ("jpg".equals(normalized) || "jpeg".equals(normalized)) {
                        return "image/jpeg";
                    }
                }
            }
        } catch (IOException ignored) {
        }

        // Dernier recours : verification de l'extension du fichier.
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        return null;
    }

    /**
     * Indique si un chemin correspond a une image supportee.
     *
     * @param path chemin du fichier a tester.
     * @return {@code true} si le fichier est une image PNG, JPG ou JPEG reconnue.
     */
    public static boolean isSupportedImage(Path path) {
        return isSupportedMime(probeMimeType(path));
    }

    /**
     * Indique si un type MIME est accepte par l'application.
     *
     * @param mime type MIME a verifier.
     * @return {@code true} si le type MIME est supporte.
     */
    public static boolean isSupportedMime(String mime) {
        if (mime == null || mime.isBlank()) {
            return false;
        }
        return SUPPORTED_MIME_TYPES.contains(normalizeMime(mime));
    }

    /**
     * Valide qu'un chemin pointe vers une image supportee existante.
     *
     * @param imagePath chemin de l'image a valider.
     * @throws IllegalArgumentException si le chemin est nul, inexistant, non fichier ou non supporte.
     */
    public static void validateSupportedImage(Path imagePath) {
        if (imagePath == null) {
            throw new IllegalArgumentException("Le chemin de l'image est requis.");
        }
        if (!Files.exists(imagePath)) {
            throw new IllegalArgumentException("Le fichier n'existe pas : " + imagePath);
        }
        if (!Files.isRegularFile(imagePath)) {
            throw new IllegalArgumentException("Le chemin fourni n'est pas un fichier : " + imagePath);
        }
        if (!isSupportedImage(imagePath)) {
            throw new IllegalArgumentException("Seules les images PNG, JPG et JPEG sont acceptees (verification MIME).");
        }
    }

    /**
     * Retourne une description courte du type MIME d'un fichier.
     *
     * @param path chemin du fichier a decrire.
     * @return type MIME reconnu, ou {@code "type inconnu"}.
     */
    public static String describe(Path path) {
        String mime = probeMimeType(path);
        return mime == null ? "type inconnu" : mime;
    }

    /**
     * Cree le filtre utilise par les boites de dialogue de selection de fichiers.
     *
     * @return filtre Swing acceptant les dossiers et les images supportees.
     */
    public static FileFilter createChooserFilter() {
        return new FileFilter() {
            /**
             * Accepte les dossiers pour permettre la navigation et les fichiers image compatibles.
             *
             * @param file fichier teste par le selecteur.
             * @return {@code true} si le fichier doit etre affiché.
             */
            @Override
            public boolean accept(java.io.File file) {
                if (file == null) {
                    return false;
                }
                if (file.isDirectory()) {
                    return true;
                }
                return isSupportedImage(file.toPath());
            }

            /**
             * Retourne le libelle affiche dans le selecteur de fichiers.
             *
             * @return description du filtre.
             */
            @Override
            public String getDescription() {
                return "Images PNG / JPG / JPEG";
            }
        };
    }

    /**
     * Normalise un type MIME afin de comparer correctement les variantes JPG/JPEG.
     *
     * @param mime type MIME brut.
     * @return type MIME en minuscules et standardise.
     */
    private static String normalizeMime(String mime) {
        String normalized = mime.toLowerCase(Locale.ROOT).trim();
        if ("image/jpg".equals(normalized)) {
            return "image/jpeg";
        }
        return normalized;
    }
}
