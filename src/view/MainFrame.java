package com.projectd10.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.projectd10.model.DirectoryService;
import com.projectd10.model.MetadataService;
import com.projectd10.model.SteganographyService;
import com.projectd10.util.ImageMimeUtils;
import com.projectd10.view.components.ImagePreviewPanel;
import com.projectd10.view.components.RoundedPanel;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Fenetre principale de l'application.
 *
 * <p>
 * Cette classe represente l'interface graphique principale de l'application
 * Metadata Extractor &amp; Steganography. Elle permet a l'utilisateur de selectionner
 * une image ou un dossier, d'extraire les metadonnees d'une image, d'afficher
 * un apercu de l'image selectionnee et d'utiliser les fonctionnalites de
 * steganographie pour encoder ou decoder un message dans une image.
 * </p>
 *
 * <p>
 * La classe utilise Swing pour la construction de l'interface graphique et
 * FlatLaf pour appliquer un style moderne et sombre. Elle s'appuie sur plusieurs
 * services metier :
 * </p>
 *
 * <ul>
 *     <li>{@link MetadataService} pour valider et extraire les metadonnees.</li>
 *     <li>{@link DirectoryService} pour rechercher les images dans un dossier.</li>
 *     <li>{@link SteganographyService} pour encoder et decoder des messages.</li>
 * </ul>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    /*
     * Couleurs principales de l'interface.
     * Elles permettent de garder un style coherent dans toute la fenetre.
     */
    private static final Color BACKGROUND = new Color(31, 34, 39);
    private static final Color CARD = new Color(42, 46, 53);
    private static final Color CARD_DARK = new Color(36, 40, 46);
    private static final Color INPUT = new Color(50, 54, 61);
    private static final Color PRIMARY = new Color(72, 108, 245);
    private static final Color TEXT = new Color(238, 241, 246);
    private static final Color MUTED = new Color(165, 173, 186);
    private static final Color BORDER = new Color(255, 255, 255, 16);
    private static final Color SELECTION = new Color(64, 82, 140);

    /*
     * Services metier utilises par l'interface.
     * L'interface ne fait pas directement le traitement : elle delegue aux services.
     */
    private final MetadataService metadataService;
    private final DirectoryService directoryService;
    private final SteganographyService steganographyService;

    /*
     * Composants graphiques principaux.
     */
    private final JTextField pathField = new JTextField();
    private final JButton browseAnalyzeButton = new JButton("Explorer / Extraire");

    private final JTree imageTree = new JTree(new DefaultMutableTreeNode("Aucune image"));
    private final JTextArea metadataArea = new JTextArea();
    private final ImagePreviewPanel imagePreviewPanel = new ImagePreviewPanel();
    private final JLabel statusLabel = new JLabel("");
    private final JLabel fileInfoLabel = new JLabel("Aucun fichier selectionne");

    /*
     * Etat courant de l'application.
     */
    private Path currentDirectory;
    private Path currentImage;

    /**
     * Construit la fenetre principale de l'application.
     *
     * <p>
     * Le constructeur recoit les services necessaires au fonctionnement de
     * l'application, les stocke dans des attributs, puis initialise l'interface
     * graphique.
     * </p>
     *
     * @param metadataService service charge de valider les images et d'extraire leurs metadonnees
     * @param directoryService service charge de lister les images presentes dans un dossier
     * @param steganographyService service charge de l'encodage et du decodage de messages dans les images
     */
    public MainFrame(MetadataService metadataService,
                     DirectoryService directoryService,
                     SteganographyService steganographyService) {
        super("Metadata Extractor & Steganography");
        this.metadataService = metadataService;
        this.directoryService = directoryService;
        this.steganographyService = steganographyService;
        initUi();
    }

    /**
     * Initialise l'interface graphique principale.
     *
     * <p>
     * Cette methode configure la taille de la fenetre, sa position, son layout
     * principal, puis ajoute les trois zones principales :
     * </p>
     *
     * <ul>
     *     <li>la barre du haut pour saisir ou selectionner un chemin ;</li>
     *     <li>la zone centrale avec l'arborescence, l'apercu et les metadonnees ;</li>
     *     <li>la barre du bas avec les actions de steganographie.</li>
     * </ul>
     */
    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Taille de lancement de l'application.
        setMinimumSize(new Dimension(820, 560));
        setSize(980, 620);
        setLocationRelativeTo(null);

        // Panneau racine de toute la fenetre.
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMainArea(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);

        setContentPane(root);

        // Application du logo de la fenetre.
        setIcons();
    }

    /**
     * Charge et applique l'icone de l'application.
     *
     * <p>
     * L'icone doit etre placee dans le dossier :
     * {@code src/main/resources/images/logo.png}.
     * Lors de l'execution, elle est chargee via le classpath avec le chemin
     * {@code /images/logo.png}.
     * </p>
     */
    private void setIcons() {
        try {
            java.net.URL iconUrl = getClass().getResource("/resources/images/logo.png");

            if (iconUrl == null) {
                System.err.println("Logo introuvable : /resources/images/logo.png");
                return;
            }

            BufferedImage iconImage = ImageIO.read(iconUrl);
            setIconImage(iconImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construit la barre superieure de l'application.
     *
     * <p>
     * Cette barre contient le champ de saisie du chemin et le bouton
     * {@code Explorer / Extraire}. Si le champ est vide, le bouton ouvre
     * l'explorateur de fichiers. Si un chemin valide est deja saisi, il lance
     * directement l'analyse.
     * </p>
     *
     * @return le panneau Swing representant la barre superieure
     */
    private JPanel buildTopBar() {
        RoundedPanel panel = new RoundedPanel(CARD, 16, BORDER);
        panel.setLayout(new BorderLayout(10, 0));
        panel.setBorder(new EmptyBorder(8, 10, 8, 10));
        panel.setPreferredSize(new Dimension(0, 54));

        styleMainField(pathField, "Veuillez ecrire le chemin ou ouvrir l'exploration avec le bouton");
        stylePrimaryButton(browseAnalyzeButton);
        browseAnalyzeButton.setPreferredSize(new Dimension(160, 34));

        // Appuyer sur Entree dans le champ lance l'analyse du chemin.
        pathField.addActionListener(e -> analyzeFromField());

        // Le bouton decide automatiquement s'il doit ouvrir l'explorateur ou analyser.
        browseAnalyzeButton.addActionListener(e -> handleBrowseAnalyzeAction());

        panel.add(pathField, BorderLayout.CENTER);
        panel.add(browseAnalyzeButton, BorderLayout.EAST);

        return panel;
    }

    /**
     * Gere le comportement du bouton {@code Explorer / Extraire}.
     *
     * <p>
     * Si le champ contient un chemin valide, l'application extrait directement
     * les metadonnees. Si le champ est vide, elle ouvre l'explorateur de fichiers.
     * Si le chemin est invalide, un message d'erreur est affiche.
     * </p>
     */
    private void handleBrowseAnalyzeAction() {
        String input = pathField.getText().trim();

        if (!input.isBlank()) {
            Path path = Path.of(input);

            if (Files.exists(path)) {
                analyzeFromField();
                return;
            }

            showError("Chemin invalide", "Le chemin saisi n'existe pas.");
            return;
        }

        openPathChooser();
    }

    /**
     * Construit la zone centrale principale.
     *
     * <p>
     * Elle contient une separation horizontale entre l'arborescence a gauche
     * et la zone de droite contenant l'apercu et les metadonnees.
     * </p>
     *
     * @return le panneau central principal
     */
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setOpaque(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildTreeCard(), buildRightArea());
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setResizeWeight(0.22);
        splitPane.setDividerSize(6);
        splitPane.setContinuousLayout(true);

        main.add(splitPane, BorderLayout.CENTER);

        return main;
    }

    /**
     * Construit la partie droite de l'interface.
     *
     * <p>
     * Cette zone contient deux parties verticales :
     * l'apercu de l'image en haut et les metadonnees en bas.
     * </p>
     *
     * @return le panneau droit de l'interface
     */
    private JPanel buildRightArea() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildPreviewCard(), buildMetadataCard());
        rightSplit.setOpaque(false);
        rightSplit.setBorder(null);
        rightSplit.setResizeWeight(0.48);
        rightSplit.setDividerSize(6);
        rightSplit.setContinuousLayout(true);

        right.add(rightSplit, BorderLayout.CENTER);

        return right;
    }

    /**
     * Construit la carte contenant l'arborescence des images.
     *
     * <p>
     * L'arborescence affiche les images detectees dans un dossier. Lorsqu'une
     * image est selectionnee, ses metadonnees et son apercu sont affiches.
     * </p>
     *
     * @return une carte arrondie contenant le JTree
     */
    private RoundedPanel buildTreeCard() {
        RoundedPanel card = createCard();
        card.setLayout(new BorderLayout(8, 8));

        JLabel title = createTitle("Arborescence");

        imageTree.setRootVisible(true);
        imageTree.setShowsRootHandles(true);
        imageTree.setRowHeight(24);
        imageTree.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        imageTree.setBackground(CARD_DARK);
        imageTree.setForeground(TEXT);
        imageTree.setCellRenderer(createTreeRenderer());
        imageTree.addTreeSelectionListener(this::onTreeSelected);

        JScrollPane scrollPane = createScrollPane(imageTree);

        card.add(title, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Construit la carte d'apercu de l'image.
     *
     * <p>
     * Cette carte affiche le titre, le nom du fichier selectionne, son type MIME
     * et l'image elle-meme via {@link ImagePreviewPanel}.
     * </p>
     *
     * @return une carte arrondie contenant l'apercu de l'image
     */
    private RoundedPanel buildPreviewCard() {
        RoundedPanel card = createCard();
        card.setLayout(new BorderLayout(8, 8));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);

        JLabel title = createTitle("Contenu de l'image");

        fileInfoLabel.setForeground(MUTED);
        fileInfoLabel.setFont(fileInfoLabel.getFont().deriveFont(Font.PLAIN, 12f));
        fileInfoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(fileInfoLabel, BorderLayout.EAST);

        // Repeint l'apercu lorsque la zone change de taille.
        imagePreviewPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                imagePreviewPanel.repaint();
            }
        });

        card.add(titleBar, BorderLayout.NORTH);
        card.add(imagePreviewPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Construit la carte affichant les metadonnees.
     *
     * <p>
     * Les metadonnees extraites sont affichees dans une zone de texte non
     * editable afin d'eviter les modifications accidentelles.
     * </p>
     *
     * @return une carte arrondie contenant la zone de metadonnees
     */
    private RoundedPanel buildMetadataCard() {
        RoundedPanel card = createCard();
        card.setLayout(new BorderLayout(8, 8));

        JLabel title = createTitle("Metadonnees");

        metadataArea.setEditable(false);
        metadataArea.setLineWrap(false);
        metadataArea.setWrapStyleWord(false);
        metadataArea.setOpaque(true);
        metadataArea.setForeground(TEXT);
        metadataArea.setBackground(new Color(32, 36, 42));
        metadataArea.setCaretColor(TEXT);
        metadataArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        metadataArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        metadataArea.setText("Le resultat d'analyse s'affichera ici.");

        card.add(title, BorderLayout.NORTH);
        card.add(createScrollPane(metadataArea), BorderLayout.CENTER);

        return card;
    }

    /**
     * Construit la barre inferieure de l'application.
     *
     * <p>
     * Cette barre contient les actions de steganographie : encoder un message
     * dans une image et decoder un message depuis une image.
     * </p>
     *
     * @return le panneau inferieur contenant les boutons de steganographie
     */
    private JPanel buildBottomBar() {
        RoundedPanel panel = new RoundedPanel(CARD, 16, BORDER);
        panel.setLayout(new BorderLayout(12, 0));
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));
        panel.setPreferredSize(new Dimension(0, 56));

        JLabel section = new JLabel("Steganographie");
        section.setForeground(TEXT);
        section.setFont(section.getFont().deriveFont(Font.PLAIN, 15f));

        JPanel actionsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actionsWrapper.setOpaque(false);

        JButton encodeButton = new JButton("Encoder");
        JButton decodeButton = new JButton("Decoder");

        stylePrimaryButton(encodeButton);
        stylePrimaryButton(decodeButton);

        encodeButton.setPreferredSize(new Dimension(118, 32));
        decodeButton.setPreferredSize(new Dimension(118, 32));

        encodeButton.addActionListener(e -> openEncodeDialog());
        decodeButton.addActionListener(e -> openDecodeDialog());

        actionsWrapper.add(encodeButton);
        actionsWrapper.add(decodeButton);

        statusLabel.setForeground(MUTED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        statusLabel.setPreferredSize(new Dimension(180, 28));

        panel.add(section, BorderLayout.WEST);
        panel.add(actionsWrapper, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Cree une carte graphique arrondie utilisee dans l'interface.
     *
     * @return une instance de {@link RoundedPanel} avec le style de carte standard
     */
    private RoundedPanel createCard() {
        RoundedPanel card = new RoundedPanel(CARD_DARK, 16, BORDER);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        return card;
    }

    /**
     * Cree un titre de section avec le style visuel de l'application.
     *
     * @param text texte du titre
     * @return le label configure
     */
    private JLabel createTitle(String text) {
        JLabel title = new JLabel(text);
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 15f));
        return title;
    }

    /**
     * Applique le style principal aux champs de texte.
     *
     * @param field champ de texte a styliser
     * @param placeholder texte indicatif affiche lorsque le champ est vide
     */
    private void styleMainField(JTextField field, String placeholder) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;" +
                        "borderWidth:0;" +
                        "background:#32363D;" +
                        "foreground:#EEF1F6;" +
                        "innerFocusWidth:0;" +
                        "focusWidth:1;" +
                        "focusedBorderColor:#486CF5");

        field.setBorder(new EmptyBorder(6, 12, 6, 12));
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 13f));
        field.setPreferredSize(new Dimension(0, 30));
        field.setMinimumSize(new Dimension(0, 30));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    /**
     * Applique le style aux champs de texte utilises dans les boites de dialogue.
     *
     * @param field champ de texte a styliser
     * @param placeholder texte indicatif du champ
     */
    private void styleDialogField(JTextField field, String placeholder) {
        styleMainField(field, placeholder);
    }

    /**
     * Applique le style principal aux boutons importants.
     *
     * @param button bouton a styliser
     */
    private void stylePrimaryButton(AbstractButton button) {
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;" +
                        "borderWidth:0;" +
                        "background:#486CF5;" +
                        "foreground:#FFFFFF;" +
                        "hoverBackground:#5679FF;" +
                        "pressedBackground:#3857D2;" +
                        "focusWidth:0");

        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 12f));
    }

    /**
     * Cree un panneau defilant sans bordure visible.
     *
     * @param component composant a placer dans le panneau defilant
     * @return le {@link JScrollPane} configure
     */
    private JScrollPane createScrollPane(java.awt.Component component) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.getViewport().setOpaque(false);
        pane.setOpaque(false);
        pane.getVerticalScrollBar().setUnitIncrement(14);
        pane.getHorizontalScrollBar().setUnitIncrement(14);
        return pane;
    }

    /**
     * Cree le rendu graphique personnalise des elements de l'arborescence.
     *
     * @return le renderer configure pour le {@link JTree}
     */
    private DefaultTreeCellRenderer createTreeRenderer() {
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

        renderer.setBackgroundNonSelectionColor(CARD_DARK);
        renderer.setBackgroundSelectionColor(SELECTION);
        renderer.setTextNonSelectionColor(TEXT);
        renderer.setTextSelectionColor(Color.WHITE);
        renderer.setBorderSelectionColor(null);

        // Suppression des icones par defaut pour un rendu plus minimaliste.
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        renderer.setLeafIcon(null);

        return renderer;
    }

    /**
     * Ouvre un explorateur de fichiers pour choisir une image ou un dossier.
     *
     * <p>
     * Si l'utilisateur choisit un dossier, toutes les images compatibles sont
     * listees. S'il choisit une image, elle est directement chargee et analysee.
     * </p>
     */
    private void openPathChooser() {
        JFileChooser chooser = new JFileChooser(currentDirectory != null ? currentDirectory.toFile() : null);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(ImageMimeUtils.createChooserFilter());

        int choice = chooser.showOpenDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selected = chooser.getSelectedFile();
        pathField.setText(selected.getAbsolutePath());
        analyzeFromField();
    }

    /**
     * Analyse le chemin saisi dans le champ principal.
     *
     * <p>
     * Si le chemin correspond a un dossier, l'application liste les images du
     * dossier. Si le chemin correspond a un fichier image valide, l'image est
     * chargee et ses metadonnees sont affichees.
     * </p>
     */
    private void analyzeFromField() {
        executeSafely(() -> {
            String input = pathField.getText().trim();
            if (input.isBlank()) {
                throw new IllegalArgumentException("Veuillez saisir un chemin.");
            }

            Path path = Path.of(input);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Le chemin n'existe pas.");
            }

            if (Files.isDirectory(path)) {
                loadDirectory(path);
            } else {
                metadataService.validateSupportedImage(path);
                loadFile(path);
            }
        });
    }

    /**
     * Charge un dossier et liste les images compatibles qu'il contient.
     *
     * @param directory dossier a analyser
     */
    private void loadDirectory(Path directory) {
        currentDirectory = directory.toAbsolutePath().normalize();
        List<Path> images = directoryService.listImageFiles(currentDirectory);
        buildTree(currentDirectory, images);

        if (images.isEmpty()) {
            currentImage = null;
            imagePreviewPanel.clear();
            metadataArea.setText("Aucune image PNG/JPG/JPEG detectee via MIME dans ce dossier.");
            fileInfoLabel.setText("Aucune image");
            statusLabel.setText("0 image trouvee");
            return;
        }

        selectImage(images.get(0));
        statusLabel.setText(images.size() + " image(s) detectee(s)");
    }

    /**
     * Charge une image unique et met a jour l'arborescence.
     *
     * @param file image a charger
     */
    private void loadFile(Path file) {
        currentImage = file.toAbsolutePath().normalize();
        currentDirectory = currentImage.getParent();

        List<Path> images;
        if (currentDirectory != null && Files.isDirectory(currentDirectory)) {
            images = directoryService.listImageFiles(currentDirectory);

            if (!images.contains(currentImage)) {
                images = new ArrayList<>(images);
                images.add(currentImage);
                images.sort(Comparator.comparing(path -> path.toAbsolutePath().toString().toLowerCase(Locale.ROOT)));
            }

            buildTree(currentDirectory, images);
        } else {
            images = List.of(currentImage);
            buildTree(currentImage.getParent(), images);
        }

        selectImage(currentImage);
        statusLabel.setText("Image chargee");
    }

    /**
     * Construit l'arborescence graphique des images.
     *
     * @param rootDirectory dossier racine affiche dans l'arborescence
     * @param images liste des images a afficher
     */
    private void buildTree(Path rootDirectory, List<Path> images) {
        String rootLabel = rootDirectory == null ? "Images"
                : (rootDirectory.getFileName() != null ? rootDirectory.getFileName().toString() : rootDirectory.toString());

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new NodeData(rootLabel, rootDirectory, false));
        Map<Path, DefaultMutableTreeNode> directories = new HashMap<>();

        if (rootDirectory != null) {
            directories.put(rootDirectory.toAbsolutePath().normalize(), rootNode);
        }

        List<Path> sorted = new ArrayList<>(images);
        sorted.sort(Comparator.comparing(path -> path.toAbsolutePath().toString().toLowerCase(Locale.ROOT)));

        for (Path image : sorted) {
            Path absoluteImage = image.toAbsolutePath().normalize();
            Path parent = absoluteImage.getParent();
            DefaultMutableTreeNode parentNode = rootNode;

            /*
             * Reconstruction de l'arborescence des sous-dossiers pour placer
             * chaque image au bon endroit.
             */
            if (rootDirectory != null && parent != null) {
                List<Path> lineage = new ArrayList<>();
                Path cursor = parent;

                while (cursor != null && !cursor.equals(rootDirectory.toAbsolutePath().normalize())) {
                    lineage.add(0, cursor);
                    cursor = cursor.getParent();
                }

                for (Path directory : lineage) {
                    DefaultMutableTreeNode node = directories.get(directory);

                    if (node == null) {
                        node = new DefaultMutableTreeNode(new NodeData(directory.getFileName().toString(), directory, false));
                        parentNode.add(node);
                        directories.put(directory, node);
                    }

                    parentNode = node;
                }
            }

            parentNode.add(new DefaultMutableTreeNode(new NodeData(
                    absoluteImage.getFileName().toString() + " [" + ImageMimeUtils.describe(absoluteImage) + "]",
                    absoluteImage,
                    true
            )));
        }

        imageTree.setModel(new DefaultTreeModel(rootNode));

        // Ouverture automatique des lignes pour rendre les images directement visibles.
        for (int i = 0; i < imageTree.getRowCount(); i++) {
            imageTree.expandRow(i);
        }
    }

    /**
     * Gere la selection d'une image dans l'arborescence.
     *
     * @param event evenement de selection du {@link JTree}
     */
    private void onTreeSelected(TreeSelectionEvent event) {
        Object selected = event.getPath().getLastPathComponent();

        if (!(selected instanceof DefaultMutableTreeNode node)) {
            return;
        }

        Object value = node.getUserObject();

        if (value instanceof NodeData data && data.image && data.path != null) {
            executeSafely(() -> selectImage(data.path));
        }
    }

    /**
     * Selectionne une image, extrait ses metadonnees et affiche son apercu.
     *
     * @param imagePath chemin de l'image a selectionner
     */
    private void selectImage(Path imagePath) {
        metadataService.validateSupportedImage(imagePath);

        currentImage = imagePath.toAbsolutePath().normalize();
        pathField.setText(currentImage.toString());
        fileInfoLabel.setText(currentImage.getFileName() + "  |  " + ImageMimeUtils.describe(currentImage));

        metadataArea.setText(metadataService.extract(currentImage));
        metadataArea.setCaretPosition(0);

        displayImage(currentImage);
        revealInTree(currentImage);
        statusLabel.setText("Image selectionnee");
    }

    /**
     * Rend visible dans l'arborescence l'image actuellement selectionnee.
     *
     * @param imagePath chemin de l'image a retrouver dans le {@link JTree}
     */
    private void revealInTree(Path imagePath) {
        Object rootObj = imageTree.getModel().getRoot();

        if (!(rootObj instanceof DefaultMutableTreeNode root)) {
            return;
        }

        TreePath found = findPath(root, imagePath.toAbsolutePath().normalize());

        if (found != null) {
            imageTree.setSelectionPath(found);
            imageTree.scrollPathToVisible(found);
        }
    }

    /**
     * Recherche recursivement un chemin dans l'arborescence.
     *
     * @param node noeud courant
     * @param target chemin recherche
     * @return le {@link TreePath} correspondant, ou {@code null} si aucun chemin n'est trouve
     */
    private TreePath findPath(DefaultMutableTreeNode node, Path target) {
        Object value = node.getUserObject();

        if (value instanceof NodeData data && target.equals(data.path)) {
            return new TreePath(node.getPath());
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            TreePath child = findPath((DefaultMutableTreeNode) node.getChildAt(i), target);

            if (child != null) {
                return child;
            }
        }

        return null;
    }

    /**
     * Affiche l'image dans le panneau d'apercu.
     *
     * @param path chemin de l'image a afficher
     */
    private void displayImage(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());

            if (image == null) {
                throw new IllegalArgumentException("Impossible d'afficher cette image.");
            }

            imagePreviewPanel.setImage(image);
        } catch (Exception e) {
            imagePreviewPanel.clear();
            throw new IllegalArgumentException("Impossible d'afficher l'image : " + e.getMessage(), e);
        }
    }

    /**
     * Ouvre la boite de dialogue permettant d'encoder un message dans une image.
     *
     * <p>
     * L'utilisateur choisit une image source, un fichier de sortie, puis saisit
     * le message a cacher dans l'image. Le traitement est delegue au service
     * {@link SteganographyService}.
     * </p>
     */
    private void openEncodeDialog() {
        JDialog dialog = createDialog("Encoder un message", 720, 430);

        RoundedPanel body = new RoundedPanel(CARD, 16, BORDER);
        body.setLayout(new BorderLayout(12, 12));
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        JTextField sourceField = new JTextField(currentImage != null ? currentImage.toString() : "");
        JTextField outputField = new JTextField(suggestOutputPath());
        JTextArea messageArea = new JTextArea();

        styleDialogField(sourceField, "Image source");
        styleDialogField(outputField, "Image encodee de sortie");
        styleDialogTextArea(messageArea);

        sourceField.setPreferredSize(new Dimension(0, 30));
        outputField.setPreferredSize(new Dimension(0, 30));

        JPanel pathsPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        pathsPanel.setOpaque(false);
        pathsPanel.setPreferredSize(new Dimension(0, 106));

        JPanel sourceBlock = new JPanel(new BorderLayout(0, 4));
        sourceBlock.setOpaque(false);
        sourceBlock.add(createSmallLabel("Image source"), BorderLayout.NORTH);
        sourceBlock.add(
                createFieldWithBrowseButton(sourceField, "Parcourir", e -> chooseImageFile(sourceField)),
                BorderLayout.CENTER
        );

        JPanel outputBlock = new JPanel(new BorderLayout(0, 4));
        outputBlock.setOpaque(false);
        outputBlock.add(createSmallLabel("Image de sortie"), BorderLayout.NORTH);
        outputBlock.add(
                createFieldWithBrowseButton(outputField, "Enregistrer sous", e -> chooseSavePng(outputField)),
                BorderLayout.CENTER
        );

        pathsPanel.add(sourceBlock);
        pathsPanel.add(outputBlock);

        JPanel messageBlock = new JPanel(new BorderLayout(0, 6));
        messageBlock.setOpaque(false);
        messageBlock.add(createSmallLabel("Message a encoder"), BorderLayout.NORTH);

        JScrollPane messageScroll = createScrollPane(messageArea);
        messageScroll.setPreferredSize(new Dimension(0, 190));
        messageBlock.add(messageScroll, BorderLayout.CENTER);

        JButton encodeButton = new JButton("Encoder");
        stylePrimaryButton(encodeButton);
        encodeButton.setPreferredSize(new Dimension(110, 30));

        encodeButton.addActionListener(e -> {
            try {
                String source = sourceField.getText().trim();
                String output = outputField.getText().trim();
                String message = messageArea.getText().trim();

                if (source.isBlank() && message.isBlank()) {
                    throw new IllegalArgumentException("Il manque l'image et le message.");
                }
                if (source.isBlank()) {
                    throw new IllegalArgumentException("Il manque l'image.");
                }
                if (message.isBlank()) {
                    throw new IllegalArgumentException("Il manque le message.");
                }

                Path created = steganographyService.encodeMessage(
                        Path.of(source),
                        messageArea.getText(),
                        output.isBlank() ? null : Path.of(output)
                );

                showSuccess("Encodage reussi", "Le message a bien ete encode.\n\nImage creee : " + created);
                dialog.dispose();
                pathField.setText(created.toString());
                analyzeFromField();
            } catch (Exception ex) {
                showError("Encodage impossible", ex.getMessage());
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 34));
        footer.add(encodeButton);

        body.add(pathsPanel, BorderLayout.NORTH);
        body.add(messageBlock, BorderLayout.CENTER);
        body.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(body);
        dialog.setVisible(true);
    }

    /**
     * Ouvre la boite de dialogue permettant de decoder un message cache dans une image.
     */
    private void openDecodeDialog() {
        JDialog dialog = createDialog("Decoder un message", 720, 380);

        RoundedPanel body = new RoundedPanel(CARD, 16, BORDER);
        body.setLayout(new BorderLayout(12, 12));
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        JTextField sourceField = new JTextField(currentImage != null ? currentImage.toString() : "");
        styleDialogField(sourceField, "Image a decoder");

        JPanel sourceBlock = new JPanel(new BorderLayout(0, 5));
        sourceBlock.setOpaque(false);
        sourceBlock.add(createSmallLabel("Image a decoder"), BorderLayout.NORTH);
        sourceBlock.add(
                createFieldWithBrowseButton(sourceField, "Parcourir", e -> chooseImageFile(sourceField)),
                BorderLayout.CENTER
        );

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setText("Le message decode s'affichera ici.");
        styleDialogTextArea(outputArea);

        JPanel outputBlock = new JPanel(new BorderLayout(0, 5));
        outputBlock.setOpaque(false);
        outputBlock.add(createSmallLabel("Message decode"), BorderLayout.NORTH);
        outputBlock.add(createScrollPane(outputArea), BorderLayout.CENTER);

        JButton decodeButton = new JButton("Decoder");
        stylePrimaryButton(decodeButton);
        decodeButton.setPreferredSize(new Dimension(118, 32));

        decodeButton.addActionListener(e -> {
            try {
                String source = sourceField.getText().trim();

                if (source.isBlank()) {
                    throw new IllegalArgumentException("Il manque l'image.");
                }

                String message = steganographyService.decodeMessage(Path.of(source));
                outputArea.setText(message);
                pathField.setText(source);
                analyzeFromField();
            } catch (Exception ex) {
                showError("Decodage impossible", ex.getMessage());
            }
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        footer.setOpaque(false);
        footer.add(decodeButton);

        body.add(sourceBlock, BorderLayout.NORTH);
        body.add(outputBlock, BorderLayout.CENTER);
        body.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(body);
        dialog.setVisible(true);
    }

    /**
     * Cree une boite de dialogue modale avec le style de l'application.
     *
     * @param title titre de la boite de dialogue
     * @param width largeur de la boite de dialogue
     * @param height hauteur de la boite de dialogue
     * @return la boite de dialogue configuree
     */
    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(width, height);
        dialog.setMinimumSize(new Dimension(width, height));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.getRootPane().putClientProperty(
                FlatClientProperties.STYLE,
                "background:#1F2227; borderColor:#3B414A"
        );
        return dialog;
    }

    /**
     * Ouvre un selecteur de fichier image et place le chemin choisi dans un champ.
     *
     * @param target champ de texte qui recevra le chemin choisi
     */
    private void chooseImageFile(JTextField target) {
        JFileChooser chooser = new JFileChooser(currentDirectory != null ? currentDirectory.toFile() : null);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(ImageMimeUtils.createChooserFilter());

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Ouvre un selecteur de fichier de sortie au format PNG.
     *
     * @param target champ de texte qui recevra le chemin du fichier de sortie
     */
    private void chooseSavePng(JTextField target) {
        JFileChooser chooser = new JFileChooser(currentDirectory != null ? currentDirectory.toFile() : null);
        chooser.setSelectedFile(new File(target.getText().isBlank() ? "image_encodee.png" : target.getText()));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();

            String path = selected.getAbsolutePath().toLowerCase(Locale.ROOT).endsWith(".png")
                    ? selected.getAbsolutePath()
                    : selected.getAbsolutePath() + ".png";

            target.setText(path);
        }
    }

    /**
     * Propose automatiquement un chemin de sortie pour une image encodee.
     *
     * @return le chemin suggere, ou une chaine vide si aucune image n'est selectionnee
     */
    private String suggestOutputPath() {
        if (currentImage == null) {
            return "";
        }

        String fileName = currentImage.getFileName().toString();
        int index = fileName.lastIndexOf('.');
        String baseName = index >= 0 ? fileName.substring(0, index) : fileName;
        Path parent = currentImage.getParent();

        if (parent == null) {
            return baseName + "_encoded.png";
        }

        return parent.resolve(baseName + "_encoded.png").toString();
    }

    /**
     * Execute une action en capturant les exceptions pour eviter un crash de l'interface.
     *
     * @param action action a executer
     */
    private void executeSafely(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }

    /**
     * Affiche une boite de dialogue d'erreur et met a jour le statut.
     *
     * @param title titre de l'erreur
     * @param message message a afficher
     */
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        statusLabel.setText(title);
    }

    /**
     * Affiche une boite de dialogue de succes et met a jour le statut.
     *
     * @param title titre du message de succes
     * @param message message a afficher
     */
    private void showSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText(title);
    }

    /**
     * Cree un petit label utilise dans les formulaires.
     *
     * @param text texte du label
     * @return le label configure
     */
    private JLabel createSmallLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        return label;
    }

    /**
     * Applique le style aux zones de texte utilisees dans les dialogues.
     *
     * @param area zone de texte a styliser
     */
    private void styleDialogTextArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(TEXT);
        area.setBackground(INPUT);
        area.setCaretColor(TEXT);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    /**
     * Applique un style secondaire aux boutons de selection de fichier.
     *
     * @param button bouton a styliser
     */
    private void styleSecondaryButton(AbstractButton button) {
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc:12;" +
                        "borderWidth:1;" +
                        "borderColor:#5A606B;" +
                        "background:#3A3F47;" +
                        "foreground:#EEF1F6;" +
                        "hoverBackground:#454B55;" +
                        "pressedBackground:#30343B;" +
                        "focusWidth:0");

        button.setBackground(new Color(58, 63, 71));
        button.setForeground(TEXT);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 12f));
    }

    /**
     * Cree une ligne contenant un champ texte et un bouton de navigation.
     *
     * @param field champ de texte contenant le chemin
     * @param buttonText texte du bouton
     * @param listener action executee lors du clic sur le bouton
     * @return le panneau contenant le champ et le bouton
     */
    private JPanel createFieldWithBrowseButton(JTextField field,
                                               String buttonText,
                                               java.awt.event.ActionListener listener) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JButton button = new JButton(buttonText);
        styleSecondaryButton(button);

        if ("Enregistrer sous".equals(buttonText)) {
            button.setPreferredSize(new Dimension(116, 30));
        } else {
            button.setPreferredSize(new Dimension(88, 30));
        }

        button.addActionListener(listener);

        row.add(field, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);

        return row;
    }

    /**
     * Donnee associee a chaque noeud de l'arborescence.
     *
     * <p>
     * Cette classe interne permet de stocker le texte affiche dans le JTree,
     * le chemin reel correspondant et une information indiquant si le noeud
     * represente une image ou un dossier.
     * </p>
     *
     * @author Ayoub ABDELLI
     * @version 1.0
     * @since 20-09-2025
     */
    private static final class NodeData {
        private final String label;
        private final Path path;
        private final boolean image;

        /**
         * Cree une donnee de noeud pour l'arborescence.
         *
         * @param label texte affiche dans l'arborescence
         * @param path chemin associe au noeud
         * @param image {@code true} si le noeud represente une image, sinon {@code false}
         */
        private NodeData(String label, Path path, boolean image) {
            this.label = label;
            this.path = path;
            this.image = image;
        }

        /**
         * Retourne le texte affiche dans l'arborescence.
         *
         * @return le label du noeud
         */
        @Override
        public String toString() {
            return label;
        }
    }
}