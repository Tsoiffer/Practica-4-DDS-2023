package ar.utn.dds.copiame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class AnalisisAddController implements Handler {

	private AnalsisRepository repo;

	public AnalisisAddController(AnalsisRepository repo) {
		super();
		this.repo = repo;
	}
	
	@Override
	public void handle(Context ctx) throws Exception {
		
		// Proceso de parámetros de entrada
		String destDirectory = "/tmp/unlugar";
		System.out.print(ctx.uploadedFiles());
		UnzipUtility.unzip(ctx.uploadedFile("file").content() ,destDirectory);
		
		// Armado del Análisis
		Lote lote = new Lote(destDirectory);
		lote.validar();
		lote.cargar();
		float umbral = 0.5f;
		AnalsisDeCopia analisis = new AnalsisDeCopia(umbral, lote);
		analisis.addEvaluador(new EvaluadorDeCopiaAutomatico());
		Revisor revisor = new Revisor();
		List<Revisor> revisores = Arrays.asList(revisor);
		
		EvaluadorDeCopiaManual eval = new EvaluadorDeCopiaManual(revisores,1.0);
		analisis.addEvaluador(eval);		
		
		// Proceso del lote
		analisis.procesar();
		
		// Guardado
		repo.save(analisis);
		
		// Armado de la respuesta
		Map<String,String> rta = new HashMap<String, String>();
		rta.put("analisis", analisis.getId());
		rta.put("terminado", analisis.finalizado().toString());
		ctx.json(rta);

	}

}
