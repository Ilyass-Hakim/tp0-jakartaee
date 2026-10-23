package ma.hakim.tp0jakartaee.service;

import jakarta.enterprise.context.Dependent;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Service qui génère la réponse à une question.
 * Traitement personnel (bonus TP 0) : détecteur d'humeur.
 * La question est découpée en mots ; les mots positifs et négatifs (français et darija)
 * sont comptés pour en déduire l'humeur de la question.
 * Un score cumulé donne aussi l'humeur globale de la conversation.
 */
@Dependent // L'instance sera supprimée quand le backing bean qui l'a injectée sera supprimé (nouveau chat).
public class GenerateurReponse implements Serializable { // Serializable car injectée dans un bean @ViewScoped.

    /** Mots positifs, en minuscules et sans accents. */
    private static final Set<String> MOTS_POSITIFS = Set.of(
            "bien", "bon", "bonne", "super", "genial", "geniale", "content", "contente",
            "heureux", "heureuse", "merci", "top", "excellent", "parfait", "aime", "adore",
            "cool", "magnifique", "bravo", "joie", "beau", "belle",
            // darija
            "mezyan", "mzyan", "zwin", "zwina", "hamdoulah", "hamdullah", "bsaha", "nadi",
            "wa3r", "chokran", "shukran", "fer7an", "fer7ana");

    /** Mots négatifs, en minuscules et sans accents. */
    private static final Set<String> MOTS_NEGATIFS = Set.of(
            "mal", "mauvais", "mauvaise", "triste", "nul", "nulle", "deteste", "fatigue",
            "fatiguee", "colere", "probleme", "peur", "ennui", "horrible", "pire", "dommage", "malade",
            // darija
            "khayb", "khayba", "3iyan", "3iyana", "m9elle9", "mqelleq", "7zin", "7zina",
            "hzin", "mrid", "mrida");

    /**
     * Score cumulé de toutes les questions de la conversation.
     * Conservé entre les questions grâce à la portée du backing bean,
     * remis à zéro par "Nouveau chat".
     */
    private int scoreConversation = 0;

    /** Nombre de questions posées depuis le début de la conversation. */
    private int nbQuestions = 0;

    /**
     * Génère une réponse à une question : l'humeur de la question et l'humeur globale du chat.
     *
     * @param question    la question de l'utilisateur.
     * @param roleSysteme le rôle de l'API ; non null seulement pour la première question.
     * @return la réponse.
     */
    public String genererReponse(String question, String roleSysteme) {
        StringBuilder reponse = new StringBuilder();
        if (roleSysteme != null) {
            reponse.append(roleSysteme.toUpperCase(Locale.FRENCH)).append("\n");
        }

        // Repère les mots positifs et négatifs de la question.
        List<String> positifs = new ArrayList<>();
        List<String> negatifs = new ArrayList<>();
        // Découpe sur tout ce qui n'est ni une lettre ni un chiffre (les chiffres servent en darija : 3, 7, 9).
        for (String mot : normaliser(question).split("[^\\p{L}\\p{N}]+")) {
            if (MOTS_POSITIFS.contains(mot)) {
                positifs.add(mot);
            } else if (MOTS_NEGATIFS.contains(mot)) {
                negatifs.add(mot);
            }
        }

        // Score de la question et mise à jour de l'état de la conversation.
        int score = positifs.size() - negatifs.size();
        scoreConversation += score;
        nbQuestions++;

        reponse.append("Humeur de ta question : ").append(humeur(score)).append("\n")
                .append("Mots positifs : ").append(enTexte(positifs)).append("\n")
                .append("Mots négatifs : ").append(enTexte(negatifs)).append("\n")
                .append("Humeur globale du chat après ").append(nbQuestions).append(" question(s) (score ")
                .append(scoreConversation).append(") : ").append(humeur(scoreConversation));
        return reponse.toString();
    }

    /**
     * Met le texte en minuscules et enlève les accents ("Génial" devient "genial").
     */
    private static String normaliser(String texte) {
        String minuscules = texte.toLowerCase(Locale.FRENCH);
        // NFD sépare chaque lettre de son accent ; \p{M} supprime ensuite les accents.
        return Normalizer.normalize(minuscules, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /**
     * Traduit un score en humeur.
     */
    private static String humeur(int score) {
        if (score > 0) {
            return "positive 😊";
        }
        if (score < 0) {
            return "négative 😞";
        }
        return "neutre 😐";
    }

    /**
     * Liste de mots en texte, ou "aucun" si la liste est vide.
     */
    private static String enTexte(List<String> mots) {
        return mots.isEmpty() ? "aucun" : String.join(", ", mots);
    }
}