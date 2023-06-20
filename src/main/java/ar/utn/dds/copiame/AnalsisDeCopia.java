package ar.utn.dds.copiame;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import javax.persistence.*;

public class AnalsisDeCopia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pk;
	private String id;
	private float umbral;
	@Transient
	private Lote lote;
	@OneToMany
	@JoinColumn(name = "analisis_id")
	private List<ParDocumentos> pares;
	@OneToMany
	@JoinColumn(name = "analisis_id")
	private List<EvaluadorDeCopia> evaluadores;
	@OneToOne
	private ResultadoLote rl;
		
	public AnalsisDeCopia(float umbral, Lote lote) {
		super();
		this.umbral = umbral;
		this.lote = lote;
		
		this.evaluadores = new ArrayList<EvaluadorDeCopia>();
		
	}
	
	
	
	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public float getUmbral() {
		return umbral;
	}
	
	public void setUmbral(float umbral) {
		this.umbral = umbral;
	}
	
	public Lote getLote() {
		return lote;
	}
	
	public void addEvaluador(EvaluadorDeCopia eval) {
		this.evaluadores.add(eval);
	}
	
	public void procesar() {
		// Genero todos los pares de documentos Posibles
		this.pares = Generator.combination(this.lote.getDocumentos())
	       .simple(2)
	       .stream()
	       .map(t-> new ParDocumentos(t.get(0),t.get(1)) ) 
	       .collect(Collectors.toList());
		
		// Armo el resultado procesando cada par
		this.rl = new ResultadoLote();
		rl.setFechaInicio(LocalDateTime.now());
		for (EvaluadorDeCopia evaluador : this.evaluadores) {
			evaluador.procesar(pares);
			
		}
		
	}
	
	public ResultadoLote resultado() {
		for (ParDocumentos parDocumentos : pares) {
			if(parDocumentos.esCopia(this.umbral)) {
				rl.agregarPar(parDocumentos);	
			}			
		}
		rl.setFechaFin(LocalDateTime.now());
		return rl;
	}



	public Boolean finalizado() {
		
		return this.pares.stream().allMatch( p-> p.finalizado());
	}
	

}
