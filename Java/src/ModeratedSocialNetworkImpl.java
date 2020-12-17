import Exceptions.Post.PostNotFoundException;
import Exceptions.Reporting.AutoReportException;
import Exceptions.Post.PostAlreadyPublishedException;
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
 *  Typical Element:
 *      <postLikes, userPublished, userFollowing, reportings, forbiddenWords>, in cui:
 *          - postLikes, userPublished e userFollowing descritti come in SocialNetwork
 *          - reportings = {(p1, {r1, r2, ...}), (p2, {s1, s2, ...}), ..} in cui
 *              reportings(p): Post -> {r1, r2, ...} in cui ri è una segnalazione
 *              relativa al post p
 *          - forbiddenWords = {w1, w2, w3, ...} in cui wi è una parola il cui uso
 *              può essere considerato offensivo o fonte di comportamenti
 *              inappropriati all'interno della rete sociale
 *
 *  Abstraction Function:
 *      f(c): C -> A = <postLikes, userPublished, userFollowing, reportings, forbiddenWords>, in cui:
 *          postLikes, userPublished e userFollowing sono descritte come in SocialNetwork
 *          reportings: Post -> Set<Reporting>
 *              reportings(p) = {r | r è una segnalazione del post p}
 *          forbiddenWords: List<String>
 *              forbiddenWords(i) = s | s è una parola considerata inappropriata per la rete sociale
 *
 *  Representation invariant:
 *      f(c): C -> Bool = super.f(c) &&
 *      // Gli attributi non possono essere null
 *      reportings != null && forbiddenWords != null &&
 *
 *      // Le chiavi non possono essere null
 *      foreach k in reporting.keys -> k != null
 *
 *      // Gli insiemi collegati alle chiavi non possono essere null
 *      foreach k in reporting.keys -> reporting(k) != null
 *
 *      // Gli elementi degli insiemi collegati alle chiavi non possono essere null
 *      foreach k in reportings.keys ->
 *          (foreach r in reportings(k) -> r != null)
 *
 *      // Le parole inappropriate non possono essere null
 *      foreach e in forbiddenWords -> e != null
 *
 *      // Gli autori delle segnalazioni devono essere registrati
 *      foreach p in reportings.keys ->
 *          (foreach r in reportings(p) -> userFollowing(r.author) != null) &&
 *
 *      // Gli utenti non possono segnalare post scritti da loro stessi
 *      foreach p in reportings.keys ->
 *          (foreach r in reportings(p) -> r.author != p.author) &&
 *
 *      // I post segnalati devono essere stati pubblicati
 *      foreach p in reportings.keys -> postLikes(p) != null &&
 *
 *      // Le segnalazioni automatiche devono avere un autore "-System-"
 *      foreach p in reportings.keys ->
 *          (foreach r in reportings(p) -> r.author == "-System-") &&
 *      // Le segnalazioni automatiche devono avere peso 1
 *      foreach p in reporting.leys ->
 *          (foreach r in reportings(p) -> r.automatic => r.weight == 1) &&
 *      // Le segnalazioni non automatiche devono avere peso 2
 *      foreach p in reporting.leys ->
 *          (foreach r in reportings(p) -> !r.automatic => r.weight == 2)
 *
 */
public class ModeratedSocialNetworkImpl extends SocialNetworkImpl implements ModeratedSocialNetwork{
    /**
     * Collega ogni post della rete sociale a un insieme di segnalazioni
     */
    private Map<Post, Set<Reporting>> reportings;
    /**
     * Lista delle parole considerate inappropriate
     */
    private Set<String> forbiddenWords;

    /**
     * @requires forbiddenWords != null
     * @param forbiddenWords La lista delle parole considerate inappropriate per questa rete
     *
     * @effects Crea un oggetto di tipo ModeratedSocialNetworkImpl
     * @modifies this.forbiddenWords, this.reportings, this.postLikes, this.userPublished,
     *           this.userFollowing
     * @throws  NullPointerException Se forbiddenWords == null
     */
    public ModeratedSocialNetworkImpl(Set<String> forbiddenWords) {
        super();

        if (forbiddenWords == null) {
            throw new NullPointerException("La lista delle parole proibite non può essere null");
        }

        this.forbiddenWords = new TreeSet<>();
        for (String s : forbiddenWords) {
            this.forbiddenWords.add(s.toLowerCase());
        }
        reportings = new TreeMap<>();
    }

    /**
     * @requires author != null && post != null && userFollowing(author) != null &&
     *           postLikes(post) != null
     * @param author L'autore della segnalazione
     * @param post   Il post da segnalare
     *
     * @effects Aggiunge a un post una segnalazione. Tale segnalazione può essere automatica
     *          e aggiunta nel momento in cui il post viene pubblicato oppure può essere
     *          inviata da un utente: quest'ultimo tipo di segnalazioni ha un peso maggiore.
     * @modifies this.reportings
     * @throws ReportingAlreadySentException Se author ha già inviato una segnalazione per post
     * @throws AutoReportException Se author sta cercando di segnalare un post da egli stesso
     *                             pubblicato
     * @throws UserNotFoundException Se author non è parte del social network
     * @throws PostNotFoundException Se postLikes(post) == null
     * @throws NullPointerException Se author == null || post == null
     */
    @Override
    public void report(String author, Post post)
            throws ReportingAlreadySentException, AutoReportException, UserNotFoundException, PostNotFoundException {
        if (post == null) {
            throw new NullPointerException("Il post da segnalare non può essere null");
        }
        if (postLikes.get(post) == null) {
            throw new PostNotFoundException(post.getId());
        }
        if (author == null) {
            throw new NullPointerException("L'utente che invia la segnalazione non può essere null");
        }
        if (!userFollowing.containsKey(author)) {
            throw new UserNotFoundException(author);
        }
        if (post.getAuthor().equals(author)) {
            throw new AutoReportException("Impossibile segnalare un post scritto da sé stessi");
        }

        Reporting reporting = new ReportingImpl(author, MANUAL_REPORTING_WEIGHT);

        if (reportings.get(post) != null) {
            for (Reporting r : reportings.get(post)) {
                if (r.getAuthor().equals(author)) {
                    throw new ReportingAlreadySentException("L'utente " + author + " ha già segnalato questo post");
                }
            }
        }

        if (reportings.get(post) == null) {
            reportings.put(post, new TreeSet<>());
        }

        reportings.get(post).add(reporting);
    }

    /**
     * @effects Fornisce la lista dei post presenti nella rete sociale ordinata in ordine
     *          non crescente di somma dei pesi delle segnalazioni. Se un post non ha
     *          segnalazioni, non viene aggiunto alla lista. Se non ci sono segnalazioni, la lista
     *          che viene fornita è vuota.
     * @return  La lista dei post della rete ordinata in ordine non crescente di somma
     *          dei pesi delle segnalazioni
     */
    @Override
    public List<Post> getControversialPosts() {
        Map<Post, Integer> nReportingsMap = new TreeMap<>();
        List<Map.Entry<Post, Integer>> reportingsEntrySet;
        List<Post> ret = new ArrayList<>();

        // Metto ogni post con il peso delle segnalazioni
        for (Post p : reportings.keySet()) {
            Integer totalWeight = 0;

            for (Reporting r : reportings.get(p)) {
                totalWeight += r.getWeight();
            }

            if (totalWeight > 0) {
                nReportingsMap.put(p, totalWeight);
            }
        }

        reportingsEntrySet = new ArrayList<>(nReportingsMap.entrySet());

        // Ordino i post per peso totale delle segnalazioni
        Collections.sort(reportingsEntrySet, new Comparator<Map.Entry<Post, Integer>>() {
            public int compare(Map.Entry<Post, Integer> a, Map.Entry<Post, Integer> b) {
                if (a.getValue() < b.getValue()) {
                    return 1;
                }
                else if (a.getValue().equals(b.getValue())) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
        });

        // Aggiungo tutto alla lista di ritorno
        for (int i=0; i<reportingsEntrySet.size(); i++) {
            ret.add(reportingsEntrySet.get(i).getKey());
        }

        return ret;
    }

    /**
     * @requires toAdd != null && !forbiddenWords.contains(toAdd)
     * @param toAdd La parola da aggiungere all'insieme
     *
     * @effects Aggiunge una parola all'insieme delle parole considerate inappropriate
     * @modifies this.forbiddenWords
     * @throws NullPointerException Se toAdd == null
     */
    @Override
    public void addForbiddenWord(String toAdd) {
        if (toAdd == null) {
            throw new NullPointerException("Impossibile aggiungere una stringa null alla lista delle parole proibite");
        }

        forbiddenWords.add(toAdd);
    }

    /**
     * @requires toRemove != null && forbiddenWords.contains(toRemove)
     * @param toRemove La parola da rimuovere dall'insieme
     *
     * @effects Rimuove toRemove dalla lista delle parole considerate inappropriate
     * @modifies this.forbiddenWords
     * @throws NoSuchElementException Se toRemove non appartiene all'insieme
     * @throws NullPointerException Se toRemove == null
     */
    @Override
    public void removeForbiddenWord(String toRemove) {
        if (toRemove == null) {
            throw new NullPointerException("Impossibile rimuovere una stringa null alla lista delle parole proibite");
        }

        if (!forbiddenWords.contains(toRemove)) {
            throw new NoSuchElementException("La parola da rimuovere non era presente nella lista");
        }
        forbiddenWords.remove(toRemove);
    }

    /**
     * @requires author != null && userFollowing(author) != null
     * @param author L'autore delle segnalazioni
     *
     * @effects Fornisce una lista delle segnalazioni inviate dall'utente passato come
     *          parametro; fornisce una lista vuota se l'utente non ha inviato segnalazioni
     *          per nessun post.
     * @return La lista delle segnalazioni inviate da author, una lista vuota se
     *         author non ha mai inviato segnalazioni
     * @throws UserNotFoundException Se author non è registrato nella rete
     * @throws NullPointerException Se author == null
     */
    @Override
    public List<Reporting> getReportingsByAuthor(String author) throws UserNotFoundException {
        if (author == null) {
            throw new NullPointerException("L'autore delle segnalazioni non può essere null");
        }
        if (!userFollowing.containsKey(author)) {
            throw new UserNotFoundException(author);
        }

        List<Reporting> ret = new ArrayList<>();

        // Per ogni set di segnalazioni nella mappa
        for (Set<Reporting> reportingSet : reportings.values()) {
            // Per ogni segnalazione nel set
            for (Reporting r : reportingSet) {
                // Controllo che la segnalazione sia stata inviata da author
                if (r.getAuthor().equals(author)) {
                    // In tal caso la aggiungo alla lista di ritorno
                    ret.add(r);
                }
            }
        }

        return ret;
    }

    /**
     * @requires post != null && postLikes(post) == null
     * @param post Il post di cui ottenere le segnalazioni
     *
     * @effects Fornisce una lista delle segnalazioni relative a un post passato come
     *          parametro
     * @return  La lista delle segnalazioni relative al post, una lista vuota se il
     *          post non ha segnalazioni
     * @throws PostNotFoundException Se post non è stato pubblicato nella rete
     * @throws NullPointerException  Se post == null
     */
    @Override
    public List<Reporting> getReportingsForPost(Post post) throws PostNotFoundException {
        if (post == null) {
            throw new NullPointerException("Il post di cui ottenre le segnalazioni non può essere null");
        }
        if (!postLikes.containsKey(post)) {
            throw new PostNotFoundException(post.getId());
        }

        Set<Reporting> rep = reportings.get(post);
        if (rep == null) {
            return new ArrayList<Reporting>();
        }
        else {
            return new ArrayList<>(rep);
        }
    }

    /**
     * @requires toDelete != null && postLikes(toDelete) != null
     * @param toDelete  Il post da rimuovere dalla rete sociale.
     *
     * @effects Rimuove un post dalla rete sociale: rimuove anche i like di quel post, di
     *          conseguenza se il post era l'unico like da parte di un certo utente,
     *          quell'utente smette di seguire l'autore del post. Inoltre rimuove
     *          tutte le segnalazioni relative a quel post.
     * @modifies this.postLikes, this.userPublished, this.userFollowing, this.reportings
     * @throws PostNotFoundException Se toDelete non è presente all'interno della rete sociale
     * @throws NullPointerException Se toDelete == null
     */
    @Override
    public void deletePost(Post toDelete) throws PostNotFoundException {
        super.deletePost(toDelete);

        // Rimuovo anche tutte le segnalazioni relative al post
        reportings.remove(toDelete);
    }

    /**
     * @requires user != null && userFollowing(user) != null
     * @param user L'utente da rimuovere dalla rete sociale
     *
     * @effects Rimuove un utente dalla rete sociale: rimuove anche tutti i suoi post e i
     *          likes da lui o da lei pubblicati, e rimuove tale utente
     *          dalla lista degli utenti seguiti degli altri. Rimuove inoltre tutte
     *          le segnalazioni effettuate dall'utente.
     * @modifies this.postLikes, this.userPublished, this.userFollowing, this.reportings
     * @throws UserNotFoundException Se l'utente non è registrato nella rete
     * @throws NullPointerException Se user == null
     */
    @Override
    public void removeUser(String user) throws UserNotFoundException {
        super.removeUser(user);
        Map<Post, Set<Reporting>> copy = new TreeMap<>(reportings);

        // Rimuovo anche tutte le segnalazioni effettuate dall'utente
        for (Post p : copy.keySet()) {
            for (Reporting r : copy.get(p)) {
                if (r.getAuthor().equals(user)) {
                    reportings.get(p).remove(r);
                }
            }
        }
    }

    /**
     * @requires toPublish != null && postLikes(toPublish) == null &&
     *           userFollowing(toPublish.author) != null
     * @param toPublish Il post da pubblicare
     *
     * @effects Aggiunge un post alla rete sociale.
     * @modifies this.postLikes, this.userPublished
     * @throws UserNotFoundException Se l'autore del post non è registrato nella rete sociale
     * @throws NullPointerException Se toPublish == null
     */
    @Override
    public void publishPost(Post toPublish) throws UserNotFoundException{
        try {
            super.publishPost(toPublish);
        }
        catch (PostAlreadyPublishedException e) {
            System.err.println("Il controllo delle parole proibite è già stato effettuato per il post " + toPublish.getId()
            + ": potrebbe essere già stato pubblicato");
        }

        for (String s : toPublish.getText().split(" ")) {
            for (String w : forbiddenWords) {
                if (s.toLowerCase().contains(w)) {
                    automaticReport(toPublish);
                }
            }
        }
    }

    /**
     * @requires p != null && postLikes(p) != null
     * @param p Il post a cui aggiungere una segnalazione automatica
     *
     * @effects Aggiunge al social network una segnalazione automatica per il post p.
     * @modifies this.reportings
     */
    private void automaticReport(Post p) {
        Reporting reporting = new ReportingImpl(Reporting.AUTOMATIC_REPORTING_AUTHOR, AUTOMATIC_REPORTING_WEIGHT);

        if (reportings.get(p) == null) {
            reportings.put(p, new TreeSet<>());
        }

        reportings.get(p).add(reporting);
    }
}
