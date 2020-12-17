let env0 = emptyenv Unbound;;

let empty_set = EmptySet(Tint);;

(*Valutazione di un set vuoto*)
eval empty_set env0;;

let set_a = Eset(Tint, Item(Eint 3, Item(Eint 5, EmptySetList)));;
let set_b = Eset(Tint, Item(Eint 3, Item(Eint 4, Item (Eint 9, EmptySetList))));;
let set_string = Eset(Tstring, Item(Estring "esempio", Item(Estring "test", Item(Estring "prova", EmptySetList))));;

let type_error = Eset(Tint, Item(Estring "test", EmptySetList));;
let duplicates = Eset(Tint, Item(Eint 2, Item(Eint 2, EmptySetList)));;

(* Valutazione di un set di interi *)
eval set_a env0;;
(* Valutazione di un set di stringhe *)
eval set_string env0;;
(* Fornisce errore di tipo perché un insieme di int contiene una stringa *)
eval type_error env0;;
(* Valutazione di un set con duplicati (i duplicati vengono eliminati) *)
eval duplicates env0;;

let set_zero = Eset(Tint, Item(Eint 0, Item(Eint 0, Item(Eint 0, EmptySetList))));;
let set_one = Eset(Tint, Item(Eint 1, Item(Eint 1, Item(Eint 1, EmptySetList))));;

let set_zeroone = Eset(Tint, Item(Eint 0, Item(Eint 1, Item(Eint 0, EmptySetList))));;
let set_onezero = Eset(Tint, Item(Eint 1, Item(Eint 0, Item(Eint 1, EmptySetList))));;

(* La prima dà false, la seconda true *)
let contains2 = Contains((set_a, Eint 2));;
(* Verifico se il set di interi contiene 2 *)
eval contains2 env0;;
let contains3 = Contains((set_a, Eint 3));;
(* Verifico se il set di interi contiene 3 *)
eval contains2 env0;;

(* Inserisco 2 e 4 *)
let insert = Insert((Insert(set_a, Eint 4), Eint 2));;
(* Aggiungo al set di interi i valori 4 e 2 *)
eval insert env0;;

(* Inserisco un valore già presente *)
let insert_presente = Insert((set_a, Eint 3));;
(* Aggiungo al set di interi il valore 3 già presente, ottenendo quindi l'insieme di partenza *)
eval insert env0;;

(* Rimuovo 3 *)
let remove3 = Remove((set_a, Eint 3));;
(* Rimuovo dal set di interi il valore 3 *)
eval insert env0;;
(* Rimuovo 4 (non c'è, restituisce il set di partenza) *)
let remove4 = Remove((set_a, Eint 4));;

(* Unisco al set usato finora il set contenente 3,4,9: 3 è già presente nel set di partenza, non viene aggiunto *)
let union = Union((set_a, set_b));;
eval union env0;;

(* Faccio l'intersezione tra i due set menzionati prima (ritorna solo 3) *)
let intersection = Intersection((set_a, set_b));;
eval intersection env0;;

(* Causo la valutazione di un'intersezione vuota, dato che tolgo l'unico elemento in comune ai due insiemi *)
let empty_intersection = Intersection((Remove((set_a, Eint 3)), set_b));;
eval empty_intersection env0;;

(* Differenza tra insiemi: tolgo a set_b gli elementi di set_a (effettivamente tolgo solo 3) *)
let diff = DiffSet((set_b, set_a));;
eval diff env0;;

(* Applicazione della funzione subset che ritorna false *)
let subset_false = Subset((set_a, set_b));;
eval subset_false env0;;

(* Applicazione della funzione subset che ritorna true (passo un insieme contenente due elementi di set_b) *)
let subset_true = Subset((Eset(Tint, Item(Eint 3, Item(Eint 9, EmptySetList))), set_b));;
eval subset_true env0;;

(* Calcolo il minimo di set_a (3) *)
let set_min = Min(set_a);;
eval set_min env0;;

(* Calcolo il massimo di set_b (9) *)
let set_max = Max(set_b);;
eval set_max env0;;

(* Applico la min e la max su insiemi vuoti ottenendo Unbound *)
let min_empty = Min(EmptySet(Tint));;
let max_empty = Max(EmptySet(Tstring));;

eval min_empty env0;;
eval max_empty env0;;

(* Ritorna true (verifico che un set contenente solo 0 contenga solo 0) *)
let forall_true = ForAll(set_zero, Fun("x", IsZero(Den("x"))));;
eval forall_true env0;;
(* Ritorna false (verifico che un set contenente tutti 0 e un 1 contenga solo 0*)
let forall_false = ForAll(set_zeroone, Fun("x", IsZero(Den("x"))));;
eval forall_false env0;;

(* Ritorna true (verifico che esista uno zero in un insieme composto da soli 1 tranne uno 0*)
let exists_true = Exists(set_onezero, Fun("x", IsZero(Den("x"))));;
eval exists_true env0;;
(* Ritorna false (verifico che esista uno zero in un insieme composto da soli 0) *)
let exists_false = Exists(set_one, Fun("x", IsZero(Den("x"))));;
eval exists_false env0;;

(* Map su insieme vuoto, ritorna un evT Set con lista vuota e tipo Tint (quello dell'insieme di partenza) *)
let empty_map = Map(empty_set, Fun("x", Estring("test")));;
eval empty_map env0;;;
(* Map su insieme int che restituisce stringhe (sostituisce a ogni elemento la stringa "test") *)
let type_map = Map(set_b, Fun("x", Estring("test")));;
eval type_map env0;;

(* Restituisce vuoto perché sono tutti 1*)
let empty_filter = Filter(set_one, Fun("x", IsZero(Den("x"))));;
(* Restituisce l'insieme di partenza perché sono tutti 0*)
let full_filter = Filter(set_zero, Fun("x", IsZero(Den("x"))));;
(* Toglie gli 1 da questo insieme lasciando solo gli 0 *)
let partial_filter = Filter(set_zeroone, Fun("x", IsZero(Den("x"))));;


let fact = Fun("x", ( Ifthenelse(Eq(Den("x"), Eint(1)), Eint(1), Prod(Den("x"), FunCall(Den("fact"), Diff(Den("x"), Eint(1)))))));;
let recFunTest = Letrec("fact", fact, FunCall(Den("fact"), Eint(5)));;
eval recFunTest env0;;
