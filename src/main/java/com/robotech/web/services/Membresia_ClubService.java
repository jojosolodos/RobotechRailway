package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IMembresia_clubDao;
import com.robotech.web.dao.IVictoriasDao;
import com.robotech.web.models.Membresia_club;

@Service
public class Membresia_ClubService {

	@Autowired
	private IMembresia_clubDao membresiaClubDao;

	@Autowired
	private IVictoriasDao victoriaDao;

	public List<Membresia_club> listarMembresiaClub() {
		return membresiaClubDao.findAll();
	}

	public Membresia_club listarID(Integer id) {
		return membresiaClubDao.findById(id).orElse(null);
	}

	public void guardarMembresiaClub(Membresia_club membresiaClub) {
		membresiaClubDao.save(membresiaClub);
	}

	public void eliminarMembresiaClub(Integer id) {
		membresiaClubDao.deleteById(id);
	}

	public Membresia_club obtenerPorUsuario(Integer usuarioId) {
		return membresiaClubDao.findByUsuarioId_Id(usuarioId);
	}

	public List<Membresia_club> listarJugadoresPorClubYEstado(Integer clubId) {
		return membresiaClubDao.listarJugadoresPorClubYEstado(clubId);
	}

	public List<Membresia_club> obtenerMiembrosPorEstado(Integer clubId, Integer estadoMembresiaId) {
		return membresiaClubDao.obtenerJugadoresPorClubYEstado(clubId, estadoMembresiaId);
	}

	public Membresia_club obtenerPorUsuarioYClub(int usuarioId, int clubId) {
		// Buscar la membresía del usuario para el club específico
		return membresiaClubDao.findByUsuarioId_IdAndClubId_Id(usuarioId, clubId);
	}
	
	public Membresia_club obtenerMembresiaRechazadaPorUsuario(int usuarioId) {
	    return membresiaClubDao.findFirstByUsuarioId_IdAndEstadoMembresiaId_Id(usuarioId, 2);
	}


	public int contarUsuariosEnClub(int clubId) {
		return membresiaClubDao.countByClubIdAndEstadoMembresiaId(clubId, 1);
	}

	public int contarVictoriasEnClub(int clubId) {
		Integer victorias = victoriaDao.sumarVictoriasPorClub(clubId);
		return (victorias != null) ? victorias : 0;
	}

	/////////////////////////////////////////////////////////////////////
	////////////////////////// PAGES ///////////////////////////////
	/////////////////////////////////////////////////////////////////////

	public Page<Membresia_club> listarJugadoresPorClubYEstadoPages(int clubId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return membresiaClubDao.listarJugadoresPorClubYEstadoPages(clubId, pageable);	
	}
	
	//vista Home
	public Page<Membresia_club> obtenerMiembrosPorEstado(int page, int size, int clubId, int estado) { 
	    Pageable pageable = PageRequest.of(page, size);
	    return membresiaClubDao.obtenerMiembrosPorEstado(clubId, estado, pageable);
	}
	
	public Page<Membresia_club> listarJugadoresAprobadosPorClub(int clubId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return membresiaClubDao.listarJugadoresAprobadosPorClub(clubId, pageable);
    }
	
	public Page<Membresia_club> listarJugadoresAprobadosPorClubPorFecha(int clubId, int page, int size) {
        return membresiaClubDao.findJugadoresAprobadosPorClubOrderByFechaDescH(clubId, PageRequest.of(page, size));
    }
	
	//vista club
	public Page<Membresia_club> obtenerMiembrosPorEstadoPorFecha(int page, int size, int clubId, int estadoMembresiaId) {
        return membresiaClubDao.findByClubIdAndEstadoMembresiaOrderByFechaDesc(clubId, estadoMembresiaId, PageRequest.of(page, size));
    }
	


}
