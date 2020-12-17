import java.util.*;

import Exceptions.Likes.AutoLikeException;
import Exceptions.Likes.LikeNotFoundException;
import Exceptions.Post.EmptyContentException;
import Exceptions.Post.PostAlreadyPublishedException;
import Exceptions.Post.PostLengthExceededException;
import Exceptions.Post.PostNotFoundException;
import Exceptions.Reporting.AutoReportException;
import Exceptions.Reporting.ReportingAlreadySentException;
import Exceptions.User.InvalidUsernameException;
import Exceptions.User.UserNotFoundException;

public class Main {
    public static void main(String[] args) {
        List<Post> allPosts = new ArrayList<>();
        Set<String> resultSetString = new TreeSet<>();
        List<String> resultListString = new ArrayList<>();
        List<Post> resultListPost = new ArrayList<>();
        List<Reporting> resultListReportings = new ArrayList<>();
        List<String> wordList = new ArrayList<>();
        Map<String, Set<String>> socialNetwork = new TreeMap<String, Set<String>>();

        /********************************************INIZIO TEST**************************************************/
        System.out.println("INIZIO TEST SOCIALNETWORKIMPL");

        String marco = "Marco";
        String anna = "Anna_00";
        String federico = "Fed_erico_98";
        String laura = "_laura_";
        String michele = "MIcHe_Le";
        String sofia = "Sooooooooooofia";

        String nomeLungo = "NomeUtenteEstremamenteLungo";
        String nomeSbagliato = "?!$%&vietato";

        System.out.println("\nTest creazione post\n");

        /**************************************CREAZIONE POST**************************************************/

        Post postMarco1, postMarco2, postAnna1, postFederico1, postLaura1, postMichele1, postAnna2, unpublished, postSofia1;

        postMarco1 = null;
        postMarco2 = null;
        postAnna1 = null;
        postLaura1 = null;
        postFederico1 = null;
        postMichele1 = null;
        postAnna2 = null;
        unpublished = null;
        postSofia1 = null;

        try {
            postMarco1 = new PostImpl(marco, "#Testo del #post di marco");
            postMarco2 = new PostImpl(marco, "Secondo #post di marco");
            postAnna1 = new PostImpl(anna, "Post di esempio per anna (esempio)");
            postFederico1 = new PostImpl(federico, "#Testo del #post di federico che menziona @Anna_00");
            postLaura1 = new PostImpl(laura, "Anche laura menziona @Anna_00");
            postMichele1 = new PostImpl(michele, "Michele vorrebbe menzionare @anna ma ha sbagliato a scrivere");
            postAnna2 = new PostImpl(anna, "Un altro post di Anna");
            unpublished = new PostImpl(michele, "Post non pubblicato");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione dei post");
        }

        try  {
            allPosts.add(postMarco1);
            allPosts.add(postMarco2);
            allPosts.add(postAnna1);
            allPosts.add(postFederico1);
            allPosts.add(postLaura1);
            allPosts.add(postMichele1);
            allPosts.add(postAnna2);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella pubblicazione dei post");
        }

        try {
            System.out.println("Provo a scrivere un post con autore null");
            postSofia1 = new PostImpl(null, "Prova");
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        try {
            System.out.println("Provo a scrivere un post con testo null");
            postSofia1 = new PostImpl(sofia, null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        System.out.println("Provo a scrivere un post con lunghezza eccessiva");
        try {
            postSofia1 = new PostImpl(sofia, "Sofia sta cercando di scrivere un post con un" +
                    "testo estremamente lungo e con un numero di caratteri sicuramente maggiore" +
                    "di 140 e questo testo estremamente lungo serve proprio ad essere certi" +
                    "di superare questo limite anche se probabilmente avrei potuto fare " +
                    "copia e incolla di un lorem ipsum generato online invece di perdere " +
                    "2 minuti a scrivere questo post");
        }
        catch (PostLengthExceededException e) {
            System.out.println("Post troppo lungo, eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        System.out.println("Provo a creare un post con testo vuoto");
        try {
            postSofia1 = new PostImpl(sofia, "");
        }
        catch (EmptyContentException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        try {
            postSofia1 = new PostImpl("UtenteNonRegistrato", "Testo di esempio");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        // Creo un nuovo social network
        SocialNetworkImpl sn = new SocialNetworkImpl();

        /**************************************TEST REGISTRAZIONE UTENTI**************************************************/
        System.out.println("\nTest registrazione utenti\n");
        // Registro gli utenti
        try {
            sn.registerUser(marco);
            sn.registerUser(anna);
            sn.registerUser(federico);
            sn.registerUser(laura);
            sn.registerUser(michele);
            sn.registerUser(sofia);
        }
        catch(Exception e) {
            System.out.println("Errore grave nella registrazione degli utenti");
        }
        try {
            System.out.println("Provo a registrare un utente null");
            sn.registerUser(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        // Provo a registrare i nomi sbagliati
        try {
            System.out.println("Provo a registrare un utente con nome lungo:");
            sn.registerUser(nomeLungo);
        }
        catch (InvalidUsernameException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch(Exception e) {
            System.out.println("Errore grave nella registrazione degli utenti");
        }

        try {
            System.out.println("Provo a registrare un utente caratteri non consentiti:");
            sn.registerUser(nomeSbagliato);
        }
        catch (InvalidUsernameException e) {
            System.out.println("Eccezione generata correttamente");
            System.out.println("");
        }
        catch(Exception e) {
            System.out.println("Errore grave nella registrazione degli utenti");
        }

        /**************************************TEST PUBBLICAZIONE POST**************************************************/
        System.out.println("Test pubblicazione post\n");

        ArrayList<Post> posts = new ArrayList<>();
        posts.add(postMarco1);
        posts.add(postAnna1);
        posts.add(postFederico1);
        posts.add(postMichele1);
        posts.add(postLaura1);
        posts.add(postAnna2);
        posts.add(postSofia1);

        try {
            System.out.println("Provo a pubblicare null");
            sn.publishPost(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        for (int i=0; i<posts.size() - 1; i++) {
            try {
                sn.publishPost(posts.get(i));
            }
            catch(Exception e) {
                System.out.println("Errore grave nella pubblicazione dei post");
            }
            System.out.println("Post con id " + posts.get(i).getId() + " aggiunto");
        }

        System.out.println("Provo a pubblicare un post con autore non registrato");

        try {
            sn.publishPost(postSofia1);
        }
        catch (UserNotFoundException e) {
            try {
                System.out.println("Eccezione generata correttamente, aggiusto il post");
                postSofia1 = new PostImpl(sofia, "Post di esempio di sofia che menziona @MIcHe_Le");
                sn.publishPost(postSofia1);
            }
            catch(Exception e1) {
                System.out.println("Errore grave nella pubblicazione e creazione del post di Sofia");
            }
        }
        catch (Exception e) {
            System.out.println("Errore grave nella pubblicazione dei post");
        }

        System.out.println("Provo a pubblicare un post già pubblicato");
        try {
            sn.publishPost(postMarco1);
        }
        catch (PostAlreadyPublishedException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella pubblicazione dei post");
        }


        /**************************************LIKE POST**************************************************/
        System.out.println("\nTEST LIKE AI POST\n");
        try {
            sn.like(postFederico1, sofia);
            sn.like(postFederico1, michele);
            sn.like(postFederico1, marco);

            sn.like(postSofia1, marco);
            sn.like(postSofia1, federico);

            sn.like(postAnna1, laura);
            sn.like(postAnna1, federico);
            sn.like(postAnna2, federico);

            sn.like(postMichele1, sofia);

            sn.like(postLaura1, anna);
        }
        catch (Exception e) {
            System.out.println("Errore grave nel like");
        }

        try {
            System.out.println("Provo a mettere like a un post null");
            sn.like(null, marco);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        try {
            System.out.println("Provo a mettere like con un follower null");
            sn.like(postMarco1, null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella creazione del post");
        }

        System.out.println("Provo a mettere like a un post non pubblicato");
        try {
            sn.like(postMarco2, sofia);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nel like");
        }

        System.out.println("Provo a mettere like a un post da parte del suo autore");
        try {
            sn.like(postMarco1, marco);
        }
        catch (AutoLikeException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nel like");
        }

        System.out.println("Provo a mettere like con un utente non registrato");
        try {
            sn.like(postMarco1, "NonEsistente");
        }
        catch(UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nel like");
        }

        /**************************************TEST WRITTENBY**************************************************/
        System.out.println("\nTEST writtenBy(String)\n");

        try {
            sn.publishPost(postMarco2);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella pubblicazione del secondo post di Marco");
        }

        try {
            System.out.println("Provo a ottenere i post scritti da null");
            sn.writtenBy(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy del post");
        }

        try {
            resultListPost = sn.writtenBy(marco);
            for (int i = 0; i < resultListPost.size(); i++) {
                System.out.println("Marco ha scritto " + resultListPost.get(i).getId());
            }
            System.out.println("");
            resultListPost = sn.writtenBy(sofia);
            for (int i = 0; i < resultListPost.size(); i++) {
                System.out.println("Sofia ha scritto " + resultListPost.get(i).getId());
            }
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        System.out.println("Provo a ottenere i post di un utente non registrato");
        try {
            sn.writtenBy("NonEsistente");
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        System.out.println("\nTEST writtenBy(List<Post>, String)\n");

        posts = new ArrayList<>();
        posts.add(postMarco1);
        posts.add(postMichele1);
        posts.add(postSofia1);
        posts.add(unpublished);

        try {
            System.out.println("Provo a ottenere i post scritti da null");
            sn.writtenBy(posts, null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        try {
            System.out.println("Provo a ottenere i post da una lista null");
            sn.writtenBy(null, marco);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        try {
            System.out.println("Provo a ottenere i post da una lista contenente null");
            ArrayList<Post> withNull = new ArrayList<>();
            withNull.add(postMarco1);
            withNull.add(null);
            sn.writtenBy(withNull, marco);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        System.out.println("Provo a cercare in una lista con un post non pubblicato");
        try {
            sn.writtenBy(posts, marco);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente, aggiusto la lista");
            posts.remove(unpublished);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        System.out.println("Provo a cercare i post di un utente non registrato");
        try {
            sn.writtenBy(posts, "NonEsistente");
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy");
        }

        try {
            resultListPost = sn.writtenBy(posts, sofia);
            for (int i = 0; i < resultListPost.size(); i++) {
                System.out.println("Sofia ha scritto " + resultListPost.get(i).getId());
            }
            resultListPost = sn.writtenBy(posts, federico);
            if (resultListPost.size() == 0) {
                System.out.println("Federico non ha scritto nessun post tra quelli passati nella lista");
            }
        }
        catch (Exception e) {
            System.out.println("Errore grave nella writtenBy per Sosfia e Federico");
        }
        /**************************************TEST GETMENTIONEDUSERS*******************************************/
        System.out.println("\nTEST getMentionedUsers()");

        resultSetString = sn.getMentionedUsers();
        for (String u : resultSetString) {
            System.out.println("Menzionato: " + u);
        }

        System.out.println("\nTEST getMentionedUsers(List<Post>)");

        try {
            System.out.println("Provo a ottenere i post da una lista null");
            sn.getMentionedUsers(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getMentionedUsers");
        }

        try {
            System.out.println("Provo a ottenere i post da una lista contenente null");
            ArrayList<Post> withNull = new ArrayList<>();
            withNull.add(postMarco1);
            withNull.add(null);
            sn.getMentionedUsers(withNull);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getMentionedUsers");
        }

        try {
            resultSetString = sn.getMentionedUsers(posts);
            for (String u : resultSetString) {
                System.out.println("Menzionato " + u);
            }
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getMentionedUsers");
        }

        System.out.println("Provo a cercare in una lista con un post non pubblicato");
        posts.add(unpublished);
        try {
            sn.getMentionedUsers(posts);
        }
        catch(PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        /**************************************TEST CONTAINING*******************************************/
        System.out.println("\nTest containing()");
        System.out.println("Cerco le parole \"esempio\" e \"#testo\"");
        wordList.add("esempio");
        wordList.add("#testo");

        try {
            System.out.println("Provo a ottenere i post da una lista null");
            sn.containing(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }

        try {
            System.out.println("Provo a ottenere i post da una lista contenente null");
            ArrayList<String> withNull = new ArrayList<>();
            withNull.add("prova");
            withNull.add(null);
            sn.containing(withNull);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }

        resultListPost = sn.containing(wordList);

        for (Post p : resultListPost) {
            System.out.println("Post " + p.getId() + " con testo \"" + p.getText() + "\"\n");
        }

        /****************************************TEST GUESSFOLLOWERS********************************************/
        System.out.println("\nTest guessFollowers(List<Post>)\n");
        System.out.println("Provo a chiamare la funzione con una lista contenente un post non pubblicato");

        try {
            System.out.println("Provo a chiamare guessFollowers con una lista null");
            sn.guessFollowers(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella guessFollowers");
        }

        try {
            System.out.println("Provo a Provo a chiamare guessFollowers con una lista contenente null");
            ArrayList<Post> withNull = new ArrayList<>();
            withNull.add(postMarco1);
            withNull.add(null);
            sn.guessFollowers(withNull);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella guessFollowers");
        }

        try {
            socialNetwork = sn.guessFollowers(posts);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
            posts.remove(unpublished);
        }

        try {
            socialNetwork = sn.guessFollowers(allPosts);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella guessFollowers");
        }

        for (Map.Entry<String, Set<String>> e : socialNetwork.entrySet()) {
            System.out.println(e.getKey() + " è seguito/a da: ");

            for (String s : e.getValue()) {
                System.out.println("\t-" + s);
            }
        }

        /****************************************TEST INFLUENCERS********************************************/

        System.out.println("\nTest influencers\n");
        resultListString = sn.influencers();
        for (int i=0; i<resultListString.size(); i++) {
            System.out.println("Influencer numero " + i + " " + resultListString.get(i));
        }

        /****************************************TEST UNLIKE********************************************/
        System.out.println("\nTest unlike\n");
        System.out.println("Rimuovo il like di michele, che dovrebbe scendere nella classifica degli influencers");

        try {
            System.out.println("Provo a togliere il like a un post null");
            sn.unLike(null, sofia);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nell'unlike");
        }

        try {
            System.out.println("Provo a togliere il like di un follower null");
            sn.unLike(postMarco1, null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nell'unlike");
        }

        try {
            sn.unLike(postMichele1, sofia);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella unlike");
        }

        resultListString = sn.influencers();
        for (int i=0; i<resultListString.size(); i++) {
            System.out.println("Influencer numero " + i + " " + resultListString.get(i));
        }

        System.out.println("\nProvo a togliere un like non messo");
        try {
            sn.unLike(postMarco1, federico);
        }
        catch (LikeNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella unlike");
        }

        System.out.println("\nProvo a togliere un like con un utente non registrato");
        try {
            sn.unLike(postFederico1, "NonEsistente");
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella unlike");
        }

        System.out.println("Provo a togliere un like a un post non pubblicato");
        try {
            sn.unLike(unpublished, marco);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella unlike");
        }

        /****************************************TEST DELETEPOST********************************************/
        System.out.println("\nTest deletePost(Post)\n");

        System.out.println("Provo a cancellare un post non pubblicato");
        try {
            sn.deletePost(unpublished);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        try {
            System.out.println("Provo a rimuovere un post null");
            sn.deletePost(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella deletePost");
        }

        System.out.println("Cancello il post di anna, se deletePost() funziona, la funzione containing non dovrebbe" +
                " più ritornare quel post");
        try {
            sn.deletePost(postAnna1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella deletePost");
        }

        resultListPost = sn.containing(wordList);
        for (Post p : resultListPost) {
            System.out.println("Il post " + p.getId() + ", scritto da " + p.getAuthor() + " conteneva una parola");
        }

        System.out.println("Laura non ha più follower in quanto l'unico post che seguiva di Anna è stato cancellato. La" +
                " guessFollowers su tutti i post dovrebbe confermarlo.\n Invece federico resta follower di Anna," +
                "perché aveva messo like a due suoi post. Ad ogni modo \nAnna scende in classifica tra gli " +
                "influencers perché perde il seguito di Laura.");
        allPosts.remove(postAnna1);
        try {
            socialNetwork = sn.guessFollowers(allPosts);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella guessFollowers");
        }

        for (Map.Entry<String, Set<String>> e : socialNetwork.entrySet()) {
            System.out.println(e.getKey() + " è seguito/a da: ");

            for (String s : e.getValue()) {
                System.out.println("\t-" + s);
            }
        }
        System.out.println("\nProva che Anna si trova più in basso nella classifica degli influencers: ");
        resultListString = sn.influencers();
        for (int i=0; i<resultListString.size(); i++) {
            System.out.println("Influencer numero " + i + " " + resultListString.get(i));
        }

        /*****************************************TEST REMOVEUSER***********************************/
        System.out.println("\nTest removeUser\n");
        System.out.println("Provo a rimuovere un utente non registrato");
        try {
            sn.removeUser("NonEsistente");
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        try {
            System.out.println("Provo a rimuovere un utente null");
            sn.removeUser(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella removeUser");
        }

        System.out.println("Rimuovo Anna dal social network, nessuno dovrebbe più poterla seguire o essere seguito" +
                "da lei, controllo il risultato di guessFollowers");
        try {
            sn.removeUser(anna);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella removeUser");
        }

        allPosts.remove(postAnna2);

        try {
            socialNetwork = sn.guessFollowers(allPosts);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella guessFollowers");
        }

        for (Map.Entry<String, Set<String>> e : socialNetwork.entrySet()) {
            System.out.println(e.getKey() + " è seguito/a da: ");

            for (String s : e.getValue()) {
                System.out.println("\t-" + s);
            }
        }
        System.out.println("Essendo stati cancellati anche i suoi post, non dovrebbe essere possibile " +
                "aggiungere un like ai suddetti post. Lo verifico provando a mettere like a un post di Anna.");
        try {
            sn.like(postAnna1, marco);
        }
        catch(PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella like");
        }

        System.out.println("Anche le vecchie menzioni per Anna non vengono più contate, lo verifico" +
                " chiamando la getMentionedUsers");
        resultListString = new ArrayList<>(sn.getMentionedUsers());
        System.out.println(resultListString.toString());

        /*****************************************TEST GETTRENDING******************************************/
        System.out.println("\nTest getTrending\n");
        System.out.println("Gli hashtag più usati sono:");
        resultListString = sn.getTrending();
        for (String s : resultListString) {
            System.out.println(s);
        }

        /***********************************TEST MODERATEDSOCIALNETWORK*************************************/
        System.out.println("\nINIZIO TEST MODERATEDSOCIALNETWORK\n");
        System.out.println("Creo un ModeratedSocialNetwork, registro gli utenti di prima e pubblico gli " +
                " stessi post");
        resultSetString = new TreeSet<>();
        resultSetString.add("tEsto");
        resultSetString.add("poSt");

        ModeratedSocialNetworkImpl msn = new ModeratedSocialNetworkImpl(resultSetString);

        try {
            msn.registerUser(marco);
            msn.registerUser(federico);
            msn.registerUser(sofia);
            msn.registerUser(anna);
            msn.registerUser(laura);
            msn.registerUser(michele);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella registrazione degli utenti");
        }

        try {
            for (Post p : allPosts) {
                msn.publishPost(p);
            }
            msn.publishPost(postAnna1);
            msn.publishPost(postAnna2);
            msn.publishPost(postSofia1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella pubblicazione dei post");
        }

        System.out.println("\nTest report\n");

        try {
            System.out.println("Provo a effettuare una segnalazione con autore null");
            msn.report(null, postAnna1);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        try {
            System.out.println("Provo a effettuare una segnalazione per un post null");
            msn.report(anna, null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        System.out.println("Provo a effettuare una segnalazione da parte di un utente non registrato");
        try {
            msn.report("NonEsistente", postAnna1);
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        System.out.println("Provo a segnalare un post non pubblicato");
        try {
            msn.report(marco, unpublished);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        System.out.println("Provo a far segnalare un post dal suo stesso autore");
        try {
            msn.report(marco, postMarco1);
        }
        catch (AutoReportException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        System.out.println("Anna segnala il post di marco");
        try {
            msn.report(anna, postMarco1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        System.out.println("Provo a ripetere la segnalazione");

        try {
            msn.report(anna, postMarco1);
        }
        catch (ReportingAlreadySentException e) {
            System.out.println("Eccezione generata correttamente");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        /***********************************TEST GETCONTROVERSIALPOSTS*************************************/
        System.out.println("\nTest getControversialposts\n");
        resultListPost = msn.getControversialPosts();
        System.out.println("Post controversi:");
        for (int i=0; i<resultListPost.size(); i++) {
            System.out.println((i + 1) + ":\t " + resultListPost.get(i).toString());
        }

        /***********************************TEST ADDFORBIDDENWORD******************************************/
        System.out.println("\nTest addForbiddenWords\n");
        try {
            System.out.println("Provo ad aggiungere una parola null");
            msn.addForbiddenWord(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }

        System.out.println("Rimuovo un post di Anna, aggiungo la parola \"esempio\" alla lista delle parole" +
                "probite: ripubblico il post e il suddetto post dovrebbe essere salito nella classifica" +
                "dei post più controversi");
        try {
            msn.deletePost(postAnna1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella deletePost");
        }

        msn.addForbiddenWord("esempio");

        try {
            msn.publishPost(postAnna1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella publishPost");
        }

        resultListPost = msn.getControversialPosts();
        System.out.println("Post controversi:");
        for (int i=0; i<resultListPost.size(); i++) {
            System.out.println((i + 1) + ":\t " + resultListPost.get(i).toString());
        }

        /***********************************TEST REMOVEFORBIDDENWORD******************************************/
        System.out.println("\nTest removeForbiddenWord\n");
        try {
            System.out.println("Provo a rimuovere una parola null");
            msn.addForbiddenWord(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }

        System.out.println("Provo a rimuovere una parola che non era in lista");
        try {
            msn.removeForbiddenWord("NonPresente");
        }
        catch (NoSuchElementException e) {
            System.out.println("Eccezione generata corettamente");
        }

        System.out.println("Rimuovo il post più controverso di Marco, rimuovo le parole per cui era " +
                "considerato offensivo e lo ripubblico: dovrebbe essere sceso nella classifica" +
                "dei post più controversi");
        try {
            msn.deletePost(postMarco1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella deletePost");
        }

        msn.removeForbiddenWord("testo");
        msn.removeForbiddenWord("post");

        try {
            msn.publishPost(postMarco1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella publishPost");
        }

        // Aggiungo anche la vecchia segnalazione per dimostrare che Marco non è sceso in classifica poiché si
        // sono perse le segnalazioni ai suoi post
        try {
            msn.report(anna, postMarco1);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella report");
        }

        resultListPost = msn.getControversialPosts();
        System.out.println("Post controversi:");
        for (int i=0; i<resultListPost.size(); i++) {
            System.out.println((i + 1) + ":\t " + resultListPost.get(i).toString());
        }

        /***********************************TEST GETREPORTINGSBYAUTHOR******************************************/
        System.out.println("\nTest getReportingsByAuthor");

        try {
            System.out.println("Provo a ottenere le segnalazioni di un autore null");
            msn.getReportingsByAuthor(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getReportingsByAuthor");
        }

        System.out.println("Provo a ottenere le segnalazioni di un utente non registrato");
        try {
            resultListReportings = msn.getReportingsByAuthor("NonEsistente");
        }
        catch (UserNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        // Prendo le segnalazioni effettuate da Anna (una sola) e le stampo
        try {
            resultListReportings = msn.getReportingsByAuthor(anna);
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getReportingsByAuthor");
        }

        for (Reporting r : resultListReportings) {
            System.out.println(r.toString());
        }

        /***********************************TEST GETREPORTINGSFORPOST******************************************/
        System.out.println("\nTest getReportingForPost\n");

        try {
            System.out.println("Provo a ottenere le segnalazioni di un post null");
            msn.getReportingsForPost(null);
        }
        catch (NullPointerException e) {
            System.out.println("Eccezione generata correttamente\n");
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getReportingForPost");
        }

        System.out.println("Provo a ottenere le segnalazioni di un post non pubblicato");
        try {
            resultListReportings = msn.getReportingsForPost(unpublished);
        }
        catch (PostNotFoundException e) {
            System.out.println("Eccezione generata correttamente");
        }

        System.out.println("Stampo ora le segnalazioni al primo post di marco (quella manuale " +
                "da parte di Anna");

        try {
            resultListReportings = msn.getReportingsForPost(postMarco1);
            for (Reporting r : resultListReportings) {
                System.out.println(r.toString());
            }

            System.out.println("Stampo ora le segnalazioni al primo post di Anna (3 segnalazioni automatiche)");
            resultListReportings = msn.getReportingsForPost(postAnna1);
            for (Reporting r : resultListReportings) {
                System.out.println(r.toString());
            }
        }
        catch (Exception e) {
            System.out.println("Errore grave nella getReportingsForPost");
        }
    }
}
