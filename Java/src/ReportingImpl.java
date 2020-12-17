/** Overview:
 *         Reporting rappresenta una segnalazione di un post da parte di un
 *         utente di una rete sociale, immutabile e comparabile, tramite il suo identificatore,
 *         ad altre segnalazioni.
 *
 *  Typical element:
 *      <weight, autore, id> in cui:
 *          - weight rappresenta la gravità della segnalazione
 *          - autore rappresenta l'autore della segnalazione
 *          - id rappresenta l'identificatore univoco assegnato alla segnalazione
 *
 *  Abstraction Function:
 *      f(c): C -> A = <c.weight, c.autore, c.id>
 *
 *  Representation Invariant:
 *      f(c): C -> Bool = this.author != null
 *
 */
public class ReportingImpl implements Reporting, Comparable<ReportingImpl> {
    /**
     * Autore della segnalazione
     */
    private String author;

    /**
     * Peso della segnalazione, rapprsenta la sua gravità
     */
    private int weight;

    /**
     * Identificatore univoco della segnalazione
     */
    private int id;

    /**
     * Identificatore della prossima segnalazione che sarà creata
     */
    private static int currentId = 0;

    /**
     * @requires author != null
     *
     * @param author L'autore della segnalazione
     *
     * @effects Crea un oggetto di tipo ReportingImpl, assegnando un peso diverso alle
     *          segnalazioni automatiche e a quelle inviate da un utente e assegnando all'oggetto
     *          un id univoco
     * @modifies this.id, this.author, this.weight, currentId
     *
     * @throws NullPointerException Se author == null
     */
    public ReportingImpl(String author, int weight) {
        if (author == null) {
            throw new NullPointerException("L'autore di una segnalazione non può essere null");
        }

        this.weight = weight;

        this.id = currentId;
        this.author = author;
        currentId++;
    }

    /**
     * @effects Fornisce il peso della segnalazione
     * @return il peso della segnalazione (this.weight);
     */
    @Override
    public int getWeight() {
        return this.weight;
    }

    /**
     * @effects Fornisce l'autore della segnalazione
     * @return l'autore della segnalazione (this.author)
     */
    @Override
    public String getAuthor() {
        return this.author;
    }

    /**
     * @effects Fornisce l'identificatore univoco della segnalazione
     * @return L'identificatore univoco della segnalazione (this.id)
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * @requires o != null
     *
     * @param o La segnalazione con cui comparare this
     *
     * @effects Compara this a un'altra ReportingImpl o
     * @throws NullPointerException Se o == null
     * @return -1 se this.id < o.id
     *          0 se this.id == o.id
     *          1 se this.id > o.id (in ogni altro caso)
     */
    @Override
    public int compareTo(ReportingImpl o) {
        if (o == null)  {
            throw new NullPointerException("Impossibile comparare la segnalazione di id " +
                    id + " con null");
        }
        return Integer.compare(this.id, o.getId());
    }

    /**
     * @effects Fornisce una rappresentazione in formato di stringa della segnalazione
     * @return La rappresentazione in formato di stringa della segnalazione
     */
    @Override
    public String toString() {
        return "Author: " + this.author + ", weight: " + this.weight + "\n";
    }

    /**
     * @requires o != null
     * @effects Verifica che l'oggetto passato come parametro sia di tipo ReportingImpl e che il suo stato
     *          sia lo stesso.
     * @param o L'oggetto con cui confrontare this
     * @return True se o è di tipo ReportingImpl e il suo stato è lo stesso di this, false altrimenti
     * @throws NullPointerException Se o == null
     */
    public boolean equals(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (o.getClass().equals(getClass())) {
            return ((Reporting) o).getId() == id;
        }
        return false;
    }

    /**
     * @effects Fornisce l'hashCode della segnalazione (dato dal suo id, dal momento che è univoco)
     * @return L'hashcode della segnalazione
     */
    public int hashCode() {
        return (int)id;
    }
}