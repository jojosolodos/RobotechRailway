package com.robotech.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.robotech.web.models.Tipo_usuario;
import com.robotech.web.models.Usuario;
import com.robotech.web.services.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Component
public class FormLogin implements UserDetailsService {

	@Autowired
	private UsuarioService usuarioService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuar = usuarioService.seleccionarCorreoUsuario(username);

		if (usuar == null) {
			throw new UsernameNotFoundException("No existe el usuario.");
		}

		List<GrantedAuthority> authorities = new ArrayList<>();
		Tipo_usuario tipo = usuar.getTipoUsuario();

		if (tipo != null) {
			authorities.add(new SimpleGrantedAuthority(tipo.getNombre()));
		} else {
			throw new UsernameNotFoundException("No tiene los permisos necesarios para ingresar");
		}

		return new CustomUserDetails(usuar, authorities);
	}

}
