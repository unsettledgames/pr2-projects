import Exceptions.Post.PostNotFoundException;
import Exceptions.Reporting.AutoReportException;
import Exceptions.Reporting.ReportingAlreadySentException;
import Exceptions.User.UserNotFoundException;

import java.util.*;

/** Overview:
 *      ModeratedSocialNetwork è un dato mutabile che rappresenta una rete sociale in cui,
 *      oltre alle operazioni ereditate e descritte in SocialNetwork, è anche possibile
 *      raccogliere delle segnalazioni rigardo a contenuti considerati non consoni. Si
 *      effettuano inoltre segnalazioni automatiche al momento della pubblicazione di un
 *      post.
 *
 *      Typical Element:
 *          <postLikes, userPublished, userFollowing, reportings, forbiddenWords>, in cui:
 *              - postLikes, userPublished e userFollowing descritti come in SocialNetwork
 *              - reportings = {(p1, {r1, r2, ...}), (p2, {s1, s2, ...}), ..} in cui
 *                  reportings(p): Post -> {r1, r2, ...} in cui ri è una segnalazione
 *                  relativa al post p
 *              - forbiddenWords = {w1, w2, w3, ...} in cui wi è una parola il cui uso
 *                  può essere considerato offensivo o fonte di comportamenti
 *                  inappropriati all'interno della rete sociale
 *
 */
public interface ModeratedSocialNetwork extends SocialNetwork {
    public static final int AUTOMATIC_REPORTING_WEIGHT = 1;
    public static final int MANUAL_REPORTING_WEIGHT = 2;
    /**
     * @requires author != null && post != null && userFollowing(author) != null &&
     *           postLikes(post) != null
     * @param author L'autore della segnalazione
     * @param post   Il post da segnalare
     * @effects Aggiunge a un post una segnalazione. Tale segnalazione può essere automatica
     *          e aggiunta nel momento in cui il post viene pubblicato oppure può essere
     *          inviata da un utente: quest'ultimo tipo di segnalazioni ha un peso maggiore.
     * @throws ReportingAlreadySentException Se author ha già inviato una segnalazione per post
     * @throws AutoReportException Se author sta cercando di segnalare un post da egli stesso
     *                             pubblicato
     * @throws UserNotFoundException Se author non è parte del social network
     * @throws NullPointerException Se author == null || post == null
     * @throws PostNotFoundException Se postLikes(post) == null
     */
    public void report(String author, Post post)
            throws ReportingAlreadySentException, AutoReportException, UserNotFoundException, PostNotFoundException;

    /**
     * @effects Fornisce la lista dei post presenti nella rete sociale ordinata in ordine
     *          non crescente di somma dei pesi delle segnalazioni. Se un post non ha
     *          segnalazioni, non viene aggiunto alla lista. Se non ci sono segnalazioni, la lista
     *          che viene fornita è vuota.
     * @return  La lista dei post della rete ordinata in ordine non crescente di somma
     *          dei pesi delle segnalazioni
     */
    public List<Post> getControversialPosts();

    /**
     * @requires toAdd != null && !forbiddenWords.contains(toAdd)
     * @param toAdd La parola da aggiungere all'insieme
     *
     * @effects Aggiunge una parola all'insieme delle parole considerate inappropriate
     * @throws NullPointerException Se toAdd == null
     */
    public void addForbiddenWord(String toAdd);

    /**
     * @requires toRemove != null && forbiddenWords.contains(toRemove)
     * @param toRemove La parola da rimuovere dall'insieme
     *
     * @effects Rimuove toRemove dalla lista delle parole considerate inappropriate
     * @throws NoSuchElementException Se toRemove non appartiene all'insieme
     * @throws NullPointerException Se toRemove == null
     */
    public void removeForbiddenWord(String toRemove);

    /**
     * @requires author != null && userFollowing(author) != null
     * @param author L'autore delle segnalazioni
     *
     * @effects Fornisce una lista delle segnalazioni inviate dall'utente passato come
     *          parametro; fornisce una lista vuota se l'utente non ha inviato segnalazioni
     *          per nessun post.
     * @return  La lista delle segnalazioni inviate da author, una lista vuota se
     *          author non ha mai inviato segnalazioni
     * @throws UserNotFoundException Se author non è registrato nella rete
     * @throws NullPointerException Se author == null
     */
    public List<Reporting> getReportingsByAuthor(String author) throws UserNotFoundException;

    /**
     * @requires post != null && postLikes(post) == null
     * @param post Il post di cui ottenere le segnalazioni
     *
     * @effects Fornisce una lista delle segnalazioni relative a un post passato come
     *          parametro
     * @return La lista delle segnalazioni relative al post, una lista vuota se il
     *         post non ha segnalazioni
     * @throws PostNotFoundException Se post non è stato pubblicato nella rete
     * @throws NullPointerException  Se post == null
     */
    public List<Reporting> getReportingsForPost(Post post) throws PostNotFoundException;
}
