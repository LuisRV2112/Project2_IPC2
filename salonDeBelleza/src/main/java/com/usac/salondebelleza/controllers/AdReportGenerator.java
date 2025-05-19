/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.usac.salondebelleza.controllers;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

public class AdReportGenerator {
    
    public void exportMostShownAdsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportAdReport(startDate, endDate, conn, out, 
                        "Top 5 Anuncios Más Mostrados", 
                        getMostShownAdsQuery("DESC"));
    }

    private String getMostShownAdsQuery(String order) {
        return "SELECT a.content_url, COUNT(ai.id) AS times_shown " +
               "FROM advertisements a " +
               "LEFT JOIN ad_impressions ai ON ai.ad_id = a.id " +
               "WHERE ai.shown_at BETWEEN $P{start_date} AND $P{end_date} " +
               "GROUP BY a.id " +
               "ORDER BY times_shown " + order + " " +
               "LIMIT 5";
    }
    
    public void exportLeastShownAdsReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        exportAdReport(startDate, endDate, conn, out, 
                        "Top 5 Anuncios Menos Mostrados", 
                        getMostShownAdsQuery("ASC"));
    }

    public void exportAdUsageHistoryReport(Date startDate, Date endDate, Connection conn, OutputStream out) throws Exception {
        JasperDesign design = new JasperDesign();
        design.setName("AdUsageHistoryReport");
        design.setPageWidth(595);
        design.setPageHeight(842);

        addParameter(design, "start_date", Date.class);
        addParameter(design, "end_date", Date.class);

        addField(design, "ad_type", String.class);
        addField(design, "ad_details", String.class);
        addField(design, "used_at", Date.class);

        JRDesignQuery query = new JRDesignQuery();
        query.setText(getAdUsageHistoryQuery());
        design.setQuery(query);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(40);
        titleBand.addElement(textElement("Historial de Anuncios Más Usados", 0, 0, 500, 30, 18, true));
        design.setTitle(titleBand);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(30);
        detail.addElement(textField("$F{ad_type}", 0, 0, 250));
        detail.addElement(textField("$F{ad_details}", 250, 0, 250));
        detail.addElement(textField("$F{used_at}", 500, 0, 100));

        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }

    private String getAdUsageHistoryQuery() {
        return "SELECT ad.content_url, au.type AS ad_type, au.details AS ad_details, au.used_at " +
               "FROM ad_usages au " +
               "JOIN advertisements ad ON ad.id = au.ad_id " +
               "WHERE au.used_at BETWEEN $P{start_date} AND $P{end_date}";
    }
    
    private void exportAdReport(Date startDate, Date endDate, Connection conn, OutputStream out, 
                                 String title, String queryText) throws Exception {
        JasperDesign design = new JasperDesign();
        design.setName("AdReport");
        design.setPageWidth(595);
        design.setPageHeight(842);

        addParameter(design, "start_date", Date.class);
        addParameter(design, "end_date", Date.class);

        addField(design, "content_url", String.class);
        addField(design, "times_shown", Integer.class);

        JRDesignQuery query = new JRDesignQuery();
        query.setText(queryText);
        design.setQuery(query);

        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(40);
        titleBand.addElement(textElement(title, 0, 0, 500, 30, 18, true));
        design.setTitle(titleBand);

        JRDesignBand detail = new JRDesignBand();
        detail.setHeight(30);
        detail.addElement(textField("$F{content_url}", 0, 0, 250));
        detail.addElement(textField("$F{times_shown}", 250, 0, 100));

        ((JRDesignSection) design.getDetailSection()).addBand(detail);

        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", startDate);
        params.put("end_date", endDate);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }

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
