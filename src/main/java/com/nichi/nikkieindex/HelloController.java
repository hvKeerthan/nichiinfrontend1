package com.nichi.nikkieindex;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class HelloController {
    @FXML private Label adjustedPriceLabel;
    @FXML private Label nikkeiIndexLabel;
    @FXML private TableView<StockData> stockTable;
    @FXML private TableColumn<StockData, String> codeColumn;
    @FXML private TableColumn<StockData, String> dtColumn;
    @FXML private TableColumn<StockData, String> classificationColumn;
    @FXML private TableColumn<StockData, String> codeNameColumn;
    @FXML private TableColumn<StockData, Double> pafColumn;
    @FXML private TableColumn<StockData, Double> priceColumn;
    @FXML private TableColumn<StockData, String> sectorColumn;
    @FXML private TableColumn<StockData, String> adjustedPriceColumn;
    @FXML private TableColumn<StockData, Double> divisorColumn;
    @FXML private TableColumn<StockData, String> updateSourcecolumn;
    @FXML private TableColumn<StockData, String> updateTimeColumn;


    private double totalDivisor = 0.0;

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        dtColumn.setCellValueFactory(new PropertyValueFactory<>("dt"));
        classificationColumn.setCellValueFactory(new PropertyValueFactory<>("classification"));
        codeNameColumn.setCellValueFactory(new PropertyValueFactory<>("code_name"));
        pafColumn.setCellValueFactory(new PropertyValueFactory<>("paf"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        sectorColumn.setCellValueFactory(new PropertyValueFactory<>("sector"));
        adjustedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("adjustedPrice"));
        divisorColumn.setCellValueFactory(new PropertyValueFactory<>("divisor"));
        updateSourcecolumn.setCellValueFactory(new PropertyValueFactory<>("updateSource"));
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        divisorDistinctValue();
        loadStockData();
    }

    @FXML
    public void loadStockData() {
        String query = "SELECT * FROM nikkei225pafprice";
        ObservableList<StockData> stockList = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                double paf = rs.getDouble("paf");
                if (rs.wasNull()) paf = 0.0;

                double price = rs.getDouble("price");
                if (rs.wasNull()) price = 0.0;

                double adjustedPrice = price * paf;

                StockData stockData = new StockData(
                        rs.getString("code"),
                        rs.getString("dt"),
                        rs.getString("classification"),
                        rs.getString("code_name"),
                        paf,
                        price,
                        rs.getString("sector"),
                        totalDivisor,
                        adjustedPrice,
                        rs.getString("updatesource"),
                        rs.getString("updatetime")
                );
                stockData.setAdjustedPrice(adjustedPrice);


                stockList.add(stockData);
            }

            stockTable.setItems(stockList);
            updateIndexPrice(stockList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void divisorDistinctValue() {
        try {
            File xmlFile = new File("config.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("divisor");
            if (nodeList.getLength() > 0) {
                totalDivisor = Double.parseDouble(nodeList.item(0).getTextContent());
            }
            System.out.println("Divisor from XML: " + totalDivisor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateIndexPrice(ObservableList<StockData> stocks) {
        double totalAdjustedPrice = stocks.stream().mapToDouble(StockData::getAdjustedPrice).sum();
        double nikkei225Price = totalDivisor == 0 ? 0 : totalAdjustedPrice / totalDivisor;

        System.out.println("Total Adjusted Price: " + totalAdjustedPrice);
        System.out.println("Divisor Used: " + totalDivisor);
        System.out.println("Calculated Nikkei 225 Price: " + nikkei225Price);

        adjustedPriceLabel.setText("Adjusted Price: " + String.format("%.2f", totalAdjustedPrice));
        nikkeiIndexLabel.setText("Nikkei 225 Price: " + String.format("%.2f", nikkei225Price));
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}