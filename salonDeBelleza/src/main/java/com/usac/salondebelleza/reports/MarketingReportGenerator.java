package com.usac.salondebelleza.reports;

import java.io.OutputStream;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;

import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class MarketingReportGenerator {

    public void exportTopAds(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Anuncios Más Mostrados", getTopAdsQuery(), conn, out, startDate, endDate);
    }

    public void exportLeastAds(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Top 5 Anuncios Menos Mostrados", getLeastAdsQuery(), conn, out, startDate, endDate);
    }

    public void exportAdUsageHistory(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportReport("Historial de Uso de Anuncios", getAdUsageHistoryQuery(), conn, out, startDate, endDate);
    }

    private void exportReport(String title, String queryText, Connection conn, OutputStream out,
                              Date startDate, Date endDate) throws Exception {

        JasperDesign design = new JasperDesign();
        design.setName("MarketingReport");
        design.setPageWidth(595);
        design.setPageHeight(842);
        design.setColumnWidth(515);
        design.setLeftMargin(40);
        design.setRightMargin(40);
        design.setTopMargin(50);
        design.setBottomMargin(50);

        addParameter(design, "start_date", Date.class);
        addParameter(design, "end_date", Date.class);

        addField(design, "detalle", String.class);
        addField(design, "total", Integer.class);

        JRDesignQuery query = new JRDesignQuery();
        query.setText(queryText);
        design.setQuery(query);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(40);
        titleBand.addElement(textElement(title, 0, 0, 500, 30, 18, true));
        design.setTitle(titleBand);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(30);
        detail.addElement(textField("$F{detalle}", 0, 0, 350));
        detail.addElement(textField("$F{total}", 360, 0, 100));
        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }

    // === QUERIES ACTUALIZADAS ===

    private String getTopAdsQuery() {
        return "SELECT CONCAT(a.type, ' - ', a.category) AS detalle, COUNT(*) AS total " +
               "FROM ad_impressions i " +
               "JOIN advertisements a ON i.ad_id = a.id " +
               "WHERE a.is_active = TRUE " +
               "AND ($P{start_date} IS NULL OR i.shown_at >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR i.shown_at <= $P{end_date}) " +
               "GROUP BY a.id, a.type, a.category " +
               "ORDER BY total DESC LIMIT 5";
    }

    private String getLeastAdsQuery() {
        return "SELECT CONCAT(a.type, ' - ', a.category) AS detalle, COUNT(*) AS total " +
               "FROM ad_impressions i " +
               "JOIN advertisements a ON i.ad_id = a.id " +
               "WHERE a.is_active = TRUE " +
               "AND ($P{start_date} IS NULL OR i.shown_at >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR i.shown_at <= $P{end_date}) " +
               "GROUP BY a.id, a.type, a.category " +
               "ORDER BY total ASC LIMIT 5";
    }

    private String getAdUsageHistoryQuery() {
        return "SELECT CONCAT(a.type, ' - Categoría: ', a.category) AS detalle, " +
               "a.purchase_count AS total " +
               "FROM advertisements a " +
               "WHERE a.is_active = TRUE " +
               "AND ($P{start_date} IS NULL OR a.start_date >= $P{start_date}) " +
               "AND ($P{end_date} IS NULL OR a.end_date <= $P{end_date}) " +
               "ORDER BY a.purchase_count DESC";
    }

    // === MÉTODOS UTILITARIOS (sin cambios) ===

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