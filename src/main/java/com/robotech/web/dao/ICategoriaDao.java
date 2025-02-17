package com.robotech.web.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.robotech.web.models.Categoria;

public interface ICategoriaDao extends JpaRepository<Categoria, Integer> {

}
