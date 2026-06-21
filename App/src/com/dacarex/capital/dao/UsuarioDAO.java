package com.dacarex.capital.dao;

import com.dacarex.capital.modelo.Usuario;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO extends DAOBase<Usuario> {

    @Override
    protected Class<Usuario> getClase() { return Usuario.class; }

    @Override
    protected String getNombreEntidad() { return "Usuario"; }

    public Optional<Usuario> buscarPorEmail(String email) {
        TypedQuery<Usuario> q = getEm().createQuery(
            "SELECT u FROM Usuario u WHERE u.email = :email",
            Usuario.class
        );
        q.setParameter("email", email);
        List<Usuario> resultado = q.getResultList();
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    public void crearUsuarioDemoSiVacio() {
        if (buscarTodos().isEmpty()) {
            guardar(new Usuario(
                "Usuario Demo", "demo@dacarex.com", "demo1234",
                com.dacarex.capital.enums.TipoCuenta.PERSONAL, null
            ));
            System.out.println("Usuario demo creado.");
        }
    }
}