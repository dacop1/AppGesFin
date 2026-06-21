package com.dacarex.capital.dao;

import com.dacarex.capital.modelo.EntidadBase;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public abstract class DAOBase<T extends EntidadBase> implements IRepositorio<T, Long> {

    protected abstract Class<T> getClase();
    protected abstract String getNombreEntidad();

    protected EntityManager getEm() {
        return ConexionDB.getInstance().getEm();
    }

    @Override
    public void guardar(T entidad) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        em.persist(entidad);
        em.getTransaction().commit();
    }

    @Override
    public void actualizar(T entidad) {
        EntityManager em = getEm();
        em.getTransaction().begin();
        em.merge(entidad);
        em.getTransaction().commit();
    }

    @Override
    public void eliminar(Long id) {
        EntityManager em = getEm();
        T entidad = em.find(getClase(), id);
        if (entidad != null) {
            em.getTransaction().begin();
            em.remove(entidad);
            em.getTransaction().commit();
        }
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(getEm().find(getClase(), id));
    }

    @Override
    public List<T> buscarTodos() {
        TypedQuery<T> q = getEm().createQuery(
            "SELECT e FROM " + getNombreEntidad() + " e",
            getClase()
        );
        return q.getResultList();
    }
}