import Exceptions.Post.PostAlreadyPublishedException;
import Exceptions.Post.PostNotFoundException;
import Exceptions.User.InvalidUsernameException;
import Exceptions.User.UserAlreadyRegisteredExeption;
import Exceptions.User.UserNotFoundException;
import Exceptions.Likes.*;

import java.util.*;

/** Overview:
 *      SocialNetwork è un dato mutabile che rappresenta una rete sociale. Sono disponibili
 *      diverse funzioni di analisi dei contenuti (guessFollowers, influencers, getTrending,
 *      getMentionedUsers, writtenBy, containing) e di salvataggio di eventi (like, unlike,
 *      registerUser, removeUser, publishPost, deletePost).
 *
 *  Typical element:
 *      <postLikes, userPublished, userFollowing> in cui
 *          - postLikes = {(p1, {u1, u2, ... }), (p2, {v1, v2, ...}), ...}
 *              in cui postLikes(p) : Post -> {u1, u2...}, in cui ui è un utente che ha messo
 *              like a p
 *          - userPublished = {(u1, {p1, p2, ...}), (u2, {q1, q2, ...}, ...}
 *              in cui userPublished(u): Utente -> {p1, p2, ...} in cui pi è un post
 *              pubblicato da u
 *          - userFollowing = {(u1, {v1, v2, ...}, (u2, {w1, w2, ...}), ...}
 *              in cui userPublished(u) : Utente -> {u1, u2, ...}, in cui ui è un utente
 *              seguito da u
 *
 */
public interface SocialNetwork {
    /**
     * L'espressione regolare che permette di controllare la validità di uno username, che
     * può essere composto da caratteri alfanumerici e underscores.
     */
    public static final String USERNAME_REGEX = "\\w{1,15}\\b";

    /**
     * @requires ps != null && foreach p in ps -> p != null &&
     *           foreach p in ps -> postLikes(p) != null
     *
     * @param ps Lista di post di cui ottenere la rete sociale
     *
     * @effects Ritorna una rete sociale che collega gli autori dei post in ps agli utenti
     *          che li seguono.
     * @return  La rete sociale ottenuta dalla lista ps e rappresentata sottoforma di mappa
     *          con chiave data dall'utente (autore di un post in ps) e valori che sono
     *          costituiti da insiemi di utenti (i follower del relativo utente chiave).
     * @throws PostNotFoundException Se uno dei post in ps non è presente nella rete sociale
     * @throws NullPointerException Se ps == null || exists(p in ps -> p == null)
     */
    public Map<String, Set<String>> guessFollowers(List<Post> ps) throws PostNotFoundException;

    /**
     * @effects Fornisce gli utenti della rete sociale in ordine di quantità di followers
     *          non crescente.
     * @return  Gli utenti della rete sociale oridnati per quantità di followers non crescente
     *          rappresentati come elementi di una lista. Ritorna una lista vuota se non
     *          ci sono utenti nella rete.
     */
    public List<String> influencers ();

    /**
     * @effects Fornisce l'insieme degli utenti menzionati da altri utenti all'interno del
     *          social network. Una menzione avviene quando un utente include nel testo di un
     *          post la dicitura "@nome_utente" in cui @ segnala che la parola seguente è un
     *          nome utente e nome_utente è il nome dell'utente menzionato. Una stringa del
     *          tipo @nome_utente si considera menzione se e solo se nome_utente è un utente
     *          registrato nella rete.
     *
     * @return  L'insieme degli utenti menzionati nella rete sociale, ritorna un insieme vuoto
     *          se non ce ne sono.
     */
    public Set<String> getMentionedUsers() throws PostNotFoundException;

    /**
     * @requires    ps != null && foreach p in ps -> p != null &&
     *              foreach p in ps -> postLikes(p) != null
     * @param ps    La lista di post da cui ottenere gli utenti menzionati.
     *
     * @effects Fornisce l'insieme degli utenti menzionati da altri utenti all'interno della
     *          lista passata come parametro. Una menzione avviene quando un utente include
     *          nel testo di un post la dicitura "@nome_utente" in cui @ segnala che la parola
     *          seguente è un nome utente e nome_utente è il nome dell'utente menzionato.
     *          Una stringa del tipo @nome_utente si considera menzione se e solo se
     *          "nome_utente" è un utente registrato nella rete.
     * @return  L'insieme degli utenti menzionati all'interno della lista di post ps, ritorna
     *          un insieme vuoto se non ce ne sono.
     *
     * @throws NullPointerException se ps == null || exists(p in ps -> p == null)
     * @throws PostNotFoundException Se uno dei post della lista ps non è stato pubblicato
     */
    public Set<String> getMentionedUsers(List<Post> ps) throws PostNotFoundException;

    /**
     * @requires username != null && userFollowing.containsKey(username)
     * @param username  L'utente di cui ottenere i post
     *
     * @effects Fornisce la lista dei post scritti dall'utente con nome utente "username"
     * @return  I post scritti dall'utente "username" sottoforma di lista, restituisce una
     *          lista vuota se l'utente non ha scritto nessun post.
     *
     * @throws UserNotFoundException Se l'utente username non è registrato nel social network
     * @throws NullPointerException Se username == null
     */
    public List<Post> writtenBy(String username) throws UserNotFoundException;

    /**
     * @requires username != null && ps != null && foreach p in ps -> p != null &&
     *           userFollowing.containsKey(username) && foreach p in ps -> postLikes(p) != null
     * @param ps La lista dei post in cui cercare post scritti da username
     * @param username Utente di cui fornire la lista dei post da lui o da lei scritti
     *
     * @effects Fornisce la lista dei post appartenenti alla lista passata come parametro
     *          e scritti dall'utente anch'esso passato come parametro.
     * @return  La lista dei post appartenenti a ps e scritti da username. Ritorna una lista
     *          vuota se non ne esistono.
     * @throws UserNotFoundException Se l'utente username non è registrato nel social network
     * @throws PostNotFoundException Se uno dei post passati come parametro non è stato pubblicato
     * @throws NullPointerException Se ps == null || exists(p in ps -> p == null)
     */
    public List<Post> writtenBy(List<Post> ps, String username)
            throws UserNotFoundException, PostNotFoundException;

    /**
     * @requires words != null && foreach word in words -> word != null
     * @param words La lista delle parole di cui controllare la presenza nei post
     *
     * @effects Fornisce una lista dei post che contengono almeno una tra le parole
     *          contenute nella lista passata come parametro. Una parola che combacia parzialmente ("test" con "testo")
     *          non viene presa in considerazione. La funzione è case insensitive.
     * @return La lista dei post contenti almeno una delle parole presenti in words,
     *         restituisce una lista vuota se non esistono post come quelli descritti sopra.
     * @throws NullPointerException Se words == null || exists(word in words -> word == null)
     */
    public List<Post> containing(List<String> words);

    /**
     * @requires toLike != null && follower != null && postLikes(toLike) != null &&
     *           userFollowing(follower) != null && toLike.author != follower
     * @param toLike Il post a cui mettere like
     * @param follower L'utente che ha messo like a toLike
     *
     * @effects Aggiunge un like da parte di un utente a un post nella rete sociale.
     * @throws PostNotFoundException Se il post a cui mettere like non è presente nella rete sociale
     * @throws AutoLikeException Se follower è l'autore di toLike
     * @throws UserNotFoundException Se follower non è registrato nella rete sociale
     * @throws NullPointerException Se toLike == null || follower == null
     */
    public void like(Post toLike, String follower) throws PostNotFoundException,
            AutoLikeException, UserNotFoundException;

    /**
     * @requires toUnlike != null && follower != null && userFollowing(follower) != null &&
     *           postLikes(toUnlike) != null && postLikes(toUnlike).contains(follower)
     * @param toUnlike Il post a cui togliere like
     * @param follower L'utente che toglie il like da toUnlike
     * @effects Rimuove un like di un post da parte di un utente: se è l'unico post a cui
     *          aveva messo like, l'utente smette di seguire l'autore del post
     * @throws PostNotFoundException Se il post a cui togliere like non è presente nella rete
     * @throws UserNotFoundException Se l'utente che toglie il like non è registrato nella rete
     * @throws LikeNotFoundException Se si sta cercando di rimuovere un like non registrato
     * @throws NullPointerException Se toUnlike == null || follower == null
     */
    public void unLike(Post toUnlike, String follower) throws PostNotFoundException, UserNotFoundException,
            LikeNotFoundException;

    /**
     * @requires toPublish != null && postLikes(toPublish) == null &&
     *           userFollowing(toPublish.author) != null
     * @param toPublish Il post da pubblicare
     *
     * @effects Aggiunge un post alla rete sociale.
     * @throws PostAlreadyPublishedException Se il post è già stato pubblicato
     * @throws UserNotFoundException Se l'autore del post non è registrato nella rete sociale
     * @throws NullPointerException Se toPublish == null
     */
    public void publishPost(Post toPublish) throws PostAlreadyPublishedException,
            UserNotFoundException;

    /**
     * @requires toDelete != null && postLikes(toDelete) != null
     * @param toDelete  Il post da rimuovere dalla rete sociale.
     *
     * @effects Rimuove un post dalla rete sociale.
     * @throws PostNotFoundException Se toDelete non è presente all'interno della rete sociale
     * @throws NullPointerException Se toDelete == null
     */
    public void deletePost(Post toDelete) throws PostNotFoundException;

    /**
     * @requires toRegister != null && userFollowing(toRegister) == null &&
     *           toRegister.matches(USERNAME_REGEX)
     * @param toRegister Il nome dell'utente da registrare nella rete sociale.
     *
     * @effects Aggiunge un utente alla rete sociale
     * @throws InvalidUsernameException Se lo username è composto da caratteri che non sono
     *      alfanumerici o underscores (ovvero se !toRegister.matches(USERNAME_REGEX)
     * @throws UserAlreadyRegisteredExeption Se l'utente toRegister è già presente nella rete
     * @throws NullPointerException Se toRegister == null
     */
    public void registerUser(String toRegister) throws InvalidUsernameException,
            UserAlreadyRegisteredExeption;

    /**
     * @requires user != null && userFollowing(user) != null
     * @param user L'utente da rimuovere dalla rete sociale
     *
     * @effects Rimuove un utente dalla rete sociale.
     * @throws UserNotFoundException Se l'utente non è registrato nella rete
     * @throws NullPointerException Se user == null
     */
    public void removeUser(String user) throws UserNotFoundException;

    /**
     * @effects Fornisce la lista degli hashtag presenti all'interno della rete in ordine
     *          non crescente di numero di occorrenze all'interno dei testi dei post.
     *          Si considera hashtag una qualsiasi stringa alfanumerica
     *          potenzialmente contenente underscores (la lunghezza di un hashtag è
     *          limitata alla lunghezza del testo di un post, overo 140 caratteri). Gli
     *          hashtags sono case insensitive.
     * @return  La lista degli hashtag presenti all'interno della rete in ordine di
     *          numero di occorrenze non crescente.
     */
    public List<String> getTrending();
}
