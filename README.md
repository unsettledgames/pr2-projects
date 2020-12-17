# pr2-projects

Progetti relativi al corso di Programmazione 2 tenuto all'Università di Pisa, corso di laurea triennale in informatica, a.a. 2020/21.

Il progetto OCaml, consegnato per il preappello, ha typechecker dinamico.
C'è un errore nella valutazione delle operazioni funzionali (ForAll, Exists, Filter e Map). Si richiedeva un interpete, ma creando una FunCall nella funzione eval, sostanzialmente si modifica la struttura del programma, poiché si va a creare un pezzo di sintassi astratta. Il modo corretto di farlo sarebbe dovuto consistere nell'usare una funzione ausiliaria, che non richiedesse di modificare la sintassi astratta del programma.

Non ho intenzione di correggere l'errore al momento, pull requests sono ben accette.