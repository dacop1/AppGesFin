package com.dacarex.capital;

import com.dacarex.capital.dao.CategoriaDAO;
import com.dacarex.capital.dao.ConexionDB;
import com.dacarex.capital.dao.UsuarioDAO;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.VentanaLogin;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== DACAREX CAPITAL ===");

        ConexionDB.getInstance();

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        categoriaDAO.cargarIniciales();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.crearUsuarioDemoSiVacio();

        System.out.println("Datos iniciales listos.");
        System.out.println("Login demo -> demo@dacarex.com / demo1234");

        SwingUtilities.invokeLater(() -> {
            TemaManager.aplicarLaf();
            new VentanaLogin().setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ConexionDB.getInstance().cerrar();
            System.out.println("Aplicacion cerrada.");
        }));
    }
}