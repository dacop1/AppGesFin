package com.dacarex.capital.util;

import com.dacarex.capital.modelo.Movimiento;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorCSV {

    private static final String CARPETA  = "exportaciones/";
    private static final String CABECERA = "Fecha,Tipo,Descripcion,Importe,Categoria,Notas";

    public static String exportar(List<Movimiento> movimientos) throws IOException {

        new File(CARPETA).mkdirs();

        String nombreFichero = CARPETA + "movimientos_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreFichero))) {
            bw.write(CABECERA);
            bw.newLine();
            for (Movimiento m : movimientos) {
                bw.write(m.toCsv());
                bw.newLine();
            }
        }

        System.out.println("CSV exportado: " + nombreFichero);
        return nombreFichero;
    }
}