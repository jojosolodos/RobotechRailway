package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IEstado_membresiaDao;
import com.robotech.web.models.Estado_membresia;

@Service
public class Estado_membresiaService {

	@Autowired
	private IEstado_membresiaDao estadoMembDao;

	public List<Estado_membresia> listarEstadoMem() {
		return estadoMembDao.findAll();
	}

	public Estado_membresia listarID(Integer id) {
		return estadoMembDao.findById(id).orElse(null);
	}

	public void guardarEstadoMem(Estado_membresia estadoMem) {
		estadoMembDao.save(estadoMem);
	}

	public void eliminarEstadoMem(Integer id) {
		estadoMembDao.deleteById(id);
	}
}
