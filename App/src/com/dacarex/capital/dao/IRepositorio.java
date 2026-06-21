package com.dacarex.capital.dao;

import java.util.List;
import java.util.Optional;

public interface IRepositorio<T, ID> {

    void guardar(T entidad);

    void actualizar(T entidad);

    void eliminar(ID id);

    Optional<T> buscarPorId(ID id);

    List<T> buscarTodos();
}