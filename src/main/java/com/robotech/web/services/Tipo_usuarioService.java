package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.ITipo_usuarioDao;
import com.robotech.web.models.Tipo_usuario;

@Service
public class Tipo_usuarioService {

	@Autowired
	private ITipo_usuarioDao tipoUsuarioDao;

	public List<Tipo_usuario> listarTipoUsuario() {
		return tipoUsuarioDao.findAll();
	}

	public Tipo_usuario listarID(Integer id) {
		return tipoUsuarioDao.findById(id).orElse(null);
	}

	public void guardarTipoUsuario(Tipo_usuario tipoUsu) {
		tipoUsuarioDao.save(tipoUsu);
	}

	public void eliminarTipoUsu(Integer id) {
		tipoUsuarioDao.deleteById(id);
	}
}
