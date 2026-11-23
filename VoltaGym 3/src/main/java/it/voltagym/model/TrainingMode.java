package it.voltagym.model;
public enum TrainingMode { INDIVIDUAL, GROUP; 
	public String toString(){ //questo metodo permette di rappresentare i valori attraverso stringhe comprensibili all'utente
		return this==INDIVIDUAL
				?"Individuale":"Di gruppo";
		} 
	}
//in questa classe viene identificata la modalità di allenamento