package com.projectd10.view.components;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Panneau Swing avec fond arrondi et bordure discrete.
 * <p>
 * Cette classe sert a donner un aspect moderne aux cartes de l'interface graphique en dessinant
 * manuellement un rectangle arrondi avant de laisser Swing peindre les composants enfants.
 * </p>
 *
 * @author Aybskt
 * @version 3.0
 * @since 29-04-2026
 */
public class RoundedPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final int arc;
    private final Color borderColor;


    /**
     * Cree un panneau arrondi avec une couleur de bordure personnalisee.
     *
     * @param background couleur de fond du panneau.
     * @param arc rayon d'arrondi des coins.
     * @param borderColor couleur de la bordure dessinee autour du panneau.
     */
    public RoundedPanel(Color background, int arc, Color borderColor) {
        this.arc = arc;
        this.borderColor = borderColor;
        setBackground(background);
        setOpaque(false);
    }

    /**
     * Dessine le fond arrondi et la bordure du panneau.
     *
     * @param g contexte graphique fourni par Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Le fond est dessine manuellement pour obtenir des coins arrondis propres.
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }
}