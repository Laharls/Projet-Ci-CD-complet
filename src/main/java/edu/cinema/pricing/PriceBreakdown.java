package edu.cinema.pricing;

import java.util.Locale;

/**
 * Récapitulatif détaillé d'une commande de billets.
 * Classe immuable (final + fields final).
 */
public final class PriceBreakdown {
    private final double subtotal;          // Somme des prix de base
    private final double wednesdayDiscount; // Remise -20% si mercredi
    private final double threeDSurcharge;   // Supplément +2€ par billet si 3D
    private final double groupDiscount;     // Remise -10% si ≥4 billets
    private final double total;             // Total final arrondi

    /**
     * Constructeur complet.
     * 
     * @param subtotal          Sous-total avant remises/suppléments
     * @param wednesdayDiscount Montant de la remise mercredi (positif)
     * @param threeDSurcharge   Montant du supplément 3D (positif)
     * @param groupDiscount     Montant de la remise groupe (positif)
     * @param total             Total final après toutes les règles
     */
    public PriceBreakdown(double subtotal, double wednesdayDiscount, 
                          double threeDSurcharge, double groupDiscount, 
                          double total) {
        this.subtotal = subtotal;
        this.wednesdayDiscount = wednesdayDiscount;
        this.threeDSurcharge = threeDSurcharge;
        this.groupDiscount = groupDiscount;
        this.total = total;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getWednesdayDiscount() {
        return wednesdayDiscount;
    }

    public double getThreeDSurcharge() {
        return threeDSurcharge;
    }

    public double getGroupDiscount() {
        return groupDiscount;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public String toString() {
        // Utilise Locale.US pour garantir un format avec point décimal
        return String.format(Locale.US,
            "PriceBreakdown{subtotal=%.2f, wednesdayDiscount=%.2f, " +
            "threeDSurcharge=%.2f, groupDiscount=%.2f, total=%.2f}",
            subtotal, wednesdayDiscount, threeDSurcharge, groupDiscount, total
        );
    }
}