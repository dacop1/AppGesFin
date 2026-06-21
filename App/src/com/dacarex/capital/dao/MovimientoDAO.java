package com.dacarex.capital.dao;

import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.modelo.Movimiento;

import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class MovimientoDAO extends DAOBase<Movimiento> {

    @Override
    protected Class<Movimiento> getClase() { return Movimiento.class; }

    @Override
    protected String getNombreEntidad() { return "Movimiento"; }

    public List<Movimiento> buscarPorTipo(TipoMovimiento tipo) {
        TypedQuery<Movimiento> q = getEm().createQuery(
            "SELECT m FROM Movimiento m WHERE m.tipo = :tipo ORDER BY m.fecha DESC",
            Movimiento.class
        );
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<Movimiento> buscarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        TypedQuery<Movimiento> q = getEm().createQuery(
            "SELECT m FROM Movimiento m WHERE m.fecha >= :desde AND m.fecha <= :hasta ORDER BY m.fecha DESC",
            Movimiento.class
        );
        q.setParameter("desde", desde);
        q.setParameter("hasta", hasta);
        return q.getResultList();
    }

    public List<Movimiento> buscarPorTexto(String texto) {
        TypedQuery<Movimiento> q = getEm().createQuery(
            "SELECT m FROM Movimiento m WHERE LOWER(m.descripcion) LIKE :texto ORDER BY m.fecha DESC",
            Movimiento.class
        );
        q.setParameter("texto", "%" + texto.toLowerCase() + "%");
        return q.getResultList();
    }
}