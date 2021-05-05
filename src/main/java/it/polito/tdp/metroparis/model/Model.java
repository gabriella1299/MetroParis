package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata,DefaultEdge> grafo;
	
	Map<Fermata,Fermata> predecessore;
	
	public void creaGrafo() {
		this.grafo=new SimpleGraph<>(DefaultEdge.class);
		
		MetroDAO dao=new MetroDAO();
		List<Fermata> fermate=dao.getAllFermate();
		
		//Aggiungo al grafo tutte le fermate:
		/*for(Fermata f:fermate) {
			this.grafo.addVertex(f);
		}*/
		//In modo uguale al for si puo' sfruttare il metodo della classe Graphs:
		Graphs.addAllVertices(this.grafo, fermate);
		
		//Aggiungiamo gli archi:
		//Iteriamo su tutte le fermate di partenza f1 e di arrivo f2
		//vertexSEt restituisce i vertici del grafo
		/*for(Fermata f1:this.grafo.vertexSet()) {
			for(Fermata f2:this.grafo.vertexSet()) {
				//Se non sono uguali e se sono collegate creo un collegamento tra le due:
				if(!f1.equals(f2) && dao.fermateCollegate(f1,f2)) {
					this.grafo.addEdge(f1, f2);
				}
			}
		}*/
		//Creiamo loop sui nodi: ci mettiamo di meno, il num. di query e' piu' ridotto
		List<Connessione> connessioni=dao.getAllConnessioni(fermate);
		for(Connessione c:connessioni) {
			this.grafo.addEdge(c.getStazP(), c.getStazA());
		}
		//System.out.println(this.grafo);
		System.out.format("Grafo creato con %d vertici e %d archi\n", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
		
		/*
		Fermata f = null;
		
		Set<DefaultEdge> archi=this.grafo.edgesOf(f);
		for(DefaultEdge e:archi) {
			/*
			Fermata f1=this.grafo.getEdgeSource(e);
			Fermata f2=this.grafo.getEdgeTarget(e);
			if(f1.equals(f2)) {
				//f2 e' quello che mi serve
			}
			else {
				//f1 e' quello che mi serve
			}
			
			f1=Graphs.getOppositeVertex(this.grafo, e, f);
		}
		List<Fermata> fermateAdiacenti= Graphs.successorListOf(this.grafo, f);
		*/
	}
	
	//lista di vertici restituiti dall'iteratore usando next()
	public List<Fermata> fermateRaggiungibili(Fermata partenza){
		
		//visita in PROFONDITA
		//DepthFirstIterator<Fermata,DefaultEdge> dfv= new DepthFirstIterator<>(this.grafo,partenza);
				
		//visita in AMPIEZZA
		BreadthFirstIterator<Fermata,DefaultEdge> bfv=new BreadthFirstIterator<>(this.grafo,partenza);
		
		this.predecessore=new HashMap<>();//ogni volta che richiamo questo metodo riparte con una mappa pulita, nuova
		this.predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Fermata,DefaultEdge>() {
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				// Chiamato quando algoritmo attraversa un arco per trovare un vertice
				//Ho info su ARCO, quindi anche sui due vertici che collega-->meglio di vertexTraversed
				DefaultEdge arco=e.getEdge();
				Fermata a=grafo.getEdgeSource(arco);
				Fermata b=grafo.getEdgeTarget(arco);
				//Non devo piu fare la ricerca tra tutti gli adiacenti ma solo su questi due
				//ho scoperto 'a' arrivando da 'b' (se 'b' lo conoscevo)
				//Dobbiamo fare questo controllo perche' e' un grafo non orientato:
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {//l'avevo gia visitato quindi e' il vertice sorgente
					predecessore.put(a, b);
					//System.out.println(a+" scoperto da "+b);
				}
				else if(predecessore.containsKey(a) && !predecessore.containsKey(b)){//di sicuro conoscevo 'a' e quindi ho scoperto 'b'
					predecessore.put(b, a);
					//System.out.println(b+" scoperto da "+a);
				}
				//se fossero tutti e due contenuti in predecessore non li aggiungo
				//se fosse stato GRAFO ORIENTATO: non servivano if e elseif!
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
				
				//Fermata nuova=e.getVertex();
				//Fermata precedente= //vertice adiacente a 'nuova' che sia gia' stato raggiunto
									//gia' presente nella key della mappa
				//predecessore.put(nuova,precedente); //deve ricercare predecessore tra tutti i contesti in cui e' presente, non this.
				//Facciamo in edgeTraversed perche' piu' efficiente!
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
				// TODO Auto-generated method stub

			}
		}); //creiamo la classe inline ma possiamo anche creare la classe separatamente
		
		List<Fermata> result=new ArrayList<>();
		
		while(bfv.hasNext()) {//solo se has next ritorna vero posso chiamare next!
			Fermata f=bfv.next();
			result.add(f);
		}
		//iteratore si e' esaurito--> ho la lista dei vertici adiacenti: result
		return result;
		
	}
	
	public Fermata trovaFermata(String nome) {
		//Scandisco collection per trovare un elemento
		//Complessita O(n)
		//Se dovessi farla piu' volte mi converrebbe avere una mappa per trovare il valore data la chiave
		//quando costruisco il grafo creo la mappa e poi-->mappa.get(nome)
		for(Fermata f:this.grafo.vertexSet()) {
			if(f.getNome().equals(nome)) {
				return f;
			}
		}
		return null;
	}
	
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo) {
		
		fermateRaggiungibili(partenza);
		
		List<Fermata> result=new LinkedList<>(); //in un arrayList sposto di uno gli elementi gia esistenti, mentre nella linked e' 0(1)
		result.add(arrivo);
		
		Fermata f=arrivo;
		while(predecessore.get(f)!=null) {
			f=predecessore.get(f); //predecessore.getParent(f);
			result.add(0,f); //per mettere ordine corretto, aggiungo in testa
		}
		
		return result;//contiene tutte le fermate da arrivo a partenza sul cammino minimo (prima di 0,f...dopo l'ordine e' corretto)
		
	}
	
	// Implementazione di 'trovaCammino' che NON usa il traversal listener ma sfrutta
	// il metodo getParent presente in BreadthFirstIterator
	public List<Fermata> trovaCammino2(Fermata partenza, Fermata arrivo) {
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = 
				new BreadthFirstIterator<Fermata, DefaultEdge>(this.grafo, partenza) ;

		// fai lavorare l'iteratore per trovare tutti i vertici
		while(bfv.hasNext())
			bfv.next() ; // non mi serve il valore

		List<Fermata> result = new LinkedList<>() ;
		Fermata f = arrivo ;
		while(f!=null) {
			result.add(f) ;
			f = bfv.getParent(f) ;
		}

		return result ;

	}
	
}
