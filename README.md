# Cinema Pricing Engine 🎬

Moteur de tarification pour un cinéma - Projet académique avec tests JUnit 5 et couverture JaCoCo.

## 📋 Description

Ce projet implémente un moteur de calcul de prix pour des billets de cinéma avec les règles suivantes :

### Types de billets et prix de base
- **ADULT** : 10.00 €
- **CHILD** : 6.00 €
- **SENIOR** : 7.50 €
- **STUDENT** : 8.00 €

### Règles de tarification (appliquées dans cet ordre)
1. **Mercredi** : -20% sur tout le panier
2. **3D** : +2.00 € par billet
3. **Groupe** (≥4 billets) : -10% sur le panier

Le total final est arrondi au centime (2 décimales).

## 🏗️ Structure du projet

```
cinema-pricing/
├── pom.xml
├── README.md
└── src/
    ├── main/java/edu/cinema/pricing/
    │   ├── TicketType.java          # Énumération des types de billets
    │   ├── PriceBreakdown.java      # POJO pour le détail du calcul
    │   └── PricingEngine.java       # Logique métier de tarification
    └── test/java/edu/cinema/pricing/
        └── PricingEngineTest.java   # Tests JUnit 5 exhaustifs (30+ tests)
```

## 🚀 Commandes d'exécution

### Prérequis
- Java 11 ou supérieur
- Maven (ou utiliser le Maven Wrapper inclus)

### Build complet avec tests et rapport JaCoCo
```bash
# Linux/Mac
./mvnw clean verify

# Windows
mvnw.cmd clean verify
```

### Lancer uniquement les tests
```bash
./mvnw test
```

### Lancer uniquement le build sans tests
```bash
./mvnw clean compile
```

### Générer le rapport JaCoCo
```bash
./mvnw jacoco:report
```

## 📊 Rapports

### Rapport JaCoCo (Couverture de code)
Après `./mvnw verify`, ouvrir dans un navigateur :
```
target/site/jacoco/index.html
```

**Couverture attendue : ≥ 85%** (Instructions, Branches, Lignes)

### Rapport de tests JUnit
```
target/surefire-reports/
```

## 🧪 Exemples de calculs

| Tickets | 3D | Jour | Calcul | Total |
|---------|----|----- |--------|-------|
| [ADULT] | non | Lundi | 10.00 | **10.00 €** |
| [ADULT, CHILD] | oui | Lundi | (10+6) + 2×2 = 20.00 | **20.00 €** |
| [STUDENT×4] | non | Lundi | 32.00 → groupe -10% = 28.80 | **28.80 €** |
| [ADULT, SENIOR, CHILD] | non | Mercredi | 23.50 → mercredi -20% = 18.80 | **18.80 €** |
| [ADULT×4] | oui | Mercredi | 40.00 → -20% = 32.00 → +8 = 40.00 → -10% = 36.00 | **36.00 €** |

## ✅ Couverture des tests

Le projet inclut **plus de 30 tests** couvrant :

### Tests `basePrice()`
- ✅ Prix de chaque type de billet (4 tests)
- ✅ Exception si type null

### Tests `computeTotal()`
- ✅ Panier vide
- ✅ 1 billet de chaque type sans options (4 tests)
- ✅ Remise mercredi seule (2 tests)
- ✅ Supplément 3D seul (2 tests)
- ✅ Remise groupe seule (3 tests)
- ✅ Combinaisons : Mercredi+3D, Mercredi+3D+Groupe, 3D+Groupe
- ✅ Cas d'arrondis complexes
- ✅ Exceptions : tickets null, day null
- ✅ Tests paramétrés pour tous les jours de la semaine
- ✅ Cas limites (exactement 4 billets, grands paniers)

### Tests `PriceBreakdown`
- ✅ Tous les getters
- ✅ Méthode toString()

### Tests `TicketType`
- ✅ Existence de tous les types

## 🔧 Intégration Jenkins (Optionnel)

### Configuration Freestyle Job

1. **Source Code Management** : Git (URL du dépôt)
2. **Build Steps** :
   ```bash
   ./mvnw clean verify
   ```
3. **Post-build Actions** :
   - **Publish JUnit test result report** : `**/target/surefire-reports/*.xml`
   - **Record JaCoCo coverage report** : `**/target/site/jacoco/jacoco.xml`

### Seuils JaCoCo recommandés
- Instructions : minimum 80%
- Branches : minimum 75%
- Lignes : minimum 85%

## 📦 Technologies utilisées

- **Java 11**
- **Maven 3.8+**
- **JUnit 5.10.0** (tests unitaires)
- **JaCoCo 0.8.10** (couverture de code)
- **Maven Surefire 3.1.2** (exécution des tests)

## 👨‍💻 Auteur

Réalisé par Anthony Urbanski et Gauvin Caillou