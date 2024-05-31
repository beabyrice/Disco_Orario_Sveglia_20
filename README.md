Tutte le richieste per questa applicazione sono state realizzate.

Ho implementato la sveglia con vibrazione attraverso l'invio di una notifica finale che denota il termine del timer. 

Ho provato a fare in modo che la vibrazione si fermasse al tocco della notifica, come si vede nel codice del file CountDownTimerService.kt, e ho lasciato la logica. 
Purtroppo non funziona anche provando in altri modi e non ho trovato la soluzione in nessuna documentazione. 

Al termine del timer la vibrazione risulterà continua e potrà essere fermata solo premendo il tasto di Lock Screen dopo essere rientrati nell'app (cliccando la notifica o rientrando normalmente).

Avrei potuto lasciare l'implementazione di una vibrazione che non si ripete ma non avrebbe avuto lo stesso effetto di una sveglia. Per questo ho lasciato questa, anche se non funzionante al 100%
