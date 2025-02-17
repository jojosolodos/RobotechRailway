package com.robotech.web.security;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.robotech.web.models.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CASH implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		// Obtener el usuario desde CustomUserDetails
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Usuario usuar = userDetails.getUsuario();

		// Almacenar el usuario en la sesión
		request.getSession().setAttribute("usuario", usuar);

		String redirectUrl = "/default";

		for (GrantedAuthority authority : authorities) {
			if (authority.getAuthority().equals("Administrador")) {
				redirectUrl = "/admin/usuarios-enespera";
				break;
			} else if (authority.getAuthority().equals("Usuario")) {
				redirectUrl = "/home";
				break;
			} else if (authority.getAuthority().equals("Dueno Club")) {
				redirectUrl = "/club";
				break;
			}
		}

		response.sendRedirect(redirectUrl);
	}

}