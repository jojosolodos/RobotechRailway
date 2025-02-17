package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.IEstado_usuarioDao;
import com.robotech.web.models.Estado_usuario;

@Service
public class Estado_usuarioService {

	@Autowired
	private IEstado_usuarioDao estadoUsuDao;

	public List<Estado_usuario> listarEstadoUsu() {
		return estadoUsuDao.findAll();
	}

	public Estado_usuario listarID(Integer id) {
		return estadoUsuDao.findById(id).orElse(null);
	}

	public void guardarEstadoUsu(Estado_usuario estadoUsu) {
		estadoUsuDao.save(estadoUsu);
	}

	public void eliminarEstadoUsu(Integer id) {
		estadoUsuDao.deleteById(id);
	}
}
