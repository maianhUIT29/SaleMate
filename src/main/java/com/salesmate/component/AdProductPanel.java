package com.salesmate.component;

public class AdProductPanel extends javax.swing.JPanel {

    public AdProductPanel() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tpProduct = new javax.swing.JTabbedPane();
        panelProductTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        panelProductReport = new javax.swing.JPanel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("CRUD Product  ");

        javax.swing.GroupLayout panelProductTableLayout = new javax.swing.GroupLayout(panelProductTable);
        panelProductTable.setLayout(panelProductTableLayout);
        panelProductTableLayout.setHorizontalGroup(
            panelProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(panelProductTableLayout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jLabel1)
                .addContainerGap(151, Short.MAX_VALUE))
        );
        panelProductTableLayout.setVerticalGroup(
            panelProductTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelProductTableLayout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tpProduct.addTab("Table", panelProductTable);

        javax.swing.GroupLayout panelProductReportLayout = new javax.swing.GroupLayout(panelProductReport);
        panelProductReport.setLayout(panelProductReportLayout);
        panelProductReportLayout.setHorizontalGroup(
            panelProductReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        panelProductReportLayout.setVerticalGroup(
            panelProductReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );

        tpProduct.addTab("Report", panelProductReport);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpProduct)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpProduct, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel panelProductReport;
    private javax.swing.JPanel panelProductTable;
    private javax.swing.JTabbedPane tpProduct;
    // End of variables declaration//GEN-END:variables
}
