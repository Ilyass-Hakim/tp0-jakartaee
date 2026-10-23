package ma.hakim.tp0jakartaee.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;

/**
 * Service qui génère la réponse à une question de l'utilisateur.
 * En attendant de l'envoyer à un LLM (prochains TPs), le traitement est simple :
 * la question est mise en minuscules, le rôle système est ajouté au début
 * de la première réponse, et le tout est entouré avec "||".
 */
@ApplicationScoped
public class GenerateurReponse {

    /**
     * Génère la réponse à une question.
     *
     * @param question    la question posée par l'utilisateur.
     * @param roleSysteme le rôle de l'API ; il n'est pas null seulement pour
     *                    la première question de la conversation.
     * @return la réponse générée.
     */
    public String genererReponse(String question, String roleSysteme) {
        StringBuilder reponse = new StringBuilder("||");
        if (roleSysteme != null) {
            reponse.append(roleSysteme.toUpperCase(Locale.FRENCH)).append("\n");
        }
        reponse.append(question.toLowerCase(Locale.FRENCH)).append("||");
        return reponse.toString();
    }
}