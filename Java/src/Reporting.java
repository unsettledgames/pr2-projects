/** Overview:
 *      Reporting rappresenta una segnalazione di un post da parte
 *      di un utente di una rete sociale, immutabile e comparabile, tramite il suo identificatore,
 *      ad altre segnalazioni.
 *
 *  Typical element:
 *      <weight, autore, id> in cui:
 *          - weight rappresenta la gravità della segnalazione
 *          - autore rappresenta l'autore della segnalazione
 *          - id rappresenta l'identificatore univoco assegnato alla segnalazione
 *
 */
public interface Reporting {
    /**
     *  Valore dell'attributo author quando la segnalazione è automatica e inviata dal sistema.
     *  Contiene caratteri '-' in modo che non sia rilevato come un nome utente (dal momento che
     *  tale carattere è proibito al momento della registrazione).
     */
    public static final String AUTOMATIC_REPORTING_AUTHOR = "-System-";

    /**
     * @return Il peso (gravità) della segnalazione
     */
    public int getWeight();

    /**
     * @return L'autore della segnalazione
     */
    public String getAuthor();

    /**
     * @return L'id univoco della segnalazione
     */
    public int getId();
}
