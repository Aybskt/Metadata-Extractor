package com.projectd10.view.components;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Panneau d'aperçu des images selectionnees.
 * <p>
 * Cette classe sert a afficher une image au centre de la zone de preview en conservant ses proportions.
 * Lorsqu'aucune image n'est chargee, elle affiche un message d'etat clair pour guider l'utilisateur.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class ImagePreviewPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color BG_TOP = new Color(35, 39, 45);
    private static final Color BG_BOTTOM = new Color(28, 31, 36);
    private static final Color EMPTY_TEXT = new Color(145, 153, 166);

    private BufferedImage image;

    /**
     * Initialise le panneau de preview avec un fond sombre non opaque.
     */
    public ImagePreviewPanel() {
        setOpaque(false);
        setBackground(BG_TOP);
    }

    /**
     * Definit l'image a afficher et demande un nouveau rendu du panneau.
     *
     * @param image image a afficher dans l'aperçu.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    /**
     * Vide l'aperçu courant et affiche l'etat vide.
     */
    public void clear() {
        this.image = null;
        repaint();
    }

    /**
     * Dessine le fond, l'etat vide ou l'image chargee.
     *
     * @param g contexte graphique fourni par Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            paintBackground(g2);

            // Sans image, on affiche un message d'aide au lieu de laisser la zone vide.
            if (image == null) {
                paintEmptyState(g2);
                return;
            }

            paintImage(g2);
        } finally {
            g2.dispose();
        }
    }

    /**
     * Dessine le fond degrade du panneau de preview.
     *
     * @param g2 contexte graphique 2D utilise pour le dessin avance.
     */
    private void paintBackground(Graphics2D g2) {
        GradientPaint gradient = new GradientPaint(
                0,
                0,
                BG_TOP,
                0,
                getHeight(),
                BG_BOTTOM
        );

        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
    }

    /**
     * Dessine le texte affiche lorsqu'aucune image n'est chargee.
     *
     * @param g2 contexte graphique 2D utilise pour le dessin du texte.
     */
    private void paintEmptyState(Graphics2D g2) {
        String title = "Aucune image chargee";
        String subtitle = "Selectionnez une image pour afficher l'apercu";

        g2.setFont(getFont().deriveFont(Font.PLAIN, 15f));
        g2.setColor(EMPTY_TEXT);

        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, Math.max(16, (getWidth() - titleWidth) / 2), getHeight() / 2 - 6);

        g2.setFont(getFont().deriveFont(Font.PLAIN, 12f));
        g2.setColor(new Color(115, 123, 136));

        int subtitleWidth = g2.getFontMetrics().stringWidth(subtitle);
        g2.drawString(subtitle, Math.max(16, (getWidth() - subtitleWidth) / 2), getHeight() / 2 + 18);
    }

    /**
     * Dessine l'image courante en l'adaptant a la taille disponible.
     *
     * @param g2 contexte graphique 2D utilise pour dessiner l'image.
     */
    private void paintImage(Graphics2D g2) {
        int panelW = getWidth();
        int panelH = getHeight();

        int imageW = image.getWidth();
        int imageH = image.getHeight();

        // On utilise le plus petit ratio pour afficher toute l'image sans la deformer.
        double scale = Math.min((double) panelW / imageW, (double) panelH / imageH);

        int targetW = Math.max(1, (int) Math.round(imageW * scale));
        int targetH = Math.max(1, (int) Math.round(imageH * scale));

        int x = (panelW - targetW) / 2;
        int y = (panelH - targetH) / 2;

        g2.drawImage(image, x, y, targetW, targetH, null);
    }
}