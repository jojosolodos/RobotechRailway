package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;


import com.robotech.web.dao.IVictoriasDao;
import com.robotech.web.models.Victorias;

@Service
public class VictoriasService {

	@Autowired
	private IVictoriasDao victoriasDao;

	public List<Victorias> listarVictorias() {
		return victoriasDao.findAll();
	}

	public Victorias listarID(Integer id) {
		return victoriasDao.findById(id).orElse(null);
	}

	public void guardarVictorias(Victorias victorias) {
		victoriasDao.save(victorias);
	}

	public void eliminarVictorias(Integer id) {
		victoriasDao.deleteById(id);
	}

	///////////////////////////////////////////////////////////

	public List<Victorias> listarVictoriasDesc() {
		return victoriasDao.findAllByOrderByCantidadDesc();
	}

    public Page<Victorias> listarVictoriasDescPageable(Pageable pageable) {
        List<Victorias> top24Victorias = victoriasDao.findTop24ByOrderByCantidadDesc(PageRequest.of(0, 24)); // Solo 24 primeros

        int start = Math.min((int) pageable.getOffset(), top24Victorias.size());
        int end = Math.min(start + pageable.getPageSize(), top24Victorias.size());

        List<Victorias> paginatedList = top24Victorias.subList(start, end);
        return new PageImpl<>(paginatedList, pageable, top24Victorias.size());
    }

	public List<Victorias> listarTop3Victorias() {
		Pageable topThree = PageRequest.of(0, 3); // Página 0 con 3 resultados
		return victoriasDao.findAllByOrderByCantidadDesc(topThree);
	}

}
