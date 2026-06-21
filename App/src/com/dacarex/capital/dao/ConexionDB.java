package com.dacarex.capital.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ConexionDB {

    private static ConexionDB instancia;
    private EntityManagerFactory emf;
    private EntityManager em;

    private ConexionDB() {
        try {
            emf = Persistence.createEntityManagerFactory("$objectdb/db/dacarex.odb");
            em = emf.createEntityManager();
            System.out.println("Conexion a ObjectDB establecida.");
        } catch (Exception e) {
            System.out.println("Error al conectar con ObjectDB: " + e.getMessage());
        }
    }

    public static ConexionDB getInstance() {
        if (instancia == null) instancia = new ConexionDB();
        return instancia;
    }

    public EntityManager getEm() {
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;
    }

    public void cerrar() {
        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
        System.out.println("Conexion cerrada.");
    }
}