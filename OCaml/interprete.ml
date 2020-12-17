type ide = string;;

type lang_types = 
	| Tint
	| Tstring
	| Tbool;;

type exp = Eint of int 
	| Ebool of bool 
	| Estring of string
	| Den of ide 
	| Prod of exp * exp 
	| Sum of exp * exp 
	| Diff of exp * exp 
	| Eq of exp * exp 
	| Minus of exp 
	| IsZero of exp 
	| Or of exp * exp 
	| And of exp * exp 
	| Not of exp 
	| Ifthenelse of exp * exp * exp 
	| Let of ide * exp * exp 
	| Fun of ide * exp 
	| FunCall of exp * exp 
	| Letrec of ide * exp * exp
    (* ESPANSIONE SUGLI INSIEMI *)
    | Eset of (lang_types * set_list)
    | EmptySet of (lang_types)
    | Singleton of lang_types * exp
    | Of of lang_types * set_list
    (* OPERAZIONI DI BASE *)
    | Union of exp * exp
    | Intersection of exp * exp
    | DiffSet of exp * exp
    (* OPERAZIONI AGGIUNTIVE *)
	| IsEmpty of exp
    | Insert of exp * exp
    | Remove of exp * exp
    | Contains of exp * exp
    | Subset of exp * exp 
    | Min of exp
    | Max of exp
    (* OPERATORI FUNZIONALI *)
    | ForAll of exp * exp
    | Exists of exp * exp
    | Filter of exp * exp
    | Map of exp * exp
	and set_list = EmptySetList | Item of exp * set_list;;

(*ambiente polimorfo*)
type 't env = ide -> 't;;
let emptyenv (v : 't) = function x -> v;;
let applyenv (r : 't env) (i : ide) = r i;;
let bind (r : 't env) (i : ide) (v : 't) = function x -> if x = i then v else applyenv r x;;

(*tipi esprimibili*)
type evT = Int of int 
| Bool of bool
| Unbound 
| FunVal of evFun 
| RecFunVal of ide * evFun
| String of string
(* Tipo esprimibile degli insiemi *)
| Set of lang_types * evT list
and evFun = ide * exp * evT env;;

(*rts*)
(*type checking*)
let typecheck (supposed_type : string) (value : evT) : bool = match supposed_type with
	"int" -> (match value with
		Int(_) -> true |
		_ -> false) |
    "string" -> (match value with
        String(_) -> true |
        _ -> false) |
    "set" -> (match value with
        Set(_) -> true |
        _ -> false) |
	"bool" -> (match value with
		Bool(_) -> true |
		_ -> false) |
	"fun" -> (match value with
		FunVal(_) -> true |
		_ -> false) |
	"recfun" -> (match value with 
		RecFunVal(_) -> true |
		_ -> false) |
	_ -> failwith("not a valid type");;

(* Funzione usata per verificare che il tipo di un evT corrisponda a un possibile tipo di insieme *)
let lang_typecheck (set_type : lang_types) (value : evT) = 
	match set_type with
		Tint -> typecheck "int" value
		| Tstring -> typecheck "string" value
		| Tbool -> typecheck "bool" value;;

(* Funzione che, dato un valore, ne ritorna il corrispondente tipo insiemistico *)
let get_lang_type (valore : evT) = 
	match valore with
		Bool(_) -> Tbool |
		String(_) -> Tstring |
		Int(_) -> Tint |
		_ -> failwith ("Errore di tipo");;

(*eval xs r in val::(validate xs settype r)*)
(*funzioni primitive*)
let prod x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n*u))
	else failwith("Type error");;

let sum x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n+u))
	else failwith("Type error");;

let diff x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Int(n-u))
	else failwith("Type error");;

let eq x y = if (typecheck "int" x) && (typecheck "int" y)
	then (match (x,y) with
		(Int(n),Int(u)) -> Bool(n=u))
	else failwith("Type error");;

let minus x = if (typecheck "int" x) 
	then (match x with
	   	Int(n) -> Int(-n))
	else failwith("Type error");;

let iszero x = if (typecheck "int" x)
	then (match x with
		Int(n) -> Bool(n=0))
	else failwith("Type error");;

let vel x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> (Bool(b||e)))
	else failwith("Type error");;

let et x y = if (typecheck "bool" x) && (typecheck "bool" y)
	then (match (x,y) with
		(Bool(b),Bool(e)) -> Bool(b&&e))
	else failwith("Type error");;

let non x = if (typecheck "bool" x)
	then (match x with
		Bool(true) -> Bool(false) |
		Bool(false) -> Bool(true))
	else failwith("Type error");;

(* Funzione di utility che controlla che valore sia contenuto in lista *)
let rec list_contains lista valore = 
	match lista with
	[] -> false |
	x::xs -> if (x = valore) then true else (list_contains xs valore);;

(* Dato un valore esprimibile Set, ne restituisce la corrispondente espressione Eset *)
let rebuild_set set = 
	match set with 
		Set(s_type, s_list) -> 
			let rec aux curr_list =
				(match curr_list with
					[] -> EmptySetList |
					x::xs -> let y = 
						(match x with 
							Bool(valore) -> Ebool(valore) |
							String(valore) -> Estring(valore) |
							Int(valore) -> Eint(valore)) in
						Item(y, (aux xs))) in
				Eset(s_type, (aux s_list)) |
		_-> failwith("Errore di tipo");;

(* FUNZIONI DI SUPPORTO ALLE OPERAZIONI *)

(* Controlla che value sia contenuto in set *)
let set_contains set value = 
	if (typecheck "set" set) then 
		match set with 
			Set(s_type, s_list) -> 
				(* Verifico che il tipo di value sia compatibile con set *)
				if (lang_typecheck s_type value) then
					list_contains s_list value
				else failwith ("L'insieme non e' del tipo del valore")
			| _-> failwith("Errore di tipo")
	else failwith("Il parametro non e' un insieme");;

(* Controlla se un insieme è vuoto o meno *)
let set_isempty set = 
	if (typecheck "set" set) then
		match set with
			Set(s_type, s_list) ->
				if (s_list = []) then true else false
			| _-> failwith ("Errore di tipo")
	else failwith ("Il parametro non è un insieme");;

(* Inserisce value in set *)
let set_insert set value = 
	if (typecheck "set" set) then
		match set with
			Set(s_type, s_list) ->
				(* Verifico che il tipo di value sia compatibile con set *)
				if (lang_typecheck s_type value) then
					(* Uso la set_contains per verificare se devo effettivamente aggiungere value o no *)
					if (set_contains set value) = false then
						Set(s_type, value::s_list)
					else
						Set(s_type, s_list)
				else failwith ("L'insieme non e' del tipo del valore")
			|_ -> failwith ("Errore di tipo")
	else failwith ("Errore di tipo");;

(* Rimuove il valore value dall'insieme set *)
let set_remove set value =
	if (typecheck "set" set) then	
		match set with 
			Set(s_type, s_list) ->
				(* Verifico che il tipo di value sia compatibile con set *)
				if (lang_typecheck s_type value) then
					(* Non controllo se il valore non è presente: semplicemente restituirò lo stesso insieme
					   di partenza in tal caso *)
					let rec aux alist avalue = 
						match alist with
							[] -> [] |
							x::xs -> if x = avalue then xs else x::(aux xs avalue)
					in Set(s_type, aux s_list value)
				else failwith ("L'insieme non e' del tipo del valore")
			| _->failwith("Errore di tipo")
	else failwith ("Errore di tipo");;

(* Effettua l'operazione di unione tra set_a e set_b *)
let set_union set_a set_b = 
	if (typecheck "set" set_a) && (typecheck "set" set_b) then
		(match (set_a, set_b) with
			(Set(s_typea, s_lista), Set(s_typeb, s_listb)) ->
				(* Controllo che i tipi degli insiemi siano lo stesso tipo *)
				if (s_typea = s_typeb) then
					let rec aux list_a list_b = 
						(match list_a with
							[] -> list_b |
							x::xs ->
								(* Aggiungo solo se l'elemento non è già presente *)
								if (set_contains set_b x) = true then
									aux xs list_b
								else x::(aux xs list_b))
					in Set(s_typea, aux s_lista s_listb)
				else failwith("Impossibile unire due insiemi di tipo diverso")
			| _-> failwith ("Errore di tipo"))
	else failwith ("Errore di tipo");;

(* Effettua l'operazione di intersezione tra set_a e set_b *)
let set_intersection set_a set_b = 
	if (typecheck "set" set_a) && (typecheck "set" set_b) then
		(match (set_a, set_b) with
			(Set(s_typea, s_lista), Set(s_typeb, s_listb)) ->
				(* Controllo che i tipi degli insiemi siano lo stesso tipo *)
				if (s_typea = s_typeb) then
					let rec aux list_a list_b = 
						(match list_a with
							[] -> [] |
							x::xs ->
								(* Aggiungo solo se l'elemento è anche presente nel set_b *)
								if (set_contains set_b x) = true then
									x::(aux xs list_b)
								else aux xs list_b)
					in Set(s_typea, aux s_lista s_listb)
				else failwith("Impossibile intersecare insiemi di tipo diverso")
		| _-> failwith("Errore di tipo"))
	else failwith ("Errore di tipo");;

(* Effettua l'operazione di differenza tra set_a e set_b (toglie da set_a gli elementi di set_b, se presenti) *)
let set_diff set_a set_b = 
	if (typecheck "set" set_a) && (typecheck "set" set_b) then
		(match (set_a, set_b) with
			(Set(s_typea, s_lista), Set(s_typeb, s_listb)) ->
				(* Controllo che i tipi degli insiemi siano lo stesso tipo *)
				if (s_typea = s_typeb) then
					let rec aux list_a = 
						(match list_a with
							[] -> [] |
							x::xs -> 
								(* Se il valore è contenuto anche nel set_b, allora lo rimuovo da set_a *)
								if (set_contains set_b x) = true then
									aux xs
								else x::(aux xs))
					in Set(s_typea, aux s_lista)
				else failwith("Impossibile applicare la differenza su insiemi di tipo diverso")
		| _-> failwith("Errore di tipo"))
	else failwith ("Errore di tipo");;

(* Verifica che set_a sia un sottoinsieme di set_b *)
let set_subset set_a set_b = 
	if (typecheck "set" set_a) && (typecheck "set" set_b) then
		(match (set_a, set_b) with
			(Set(s_typea, s_lista), Set(s_typeb, s_listb)) ->
				(* Verifico che i tipi degli insiemi siano lo stesso *)
				if (s_typea = s_typeb) then
					let rec aux list_a = 
						(match list_a with
							[] -> true |
							x::xs -> 
								(* Non appena trovo un elemento che non appartiene a set_b, ritorno false *)
								if (set_contains set_b x) = true then
									(aux xs)
								else false)
					in (aux s_lista)
				else failwith("Impossibile controllare se insiemi di tipo diverso sono inclusi l'uno nell'altro")
		| _-> failwith("Errore di tipo"))
	else failwith ("Errore di tipo");;
	
(* Ritorna il minimo elemento presente all'interno dell'insieme *)
let set_min set = 
	if (typecheck "set" set) then
		(match set with
			Set(s_typea, s_list) ->
				(* Semplice algoritmo della ricerca del minimo *)
				let rec aux list_a current_min  = 
					(match list_a with
						[] -> current_min |
						x::xs -> 
							if (x < current_min) = true then
								(aux xs x)
							else (aux xs current_min))
				in 
					(* Se la lista è vuota, restituisco Unbound *)
					(match s_list with 
						[] -> Unbound |
						x::xs -> (aux xs x))
		| _-> failwith("Errore di tipo"))
	else failwith ("Errore di tipo");;

(* Ritorna il massimo elemento presente all'interno dell'insieme *)
let set_max set = 
	if (typecheck "set" set) then
		(match set with
			Set(s_typea, s_list) ->
				let rec aux list_a current_max  = 
					(match list_a with
						[] -> current_max |
						x::xs -> 
							if (x > current_max) = true then
								(aux xs x)
							else (aux xs current_max))
				in 
					(* Se la lista è vuota, restituisco Unbound *)
					(match s_list with 
						[] -> Unbound |
						x::xs -> (aux xs x))
		| _-> failwith("Errore di tipo"))
	else failwith ("Errore di tipo");;

let rec eval (e : exp) (r : evT env) : evT = match e with
	Eint n -> Int n |
	Ebool b -> Bool b |
	Estring s -> String s |
	Eset (s_type, s_list) ->
		(* Quando valuto un set, verifico che sia formato correttamente tramite la funzione validate *)
		let rec validate (setlist : set_list) (added : evT list) (settype : lang_types) (amb : evT env) : evT list = 
			(match setlist with 
				EmptySetList -> added 
				| Item(expression, xs) ->
					let evaluated = (eval expression amb) in
						(* Verifico che il tipo dell'espressione sia coerente con il tipo dell'insieme *)
						if (lang_typecheck settype evaluated) then
							(* Se evaluated è un valore già presente, non lo aggiungo *)
							if (list_contains added evaluated) then
								(validate xs added settype amb)
							else 
								(validate xs (evaluated::added) settype amb)
						else 
							failwith("Tipo non coerente con quanto dichiarato"))
		in Set(s_type, validate s_list [] s_type r) |
	EmptySet s -> Set(s, []) |
	(* Nel caso del Singleton non devo verificare che ci siano duplicati, ma solo che i tipi corrispondano *)
	Singleton(s_type, expr) -> 
		let evaluated = (eval expr r) in 
			if (lang_typecheck s_type evaluated) then
				Set(s_type, evaluated::[])
			else 
				failwith("Tipo dell'espressione non coerente con quello dell'insieme") |
	(* La Of è identica all'Eset *)
	Of (s_type, s_list) ->
		let tmp_set = Eset((s_type, s_list)) in eval tmp_set r |
	IsZero a -> iszero (eval a r) |
	Den i -> applyenv r i |
	Eq(a, b) -> eq (eval a r) (eval b r) |
	Prod(a, b) -> prod (eval a r) (eval b r) |
	Sum(a, b) -> sum (eval a r) (eval b r) |
	Diff(a, b) -> diff (eval a r) (eval b r) |
	Minus a -> minus (eval a r) |
	And(a, b) -> et (eval a r) (eval b r) |
	Or(a, b) -> vel (eval a r) (eval b r) |
	Not a -> non (eval a r) |
	(* Valutazione delle espressioni aggiuntive sugli insiemi, uso le funzioni RTS corrispondenti *)
	Union(set_a, set_b) -> set_union (eval set_a r) (eval set_b r) |
	Intersection(set_a, set_b) -> set_intersection (eval set_a r) (eval set_b r) |
	DiffSet(set_a, set_b) -> set_diff (eval set_a r) (eval set_b r) |
	IsEmpty(set_a) -> Bool(set_isempty (eval set_a r)) |
	Insert(set_a, value) -> set_insert (eval set_a r) (eval value r) |
	Remove(set_a, value) -> set_remove (eval set_a r) (eval value r) |
	Contains(set_a, value) -> Bool((set_contains (eval set_a r) (eval value r))) |
	Subset(set_a, set_b) -> Bool((set_subset (eval set_a r) (eval set_b r))) |
	Min(set) -> set_min (eval set r) |
	Max(set) -> set_max (eval set r) |

	(* Valutazione degli operatori funzionali *)
	ForAll(set, predicate) ->
		(* Ho bisogno di valutare il set per verificarne la validità *)
		let s = eval set r in (
			if (typecheck "set" s) = true then
				(* La ForAll restituisce un evT Bool*)
				Bool(
					(* Ricostruisco il set perché per valutare il predicato ho bisogno di espressioni e non di valori *)
					match (rebuild_set s, predicate) with 
						(Eset(s_type, s_list), Fun(_)) ->
							let rec aux lista = 
								(match lista with
									(* ForAll su dominio vuoto fornisce true *)
									EmptySetList -> true |
									(* Restituisco false appena trovo un elemento che non rispetta il predicato *)
									Item(expr, rest) ->
										if (eval (FunCall(predicate, expr)) r) = Bool(true) then
											aux rest else false
								)
							in aux s_list
						| _-> failwith ("Errore di tipo")
				)
			else failwith ("Errore di tipo")) |
	(* La exists segue gli stessi principi della ForAll, ma la funzione ausiliaria verifica l'esistenza di un elemento
	   dell'insieme che verifichi il predicato e non controlla che tutti gli elementi lo verifichino *)
	Exists(set, predicate) ->
		let s = eval set r in (
			if (typecheck "set" s) = true then
				Bool(
					match (rebuild_set s, predicate) with 
						(Eset(s_type, s_list), Fun(_)) ->
							let rec aux lista = 
								(match lista with
									(* Exists su dominio vuoto fornisce false *)
									EmptySetList -> false |
									(* Restituisco true appena trovo un elemento che verifica il predicato *)
									Item(expr, rest) ->
										if (eval (FunCall(predicate, expr)) r) = Bool(true) then
											true else aux rest
								)
							in aux s_list
						| _-> failwith ("Errore di tipo")
				)
			else failwith ("Errore di tipo")) |
	(* La Map segue gli stessi principi della ForAll, ma restituisce l'insieme dei risultati dell'applicazione dell'operatore
	   passato come parametro a tutti gli elementi dell'insieme. Se l'insieme è vuoto, il tipo di ritorno è quello 
	   dell'insieme di partenza, altrimenti è quello di ritorno dell'operatore (ottenuto applicandolo sul primo elemento
	   e verificando il tipo del valore di ritorno) *)
	Map(set, operator) -> 
		let s = eval set r in (
			if (typecheck "set" s) = true then (
				match (rebuild_set s, operator) with 
					(Eset(s_type, s_list), Fun(_)) ->
						let rec aux lista ret = 
							(match lista with
								EmptySetList -> ret |
								Item(expr, rest) -> let evaluated = (eval (FunCall(operator, expr)) r) in
									if (list_contains ret evaluated) then (aux rest ret) 
									else (aux rest (evaluated::ret))
							)
						in 
							(match s_list with 
								EmptySetList -> Set(s_type, []) |
								Item(expr, rest) -> Set(get_lang_type (eval (FunCall(operator, expr)) r), (aux s_list []))
							)
					| _-> failwith ("Errore di tipo"))
			else failwith ("Errore di tipo")) |
	(* Filter segue gli stessi principi della ForAll, ma restituisce il sottoinsieme degli elementi di partenza sui quali
	   l'applicazione del predicato fornisce true *)
	Filter(set, predicate) -> 
		let s = eval set r in (
			if (typecheck "set" s) = true then
				(match (rebuild_set s, predicate) with 
					(Eset(s_type, s_list), Fun(_)) ->
						let rec aux lista = 
							(match lista with
								EmptySetList -> [] |
								Item(expr, rest) ->
									if (eval (FunCall(predicate, expr)) r) = Bool(true) then
										(eval expr r)::(aux rest) else aux rest
							)
						in Set(s_type, aux s_list)
					| _-> failwith ("Errore di tipo"))
			else failwith ("Errore di tipo")) |

	Ifthenelse(a, b, c) -> 
		let g = (eval a r) in
			if (typecheck "bool" g) 
				then (if g = Bool(true) then (eval b r) else (eval c r))
				else failwith ("nonboolean guard") |
	Let(i, e1, e2) -> eval e2 (bind r i (eval e1 r)) |
	Fun(i, a) -> FunVal(i, a, r) |
	FunCall(f, eArg) -> 
		let fClosure = (eval f r) in
			(match fClosure with
				FunVal(arg, fBody, fDecEnv) -> 
					eval fBody (bind fDecEnv arg (eval eArg r)) |
				RecFunVal(g, (arg, fBody, fDecEnv)) -> 
					let aVal = (eval eArg r) in
						let rEnv = (bind fDecEnv g fClosure) in
							let aEnv = (bind rEnv arg aVal) in
								eval fBody aEnv |
				_ -> failwith("non functional value")) |
	Letrec(f, funDef, letBody) ->
			(match funDef with
				Fun(i, fBody) -> let r1 = (bind r f (RecFunVal(f, (i, fBody, r)))) in
												eval letBody r1 |
				_ -> failwith("non functional def"))
	;;