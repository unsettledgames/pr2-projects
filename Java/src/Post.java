import java.time.LocalDateTime;

/**
 *  Overview:
 *      Post rappresenta un contenuto testuale immutabile che può essere
 *      pubblicato all'interno di una rete sociale.
 *
 *  Typical element:
 *      <id, autore, testo, timestamp>, in cui:
 *          - id è un identificatore univoco per il post
 *          - autore è l'utente che ha scritto il post
 *          - testo è il contenuto testuale del post, lungo al massimo 140 caratteri
 *          - timestamp rappresenta la data e l'ora in cui il post è stato scritto
 */
public interface Post {
    /**
     * Massima lunghezza del testo di un post
     */
    public static final int MAX_LENGTH = 140;

    /**
     * @return L'id univoco del post
     */
    public long getId();

    /**
     * @return L'autore del post
     */
    public String getAuthor();

    /**
     * @return Il testo del post
     */
    public String getText();

    /**
     * @return Il timestamp del post, che indica il momento in cui è stato pubblicato
     *         sottoforma di LocalDateTime
     */
    public LocalDateTime getTimestamp();
}
