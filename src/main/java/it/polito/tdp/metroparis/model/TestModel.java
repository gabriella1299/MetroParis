package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		Model m=new Model();
		
		m.creaGrafo();
		
		Fermata p=m.trovaFermata("La Fourche");
		
		if(p==null) {
			System.out.println("Fermata non trovata");
		}
		else {
			List<Fermata> raggiungibili=m.fermateRaggiungibili(p);//ha 619 elementi perche' e' un grafo connesso
			//System.out.println(raggiungibili);//prima abbiamo adiacenti di livello 1, poi di livello 2, e cosi' via...
											  //output con ordine di visita in ampiezza
		}
		
		Fermata a =m.trovaFermata("Temple");
		List<Fermata> percorso=m.trovaCammino(p, a);
		System.out.println(percorso);
	}

}
