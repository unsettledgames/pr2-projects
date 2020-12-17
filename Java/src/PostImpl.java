import Exceptions.Post.EmptyContentException;
import Exceptions.Post.PostLengthExceededException;

import java.time.LocalDateTime;

/**
 *  Overview:
 *     PostImpl rappresenta un contenuto testuale immutabile e comparabile tramite
 *      il suo identificatore che può essere pubblicato all'interno di una rete sociale.
 *
 *  Typical element:
 *      <id, autore, testo, timestamp>, in cui:
 *          - id è un identificatore univoco per il post
 *          - autore è l'utente che ha scritto il post
 *          - testo è il contenuto testuale del post, lungo al massimo 140 caratteri
 *          - timestamp rappresenta la data e l'ora in cui il post è stato scritto
 *
 *  Abstraction Function:
 *      f(c): C -> A = <c.id, c.autore, c.testo, c.timestamp>
 *
 *  Representation invariant:
 *      f(c): C -> Bool =
 *      this.author != null &&
 *      this.text != null &&
 *      this.text = "" &&
 *      this.text.length <= MAX_LENGTH &&
 *      this.timestamp != null
 *
 */
public class PostImpl implements Post, Comparable<Post>{
    /**
     * Id univoco del post
     */
    private long id;

    /**
     * Autore del post
     */
    private String author;

    /**
     * Testo del post
     */
    private String text;

    /**
     * Timestamp del post (momento di pubblicazione)
     */
    private LocalDateTime timestamp;

    /**
     * Identificatore per il prossimo post che verrà creato
     */
    private static long globalId = 0;

    /** Costruttore degli oggetti di tipo PostImpl. Assegna autore e testo ai parametri e genera
     *  automaticamente il corretto timestamp e un id univoco, per poi assegnarli ai rispettivi
     *  attributi.
     *
     * @requires author != null && text != null && text.length > MAX_LENGTH
     *
     * @param author L'autore del post
     * @param text  Il testo del post
     *
     * @effects Crea un oggetto di tipo PostImpl, incrementa globalId di 1 in modo tale che il
     *          prossimo oggetto a essere creato abbia un id differente e assegna testo e autore
     *          al post
     * @modifies this, this.author, this.text, globalId
     *
     * @throws PostLengthExceededException se text.length > MAX_LENGTH
     * @throws EmptyContentException se text.length == 0
     * @throws NullPointerException se author == null || text == null
     */
    public PostImpl(String author, String text) throws PostLengthExceededException, EmptyContentException {
        this.id = globalId;
        this.timestamp = LocalDateTime.now();

        setText(text);
        setAuthor(author);

        globalId++;
    }

    /**
     * @return L'id univoco del post
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * @return L'autore del post (this.author);
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * @return Il testo del post
     */
    @Override
    public String getText() {
        return this.text;
    }

    /**
     * @return Il timestamp del post, che indica il momento in cui è stato pubblicato
     *         sottoforma di LocalDateTime
     */
    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    /**
     * @return La rappresentazione del post sottoforma di stringa
     */
    @Override
    public String toString() {
        String ret = "";

        ret += "Id: " + this.id + "\n";
        ret += "Author: " + this.author + "\n";
        ret += "Text: " + this.text + "\n";
        ret += "Timestamp: " + this.timestamp.toString() + "\n";

        return ret;
    }

    /**
     * @requires author != null
     *
     * @param author L'autore del post
     *
     * @effects Imposta l'autore del post
     * @modifies this.author
     *
     * @throws NullPointerException Se author == null
     */
    private void setAuthor(String author) {
        if (author != null) {
            this.author = author;
        }
        else {
            throw new NullPointerException("Author of the post " + id + "can't be null.");
        }
    }

    /** Imposta il testo del post
     *
     * @requires text != null && text.length <= MAX_LENGTH
     *
     * @param text Il testo del post
     *
     * @effects Assegna text a this.text
     * @modifies this.text
     *
     * @throws NullPointerException Se text == null
     * @throws PostLengthExceededException Se text.length > MAX_LENGTH
     * @throws EmptyContentException Se text.length == 0
     */
    private void setText(String text) throws PostLengthExceededException, EmptyContentException {
        if (text != null) {
            if (text.length() > Post.MAX_LENGTH) {
                throw new PostLengthExceededException(text.length());
            }
            else if (text.isEmpty()) {
                throw new EmptyContentException("Il contenuto del post non può essere null");
            }
            else {
                this.text = text;
            }
        }
        else {
            throw new NullPointerException("Text of the post " + id + "can't be null.");
        }
    }

    /**
     * @requires o != null
     *
     * @param o Il post con cui comparare this
     *
     * @effects Compara this con un altro Post o
     * @throws NullPointerException Se o == null
     *
     * @return -1 se this.id < o.id
     *          0 se this.id == o.id
     *          1 se this.id > o.id (in ogni altro caso)
     */
    @Override
    public int compareTo(Post o) {
        if (o == null) {
            throw new NullPointerException("Impossibile comparare il post " + this.id + " con null");
        }

        return Long.compare(this.id, o.getId());
    }

    /**
     * @requires o != null
     * @effects Verifica che l'oggetto passato come parametro sia di tipo PostImpl e che il suo stato
     *          sia lo stesso.
     * @param o L'oggetto con cui confrontare this
     * @return True se o è di tipo PostImpl e il suo stato è lo stesso di this, false altrimenti
     * @throws NullPointerException Se o == null
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            return ((PostImpl) o).getId() == id;
        }
        return false;
    }

    /**
     * @effects Fornisce l'hashCode del post (dato dal suo id, dal momento che è univoco)
     * @return L'hashcode del post
     */
    @Override
    public int hashCode() {
        return (int)id;
    }
}
