package com.dacarex.capital;

import com.dacarex.capital.dao.CategoriaDAO;
import com.dacarex.capital.dao.ConexionDB;
import com.dacarex.capital.dao.UsuarioDAO;
import com.dacarex.capital.vista.VentanaLogin;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stageInicial) {

        System.out.println("=== DACAREX CAPITAL ===");

        ConexionDB.getInstance();

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        categoriaDAO.cargarIniciales();

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.crearUsuarioDemoSiVacio();

        System.out.println("Datos iniciales listos.");
        System.out.println("Login demo -> demo@dacarex.com / demo1234");

        new VentanaLogin().mostrar(stageInicial);
    }

    @Override
    public void stop() {
        ConexionDB.getInstance().cerrar();
        System.out.println("Aplicacion cerrada.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}