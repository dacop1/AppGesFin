package com.dacarex.capital.service;

import com.dacarex.capital.dao.CategoriaDAO;
import com.dacarex.capital.dao.MovimientoDAO;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.modelo.Movimiento;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BackupService {

    private static final String VERSION = "DACAREX_BACKUP_V1";
    private static final String SEP     = "|";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final CategoriaDAO  categoriaDAO  = new CategoriaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    // ─────────────────────────────────────
    // EXPORTAR
    // ─────────────────────────────────────
    public String exportar(String rutaFichero) throws IOException {
        List<Categoria>  categorias  = categoriaDAO.buscarTodos();
        List<Movimiento> movimientos = movimientoDAO.buscarTodos();

        File fichero = new File(rutaFichero);
        if (fichero.getParentFile() != null) fichero.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(fichero), StandardCharsets.UTF_8))) {

            pw.println(VERSION);
            pw.println("FECHA:" + LocalDateTime.now().format(FMT));
            pw.println("CATEGORIAS:" + categorias.size());

            for (Categoria c : categorias) {
                pw.println(c.getId()            + SEP
                    + enc(c.getNombre())         + SEP
                    + c.getTipo().name()         + SEP
                    + enc(c.getColorHex() != null ? c.getColorHex() : "#5e7cff"));
            }

            pw.println("MOVIMIENTOS:" + movimientos.size());

            for (Movimiento m : movimientos) {
                String catId  = (m.getCategoria() != null)
                        ? String.valueOf(m.getCategoria().getId()) : "0";
                String fecha  = (m.getFecha() != null) ? m.getFecha().toString() : "";
                String creado = (m.getCreadoEn() != null) ? m.getCreadoEn().format(FMT) : "";

                pw.println(m.getId()                + SEP
                    + m.getTipo().name()             + SEP
                    + enc(m.getDescripcion())         + SEP
                    + m.getImporte()                 + SEP
                    + catId                          + SEP
                    + fecha                          + SEP
                    + enc(m.getNotas() != null ? m.getNotas() : "") + SEP
                    + creado);
            }
        }

        System.out.println("Backup exportado: " + rutaFichero);
        return rutaFichero;
    }

    // ─────────────────────────────────────
    // IMPORTAR
    // ─────────────────────────────────────
    public ResultadoImportacion importar(String rutaFichero) throws IOException {
        ResultadoImportacion resultado = new ResultadoImportacion();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(rutaFichero), StandardCharsets.UTF_8))) {

            String version = br.readLine();
            if (version == null || !version.startsWith("DACAREX_BACKUP")) {
                throw new IOException("El fichero no es un backup valido de Dacarex Capital.");
            }

            br.readLine(); // FECHA:...

            String lineaCats = br.readLine();
            if (lineaCats == null || !lineaCats.startsWith("CATEGORIAS:"))
                throw new IOException("Formato incorrecto: falta seccion CATEGORIAS.");

            int numCats = Integer.parseInt(lineaCats.substring("CATEGORIAS:".length()).trim());
            Map<Long, Categoria> mapaCategorias = new HashMap<>();

            for (int i = 0; i < numCats; i++) {
                String linea = br.readLine();
                if (linea == null || linea.isBlank()) continue;

                String[] p = linea.split("\\|", 4);
                if (p.length < 4) continue;

                try {
                    long   idOriginal = Long.parseLong(p[0].trim());
                    String nombre     = dec(p[1].trim());
                    TipoMovimiento tipo = TipoMovimiento.valueOf(p[2].trim());
                    String colorHex   = dec(p[3].trim());

                    Optional<Categoria> existente = categoriaDAO.buscarTodos().stream()
                            .filter(c -> c.getNombre().equalsIgnoreCase(nombre)
                                    && c.getTipo() == tipo)
                            .findFirst();

                    Categoria cat;
                    if (existente.isPresent()) {
                        cat = existente.get();
                        resultado.categoriasOmitidas++;
                    } else {
                        cat = new Categoria(nombre, tipo, colorHex);
                        categoriaDAO.guardar(cat);
                        resultado.categoriasCreadas++;
                    }
                    mapaCategorias.put(idOriginal, cat);

                } catch (Exception e) {
                    resultado.errores.add("Error en categoria linea " + (i + 1) + ": " + e.getMessage());
                }
            }

            String lineaMov = br.readLine();
            if (lineaMov == null || !lineaMov.startsWith("MOVIMIENTOS:"))
                throw new IOException("Formato incorrecto: falta seccion MOVIMIENTOS.");

            int numMov = Integer.parseInt(lineaMov.substring("MOVIMIENTOS:".length()).trim());

            for (int i = 0; i < numMov; i++) {
                String linea = br.readLine();
                if (linea == null || linea.isBlank()) continue;

                String[] p = linea.split("\\|", 8);
                if (p.length < 6) continue;

                try {
                    TipoMovimiento tipo     = TipoMovimiento.valueOf(p[1].trim());
                    String descripcion      = dec(p[2].trim());
                    double importe          = Double.parseDouble(p[3].trim());
                    long   catIdOriginal    = Long.parseLong(p[4].trim());
                    LocalDate fecha         = LocalDate.parse(p[5].trim());
                    String notas            = (p.length > 6) ? dec(p[6].trim()) : "";

                    Categoria cat = mapaCategorias.get(catIdOriginal);
                    if (cat == null) {
                        resultado.movimientosConError++;
                        resultado.errores.add("Categoria no encontrada para: " + descripcion);
                        continue;
                    }

                    Movimiento m = new Movimiento(tipo, descripcion, importe, cat, fecha,
                            notas.isEmpty() ? null : notas);
                    movimientoDAO.guardar(m);
                    resultado.movimientosCreados++;

                } catch (Exception e) {
                    resultado.movimientosConError++;
                    resultado.errores.add("Error en movimiento " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        return resultado;
    }

    // ─────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────
    private String enc(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String dec(String b64) {
        if (b64 == null || b64.isEmpty()) return "";
        try {
            return new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return b64;
        }
    }

    // ─────────────────────────────────────
    // RESULTADO
    // ─────────────────────────────────────
    public static class ResultadoImportacion {
        public int categoriasCreadas   = 0;
        public int categoriasOmitidas  = 0;
        public int movimientosCreados  = 0;
        public int movimientosConError = 0;
        public final List<String> errores = new ArrayList<>();

        public String resumen() {
            StringBuilder sb = new StringBuilder();
            sb.append("Importacion completada:\n\n");
            sb.append(String.format("  Categorias nuevas:      %d%n", categoriasCreadas));
            sb.append(String.format("  Categorias existentes:  %d%n", categoriasOmitidas));
            sb.append(String.format("  Movimientos importados: %d%n", movimientosCreados));
            if (movimientosConError > 0)
                sb.append(String.format("  Movimientos con error:  %d%n", movimientosConError));
            if (!errores.isEmpty()) {
                sb.append("\nDetalle de errores:\n");
                errores.forEach(e -> sb.append("  - ").append(e).append("\n"));
            }
            return sb.toString();
        }
    }
}