package com.usac.salondebelleza.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminReportGenerator {

    // === GANANCIAS POR SERVICIO ===
    public void exportServiceEarningsReport(Date startDate, Date endDate, Integer serviceId, Connection conn, OutputStream out) throws Exception {
        exportReport("Ganancias por Servicio", getServiceEarningsQuery(serviceId != null), conn, out, startDate, endDate, serviceId);
    }

    // === ANUNCIOS MÁS MOSTRADOS ===
    public void exportTopAdsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Anuncios Más Mostrados", getTopAdsQuery(), conn, out, startDate, endDate, null);
    }

    // === CLIENTES CON MÁS CITAS ===
    public void exportTopClientsByAppointmentsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Clientes con Más Citas", getTopClientsQuery(), conn, out, startDate, endDate, null);
    }

    // === CLIENTES CON MENOS CITAS ===
    public void exportLeastClientsByAppointmentsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Clientes con Menos Citas", getLeastClientsQuery(), conn, out, startDate, endDate, null);
    }

    // === CLIENTES EN LISTA NEGRA ===
    public void exportBlacklistedClientsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Clientes en Lista Negra", getBlacklistedClientsQuery(), conn, out, startDate, endDate, null);
    }

    // === CLIENTES QUE MÁS GASTARON ===
    public void exportTopSpendersReport(Date startDate, Date endDate, Integer clientId, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Clientes que Más Gastaron", getTopSpendersQuery(clientId != null), conn, out, startDate, endDate, clientId);
    }

    // === CLIENTES QUE MENOS GASTARON ===
    public void exportLeastSpendersReport(Date startDate, Date endDate, Integer clientId, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Clientes que Menos Gastaron", getLeastSpendersQuery(clientId != null), conn, out, startDate, endDate, clientId);
    }

    // === GANANCIAS POR EMPLEADO ===
    public void exportEmployeeEarningsReport(Date startDate, Date endDate, Integer employeeId, Connection conn, OutputStream out) throws Exception {
        exportReport("Ganancias por Empleado", getEmployeeEarningsQuery(employeeId != null), conn, out, startDate, endDate, employeeId);
    }

    // === CLIENTES CON MÁS CITAS ATENDIDAS ===
    public void exportTopAttendedClientsReport(Date startDate, Date endDate, Integer employeeId, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Clientes con Más Citas Atendidas", getTopAttendedClientsQuery(employeeId != null), conn, out, startDate, endDate, employeeId);
    }

    // === MÉTODO GENERAL DE EXPORTACIÓN ===
    private void exportReport(String title, String queryText, Connection conn, OutputStream out,
                              Date startDate, Date endDate, Integer idFilter) throws Exception {

        JasperDesign design = new JasperDesign();
        design.setName("AdminReport");
        design.setPageWidth(595);
        design.setPageHeight(842);
        design.setColumnWidth(515);
        design.setLeftMargin(40);
        design.setRightMargin(40);
        design.setTopMargin(50);
        design.setBottomMargin(50);

        addParameter(design, "start_date", Date.class);
        addParameter(design, "end_date", Date.class);
        if (idFilter != null) {
            addParameter(design, "id_filter", Integer.class);
        }

        addField(design, "detalle", String.class);
        addField(design, "total", Double.class);

        JRDesignQuery query = new JRDesignQuery();
        query.setText(queryText);
        design.setQuery(query);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(40);
        titleBand.addElement(textElement(title, 0, 0, 500, 30, 18, true));
        design.setTitle(titleBand);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(30);
        detail.addElement(textField("$F{detalle}", 0, 0, 300));
        detail.addElement(textField("$F{total}", 320, 0, 150));
        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);
        if (idFilter != null) {
            params.put("id_filter", idFilter);
        }

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }

    // === CONSULTAS ===

    private String getServiceEarningsQuery(boolean filtered) {
        return "SELECT CONCAT(s.name, ' - ', a.start_time) AS detalle, i.total " +
               "FROM appointments a " +
               "JOIN services s ON a.service_id = s.id " +
               "JOIN invoices i ON i.appointment_id = a.id " +
               "WHERE a.status = 'completado' " +
               "AND ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               (filtered ? "AND s.id = $P{id_filter} " : "") +
               "ORDER BY a.start_time";
    }

    private String getTopAdsQuery() {
        return "SELECT CONCAT(a.name, ' @', ai.page_url) AS detalle, COUNT(*) AS total " +
               "FROM ad_impressions ai " +
               "JOIN advertisements a ON ai.ad_id = a.id " +
               "WHERE ($P{start_date} IS NULL OR ai.shown_at >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR ai.shown_at <= $P{end_date}) " +
               "GROUP BY a.id, ai.page_url " +
               "ORDER BY total DESC LIMIT 5";
    }

    private String getTopClientsQuery() {
        return "SELECT u.email AS detalle, COUNT(a.id) AS total " +
               "FROM appointments a " +
               "JOIN users u ON a.client_id = u.id " +
               "WHERE ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               "GROUP BY a.client_id " +
               "ORDER BY total DESC LIMIT 5";
    }

    private String getLeastClientsQuery() {
        return "SELECT u.email AS detalle, COUNT(a.id) AS total " +
               "FROM users u " +
               "LEFT JOIN appointments a ON a.client_id = u.id " +
               "WHERE u.role_id = (SELECT id FROM roles WHERE name='cliente') " +
               "GROUP BY u.id " +
               "ORDER BY total ASC LIMIT 5";
    }

    private String getBlacklistedClientsQuery() {
        return "SELECT CONCAT(u.email, ' (', b.reason, ')') AS detalle, 0.0 AS total " +
               "FROM blacklist b " +
               "JOIN users u ON b.client_id = u.id " +
               "LEFT JOIN appointments a ON a.client_id = u.id AND a.status='cancelada' " +
               "WHERE ($P{start_date} IS NULL OR b.created_at >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR b.created_at <= $P{end_date}) " +
               "GROUP BY u.id, b.reason";
    }

    private String getTopSpendersQuery(boolean filtered) {
        return "SELECT u.email AS detalle, SUM(i.total) AS total " +
               "FROM invoices i " +
               "JOIN appointments a ON i.appointment_id = a.id " +
               "JOIN users u ON a.client_id = u.id " +
               "WHERE ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               (filtered ? "AND u.id = $P{id_filter} " : "") +
               "GROUP BY u.id " +
               "ORDER BY total DESC LIMIT 5";
    }

    private String getLeastSpendersQuery(boolean filtered) {
        return "SELECT u.email AS detalle, COALESCE(SUM(i.total), 0) AS total " +
               "FROM users u " +
               "LEFT JOIN appointments a ON a.client_id = u.id " +
               "LEFT JOIN invoices i ON i.appointment_id = a.id " +
               "WHERE ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               (filtered ? "AND u.id = $P{id_filter} " : "") +
               "GROUP BY u.id " +
               "ORDER BY total ASC LIMIT 5";
    }

    private String getEmployeeEarningsQuery(boolean filtered) {
        return  "SELECT " +
                "  u.email AS detalle, " +
                "  SUM(i.total) AS total " +
                "FROM appointments a " +
                "JOIN users u       ON a.employee_id    = u.id " +
                "JOIN invoices i    ON i.appointment_id = a.id " +
                "WHERE a.status = 'completado' " +
                "  AND ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
                "  AND ($P{end_date}   IS NULL OR a.start_time <= $P{end_date}) " +
                (filtered 
                    ? "  AND u.id = $P{id_filter} " 
                    : ""
                ) +
                "GROUP BY u.email " +
                "ORDER BY u.email";
    }

    private String getTopAttendedClientsQuery(boolean filtered) {
        return "SELECT u.email AS detalle, COUNT(a.id) AS total " +
               "FROM appointments a " +
               "JOIN users u ON a.client_id = u.id " +
               "WHERE a.status = 'completado' " +
               "AND ($P{start_date} IS NULL OR a.start_time >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.start_time <= $P{end_date}) " +
               (filtered ? "AND a.employee_id = $P{id_filter} " : "") +
               "GROUP BY u.id " +
               "ORDER BY total DESC LIMIT 5";
    }

    // === UTILIDADES ===

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
        tf.setX(x); tf.setY(y);
        tf.setWidth(width); tf.setHeight(20);
        tf.setExpression(new JRDesignExpression(expr));
        return tf;
    }

    private JRDesignStaticText textElement(String text, int x, int y, int w, int h, int fontSize, boolean bold) {
        JRDesignStaticText staticText = new JRDesignStaticText();
        staticText.setText(text);
        staticText.setX(x); staticText.setY(y);
        staticText.setWidth(w); staticText.setHeight(h);
        staticText.setFontSize((float) fontSize);
        staticText.setBold(bold);
        return staticText;
    }
}
