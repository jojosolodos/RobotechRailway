package com.robotech.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.robotech.web.dao.ICategoriaDao;
import com.robotech.web.dao.IClubDao;
import com.robotech.web.dao.IEstado_usuarioDao;
import com.robotech.web.dao.ITipo_usuarioDao;
import com.robotech.web.dao.IUsuarioDao;
import com.robotech.web.models.Categoria;
import com.robotech.web.models.Club;
import com.robotech.web.models.Estado_usuario;
import com.robotech.web.models.Tipo_usuario;
import com.robotech.web.models.Usuario;

@Service
public class UsuarioService {

	@Autowired
	private IUsuarioDao usuarioDao;
	@Autowired
	private IEstado_usuarioDao estadoUsuDao;
	@Autowired
	private ITipo_usuarioDao tipoUsuDao;
	@Autowired
	private ICategoriaDao categoriaDao;
	@Autowired
	private IClubDao clubDao;

	public List<Usuario> listarUsuario() {
		return usuarioDao.findAll();
	}

	public Usuario listarID(Integer id) {
		return usuarioDao.findById(id).orElse(null);
	}

	public void guardarUsuario(Usuario usuario) {
		usuarioDao.save(usuario);
	}

	public void eliminarUsuario(Integer id) {
		usuarioDao.deleteById(id);
	}

	////////////////////////////////////////
	public Usuario obtenerUltimoUsuario3(Integer tipoUsuId) {
		return usuarioDao.findFirstByTipoUsuarioIdOrderByDesc(tipoUsuId);
	}

	///////////////////////////////////////////////
	// Metodos usados principalmente en Security

	public Usuario seleccionarCorreoUsuario(String correo) {
		return usuarioDao.findUsuarioByCorreo(correo);
	}

	public Estado_usuario obtenerEstadoInicial() {
		return estadoUsuDao.findById(3).orElse(null);
	}

	public Tipo_usuario AsignarTipoUUsu() {
		return tipoUsuDao.findById(2).orElse(null);
	}

	//////////////////////////////////////////////////
	public Tipo_usuario AsignarTipoUsu3() {
		return tipoUsuDao.findById(3).orElse(null);
	}

	public Estado_usuario obtenerEstadoDuenoC() {
		return estadoUsuDao.findById(1).orElse(null);
	}

	////////////////////////////////////////////////////////////

	// Metodos usados principamente en Registro
	public Categoria AsignarCatAmateur() {
		return categoriaDao.findById(1).orElse(null);
	}

	public Categoria AsignarCatSemiPro() {
		return categoriaDao.findById(2).orElse(null);
	}

	public Categoria AsignarCatProfesional() {
		return categoriaDao.findById(3).orElse(null);
	}

	////////////////////////////////////////////////////////////

	public List<Usuario> listarUsuarioTU2() {
		return usuarioDao.findByTipoUsuarioId(2);
	}

	/////////////////////////////////////////////////////////////////

	public Usuario buscarPorCorreo(String correo) {
		return usuarioDao.findByCorreo(correo).orElse(null);
	}

	public Usuario seleccionarUsuarioPorToken(String token) {
		return usuarioDao.findByResetToken(token).orElse(null);
	}

	// MÉTODO PARA CLUB
	public Club obtenerClubPorUsuario(Usuario usuario) {
		return clubDao.findByUsuarioId(usuario);
	}

	//////////////////////////////////////////////////////////////////////

	// Metodos de verificacion de existendia de username y correo

	public boolean existeCorreo(String correo) {
		return usuarioDao.existsByCorreo(correo);
	}

	public boolean existeUsername(String username) {
		return usuarioDao.existsByUsername(username);
	}

	/////////////////////////////////////////////////////////////////////

	// USUARIOS EN ESPERA
	public Page<Usuario> listtarUsuariosEnEspera(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return usuarioDao.listarUsuariosPorTipoYEstadoDesc(2, 3, pageable);

	}

	// USUARIOS DESAPROPBADOS

	public Page<Usuario> listtarUsuariosDesaprobados(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return usuarioDao.listarUsuariosPorTipoYEstadoDesc(2, 2, pageable);

	}
	// USUARIOS APROBADOS

	public Page<Usuario> listtarUsuariosAprobados(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return usuarioDao.listarUsuariosPorTipoYEstadoDesc(2, 1, pageable);

	}

	public Usuario buscarPorUsername(String username) {
		return usuarioDao.findByUsername(username);
	}
}
