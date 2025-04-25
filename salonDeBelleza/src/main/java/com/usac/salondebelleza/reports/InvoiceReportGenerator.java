package com.usac.salondebelleza.reports;

import java.io.OutputStream;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class InvoiceReportGenerator {

    public JasperReport buildInvoiceReport() throws JRException {
        // 1. Crear diseño del reporte
        JasperDesign design = new JasperDesign();
        design.setName("Invoice");
        design.setPageWidth(595); // A4 width
        design.setPageHeight(842); // A4 height
        design.setColumnWidth(515);
        design.setLeftMargin(40);
        design.setRightMargin(40);
        design.setTopMargin(50);
        design.setBottomMargin(50);

        // 2. Crear parámetro para ID de la factura
        JRDesignParameter param = new JRDesignParameter();
        param.setName("invoice_id");
        param.setValueClass(Integer.class);
        design.addParameter(param);

        // 3. Crear campos del reporte (nombre, total, etc.)
        addField(design, "client_email", String.class);
        addField(design, "employee_email", String.class);
        addField(design, "service_name", String.class);
        addField(design, "start_time", java.sql.Timestamp.class);
        addField(design, "end_time", java.sql.Timestamp.class);
        addField(design, "total", Double.class);
        addField(design, "payment_status", String.class);

        // 4. Crear SQL query
        JRDesignQuery query = new JRDesignQuery();
        query.setText(
            "SELECT c.email AS client_email, " +
            "e.email AS employee_email, " +
            "s.name AS service_name, " +
            "a.start_time, " +
            "a.end_time, " +
            "i.total, " +
            "i.payment_status " +
            "FROM invoices i " +
            "JOIN appointments a ON a.id = i.appointment_id " +
            "JOIN users c ON c.id = a.client_id " +
            "JOIN users e ON e.id = a.employee_id " +
            "JOIN services s ON s.id = a.service_id " +
            "WHERE i.id = $P{invoice_id}"
        );
        design.setQuery(query); // Aquí lo aplicas correctamente al diseño

        // 5. Encabezado simple (puedes personalizar esto)
        JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(50);

        JRDesignStaticText titleText = new JRDesignStaticText();
        titleText.setX(0);
        titleText.setY(0);
        titleText.setWidth(500);
        titleText.setHeight(30);
        titleText.setText("Factura de Servicio");
        titleText.setFontSize(18f);
        titleText.setBold(true);

        titleBand.addElement(titleText);
        design.setTitle(titleBand);


        // 6. Detalle
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(80);

        // Ajustamos las posiciones de los elementos para que no se desborden
        detailBand.addElement(textField("$F{client_email}", 0, 0, 250));   // Se coloca en la primera fila
        detailBand.addElement(textField("$F{service_name}", 0, 20, 250));  // Se coloca en la segunda fila
        detailBand.addElement(textField("$F{total}", 0, 40, 100));        // Se coloca en la tercera fila
        detailBand.addElement(textField("$F{payment_status}", 0, 60, 100)); // Se coloca en la cuarta fila

        // Añadimos la banda a la sección de detalle
        ((JRDesignSection) design.getDetailSection()).addBand(detailBand);


        // 7. Compilar diseño
        return JasperCompileManager.compileReport(design);
    }

    private void addField(JasperDesign design, String name, Class<?> clazz) throws JRException {
        JRDesignField field = new JRDesignField();
        field.setName(name);
        field.setValueClass(clazz);
        design.addField(field);
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

    private JRDesignTextField textField(String expression, int x, int y, int w) {
        JRDesignTextField tf = new JRDesignTextField();
        tf.setX(x);
        tf.setY(y);
        tf.setWidth(w);
        tf.setHeight(20);
        tf.setExpression(new JRDesignExpression(expression));
        return tf;
    }

    public void exportPdf(int invoiceId, Connection conn, OutputStream out) throws Exception {
        JasperReport report = buildInvoiceReport();

        Map<String, Object> params = new HashMap<>();
        params.put("invoice_id", invoiceId);

        JasperPrint print = JasperFillManager.fillReport(report, params, conn);
        JasperExportManager.exportReportToPdfStream(print, out);
    }
}
