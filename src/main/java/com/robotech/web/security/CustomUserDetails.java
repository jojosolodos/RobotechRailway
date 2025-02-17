package com.robotech.web.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.robotech.web.models.Usuario;

import java.util.Collection;

public class CustomUserDetails extends User {
	private final Usuario usuario;

	public CustomUserDetails(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getCorreo(), usuario.getPassword(), authorities);
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
