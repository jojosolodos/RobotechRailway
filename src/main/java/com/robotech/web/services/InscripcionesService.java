package com.robotech.web.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IInscripcionesDao;
import com.robotech.web.models.Inscripciones;
import com.robotech.web.models.Torneo;

@Service
public class InscripcionesService {

	@Autowired
	private IInscripcionesDao inscripcionesDao;

	@Autowired
	private FaseService faseService;

	@Autowired
	private TorneoService torneoService;

	public List<Inscripciones> listarInscripciones() {
		return inscripcionesDao.findAll();
	}

	public Inscripciones listarID(Integer id) {
		return inscripcionesDao.findById(id).orElse(null);
	}

	public void guaradarInscripciones(Inscripciones inscripciones) {
		inscripcionesDao.save(inscripciones);
	}

	public void eliminarInscripciones(Integer id) {
		inscripcionesDao.deleteById(id);
	}

	public List<Inscripciones> obtenerPorTorneo(int torneoId, int estadoId) {
		Torneo torneo = torneoService.listarID(torneoId); // Obtener el torneo por su ID
		return inscripcionesDao.findByTorneoIdAndEstadoInscripcionId_Id(torneo, estadoId);
	}

	public boolean existeInscripcion(Integer usuarioId, int torneoId) {
		return inscripcionesDao.existsByUsuarioIdIdAndTorneoIdId(usuarioId, torneoId);
	}

	public Inscripciones obtenerInscripcionPorUsuario(int usuarioId) {
		return inscripcionesDao.findByUsuarioIdId(usuarioId);
	}

	public Inscripciones obtenerOtraInscripcion(int usuarioId, int torneoActualId) {
		List<Inscripciones> inscripcionesUsuario = inscripcionesDao.findByUsuarioId_Id(usuarioId);
		return inscripcionesUsuario.stream().filter(inscripcion -> inscripcion.getTorneoId().getId() != torneoActualId)
				.findFirst().orElse(null);
	}

	public boolean puedeInscribirse(Integer usuarioId) {
	    Pageable pageable = PageRequest.of(0, 1); // Obtener solo la última inscripción
	    List<Inscripciones> inscripciones = inscripcionesDao.findUltimaInscripcion(usuarioId, pageable);

	    if (inscripciones.isEmpty()) {
	        return true; // Nunca se ha inscrito, puede hacerlo
	    }

	    LocalDate fechaUltimoTorneo = inscripciones.get(0).getTorneoId().getFecha(); // Asumiendo que es LocalDate
	    return LocalDate.now().isAfter(fechaUltimoTorneo.plusDays(5)); // Deben pasar 5 días
	}

	// Verificar si un club puede inscribir más participantes en un torneo
	public boolean puedeInscribirMasParticipantes(Integer torneoId, Integer clubId) {
		int inscritos = inscripcionesDao.countByTorneoAndClub(torneoId, clubId);
		return inscritos < 3; // Permitir solo si hay menos de 3 inscritos
	}

	// contador de limite de usuarios
	public int contarInscripcionesPorTorneo(Integer torneoId) {
		return inscripcionesDao.countByTorneoId(torneoId);
	}

	///////////////////////////////////////////////////////////////////////////////////////

}
