package it.polito.tdp.metroparis.model;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	Graph<Fermata,DefaultEdge> grafo;
	
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
	}
	
}
