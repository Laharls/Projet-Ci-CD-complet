package edu.cinema.pricing;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Moteur de tarification pour un cinéma.
 * Calcule le prix total d'une commande selon les règles métier.
 */
public class PricingEngine {
    
    // Constantes de prix
    private static final double ADULT_PRICE = 10.00;
    private static final double CHILD_PRICE = 6.00;
    private static final double SENIOR_PRICE = 7.50;
    private static final double STUDENT_PRICE = 8.00;
    
    // Constantes pour les règles
    private static final double WEDNESDAY_DISCOUNT_RATE = 0.20; // 20%
    private static final double THREE_D_SURCHARGE_PER_TICKET = 2.00;
    private static final double GROUP_DISCOUNT_RATE = 0.10; // 10%
    private static final int GROUP_THRESHOLD = 4; // ≥4 billets

    /**
     * Retourne le prix de base pour un type de billet.
     * 
     * @param type Type de billet
     * @return Prix de base en euros
     * @throws IllegalArgumentException si type est null
     */
    public double basePrice(TicketType type) {
        if (type == null) {
            throw new IllegalArgumentException("TicketType cannot be null");
        }
        
        switch (type) {
            case ADULT:
                return ADULT_PRICE;
            case CHILD:
                return CHILD_PRICE;
            case SENIOR:
                return SENIOR_PRICE;
            case STUDENT:
                return STUDENT_PRICE;
            default:
                throw new IllegalArgumentException("Unknown TicketType: " + type);
        }
    }

    /**
     * Calcule le prix total d'une commande avec toutes les règles appliquées.
     * Ordre d'application : Mercredi → 3D → Groupe
     * 
     * @param tickets Liste des types de billets
     * @param is3D    Séance 3D ou non
     * @param day     Jour de la séance
     * @return Détail complet du calcul
     * @throws IllegalArgumentException si tickets ou day est null
     */
    public PriceBreakdown computeTotal(List<TicketType> tickets, boolean is3D, DayOfWeek day) {
        // Préconditions
        if (tickets == null) {
            throw new IllegalArgumentException("Tickets list cannot be null");
        }
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }

        // 1. Calcul du sous-total
        double subtotal = 0.0;
        for (TicketType ticket : tickets) {
            subtotal += basePrice(ticket);
        }

        // Variables pour tracker les remises/suppléments
        double wednesdayDiscount = 0.0;
        double threeDSurcharge = 0.0;
        double groupDiscount = 0.0;
        
        // Montant courant (qui sera modifié au fur et à mesure)
        double currentAmount = subtotal;

        // 2. Règle Mercredi : -20% sur tout le panier
        if (day == DayOfWeek.WEDNESDAY) {
            wednesdayDiscount = currentAmount * WEDNESDAY_DISCOUNT_RATE;
            currentAmount -= wednesdayDiscount;
        }

        // 3. Règle 3D : +2€ par billet
        if (is3D) {
            threeDSurcharge = THREE_D_SURCHARGE_PER_TICKET * tickets.size();
            currentAmount += threeDSurcharge;
        }

        // 4. Règle Groupe : -10% si ≥4 billets
        if (tickets.size() >= GROUP_THRESHOLD) {
            groupDiscount = currentAmount * GROUP_DISCOUNT_RATE;
            currentAmount -= groupDiscount;
        }

        // 5. Arrondir au centime
        double total = roundToCents(currentAmount);

        return new PriceBreakdown(subtotal, wednesdayDiscount, threeDSurcharge, 
                                  groupDiscount, total);
    }

    /**
     * Arrondit un montant au centime (2 décimales).
     * 
     * @param amount Montant à arrondir
     * @return Montant arrondi
     */
    private double roundToCents(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}