package models;

public class DashboardStats {
    private double totalSales;
    private double totalPurchases;
    private double totalCredits;
    private double totalPayments;
    private int totalProducts;
    private int totalOutlets;
    private int totalInlets;
    private double totalCGST;
    private double totalSGST;
    private int lowStockProducts;
    private int pendingCredits;
    private double totalProfit;

    public DashboardStats(double totalSales, double totalPurchases, double totalCredits, 
                         double totalPayments, int totalProducts, int totalOutlets, 
                         int totalInlets, double totalCGST, double totalSGST, 
                         int lowStockProducts, int pendingCredits, double totalProfit) {
        this.totalSales = totalSales;
        this.totalPurchases = totalPurchases;
        this.totalCredits = totalCredits;
        this.totalPayments = totalPayments;
        this.totalProducts = totalProducts;
        this.totalOutlets = totalOutlets;
        this.totalInlets = totalInlets;
        this.totalCGST = totalCGST;
        this.totalSGST = totalSGST;
        this.lowStockProducts = lowStockProducts;
        this.pendingCredits = pendingCredits;
        this.totalProfit = totalProfit;
    }

    // Getters
    public double getTotalSales() { return totalSales; }
    public double getTotalPurchases() { return totalPurchases; }
    public double getTotalCredits() { return totalCredits; }
    public double getTotalPayments() { return totalPayments; }
    public int getTotalProducts() { return totalProducts; }
    public int getTotalOutlets() { return totalOutlets; }
    public int getTotalInlets() { return totalInlets; }
    public double getTotalCGST() { return totalCGST; }
    public double getTotalSGST() { return totalSGST; }
    public int getLowStockProducts() { return lowStockProducts; }
    public int getPendingCredits() { return pendingCredits; }
    public double getTotalProfit() { return totalProfit; }

    // Setters
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
    public void setTotalPurchases(double totalPurchases) { this.totalPurchases = totalPurchases; }
    public void setTotalCredits(double totalCredits) { this.totalCredits = totalCredits; }
    public void setTotalPayments(double totalPayments) { this.totalPayments = totalPayments; }
    public void setTotalProducts(int totalProducts) { this.totalProducts = totalProducts; }
    public void setTotalOutlets(int totalOutlets) { this.totalOutlets = totalOutlets; }
    public void setTotalInlets(int totalInlets) { this.totalInlets = totalInlets; }
    public void setTotalCGST(double totalCGST) { this.totalCGST = totalCGST; }
    public void setTotalSGST(double totalSGST) { this.totalSGST = totalSGST; }
    public void setLowStockProducts(int lowStockProducts) { this.lowStockProducts = lowStockProducts; }
    public void setPendingCredits(int pendingCredits) { this.pendingCredits = pendingCredits; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }
} 