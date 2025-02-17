package com.robotech.web.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String nombre;
	private String username;
	private String correo;
	private String password;
	private Integer edad;
	private Integer trayectoria;
	private String foto_robot;
	private String foto_perfil;

	@OneToOne
	@JoinColumn(name = "tipo_usuario_id")
	private Tipo_usuario tipoUsuario;

	@ManyToOne
	@JoinColumn(name = "estado_usuario_id")
	private Estado_usuario estadoUsuario;

	@OneToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoriaId;

	@OneToOne(mappedBy = "usuarioId")
	private Membresia_club membresia;

	private String resetToken;

	private LocalDateTime resetTokenExpiration;

	public Usuario() {
		super();

	}

	public Usuario(String nombre, String username, String correo, String password, Integer edad, Integer trayectoria,
			String foto_robot, String foto_perfil, Tipo_usuario tipoUsuario, Estado_usuario estadoUsuario,
			Categoria categoriaId) {
		super();
		this.nombre = nombre;
		this.username = username;
		this.correo = correo;
		this.password = password;
		this.edad = edad;
		this.trayectoria = trayectoria;
		this.foto_robot = foto_robot;
		this.foto_perfil = foto_perfil;
		this.tipoUsuario = tipoUsuario;
		this.estadoUsuario = estadoUsuario;
		this.categoriaId = categoriaId;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getEdad() {
		return edad;
	}

	public void setEdad(Integer edad) {
		this.edad = edad;
	}

	public Integer getTrayectoria() {
		return trayectoria;
	}

	public void setTrayectoria(Integer trayectoria) {
		this.trayectoria = trayectoria;
	}

	public String getFoto_robot() {
		return foto_robot;
	}

	public void setFoto_robot(String foto_robot) {
		this.foto_robot = foto_robot;
	}

	public String getFoto_perfil() {
		return foto_perfil;
	}

	public void setFoto_perfil(String foto_perfil) {
		this.foto_perfil = foto_perfil;
	}

	public Tipo_usuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(Tipo_usuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public Estado_usuario getEstadoUsuario() {
		return estadoUsuario;
	}

	public void setEstadoUsuario(Estado_usuario estadoUsuario) {
		this.estadoUsuario = estadoUsuario;
	}

	public Categoria getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Categoria categoriaId) {
		this.categoriaId = categoriaId;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getResetTokenExpiration() {
		return resetTokenExpiration;
	}

	public void setResetTokenExpiration(LocalDateTime resetTokenExpiration) {
		this.resetTokenExpiration = resetTokenExpiration;
	}

	public boolean isResetTokenValid() {
		return resetToken != null && resetTokenExpiration != null && resetTokenExpiration.isAfter(LocalDateTime.now());
	}

	public Membresia_club getMembresia() {
		return membresia;
	}

	public void setMembresia(Membresia_club membresia) {
		this.membresia = membresia;
	}

}
