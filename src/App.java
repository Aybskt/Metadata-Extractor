package com.projectd10;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.projectd10.controller.CliController;
import com.projectd10.model.DirectoryService;
import com.projectd10.model.MetadataService;
import com.projectd10.model.SteganographyService;
import com.projectd10.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Point d'entree principal de l'application.
 * <p>
 * Cette classe sert a demarrer le programme en mode ligne de commande ou en mode graphique.
 * Elle initialise les services metier, configure l'apparence FlatLaf de Swing, puis ouvre la
 * fenetre principale lorsque l'utilisateur ne fournit aucun argument CLI.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public final class App {
    /**
     * Constructeur prive pour empecher l'instanciation de cette classe utilitaire.
     */
    private App() {
    }

    /**
     * Lance l'application.
     * <p>
     * Si des arguments sont transmis, l'application passe en mode CLI. Sinon, elle applique
     * le theme graphique et ouvre la fenetre Swing principale.
     * </p>
     *
     * @param args arguments de la ligne de commande, utilises pour le mode CLI.
     */
    public static void main(String[] args) {
        // Creation des services metier partages entre le mode CLI et le mode graphique.
        MetadataService metadataService = new MetadataService();
        DirectoryService directoryService = new DirectoryService(metadataService);
        SteganographyService steganographyService = new SteganographyService();

        // Si des arguments sont fournis, on execute le programme en ligne de commande.
        if (args != null && args.length > 0) {
            new CliController(metadataService, directoryService, steganographyService).run(args);
            return;
        }

        // Sans arguments, on prepare le theme graphique puis on ouvre la fenetre principale.
        setupLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(metadataService, directoryService, steganographyService);
            frame.setVisible(true);
        });
    }

    /**
     * Configure l'apparence globale de l'application Swing.
     * <p>
     * Cette methode centralise les couleurs, les polices, les arrondis, la taille des barres
     * de defilement et les styles par defaut pour obtenir une interface sombre, propre et homogene.
     * </p>
     */
    private static void setupLookAndFeel() {
        FlatDarkLaf.setup();

        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.width", 9);
        UIManager.put("Tree.rowHeight", 24);
        UIManager.put("TitlePane.showIcon", true);

        UIManager.put("Panel.background", new Color(31, 34, 39));
        UIManager.put("RootPane.background", new Color(31, 34, 39));

        UIManager.put("Label.foreground", new Color(235, 238, 244));

        UIManager.put("Button.background", new Color(72, 108, 245));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.hoverBackground", new Color(86, 121, 255));
        UIManager.put("Button.pressedBackground", new Color(56, 87, 210));
        UIManager.put("Button.focusedBorderColor", new Color(72, 108, 245));

        UIManager.put("TextField.background", new Color(50, 54, 61));
        UIManager.put("TextField.foreground", new Color(240, 243, 248));
        UIManager.put("TextField.caretForeground", new Color(240, 243, 248));
        UIManager.put("TextField.placeholderForeground", new Color(155, 163, 176));

        UIManager.put("TextArea.background", new Color(37, 41, 47));
        UIManager.put("TextArea.foreground", new Color(240, 243, 248));
        UIManager.put("TextArea.caretForeground", new Color(240, 243, 248));

        UIManager.put("Tree.background", new Color(37, 41, 47));
        UIManager.put("Tree.foreground", new Color(240, 243, 248));
        UIManager.put("Tree.selectionBackground", new Color(64, 82, 140));
        UIManager.put("Tree.selectionForeground", Color.WHITE);

        UIManager.put("ScrollPane.border", null);
        UIManager.put("SplitPane.background", new Color(31, 34, 39));

        FlatLaf.updateUI();
    }
}