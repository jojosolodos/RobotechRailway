package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.ICategoriaDao;
import com.robotech.web.models.Categoria;

@Service
public class CategoriaService {

	@Autowired
	private ICategoriaDao categoriaDao;

	public List<Categoria> listarCategoria() {
		return categoriaDao.findAll();
	}

	public Categoria listarID(Integer id) {
		return categoriaDao.findById(id).orElse(null);
	}

	public void guardarCategoria(Categoria categoria) {
		categoriaDao.save(categoria);
	}

	public void eliminarCategoria(Integer id) {
		categoriaDao.deleteById(id);
	}

}
