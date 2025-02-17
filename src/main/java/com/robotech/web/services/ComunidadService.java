package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IComunidadDao;
import com.robotech.web.models.Comunidad;

@Service
public class ComunidadService {

	@Autowired
	private IComunidadDao comunidadDao;

	public List<Comunidad> listarComunidad() {
		return comunidadDao.findAll();
	}

	public Comunidad listarID(Integer id) {
		return comunidadDao.findById(id).orElse(null);
	}

	public void guardarComunidad(Comunidad comunidad) {
		comunidadDao.save(comunidad);
	}

	public void elimianrComunidad(Integer id) {
		comunidadDao.deleteById(id);
	}

	////////////////////////////////////////////////////////

	// metodo buscarportem(filtro)
	public List<Comunidad> buscarPorTema(String tema) {
		return comunidadDao.findByTema(tema);
	}

	///////////////////////////////////////////////////////////

	public Page<Comunidad> listarComunidadPaginado(Pageable pageable) {
		return comunidadDao.findAllByOrderByIdDesc(pageable);
	}

	public Page<Comunidad> buscarPorTemaPaginado(String tema, Pageable pageable) {
		return comunidadDao.findByTemaContainingIgnoreCaseOrderByIdDesc(tema, pageable);
	}

}
