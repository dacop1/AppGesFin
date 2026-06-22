package com.dacarex.capital.vista.componentes;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class IconoFX {

    public enum Tipo {
        CASA, BILLETERA, ETIQUETA, GRAFICO, LUNA, SOL, SALIR,
        MONEDA, TENDENCIA_SUBE, TENDENCIA_BAJA, FLECHA_ARRIBA, FLECHA_ABAJO,
        LAPIZ, PAPELERA, MAS, EXPANDIR
    }

    public static Node crear(Tipo tipo, Color color, double tamano) {
        return switch (tipo) {
            case CASA -> casa(color, tamano);
            case BILLETERA -> billetera(color, tamano);
            case ETIQUETA -> etiqueta(color, tamano);
            case GRAFICO -> grafico(color, tamano);
            case LUNA -> luna(color, tamano);
            case SOL -> sol(color, tamano);
            case SALIR -> salir(color, tamano);
            case MONEDA -> moneda(color, tamano);
            case TENDENCIA_SUBE -> tendencia(color, tamano, true);
            case TENDENCIA_BAJA -> tendencia(color, tamano, false);
            case FLECHA_ARRIBA -> flecha(color, tamano, true);
            case FLECHA_ABAJO -> flecha(color, tamano, false);
            case LAPIZ -> lapiz(color, tamano);
            case PAPELERA -> papelera(color, tamano);
            case MAS -> mas(color, tamano);
            case EXPANDIR -> expandir(color, tamano);
        };
    }

    private static Node casa(Color color, double s) {
        Polygon techo = new Polygon(s / 2, 0, s, s / 2, 0, s / 2);
        techo.setFill(color);
        Rectangle cuerpo = new Rectangle(s * 0.22, s * 0.5, s * 0.56, s * 0.48);
        cuerpo.setFill(color);
        return new Group(techo, cuerpo);
    }

    private static Node billetera(Color color, double s) {
        Rectangle base = new Rectangle(0, s * 0.18, s, s * 0.64);
        base.setArcWidth(6); base.setArcHeight(6);
        base.setFill(Color.TRANSPARENT);
        base.setStroke(color);
        base.setStrokeWidth(Math.max(1.4, s * 0.09));
        Circle boton = new Circle(s * 0.78, s * 0.5, s * 0.07);
        boton.setFill(color);
        return new Group(base, boton);
    }

    private static Node etiqueta(Color color, double s) {
        Polygon p = new Polygon(
            0, s * 0.35,
            s * 0.65, 0,
            s, s * 0.35,
            s * 0.35, s
        );
        p.setFill(Color.TRANSPARENT);
        p.setStroke(color);
        p.setStrokeWidth(Math.max(1.4, s * 0.09));
        Circle agujero = new Circle(s * 0.55, s * 0.27, s * 0.06);
        agujero.setFill(color);
        return new Group(p, agujero);
    }

    private static Node grafico(Color color, double s) {
        Line ejeY = new Line(s * 0.12, 0, s * 0.12, s);
        Line ejeX = new Line(s * 0.12, s, s, s);
        ejeY.setStroke(color); ejeX.setStroke(color);
        ejeY.setStrokeWidth(Math.max(1.2, s * 0.08));
        ejeX.setStrokeWidth(Math.max(1.2, s * 0.08));
        Rectangle b1 = new Rectangle(s * 0.25, s * 0.62, s * 0.14, s * 0.38); b1.setFill(color);
        Rectangle b2 = new Rectangle(s * 0.46, s * 0.4, s * 0.14, s * 0.6); b2.setFill(color);
        Rectangle b3 = new Rectangle(s * 0.67, s * 0.18, s * 0.14, s * 0.82); b3.setFill(color);
        return new Group(ejeY, ejeX, b1, b2, b3);
    }

    private static Node luna(Color color, double s) {
        Circle c = new Circle(s / 2, s / 2, s / 2);
        Circle recorte = new Circle(s * 0.62, s * 0.38, s * 0.42);
        Shape luna = Shape.subtract(c, recorte);
        luna.setFill(color);
        return luna;
    }

    private static Node sol(Color color, double s) {
        Group g = new Group();
        Circle nucleo = new Circle(s / 2, s / 2, s * 0.28);
        nucleo.setFill(color);
        g.getChildren().add(nucleo);
        for (int i = 0; i < 8; i++) {
            double ang = Math.toRadians(i * 45);
            double x1 = s / 2 + Math.cos(ang) * s * 0.36;
            double y1 = s / 2 + Math.sin(ang) * s * 0.36;
            double x2 = s / 2 + Math.cos(ang) * s * 0.5;
            double y2 = s / 2 + Math.sin(ang) * s * 0.5;
            Line rayo = new Line(x1, y1, x2, y2);
            rayo.setStroke(color);
            rayo.setStrokeWidth(Math.max(1.2, s * 0.08));
            g.getChildren().add(rayo);
        }
        return g;
    }

    private static Node salir(Color color, double s) {
        Rectangle puerta = new Rectangle(0, s * 0.1, s * 0.55, s * 0.8);
        puerta.setArcWidth(4); puerta.setArcHeight(4);
        puerta.setFill(Color.TRANSPARENT);
        puerta.setStroke(color);
        puerta.setStrokeWidth(Math.max(1.2, s * 0.09));
        Line flechaH = new Line(s * 0.5, s * 0.5, s, s * 0.5);
        flechaH.setStroke(color); flechaH.setStrokeWidth(Math.max(1.2, s * 0.09));
        Line p1 = new Line(s * 0.78, s * 0.32, s, s * 0.5);
        Line p2 = new Line(s * 0.78, s * 0.68, s, s * 0.5);
        p1.setStroke(color); p1.setStrokeWidth(Math.max(1.2, s * 0.09));
        p2.setStroke(color); p2.setStrokeWidth(Math.max(1.2, s * 0.09));
        return new Group(puerta, flechaH, p1, p2);
    }

    private static Node moneda(Color color, double s) {
        Circle c = new Circle(s / 2, s / 2, s / 2 - s * 0.05);
        c.setFill(Color.TRANSPARENT);
        c.setStroke(color);
        c.setStrokeWidth(Math.max(1.4, s * 0.09));
        Text t = new Text("$");
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, s * 0.55));
        t.setFill(color);
        t.setX(s * 0.27); t.setY(s * 0.68);
        return new Group(c, t);
    }

    private static Node tendencia(Color color, double s, boolean sube) {
        Polyline linea = sube
            ? new Polyline(0, s * 0.75, s * 0.33, s * 0.4, s * 0.55, s * 0.6, s, s * 0.1)
            : new Polyline(0, s * 0.25, s * 0.33, s * 0.6, s * 0.55, s * 0.4, s, s * 0.9);
        linea.setStroke(color);
        linea.setStrokeWidth(Math.max(1.4, s * 0.1));
        linea.setFill(Color.TRANSPARENT);
        double puntaX = s, puntaY = sube ? s * 0.1 : s * 0.9;
        double dir = sube ? 1 : -1;
        Polygon flecha = new Polygon(
            puntaX, puntaY,
            puntaX - s * 0.22, puntaY + dir * s * 0.03,
            puntaX - s * 0.03, puntaY + dir * s * 0.22
        );
        flecha.setFill(color);
        return new Group(linea, flecha);
    }

    private static Node flecha(Color color, double s, boolean arriba) {
        double yInicio = arriba ? s : 0;
        double yFin = arriba ? 0 : s;
        Line tallo = new Line(s / 2, yInicio, s / 2, yFin);
        tallo.setStroke(color); tallo.setStrokeWidth(Math.max(1.4, s * 0.1));
        Polygon punta = arriba
            ? new Polygon(s / 2, 0, s * 0.25, s * 0.35, s * 0.75, s * 0.35)
            : new Polygon(s / 2, s, s * 0.25, s * 0.65, s * 0.75, s * 0.65);
        punta.setFill(color);
        return new Group(tallo, punta);
    }

    private static Node lapiz(Color color, double s) {
        Rectangle cuerpo = new Rectangle(s * 0.1, s * 0.1, s * 0.65, s * 0.2);
        cuerpo.setFill(color);
        cuerpo.setRotate(45);
        Polygon punta = new Polygon(s * 0.05, s * 0.85, s * 0.2, s * 0.95, s * 0.15, s * 0.7);
        punta.setFill(color);
        Group g = new Group(cuerpo, punta);
        return g;
    }

    private static Node papelera(Color color, double s) {
        Rectangle tapa = new Rectangle(s * 0.15, s * 0.18, s * 0.7, s * 0.1);
        tapa.setFill(color);
        Rectangle cuerpo = new Rectangle(s * 0.22, s * 0.3, s * 0.56, s * 0.6);
        cuerpo.setArcWidth(4); cuerpo.setArcHeight(4);
        cuerpo.setFill(Color.TRANSPARENT);
        cuerpo.setStroke(color);
        cuerpo.setStrokeWidth(Math.max(1.2, s * 0.07));
        Rectangle asa = new Rectangle(s * 0.35, s * 0.05, s * 0.3, s * 0.1);
        asa.setArcWidth(4); asa.setArcHeight(4);
        asa.setFill(Color.TRANSPARENT);
        asa.setStroke(color);
        asa.setStrokeWidth(Math.max(1.2, s * 0.07));
        return new Group(asa, tapa, cuerpo);
    }

    private static Node mas(Color color, double s) {
        Line h = new Line(0, s / 2, s, s / 2);
        Line v = new Line(s / 2, 0, s / 2, s);
        h.setStroke(color); v.setStroke(color);
        h.setStrokeWidth(Math.max(1.6, s * 0.16));
        v.setStrokeWidth(Math.max(1.6, s * 0.16));
        return new Group(h, v);
    }

    private static Node expandir(Color color, double s) {
        Group g = new Group();
        double grosor = Math.max(1.6, s * 0.14);
        
        // Esquina superior izquierda
        Line a = new Line(0, s * 0.4, 0, 0); a.setStroke(color); a.setStrokeWidth(grosor);
        Line b = new Line(0, 0, s * 0.4, 0); b.setStroke(color); b.setStrokeWidth(grosor);
        
        // Esquina inferior derecha
        Line c = new Line(s, s * 0.6, s, s); c.setStroke(color); c.setStrokeWidth(grosor);
        Line d = new Line(s, s, s * 0.6, s); d.setStroke(color); d.setStrokeWidth(grosor);
        
        // Esquina superior derecha
        Line e = new Line(s, s * 0.4, s, 0); e.setStroke(color); e.setStrokeWidth(grosor);
        Line f = new Line(s, 0, s * 0.6, 0); f.setStroke(color); f.setStrokeWidth(grosor);
        
        // Esquina inferior izquierda
        Line h = new Line(0, s * 0.6, 0, s); h.setStroke(color); h.setStrokeWidth(grosor);
        Line i = new Line(0, s, s * 0.4, s); i.setStroke(color); i.setStrokeWidth(grosor);
        
        g.getChildren().addAll(a, b, c, d, e, f, h, i);
        return g;
    }
}