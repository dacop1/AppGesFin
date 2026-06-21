package com.dacarex.capital.service;

import com.dacarex.capital.dao.UsuarioDAO;
import com.dacarex.capital.enums.TipoCuenta;
import com.dacarex.capital.exception.AutenticacionException;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Usuario;

import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioActual;

    public Usuario registrar(String nombre, String email, String contrasenia,
                             TipoCuenta tipo, String nombreEmpresa) {

        Usuario nuevo = new Usuario(nombre, email, contrasenia, tipo, nombreEmpresa);

        if (!nuevo.esValido())
            throw new ValidacionException(nuevo.validar());

        if (usuarioDAO.buscarPorEmail(email).isPresent())
            throw new ValidacionException("Ya existe una cuenta con ese email.");

        usuarioDAO.guardar(nuevo);
        return nuevo;
    }

    public Usuario login(String email, String contrasenia) {
        Optional<Usuario> usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario.isEmpty() || !usuario.get().getContrasenia().equals(contrasenia))
            throw new AutenticacionException("Email o contrasenia incorrectos.");

        usuarioActual = usuario.get();
        return usuarioActual;
    }

    public void logout() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}