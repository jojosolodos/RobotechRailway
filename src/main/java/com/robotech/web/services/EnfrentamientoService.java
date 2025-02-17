package com.robotech.web.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.robotech.web.dao.IEnfrentamientoDao;
import com.robotech.web.dao.IInscripcionesDao;
import com.robotech.web.dao.IVictoriasDao;
import com.robotech.web.models.Enfrentamiento;
import com.robotech.web.models.Fase;
import com.robotech.web.models.Inscripciones;
import com.robotech.web.models.Usuario;
import com.robotech.web.models.Victorias;

@Service
public class EnfrentamientoService {

	@Autowired
	private IInscripcionesDao inscripcionesDao;
	@Autowired
	private FaseService faseService;
	@Autowired
	private IEnfrentamientoDao enfrentamientoDao;
	@Autowired
	private IVictoriasDao victoriasDao;

	public List<Enfrentamiento> listarEnfrentamiento() {
		return enfrentamientoDao.findAll();
	}

	public Enfrentamiento listarID(Integer id) {
		return enfrentamientoDao.findById(id).orElse(null);
	}

	public void guardarEnfrentamiento(Enfrentamiento enfrentamiento) {
		enfrentamientoDao.save(enfrentamiento);
	}

	public void eliminarEnfrentamiento(Integer id) {
		enfrentamientoDao.deleteById(id);
	}

	////////////////////////////////////////////////////////////
	// GENERACION ALEATORIA TURNOS CUARTOS
	@Transactional
	public List<Enfrentamiento> generarEnfrentamientosAleatorios(Integer torneoId, Fase fase) {
		List<Inscripciones> inscripciones = inscripcionesDao.findRandomInscripcionesByTorneo(torneoId);

		if (inscripciones.size() < 8) {
			throw new IllegalStateException(
					"No hay suficientes inscripciones en el torneo para generar los enfrentamientos.");
		}

		Collections.shuffle(inscripciones);

		List<Enfrentamiento> enfrentamientos = new ArrayList<>();
		for (int i = 0; i < 8; i += 2) {
			Enfrentamiento enfrentamiento = new Enfrentamiento();
			// enfrentamiento.setFaseId(faseService.listarID(1));
			enfrentamiento.setFaseId(fase);
			enfrentamiento.setParticipante1(inscripciones.get(i));
			enfrentamiento.setParticipante2(inscripciones.get(i + 1));
			enfrentamientos.add(enfrentamiento);
		}

		// Debug antes de guardar
		enfrentamientos.forEach(e -> System.out.println("Enfrentamiento: " + e.getParticipante1().getUsuarioId().getId()
				+ " vs " + e.getParticipante2().getUsuarioId().getId()));
		return enfrentamientoDao.saveAll(enfrentamientos);

	}

	public List<Enfrentamiento> obtenerEnfrentamientosPorTorneo(int torneoId) {
		return enfrentamientoDao.findByTorneoId(torneoId);
	}

	public List<Enfrentamiento> obtenerEnfrentamientosPorTorneo(List<Inscripciones> inscripciones) {
		List<Integer> jugadorIds = inscripciones.stream().map(inscripcion -> inscripcion.getUsuarioId().getId())
				.collect(Collectors.toList());

		return enfrentamientoDao.findByJugadorIds(jugadorIds);
	}

	public List<Enfrentamiento> obtenerPorTorneo(Integer torneoId) {
		return enfrentamientoDao.findByTorneoId(torneoId);
	}

	public Integer obtenerTorneoId(Integer enfrentamientoId) {
		Enfrentamiento enfrentamiento = enfrentamientoDao.findById(enfrentamientoId).orElse(null);
		if (enfrentamiento != null && enfrentamiento.getParticipante1() != null) {
			return enfrentamiento.getParticipante1().getTorneoId().getId();
		}
		return null; // Si el enfrentamiento no existe o no tiene torneo asociado
	}

	// GENERACIÓN DE TURNOS PARA SEMIFINAL
	@Transactional
	public List<Enfrentamiento> generarEnfrentamientosSemifinal(Integer torneoId, Fase fase) {
	    // Obtener enfrentamientos de cuartos en orden
	    List<Enfrentamiento> enfrentamientosCuartos = enfrentamientoDao.findByTorneoAndFase(torneoId, 1);
	    
	    if (enfrentamientosCuartos.size() != 4) {
	        throw new IllegalStateException("Debe haber exactamente 4 ganadores en los cuartos de final.");
	    }

	    // Obtener los ganadores en el mismo orden
	    List<Inscripciones> ganadores = enfrentamientosCuartos.stream()
	        .map(Enfrentamiento::getGanador)
	        .collect(Collectors.toList());

	    // Verificar si hay 4 ganadores
	    if (ganadores.size() != 4) {
	        throw new IllegalStateException("Error al obtener los ganadores de cuartos de final.");
	    }

	    List<Enfrentamiento> enfrentamientosSemifinal = new ArrayList<>();

	    // Crear el primer enfrentamiento: 1° vs 2°
	    Enfrentamiento enfrentamiento1 = new Enfrentamiento();
	    enfrentamiento1.setFaseId(fase);
	    enfrentamiento1.setParticipante1(ganadores.get(0));
	    enfrentamiento1.setParticipante2(ganadores.get(1));
	    enfrentamientosSemifinal.add(enfrentamiento1);

	    // Crear el segundo enfrentamiento: 3° vs 4°
	    Enfrentamiento enfrentamiento2 = new Enfrentamiento();
	    enfrentamiento2.setFaseId(fase);
	    enfrentamiento2.setParticipante1(ganadores.get(2));
	    enfrentamiento2.setParticipante2(ganadores.get(3));
	    enfrentamientosSemifinal.add(enfrentamiento2);

	    return enfrentamientoDao.saveAll(enfrentamientosSemifinal);
	}

	public List<Enfrentamiento> obtenerPorFase(int torneoId, int faseId) {
		return enfrentamientoDao.findByTorneoAndFase(torneoId, faseId);
	}

	// GENERAR TURNO FINAL
	@Transactional
	public List<Enfrentamiento> generarFinal(Integer torneoId, Fase fase) {
		// Obtener los enfrentamientos de SEMIFINAL donde ya se eligieron ganadores
		List<Enfrentamiento> semifinales = enfrentamientoDao.findByTorneoId(torneoId).stream()
				.filter(e -> e.getFaseId().getId() == 2 && e.getGanador() != null).collect(Collectors.toList());

		if (semifinales.size() < 2) {
			throw new IllegalStateException("No hay suficientes ganadores para generar la FINAL.");
		}

		List<Enfrentamiento> finales = new ArrayList<>();

		// Solo hay un enfrentamiento en la final, tomando los dos ganadores de la
		// semifinal
		Enfrentamiento enfrentamiento = new Enfrentamiento();
		enfrentamiento.setFaseId(fase); // Fase FINAL
		enfrentamiento.setParticipante1(semifinales.get(0).getGanador());
		enfrentamiento.setParticipante2(semifinales.get(1).getGanador());

		finales.add(enfrentamiento);

		return enfrentamientoDao.saveAll(finales);
	}

	// GENERAR SUMA DE VICTORIAS AL USUARIO GANADOR
	@Transactional
	public void registrarVictoriaFinal(Integer enfrentamientoId) {
		Enfrentamiento enfrentamiento = enfrentamientoDao.findById(enfrentamientoId).orElse(null);

		if (enfrentamiento == null || enfrentamiento.getGanador() == null) {
			throw new IllegalStateException("El enfrentamiento no existe o no tiene ganador asignado.");
		}

		// Verificar si la fase es la FINAL (suponiendo que la ID de la fase final es 3)
		if (enfrentamiento.getFaseId().getId() != 3) {
			throw new IllegalStateException("Este enfrentamiento no pertenece a la fase final.");
		}

		// Obtener el usuario ganador
		Usuario ganador = enfrentamiento.getGanador().getUsuarioId();

		// Verificar si ya existe un registro en la tabla Victorias para este usuario
		Victorias victoria = victoriasDao.findByUsuarioId_Id(ganador.getId());

		if (victoria == null) {
			// Si no existe, crear un nuevo registro con 1 victoria
			victoria = new Victorias();
			victoria.setUsuarioId(ganador);
			victoria.setCantidad(1);
		} else {
			// Si ya existe, sumar una victoria
			victoria.setCantidad(victoria.getCantidad() + 1);
		}

		// Guardar en la base de datos
		victoriasDao.save(victoria);
	}

}
