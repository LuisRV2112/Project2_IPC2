package com.usac.salondebelleza.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.*;

public class ServiceReportGenerator {

    // ========== MÁS RESERVADOS ==========
    public void exportMostBookedServicesReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport(startDate, endDate, conn, out,
                "Top 5 Servicios Más Reservados",
                getBookingQuery("DESC"));
    }

    // ========== MENOS RESERVADOS ==========
    public void exportLeastBookedServicesReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport(startDate, endDate, conn, out,
                "Top 5 Servicios Menos Reservados",
                getBookingQuery("ASC"));
    }

    // ========== MÁS INGRESOS ==========
    public void exportTopIncomeServiceReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport(startDate, endDate, conn, out,
                "Servicio con Más Ingresos",
                getIncomeQuery());
    }

    // ========= MÉTODO GENERAL ==========
    private void exportReport(Date startDate, Date endDate, Connection conn, OutputStream out,
                              String title, String queryText) throws Exception {

        JasperDesign design = new JasperDesign();
        design.setName("ServiceReport");
        design.setPageWidth(595); // A4
        design.setPageHeight(842);
        design.setColumnWidth(515);
        design.setLeftMargin(40);
        design.setRightMargin(40);
        design.setTopMargin(50);
        design.setBottomMargin(50);

        // Parámetros
        addParameter(design, "start_date", Date.class);
        addParameter(design, "end_date", Date.class);

        // Campos que deben estar en todos los reportes
        addField(design, "service_name", String.class);
        addField(design, "reservas", Integer.class);
        addField(design, "ingresos", Double.class);

        // Consulta
        JRDesignQuery query = new JRDesignQuery();
        query.setText(queryText);
        design.setQuery(query);

        // Título
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(40);
        titleBand.addElement(textElement(title, 0, 0, 500, 30, 18, true));
        design.setTitle(titleBand);

        // Detalle
        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(30);
        detail.addElement(textField("$F{service_name}", 0, 0, 200));
        detail.addElement(textField("$F{reservas}", 210, 0, 100));
        detail.addElement(textField("$F{ingresos}", 320, 0, 100));
        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }

    // ========= CONSULTAS ==========
    private String getBookingQuery(String order) {
        return "SELECT s.name AS service_name, " +
               "COUNT(a.id) AS reservas, " +
               "COALESCE(SUM(i.total), 0) AS ingresos " +
               "FROM services s " +
               "LEFT JOIN appointments a ON a.service_id = s.id " +
               "    AND ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "    AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               "LEFT JOIN invoices i ON i.appointment_id = a.id " +
               "GROUP BY s.id " +
               "ORDER BY reservas " + order + " " +
               "LIMIT 5";
    }

    private String getIncomeQuery() {
        return "SELECT s.name AS service_name, " +
               "COUNT(a.id) AS reservas, " +
               "COALESCE(SUM(i.total), 0) AS ingresos " +
               "FROM invoices i " +
               "JOIN appointments a ON i.appointment_id = a.id " +
               "JOIN services s ON a.service_id = s.id " +
               "WHERE ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               "GROUP BY s.id " +
               "ORDER BY ingresos DESC " +
               "LIMIT 1";
    }

    // ========= UTILIDADES ==========
    private void addField(JasperDesign design, String name, Class<?> clazz) throws JRException {
        JRDesignField field = new JRDesignField();
        field.setName(name);
        field.setValueClass(clazz);
        design.addField(field);
    }

    private void addParameter(JasperDesign design, String name, Class<?> clazz) throws JRException {
        JRDesignParameter param = new JRDesignParameter();
        param.setName(name);
        param.setValueClass(clazz);
        design.addParameter(param);
    }

    private JRDesignTextField textField(String expr, int x, int y, int width) {
        JRDesignTextField tf = new JRDesignTextField();
        tf.setX(x);
        tf.setY(y);
        tf.setWidth(width);
        tf.setHeight(20);
        tf.setExpression(new JRDesignExpression(expr));
        return tf;
    }

    private JRDesignStaticText textElement(String text, int x, int y, int w, int h, int fontSize, boolean bold) {
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setText(text);
        staticText.setX(x);
        staticText.setY(y);
        staticText.setWidth(w);
        staticText.setHeight(h);
        staticText.setFontSize((float) fontSize);
        staticText.setBold(bold);
        return staticText;
    }
}
