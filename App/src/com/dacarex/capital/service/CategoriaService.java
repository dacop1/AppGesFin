package com.dacarex.capital.service;

import com.dacarex.capital.dao.CategoriaDAO;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaService {

    private final CategoriaDAO dao = new CategoriaDAO();

    public Categoria crear(String nombre, TipoMovimiento tipo, String colorHex) {
        Categoria nueva = new Categoria(nombre, tipo, colorHex);

        if (!nueva.esValido())
            throw new ValidacionException(nueva.validar());

        boolean duplicada = dao.buscarTodos().stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre) && c.getTipo() == tipo);
        if (duplicada)
            throw new ValidacionException("Ya existe una categoria con ese nombre y tipo.");

        dao.guardar(nueva);
        return nueva;
    }

    public void eliminar(long id) {
        dao.eliminar(id);
    }

    public List<Categoria> obtenerTodas() {
        return dao.buscarTodos().stream()
                .sorted(Comparator.comparing(Categoria::getNombre))
                .collect(Collectors.toList());
    }

    public List<Categoria> obtenerPorTipo(TipoMovimiento tipo) {
        return dao.buscarPorTipo(tipo);
    }

    public void cargarIniciales() {
        dao.cargarIniciales();
    }
}