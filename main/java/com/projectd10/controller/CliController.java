package com.projectd10.controller;

import com.projectd10.model.DirectoryService;
import com.projectd10.model.MetadataService;
import com.projectd10.model.SteganographyService;

import java.nio.file.Path;

/**
 * Controleur du mode ligne de commande.
 * <p>
 * Cette classe sert a interpreter les arguments passes au programme et a declencher les actions
 * correspondantes : afficher l'aide, extraire les metadonnees d'une image, parcourir un dossier,
 * encoder un message ou decoder un message cache.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class CliController {
    private final MetadataService metadataService;
    private final DirectoryService directoryService;
    private final SteganographyService steganographyService;

    /**
     * Construit le controleur CLI avec les services necessaires aux traitements.
     *
     * @param metadataService service charge de l'extraction des metadonnees.
     * @param directoryService service charge de l'exploration des dossiers.
     * @param steganographyService service charge de l'encodage et du decodage des messages.
     */
    public CliController(MetadataService metadataService,
                         DirectoryService directoryService,
                         SteganographyService steganographyService) {
        this.metadataService = metadataService;
        this.directoryService = directoryService;
        this.steganographyService = steganographyService;
    }

    /**
     * Execute l'action demandee par l'utilisateur en fonction de la premiere option CLI.
     *
     * @param args arguments fournis au lancement du programme.
     */
    public void run(String[] args) {
        if (args == null || args.length == 0) {
            printHelp();
            return;
        }

        try {
            // La premiere option determine l'action a executer.
            switch (args[0]) {
                case "-h", "--help" -> printHelp();
                case "-f" -> handleMetadata(args);
                case "-d" -> handleDirectory(args);
                case "-s" -> handleEncode(args);
                case "-e" -> handleDecode(args);
                default -> {
                    System.err.println("Option inconnue : " + args[0]);
                    printHelp();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Affiche l'aide du mode ligne de commande dans la console.
     */
    private void printHelp() {
        System.out.println("""
                Options disponibles :
                  -h | --help
                      Affiche l'aide

                  -f <chemin_image>
                      Affiche les metadonnees d'une image PNG/JPG/JPEG
                      Validation du type d'image via MIME

                  -d <chemin_dossier>
                      Explore recursivement un dossier et liste uniquement
                      les images PNG/JPG/JPEG detectees via MIME

                  -s <chemin_image_source> <message> [chemin_image_sortie.png]
                      Encode un message dans une image
                      La sortie est toujours en PNG

                  -e <chemin_image_encodee>
                      Decode automatiquement un message cache
                """);
    }

    /**
     * Traite l'option d'extraction des metadonnees pour une seule image.
     *
     * @param args arguments attendus : {@code -f <chemin_image>}.
     */
    private void handleMetadata(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage : -f <chemin_image>");
        }
        System.out.println(metadataService.extract(Path.of(args[1])));
    }

    /**
     * Traite l'option d'analyse recursive d'un dossier d'images.
     *
     * @param args arguments attendus : {@code -d <chemin_dossier>}.
     */
    private void handleDirectory(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage : -d <chemin_dossier>");
        }
        System.out.println(directoryService.buildDirectoryReport(Path.of(args[1]), true));
    }

    /**
     * Traite l'option d'encodage d'un message dans une image.
     *
     * @param args arguments attendus : {@code -s <image_source> <message> [image_sortie.png]}.
     */
    private void handleEncode(String[] args) {
        if (args.length < 3 || args.length > 4) {
            throw new IllegalArgumentException("Usage : -s <chemin_image_source> <message> [chemin_image_sortie.png]");
        }

        Path output = args.length == 4 ? Path.of(args[3]) : null;
        Path created = steganographyService.encodeMessage(Path.of(args[1]), args[2], output);
        System.out.println("Message encode avec succes : " + created);
    }

    /**
     * Traite l'option de decodage d'un message cache dans une image.
     *
     * @param args arguments attendus : {@code -e <image_encodee>}.
     */
    private void handleDecode(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage : -e <chemin_image_encodee>");
        }

        System.out.println(steganographyService.decodeMessage(Path.of(args[1])));
    }
}
