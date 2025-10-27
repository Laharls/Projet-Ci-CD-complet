package edu.cinema.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests exhaustifs pour le moteur de tarification.
 * Couvre les cas nominaux, les bords et les erreurs.
 */
@DisplayName("Cinema Pricing Engine Tests")
class PricingEngineTest {

    private PricingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new PricingEngine();
    }

    // =========================
    // Tests basePrice()
    // =========================

    @Test
    @DisplayName("basePrice: ADULT devrait coûter 10.00€")
    void basePrice_Adult() {
        assertEquals(10.00, engine.basePrice(TicketType.ADULT), 0.001);
    }

    @Test
    @DisplayName("basePrice: CHILD devrait coûter 6.00€")
    void basePrice_Child() {
        assertEquals(6.00, engine.basePrice(TicketType.CHILD), 0.001);
    }

    @Test
    @DisplayName("basePrice: SENIOR devrait coûter 7.50€")
    void basePrice_Senior() {
        assertEquals(7.50, engine.basePrice(TicketType.SENIOR), 0.001);
    }

    @Test
    @DisplayName("basePrice: STUDENT devrait coûter 8.00€")
    void basePrice_Student() {
        assertEquals(8.00, engine.basePrice(TicketType.STUDENT), 0.001);
    }

    @Test
    @DisplayName("basePrice: null devrait lever IllegalArgumentException")
    void basePrice_Null() {
        assertThrows(IllegalArgumentException.class, () -> engine.basePrice(null));
    }

    // =========================
    // Tests computeTotal() - Cas nominaux simples
    // =========================

    @Test
    @DisplayName("computeTotal: Panier vide devrait donner total 0.00")
    void computeTotal_EmptyCart() {
        List<TicketType> tickets = Collections.emptyList();
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.MONDAY);
        
        assertEquals(0.00, result.getSubtotal(), 0.001);
        assertEquals(0.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 1 ADULT sans options = 10.00€")
    void computeTotal_OneAdult_NoOptions() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.MONDAY);
        
        assertEquals(10.00, result.getSubtotal(), 0.001);
        assertEquals(10.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 1 CHILD sans options = 6.00€")
    void computeTotal_OneChild_NoOptions() {
        List<TicketType> tickets = Arrays.asList(TicketType.CHILD);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.FRIDAY);
        
        assertEquals(6.00, result.getSubtotal(), 0.001);
        assertEquals(6.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 1 SENIOR sans options = 7.50€")
    void computeTotal_OneSenior_NoOptions() {
        List<TicketType> tickets = Arrays.asList(TicketType.SENIOR);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.SATURDAY);
        
        assertEquals(7.50, result.getSubtotal(), 0.001);
        assertEquals(7.50, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 1 STUDENT sans options = 8.00€")
    void computeTotal_OneStudent_NoOptions() {
        List<TicketType> tickets = Arrays.asList(TicketType.STUDENT);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.THURSDAY);
        
        assertEquals(8.00, result.getSubtotal(), 0.001);
        assertEquals(8.00, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Remise Mercredi uniquement
    // =========================

    @Test
    @DisplayName("computeTotal: Mercredi -20% sur 1 ADULT = 8.00€")
    void computeTotal_Wednesday_OneAdult() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.WEDNESDAY);
        
        assertEquals(10.00, result.getSubtotal(), 0.001);
        assertEquals(2.00, result.getWednesdayDiscount(), 0.001);
        assertEquals(8.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Mercredi -20% sur panier mixte = 18.80€")
    void computeTotal_Wednesday_MixedCart() {
        // ADULT + SENIOR + CHILD = 10 + 7.5 + 6 = 23.50
        // -20% = 18.80
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT, 
            TicketType.SENIOR, 
            TicketType.CHILD
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.WEDNESDAY);
        
        assertEquals(23.50, result.getSubtotal(), 0.001);
        assertEquals(4.70, result.getWednesdayDiscount(), 0.001);
        assertEquals(18.80, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Supplément 3D uniquement
    // =========================

    @Test
    @DisplayName("computeTotal: 3D +2€ par billet (1 ADULT) = 12.00€")
    void computeTotal_3D_OneAdult() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.MONDAY);
        
        assertEquals(10.00, result.getSubtotal(), 0.001);
        assertEquals(2.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(12.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 3D avec 2 billets (ADULT + CHILD) = 20.00€")
    void computeTotal_3D_TwoTickets() {
        // (10 + 6) + 2*2 = 20.00
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT, TicketType.CHILD);
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.MONDAY);
        
        assertEquals(16.00, result.getSubtotal(), 0.001);
        assertEquals(4.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(20.00, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Remise Groupe uniquement
    // =========================

    @Test
    @DisplayName("computeTotal: Groupe (4 STUDENT) -10% = 28.80€")
    void computeTotal_Group_FourStudents() {
        // 4 * 8 = 32.00 → -10% = 28.80
        List<TicketType> tickets = Arrays.asList(
            TicketType.STUDENT,
            TicketType.STUDENT,
            TicketType.STUDENT,
            TicketType.STUDENT
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.MONDAY);
        
        assertEquals(32.00, result.getSubtotal(), 0.001);
        assertEquals(3.20, result.getGroupDiscount(), 0.001);
        assertEquals(28.80, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Pas de remise groupe avec 3 billets")
    void computeTotal_NoGroup_ThreeTickets() {
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.MONDAY);
        
        assertEquals(30.00, result.getSubtotal(), 0.001);
        assertEquals(0.00, result.getGroupDiscount(), 0.001);
        assertEquals(30.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Remise groupe avec 5 billets")
    void computeTotal_Group_FiveTickets() {
        // 5 * 10 = 50.00 → -10% = 45.00
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.TUESDAY);
        
        assertEquals(50.00, result.getSubtotal(), 0.001);
        assertEquals(5.00, result.getGroupDiscount(), 0.001);
        assertEquals(45.00, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Combinaisons
    // =========================

    @Test
    @DisplayName("computeTotal: Mercredi + 3D (ordre correct)")
    void computeTotal_Wednesday_And_3D() {
        // 1 ADULT = 10.00
        // Mercredi -20% = 8.00
        // 3D +2 = 10.00
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.WEDNESDAY);
        
        assertEquals(10.00, result.getSubtotal(), 0.001);
        assertEquals(2.00, result.getWednesdayDiscount(), 0.001);
        assertEquals(2.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(10.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Mercredi + 3D + Groupe (4 ADULT) = 36.00€")
    void computeTotal_Wednesday_3D_Group() {
        // 4 ADULT = 40.00
        // Mercredi -20% = 32.00
        // 3D +8 (4*2) = 40.00
        // Groupe -10% = 36.00
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT,
            TicketType.ADULT
        );
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.WEDNESDAY);
        
        assertEquals(40.00, result.getSubtotal(), 0.001);
        assertEquals(8.00, result.getWednesdayDiscount(), 0.001);
        assertEquals(8.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(4.00, result.getGroupDiscount(), 0.001);
        assertEquals(36.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: 3D + Groupe (sans mercredi)")
    void computeTotal_3D_And_Group() {
        // 4 CHILD = 24.00
        // 3D +8 = 32.00
        // Groupe -10% = 28.80
        List<TicketType> tickets = Arrays.asList(
            TicketType.CHILD,
            TicketType.CHILD,
            TicketType.CHILD,
            TicketType.CHILD
        );
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.FRIDAY);
        
        assertEquals(24.00, result.getSubtotal(), 0.001);
        assertEquals(0.00, result.getWednesdayDiscount(), 0.001);
        assertEquals(8.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(3.20, result.getGroupDiscount(), 0.001);
        assertEquals(28.80, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Arrondis
    // =========================

    @Test
    @DisplayName("computeTotal: Test d'arrondi avec SENIOR (7.50€)")
    void computeTotal_Rounding_Senior() {
        // 1 SENIOR = 7.50, mercredi -20% = 6.00 (pas de fraction)
        List<TicketType> tickets = Arrays.asList(TicketType.SENIOR);
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.WEDNESDAY);
        
        assertEquals(6.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Arrondi avec fraction répétée")
    void computeTotal_Rounding_RepeatingFraction() {
        // 3 SENIOR = 22.50
        // Mercredi -20% = 18.00
        // 3D +6 = 24.00
        // Pas de fraction ici, mais testons avec un cas plus complexe
        List<TicketType> tickets = Arrays.asList(
            TicketType.SENIOR,
            TicketType.SENIOR,
            TicketType.SENIOR
        );
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.WEDNESDAY);
        
        // 22.50 - 4.50 = 18.00 + 6.00 = 24.00
        assertEquals(24.00, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Arrondi complexe avec toutes les règles")
    void computeTotal_Rounding_Complex() {
        // 7 STUDENT = 56.00
        // Mercredi -20% = 44.80
        // 3D +14 = 58.80
        // Groupe -10% = 52.92
        List<TicketType> tickets = Arrays.asList(
            TicketType.STUDENT, TicketType.STUDENT, TicketType.STUDENT,
            TicketType.STUDENT, TicketType.STUDENT, TicketType.STUDENT,
            TicketType.STUDENT
        );
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.WEDNESDAY);
        
        assertEquals(52.92, result.getTotal(), 0.001);
    }

    // =========================
    // Tests computeTotal() - Erreurs
    // =========================

    @Test
    @DisplayName("computeTotal: tickets null devrait lever IllegalArgumentException")
    void computeTotal_NullTickets() {
        assertThrows(IllegalArgumentException.class, 
            () -> engine.computeTotal(null, false, DayOfWeek.MONDAY));
    }

    @Test
    @DisplayName("computeTotal: day null devrait lever IllegalArgumentException")
    void computeTotal_NullDay() {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        assertThrows(IllegalArgumentException.class, 
            () -> engine.computeTotal(tickets, false, null));
    }

    // =========================
    // Tests paramétrés pour tous les jours
    // =========================

    @ParameterizedTest
    @EnumSource(value = DayOfWeek.class, names = {"MONDAY", "TUESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
    @DisplayName("computeTotal: Pas de remise mercredi les autres jours")
    void computeTotal_NoWednesdayDiscount_OtherDays(DayOfWeek day) {
        List<TicketType> tickets = Arrays.asList(TicketType.ADULT);
        PriceBreakdown result = engine.computeTotal(tickets, false, day);
        
        assertEquals(0.00, result.getWednesdayDiscount(), 0.001);
        assertEquals(10.00, result.getTotal(), 0.001);
    }

    // =========================
    // Tests supplémentaires pour 100% de couverture
    // =========================

    @Test
    @DisplayName("PriceBreakdown: toString devrait fonctionner")
    void priceBreakdown_ToString() {
        PriceBreakdown breakdown = new PriceBreakdown(10.0, 2.0, 2.0, 1.0, 9.0);
        String result = breakdown.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("10.00"));
        assertTrue(result.contains("2.00"));
        assertTrue(result.contains("9.00"));
    }

    @Test
    @DisplayName("PriceBreakdown: Tous les getters fonctionnent")
    void priceBreakdown_AllGetters() {
        PriceBreakdown breakdown = new PriceBreakdown(10.0, 2.0, 3.0, 1.5, 9.5);
        
        assertEquals(10.0, breakdown.getSubtotal(), 0.001);
        assertEquals(2.0, breakdown.getWednesdayDiscount(), 0.001);
        assertEquals(3.0, breakdown.getThreeDSurcharge(), 0.001);
        assertEquals(1.5, breakdown.getGroupDiscount(), 0.001);
        assertEquals(9.5, breakdown.getTotal(), 0.001);
    }

    @Test
    @DisplayName("TicketType: Tous les types existent")
    void ticketType_AllTypesExist() {
        assertNotNull(TicketType.ADULT);
        assertNotNull(TicketType.CHILD);
        assertNotNull(TicketType.SENIOR);
        assertNotNull(TicketType.STUDENT);
        assertEquals(4, TicketType.values().length);
    }

    // =========================
    // Tests de cas limites supplémentaires
    // =========================

    @Test
    @DisplayName("computeTotal: Exactement 4 billets active la remise groupe")
    void computeTotal_ExactlyFourTickets_GroupDiscount() {
        List<TicketType> tickets = Arrays.asList(
            TicketType.CHILD, TicketType.CHILD, TicketType.CHILD, TicketType.CHILD
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.MONDAY);
        
        // 4 * 6 = 24.00, -10% = 21.60
        assertEquals(24.00, result.getSubtotal(), 0.001);
        assertEquals(2.40, result.getGroupDiscount(), 0.001);
        assertEquals(21.60, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Grand panier avec tous les types")
    void computeTotal_LargeCart_AllTypes() {
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT, TicketType.ADULT,
            TicketType.CHILD, TicketType.CHILD,
            TicketType.SENIOR,
            TicketType.STUDENT
        );
        PriceBreakdown result = engine.computeTotal(tickets, false, DayOfWeek.SATURDAY);
        
        // 10+10+6+6+7.5+8 = 47.50
        // Groupe -10% = 42.75
        assertEquals(47.50, result.getSubtotal(), 0.001);
        assertEquals(4.75, result.getGroupDiscount(), 0.001);
        assertEquals(42.75, result.getTotal(), 0.001);
    }

    @Test
    @DisplayName("computeTotal: Scenario complet maximal")
    void computeTotal_MaximalScenario() {
        // 10 billets, mercredi, 3D
        List<TicketType> tickets = Arrays.asList(
            TicketType.ADULT, TicketType.ADULT, TicketType.ADULT,
            TicketType.CHILD, TicketType.CHILD,
            TicketType.SENIOR, TicketType.SENIOR,
            TicketType.STUDENT, TicketType.STUDENT, TicketType.STUDENT
        );
        PriceBreakdown result = engine.computeTotal(tickets, true, DayOfWeek.WEDNESDAY);
        
        // Subtotal: 3*10 + 2*6 + 2*7.5 + 3*8 = 30+12+15+24 = 81.00
        // Mercredi -20%: 81 - 16.2 = 64.80
        // 3D +20 (10*2): 64.80 + 20 = 84.80
        // Groupe -10%: 84.80 - 8.48 = 76.32
        assertEquals(81.00, result.getSubtotal(), 0.001);
        assertEquals(16.20, result.getWednesdayDiscount(), 0.001);
        assertEquals(20.00, result.getThreeDSurcharge(), 0.001);
        assertEquals(8.48, result.getGroupDiscount(), 0.001);
        assertEquals(76.32, result.getTotal(), 0.001);
    }
}