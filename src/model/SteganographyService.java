package com.projectd10.model;

import com.projectd10.util.ImageMimeUtils;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Service de steganographie pour les images.
 * <p>
 * Cette classe sert a cacher un message texte dans les bits de poids faible du canal bleu d'une
 * image, puis a relire ce message depuis une image encodee. La sortie d'encodage est toujours
 * produite au format PNG afin d'eviter les pertes dues a la compression JPEG.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class SteganographyService {

    /**
     * Encode un message dans une copie PNG de l'image source.
     *
     * @param sourceImagePath chemin de l'image source.
     * @param message message a cacher dans l'image.
     * @param outputImagePath chemin de sortie souhaite, ou {@code null} pour generer un nom automatique.
     * @return chemin absolu de l'image encodee creee.
     * @throws IllegalArgumentException si les parametres sont invalides ou si l'image est trop petite.
     * @throws IllegalStateException si l'image encodee ne peut pas etre enregistree.
     */
    public Path encodeMessage(Path sourceImagePath, String message, Path outputImagePath) {
        ImageMimeUtils.validateSupportedImage(sourceImagePath);

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Le message a encoder est requis.");
        }

        // Le message est converti en UTF-8 pour conserver correctement les caracteres speciaux.
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        BufferedImage image = readImage(sourceImagePath);

        // Les 32 premiers bits stockent la taille du message, puis viennent les bits du message.
        int requiredBits = 32 + (messageBytes.length * 8);
        int capacityBits = image.getWidth() * image.getHeight();
        if (requiredBits > capacityBits) {
            throw new IllegalArgumentException("Image trop petite. Capacite = " + capacityBits + " bits, requis = " + requiredBits + " bits.");
        }

        BufferedImage copy = copyImage(image);
        int bitIndex = 0;

        // Encodage de la longueur du message sur 32 bits.
        for (int i = 31; i >= 0; i--) {
            int bit = (messageBytes.length >> i) & 1;
            writeBlueLsb(copy, bitIndex++, bit);
        }

        // Encodage du contenu du message, bit par bit.
        for (byte value : messageBytes) {
            for (int i = 7; i >= 0; i--) {
                int bit = (value >> i) & 1;
                writeBlueLsb(copy, bitIndex++, bit);
            }
        }

        Path output = normalizeOutputPath(sourceImagePath, outputImagePath);

        try {
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            ImageIO.write(copy, "png", output.toFile());
            return output;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer l'image encodee : " + e.getMessage(), e);
        }
    }

    /**
     * Decode un message cache dans une image encodee par l'application.
     *
     * @param encodedImagePath chemin de l'image contenant le message.
     * @return message texte decode en UTF-8.
     * @throws IllegalArgumentException si l'image ne contient pas de message exploitable.
     */
    public String decodeMessage(Path encodedImagePath) {
        ImageMimeUtils.validateSupportedImage(encodedImagePath);
        BufferedImage image = readImage(encodedImagePath);
        int capacityBits = image.getWidth() * image.getHeight();

        if (capacityBits < 32) {
            throw new IllegalArgumentException("Image invalide : capacite insuffisante.");
        }

        // Lecture des 32 premiers bits pour connaitre la taille du message cache.
        int length = 0;
        for (int i = 0; i < 32; i++) {
            length = (length << 1) | readBlueLsb(image, i);
        }

        if (length <= 0) {
            throw new IllegalArgumentException("Cette image ne contient aucun message.");
        }

        int requiredBits = 32 + (length * 8);
        if (requiredBits > capacityBits) {
            throw new IllegalArgumentException("Image non encodee avec cette application ou message corrompu.");
        }

        byte[] bytes = new byte[length];
        int bitIndex = 32;

        for (int i = 0; i < length; i++) {
            int value = 0;
            for (int j = 0; j < 8; j++) {
                value = (value << 1) | readBlueLsb(image, bitIndex++);
            }
            bytes[i] = (byte) value;
        }

        String message = new String(bytes, StandardCharsets.UTF_8);
        if (message.isBlank()) {
            throw new IllegalArgumentException("Cette image ne contient aucun message exploitable.");
        }
        return message;
    }

    /**
     * Lit une image depuis le disque et verifie qu'elle est exploitable par Java.
     *
     * @param imagePath chemin de l'image a lire.
     * @return image chargee en memoire.
     */
    private BufferedImage readImage(Path imagePath) {
        try {
            BufferedImage image = ImageIO.read(imagePath.toFile());
            if (image == null) {
                throw new IllegalArgumentException("Le fichier ne peut pas etre lu comme image.");
            }
            return image;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire l'image : " + e.getMessage(), e);
        }
    }

    /**
     * Cree une copie modifiable de l'image source.
     *
     * @param source image originale.
     * @return copie independante de l'image.
     */
    private BufferedImage copyImage(BufferedImage source) {
        int type = source.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), type);
        Graphics2D g2 = copy.createGraphics();
        try {
            g2.drawImage(source, 0, 0, null);
        } finally {
            g2.dispose();
        }
        return copy;
    }

    /**
     * Ecrit un bit dans le bit de poids faible du canal bleu d'un pixel.
     *
     * @param image image a modifier.
     * @param linearBitIndex index lineaire du bit a ecrire dans l'image.
     * @param bit valeur du bit a ecrire, 0 ou 1.
     */
    private void writeBlueLsb(BufferedImage image, int linearBitIndex, int bit) {
        int width = image.getWidth();
        int x = linearBitIndex % width;
        int y = linearBitIndex / width;

        int rgb = image.getRGB(x, y);
        int blue = rgb & 0xFF;
        blue = (blue & 0xFE) | bit;
        image.setRGB(x, y, (rgb & 0xFFFFFF00) | blue);
    }

    /**
     * Lit un bit depuis le bit de poids faible du canal bleu d'un pixel.
     *
     * @param image image encodee.
     * @param linearBitIndex index lineaire du bit a lire.
     * @return bit lu, 0 ou 1.
     */
    private int readBlueLsb(BufferedImage image, int linearBitIndex) {
        int width = image.getWidth();
        int x = linearBitIndex % width;
        int y = linearBitIndex / width;
        int rgb = image.getRGB(x, y);
        return (rgb & 0xFF) & 1;
    }

    /**
     * Determine et valide le chemin de sortie de l'image encodee.
     *
     * @param sourceImagePath chemin de l'image source, utilise pour generer un nom automatique.
     * @param outputImagePath chemin demande par l'utilisateur, ou {@code null}.
     * @return chemin absolu normalise de sortie.
     */
    private Path normalizeOutputPath(Path sourceImagePath, Path outputImagePath) {
        if (outputImagePath != null) {
            String lower = outputImagePath.getFileName().toString().toLowerCase(Locale.ROOT);
            if (!lower.endsWith(".png")) {
                throw new IllegalArgumentException("L'image encodee doit etre en .png.");
            }
            return outputImagePath.toAbsolutePath().normalize();
        }

        String fileName = sourceImagePath.getFileName().toString();
        int index = fileName.lastIndexOf('.');
        String baseName = index >= 0 ? fileName.substring(0, index) : fileName;
        Path parent = sourceImagePath.getParent();

        if (parent == null) {
            return Path.of(baseName + "_encoded.png").toAbsolutePath().normalize();
        }

        return parent.resolve(baseName + "_encoded.png").toAbsolutePath().normalize();
    }
}
