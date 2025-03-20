package com.nichi.nikkieindex;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


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
    @FXML private TableColumn<StockData, Double> adjustedPriceColumn;
    @FXML private TableColumn<StockData, Double> divisorColumn;

    private static final String BACKEND_URL = "http://localhost:8080/api/nikkie225pafprice";
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
                StockData stockData = new StockData(
                        rs.getString("code"),
                        rs.getString("dt"),
                        rs.getString("classification"),
                        rs.getString("code_name"),
                        rs.getDouble("paf"),
                        rs.getDouble("price"),
                        rs.getString("sector"),
                        rs.getDouble("divisor")
                );
                stockList.add(stockData);
            }

            stockTable.setItems(stockList);
            updateIndexPrice(stockList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void divisorDistinctValue(){
        String query = "SELECT distinct divisor FROM nikkei225pafprice LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()){
                totalDivisor = rs.getDouble("divisor");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateIndexPrice(ObservableList<StockData> stocks) {
        double totalAdjustedPrice = stocks.stream().mapToDouble(StockData::getAdjustedPrice).sum();
//        double totalDivisor = stocks.stream().mapToDouble(StockData::getDivisor).sum();

        double nikkei225Price = totalDivisor == 0 ? 0 : totalAdjustedPrice / totalDivisor;

        adjustedPriceLabel.setText("Adjusted Price: " + String.format("%.2f", totalAdjustedPrice));
        nikkeiIndexLabel.setText("Nikkei 225 Price: " + String.format("%.2f", nikkei225Price));
    }


    @FXML
    public void handleExit() {
        Platform.exit();
    }
}