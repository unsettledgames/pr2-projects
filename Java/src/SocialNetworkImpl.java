import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Exceptions.Likes.*;
import Exceptions.Post.PostAlreadyPublishedException;
import Exceptions.Post.PostNotFoundException;
import Exceptions.User.InvalidUsernameException;
import Exceptions.User.UserAlreadyRegisteredExeption;
import Exceptions.User.UserNotFoundException;

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
 *  Abstraction Function:
 *      f(c): C -> A = <postLikes, userPublished, userFollowing>, in cui:
 *          postLikes: Post -> Set<String>
 *              postLikes(p) = {u | u è un utente che ha messo like a p}
 *          userPublished: String -> Set<Post>
 *              userPublished(u) = {p | p è un post pubblicato da u}
 *          userFollowing: String -> Set<String>
 *              userFollowing(u) = {v | v è seguito da u}
 *
 *  Representation Invariant:
 *      f(c): C -> Bool =
 *      // Nessuna map può essere null
 *      postLikes != null && userPublished != null && userFollowing != null &&
 *
 *      // Nessuna chiave può essere null, vale per tutte le map
 *      foreach k in postLikes.keys -> k != null
 *      foreach k in userPublished.keys -> k != null
 *      foreach k in userFollowing.keys -> k != null
 *
 *      // Nessun valore corrispondente a una chiave può essere null, vale per tutte le map
 *      foreach k in postLikes.keys -> postLikes(k) != null &&
 *      foreach k in userPublished.keys -> userPublished(k) != null &&
 *      foreach k in userFollowing.keys -> userFollowing(k) != null &&
 *
 *      // Nessun valore contenuto nei set delle map può essere null, vale per tutte le map
 *      foreach k in postLikes.keys ->
 *          (foreach s in postLikes(k) -> s != null) &&
 *      foreach k in userPublished.keys ->
 *          (foreach p in userPublished(k) -> p != null) &&
 *      foreach k in userFollowing.keys ->
 *          (foreach u in userFollowing(k) -> u != null) &&
 *
 *      // Un utente non può mettere like a post scritto da sé stesso
 *      foreach k in postLikes.keys -> !postLikes(k).contains(p.author) &&
 *      // Un utente non può seguire sé stesso
 *      foreach k in userFollowing.keys -> !userFollowing(k).contains(k) &&
 *      // Nella map Utente -> Set<Post>, l'utente deve essere l'autore dei post
 *      foreach u in userPublished.keys ->
 *          (foreach p in userPublished(p) -> p.author == u) &&
 *      // I post scritti da un utente devono essere stati pubblicati
 *      foreach u in userPublished.keys ->
 *          (foreach p in userPublished(u) -> postLikes.keys.contains(p)) &&
 *
 *      // Gli utenti che hanno messo like a un post devono essere registrati
 *      foreach p in postLikes ->
 *          (foreach u in postLikes.get(u) ->
 *              userFollowing.keys.contains(u)) &&
 *      // Gli utenti che sono seguiti da un utente devono essere registrati
 *      foreach u in userFollowing.keys ->
 *          (foreach v in userFollowing(u) ->
 *              userFollowing.keys.contains(u)) &&
 *
 *      // Un utente ne segue un altro se e solo se ha messo like ad almeno un suo post
 *      foreach u in userFollowing.keys ->
 *          (foreach v in userFollowing(u) ->
 *              (exists p in postLikes.keys -> postLikes(p).contains(u) && p.author = u)) &&
 *
 *      // I nomi utente devono essere validi
 *      foreach u in userFollowing.keys -> u.matches(USER_REGEX)
 *
 */
public class SocialNetworkImpl implements SocialNetwork{
    /**
     * Collega ogni post della rete sociale all'insieme dei like che ha ricevuto
     */
    protected Map<Post, Set<String>> postLikes;
    /**
     * Collega ogni utente della rete sociale all'insieme dei post che ha pubblicato
     */
    protected Map<String, Set<Post>> userPublished;
    /**
     * Collega ogni utente della rete sociale all'insieme degli utenti che segue
     */
    protected Map<String, Set<String>> userFollowing;

    /**
     * @effects: Costruttore del tipo SocialNetworkImpl.
     * @modifies: this.postLikes, this.userPublished, this.userFollowing
     */
    public SocialNetworkImpl() {
        postLikes = new TreeMap<>();
        userPublished = new TreeMap<>();
        userFollowing = new TreeMap<>();
    }

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
    @Override
    public Map<String, Set<String>> guessFollowers(List<Post> ps) throws PostNotFoundException {
        if (ps == null) {
            throw new NullPointerException("La lista di cui ottenere la rete sociale non può essere null");
        }

        return getFollowersMap(ps);
    }

    /**
     * @effects Fornisce gli utenti della rete sociale in ordine di quantità di followers
     *          non crescente.
     * @return  Gli utenti della rete sociale oridnati per quantità di followers non crescente
     *          rappresentati come elementi di una lista. Ritorna una lista vuota se non
     *          ci sono utenti nella rete.
     */
    @Override
    public List<String> influencers() {
        ArrayList<String> ret = new ArrayList<>();
        Map<String, Set<String>> followersMap = new TreeMap<>();
        Map<String, Integer> nFollowersMap = new TreeMap<String, Integer>();
        ArrayList<Map.Entry<String, Integer>> nFollowersList;

        try {
            followersMap = getFollowersMap(new ArrayList<>(postLikes.keySet()));
        }
        catch (PostNotFoundException ignored) {}

        // Uso una mappa intermedia con il numero di followers
        for (String name : followersMap.keySet()) {
            nFollowersMap.put(name, followersMap.get(name).size());
        }

        nFollowersList = new ArrayList<>(nFollowersMap.entrySet());

        // Ordino in base al numero di followers
        Collections.sort(nFollowersList, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
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

        // Metto in ordine le stringhe nella lista di ritorno
        for (int i=0; i<nFollowersList.size(); i++) {
            ret.add(nFollowersList.get(i).getKey());
        }

        // Aggiungo in fondo gli utenti che non hanno nessun follower
        for (String u : userFollowing.keySet()) {
            if (!ret.contains(u)) {
                ret.add(u);
            }
        }

        return ret;
    }

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
    @Override
    public Set<String> getMentionedUsers() {
        try {
            // Applico la funzione già esistente su tutti i post del social network
            return getMentionedUsers(new ArrayList<Post>(postLikes.keySet()));
        }
        catch (PostNotFoundException ignored) {}

        return null;
    }

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
    @Override
    public Set<String> getMentionedUsers(List<Post> ps) throws PostNotFoundException {
        if (ps == null) {
            throw new NullPointerException("La lista di post in cui cercare non può essere null");
        }
        TreeSet<String> ret = new TreeSet<String>();
        // Trova tutte le stringhe che iniziano con @ seguite da una stringa di caratteri alfanumerici,
        // o underscore lunghe da 1 a 15 caratteri
        Pattern regex = Pattern.compile("@"+USERNAME_REGEX);

        // Scorro tutti i post
        for (Post p : ps) {
            if (p == null) {
                throw new NullPointerException("Uno dei post della lista è null");
            }
            // Controllo che tutti i post siano stati pubblicati
            if (!postLikes.containsKey(p)) {
                throw new PostNotFoundException(p.getId());
            }

            Matcher m = regex.matcher(p.getText());

            while (m.find()) {
                String toAdd = m.group().substring(1);

                // Aggiungo l'utente alla lista delle menzioni soltanto se è registrato
                if (userFollowing.containsKey(toAdd)) {
                    ret.add(toAdd);
                }
            }
        }

        return ret;
    }

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
    @Override
    public List<Post> writtenBy(String username) throws UserNotFoundException {
        if (username == null) {
            throw new NullPointerException("L'autore dei post da cercare non può essere null");
        }
        if (!userFollowing.containsKey(username)) {
            throw new UserNotFoundException(username);
        }

        // Semplicemente ritorno il valore della chiave username nella mappa userPublished
        // sottoforma di List
        return new ArrayList<Post>(userPublished.get(username));
    }

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
    @Override
    public List<Post> writtenBy(List<Post> ps, String username) throws UserNotFoundException, PostNotFoundException {
        if (ps == null) {
            throw new NullPointerException("La lista dei post in cui cercare non può essere null");
        }
        if (username == null) {
            throw new NullPointerException("L'autore dei post da cercare non può essere null");
        }
        if (!userFollowing.containsKey(username)) {
            throw new UserNotFoundException(username);
        }

        // Lista di ritorno
        List<Post> ret = new ArrayList<>();

        // Itero tra i post passati da parametro
        for (Post p : ps) {
            if (p == null) {
                throw new NullPointerException("Uno dei post della lista era null");
            }
            if (!postLikes.containsKey(p)) {
                throw new PostNotFoundException("Il post " + p + " non è stato pubblicato");
            }

            // Se username è l'autore di p, aggiungo p alla lista di ritorno
            if (p.getAuthor().equals(username)) {
                ret.add(p);
            }
        }

        return ret;
    }

    /**
     * @requires words != null && foreach word in words -> word != null
     * @param words La lista delle parole di cui controllare la presenza nei post
     *
     * @effects Fornisce una lista dei post che contengono almeno una tra le parole
     *      contenute nella lista passata come parametro. Una parola che combacia parzialmente ("test" con "testo")
     *      non viene presa in considerazione. La funzione è case insensitive.
     * @return La lista dei post contenti almeno una delle parole presenti in words,
     *         restituisce una lista vuota se non esistono post come quelli descritti sopra.
     * @throws NullPointerException Se words == null || exists(word in words -> word == null)
     */
    @Override
    public List<Post> containing(List<String> words) {
        if (words == null) {
            throw new NullPointerException("La lista di parole da cercare non può essere null");
        }
        // Ottengo tutti i post
        List <Set<Post>> publishedPosts = new ArrayList<>(userPublished.values());
        // Per comodità invece di avere una lista di insiemi, decido di avere una lista di post
        List<Post> posts = new ArrayList<>();
        // Lista di ritorno
        List<Post> ret = new ArrayList<>();
        // Flag che stabilisce se ho trovato una parola (mi permette di interrompere il ciclo appena ne trovo una)
        boolean foundWord;

        // Sono sicuro che i post siano univoci perché svolgo dei controlli quando pubblico un post
        for (int i=0; i<publishedPosts.size(); i++) {
            // Aggiungo alla lista i post di quel set
            posts.addAll(publishedPosts.get(i));
        }

        // Per ogni post
        for (Post p : posts) {
            // Per ogni parola del post
            for (String postWord : p.getText().split(" ")) {
                boolean trovata = false;
                int i=0;

                // Controllo se la parola del post è una di quelle nella lista
                while (!trovata && i <words.size()) {
                    String toCheck = words.get(i);

                    // Se sì, la aggiungo alla lista di ritorno
                    if (toCheck.toLowerCase().equals(postWord.toLowerCase())) {
                        trovata = true;
                        ret.add(p);
                    }

                    i++;
                }
            }
        }

        return ret;
    }

    /**
     * @requires toLike != null && follower != null && postLikes(toLike) != null &&
     *           userFollowing(follower) != null && toLike.author != follower
     * @param toLike Il post a cui mettere like
     * @param follower L'utente che ha messo like a toLike
     *
     * @effects Aggiunge un like da parte di un utente a un post nella rete sociale.
     * @modifies this.postLikes, this.userFollowing
     * @throws PostNotFoundException Se il post a cui mettere like non è presente nella rete sociale
     * @throws AutoLikeException Se follower è l'autore di toLike
     * @throws UserNotFoundException Se follower non è registrato nella rete sociale
     * @throws NullPointerException Se toLike == null || follower == null
     */
    @Override
    public void like(Post toLike, String follower) throws PostNotFoundException,
            AutoLikeException, UserNotFoundException {
        if (toLike == null) {
            throw new NullPointerException("Il post a cui mettere like è null");
        }
        if (follower == null) {
            throw new NullPointerException("L'utente che mette like non può essere null");
        }
        if (!userFollowing.containsKey(follower)) {
            throw new UserNotFoundException(follower);
        }

        if (userPublished.get(follower) != null && userPublished.get(follower).contains(toLike)) {
            throw new AutoLikeException(follower, toLike.getId());
        }

        if (postLikes.containsKey(toLike)) {
            postLikes.get(toLike).add(follower);
            
            userFollowing.get(follower).add(toLike.getAuthor());
        }
        else {
            throw new PostNotFoundException(toLike.getId());
        }
    }

    /**
     * @requires toUnlike != null && follower != null && userFollowing(follower) != null &&
     *           postLikes(toUnlike) != null && postLikes(toUnlike).contains(follower)
     * @param toUnlike Il post a cui togliere like
     * @param follower L'utente che toglie il like da toUnlike
     * @effects Rimuove un like di un post da parte di un utente: se è l'unico post a cui
     *          aveva messo like, l'utente smette di seguire l'autore del post
     * @modifies this.postLikes, this.userFollowing
     * @throws PostNotFoundException Se il post a cui togliere like non è presente nella rete
     * @throws UserNotFoundException Se l'utente che toglie il like non è registrato nella rete
     * @throws InvalidOperationException Se si sta cercando di rimuovere un like non registrato
     * @throws NullPointerException Se toUnlike == null || follower == null
     */
    public void unLike(Post toUnlike, String follower) throws PostNotFoundException, UserNotFoundException,
            LikeNotFoundException {
        if (toUnlike == null) {
            throw new NullPointerException("Il post a cui togliere like non può essere null");
        }
        if (follower == null) {
            throw new NullPointerException("L'utente che toglie il like non può essere null");
        }
        if (!userFollowing.containsKey(follower)) {
            throw new UserNotFoundException(follower);
        }
        if (!postLikes.containsKey(toUnlike)) {
            throw new PostNotFoundException(toUnlike.getId());
        }
        if (!postLikes.get(toUnlike).contains(follower)) {
            throw new LikeNotFoundException(follower, toUnlike.getId());
        }

        // Rimuovo l'utente dai like
        postLikes.get(toUnlike).remove(follower);

        // Se il post era l'unico like, allora follower non segue più l'autore
        if (isOnlyLike(toUnlike, follower)) {
            userFollowing.get(follower).remove(toUnlike.getAuthor());
        }
    }

    /**
     * @requires toPublish != null && postLikes(toPublish) == null &&
     *           userFollowing(toPublish.author) != null
     * @param toPublish Il post da pubblicare
     *
     * @effects Aggiunge un post alla rete sociale.
     * @modifies this.postLikes, this.userPublished
     * @throws PostAlreadyPublishedException Se il post è già stato pubblicato
     * @throws UserNotFoundException Se l'autore del post non è registrat nella rete sociale
     * @throws NullPointerException Se toPublish == null
     */
    public void publishPost(Post toPublish) throws PostAlreadyPublishedException, UserNotFoundException {
        if (toPublish == null) {
            throw new NullPointerException("Il post da pubblicare non può essere null");
        }
        if (postLikes.containsKey(toPublish)) {
            throw new PostAlreadyPublishedException(toPublish.getId());
        }
        if (!userFollowing.containsKey(toPublish.getAuthor())) {
            throw new UserNotFoundException(toPublish.getAuthor());
        }

        // Metto il post nella mappa dei like
        postLikes.put(toPublish, new TreeSet<>());
        // Aggiungo un post all'autore
        // Se l'autore non è già presente, creo un nuovo treeset
        if (userPublished.get(toPublish.getAuthor()) == null) {
            userPublished.put(toPublish.getAuthor(), new TreeSet<>());
        }
        userPublished.get(toPublish.getAuthor()).add(toPublish);
    }

    /**
     * @requires toDelete != null && postLikes(toDelete) != null
     * @param toDelete  Il post da rimuovere dalla rete sociale.
     *
     * @effects Rimuove un post dalla rete sociale: rimuove anche i like di quel post, di
     *          conseguenza se il post era l'unico like da parte di un certo utente,
     *          quell'utente smette di seguire l'autore del post.
     * @modifies this.postLikes, this.userPublished, this.userFollowing
     * @throws PostNotFoundException Se toDelete non è presente all'interno della rete sociale
     * @throws NullPointerException Se toDelete == null
     */
    public void deletePost(Post toDelete) throws PostNotFoundException{
        if (toDelete == null) {
            throw new NullPointerException("Il post da cancellare non può essere null");
        }
        if (!postLikes.containsKey(toDelete)) {
            throw new PostNotFoundException(toDelete.getId());
        }

        // Tolgo il post e cancello i like a quel post
        postLikes.remove(toDelete);
        // Rimuovo il post da quelli pubblicati dall'utente
        userPublished.get(toDelete.getAuthor()).remove(toDelete);

        // Per ogni utente, controllo se il post era l'unico like o no
        for (String s : userFollowing.keySet()) {
            // Se era l'unico like, allora l'utente non segue più l'autore del post
            if (isOnlyLike(toDelete, s)) {
                userFollowing.get(s).remove(toDelete.getAuthor());
            }
        }
    }

    /**
     * @requires toRegister != null && userFollowing(toRegister) == null &&
     *           toRegister.matches(USERNAME_REGEX)
     * @param toRegister Il nome dell'utente da registrare nella rete sociale.
     *
     * @effects Aggiunge un utente alla rete sociale
     * @modifies this.userFollowing, this.userPublished
     * @throws InvalidUsernameException Se lo username è composto da caratteri che non sono
     *      alfanumerici o underscores (ovvero se !toRegister.matches(USERNAME_REGEX)
     * @throws UserAlreadyRegisteredExeption Se l'utente toRegister è già presente nella rete
     * @throws NullPointerException Se toRegister == null
     */
    @Override
    public void registerUser(String toRegister) throws InvalidUsernameException, UserAlreadyRegisteredExeption {
        if (toRegister == null) {
            throw new NullPointerException("L'utente da registrare non può essere null");
        }
        if (userFollowing.containsKey(toRegister)) {
            throw new UserAlreadyRegisteredExeption(toRegister);
        }

        Pattern regex = Pattern.compile(USERNAME_REGEX);
        Matcher m = regex.matcher(toRegister);

        if (!m.matches()) {
            throw new InvalidUsernameException(toRegister);
        }

        // Inizializzo le mappe relative all'utente
        userFollowing.put(toRegister, new TreeSet<>());
        userPublished.put(toRegister, new TreeSet<>());
    }

    /**
     * @requires user != null && userFollowing(user) != null
     * @param user L'utente da rimuovere dalla rete sociale
     *
     * @effects Rimuove un utente dalla rete sociale: rimuove anche tutti i suoi post e i
     *          likes da lui o da lei pubblicati, e rimuove tale utente
     *          dalla lista degli utenti seguiti degli altri.
     * @modifies this.postLikes, this.userPublished, this.userFollowing
     * @throws UserNotFoundException Se l'utente non è registrato nella rete
     * @throws NullPointerException Se user == null
     */
    @Override
    public void removeUser(String user) throws UserNotFoundException {
        if (user == null) {
            throw new NullPointerException("L'utente da rimuovere dalla rete non può essere null");
        }
        if (!userFollowing.containsKey(user)) {
            throw new UserNotFoundException(user);
        }

        // Cancello tutti i post dell'utente
        for (Post p : new ArrayList<>(userPublished.get(user))) {
            try {
                deletePost(p);
            }
            catch (PostNotFoundException e) {
                e.printStackTrace();
            }
        }
        userPublished.remove(user);

        // Rimuovo tutti i like messi dall'utente
        for (Post p : postLikes.keySet()) {
            postLikes.get(p).remove(user);
        }

        // L'utente cancellato non segue più nessuno
        userFollowing.remove(user);
    }

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
    public List<String> getTrending() {
        Pattern regex = Pattern.compile("#\\w+");
        List<String> ret = new ArrayList<>();
        Map<String, Integer> tagCount = new TreeMap<>();
        List<Map.Entry<String, Integer>> toSort;

        for (Post p : postLikes.keySet()) {
            Matcher m = regex.matcher(p.getText());

            while (m.find()) {
                String currentTag = m.group().substring(1).toLowerCase();
                if (!tagCount.containsKey(currentTag)) {
                    tagCount.put(currentTag, 0);
                }
                // Incremento di 1 il numero di occorrenze del tag
                tagCount.put(currentTag, tagCount.get(currentTag) + 1);
            }
        }

        // Metto in una lista le entry
        toSort = new ArrayList<>(tagCount.entrySet());
        // Ordino la lista per valore
        toSort.sort(new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
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

        // Aggiungo tutte le stringhe in ordine alla lista di ritorno
        for (Map.Entry<String, Integer> entry : toSort) {
            ret.add(entry.getKey());
        }

        return ret;
    }

    /**
     * @effects Fornisce una map Utente -> Set<Utente> che rappresenta i follower di un
     *          certo utente.
     * @return  Una map Utente -> Set<Utente> che rappresenta l'insieme dei follower
     *          degli utenti chiave
     */
    private Map<String, Set<String>> getFollowersMap(List<Post> ps) throws PostNotFoundException {
        Map<String, Set<String>> ret = new TreeMap<>();

        // Per ogni post, l'autore è una chiave, la lista delle persone a cui piace sono i valori
        for (Post p : ps) {
            if (p == null) {
                throw new NullPointerException("Uno dei post della lista era null");
            }
            if (!postLikes.containsKey(p)) {
                throw new PostNotFoundException(p.getId());
            }
            // Se non ho mai incontrato l'autore prima, creo un set
            if (ret.get(p.getAuthor()) == null) {
                ret.put(p.getAuthor(), new TreeSet<>());
            }

            // Aggiungo tutti i like del post al set
            for (String like : postLikes.get(p)) {
                ret.get(p.getAuthor()).add(like);
            }
        }

        return ret;
    }

    /**
     * @requires liked != null && follower != null
     * @param liked Il post di cui controllare che sia l'unico like di follower
     * @param follower L'utente di cui controllare che post sia l'unico like
     *
     * @effects Controlla che liked sia l'unico post pubblicato da liked.author a cui
     *          follower abbia messo like
     * @return true se liked è l'unico post a cui follower ha messo like tra i post di
     *         liked.author
     *         false in ogni altro caso
     * @throws NullPointerException se liked == null || follower == null
     */
    private boolean isOnlyLike(Post liked, String follower) {
        if (follower == null) {
            throw new NullPointerException("L'utente di cui verificare se è l'unico like non può" +
                    " esere null");
        }
        if (liked == null) {
            throw new NullPointerException("Il post di cui verificare se è l'unico like di follower" +
                    " non può essere null");
        }

        // Se il post era l'unico tra quelli pubblicati dall'autore di liked seguito dal follower
        for (Map.Entry<Post, Set<String>> entry : postLikes.entrySet()) {
            if (!entry.getKey().equals(liked) && entry.getValue().contains(follower) &&
                    entry.getKey().getAuthor().equals(liked.getAuthor())) {
                return false;
            }
        }

        return true;
    }
}
