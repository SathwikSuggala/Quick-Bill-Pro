package utils;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ChartExample {
    public static void main(String[] args) {
        // Example data
        Map<String, Double> salesData = new HashMap<>();
        salesData.put("Jan", 1000.0);
        salesData.put("Feb", 1500.0);
        salesData.put("Mar", 2000.0);
        salesData.put("Apr", 1800.0);
        salesData.put("May", 2500.0);
        
        // Create and display a bar chart
        JPanel barChart = ChartGenerator.createBarChart(
            "Monthly Sales",
            "Month",
            "Sales Amount",
            salesData
        );
        ChartGenerator.displayChart(barChart);
        
        // Create and display a pie chart
        JPanel pieChart = ChartGenerator.createPieChart(
            "Sales Distribution",
            salesData
        );
        ChartGenerator.displayChart(pieChart);
        
        // Create and display a line chart
        JPanel lineChart = ChartGenerator.createLineChart(
            "Sales Trend",
            "Month",
            "Sales Amount",
            salesData
        );
        ChartGenerator.displayChart(lineChart);
    }
} 