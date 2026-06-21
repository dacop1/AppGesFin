package com.dacarex.capital.dao;

import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.modelo.Categoria;

import javax.persistence.TypedQuery;
import java.util.List;

public class CategoriaDAO extends DAOBase<Categoria> {

    @Override
    protected Class<Categoria> getClase() { return Categoria.class; }

    @Override
    protected String getNombreEntidad() { return "Categoria"; }

    public List<Categoria> buscarPorTipo(TipoMovimiento tipo) {
        TypedQuery<Categoria> q = getEm().createQuery(
            "SELECT c FROM Categoria c WHERE c.tipo = :tipo ORDER BY c.nombre",
            Categoria.class
        );
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public void cargarIniciales() {
        if (buscarTodos().isEmpty()) {
            guardar(new Categoria("Nomina",      TipoMovimiento.INGRESO, "#28A745"));
            guardar(new Categoria("Ventas",      TipoMovimiento.INGRESO, "#17A2B8"));
            guardar(new Categoria("Alquiler",    TipoMovimiento.GASTO,   "#DC3545"));
            guardar(new Categoria("Suministros", TipoMovimiento.GASTO,   "#FD7E14"));
            guardar(new Categoria("Marketing",   TipoMovimiento.GASTO,   "#6F42C1"));
            guardar(new Categoria("Ocio",        TipoMovimiento.GASTO,   "#E83E8C"));
            guardar(new Categoria("Otros",       TipoMovimiento.GASTO,   "#6C757D"));
            System.out.println("Categorias iniciales cargadas.");
        }
    }
}