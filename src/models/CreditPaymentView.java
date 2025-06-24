package models;

public class CreditPaymentView {
    private final int paymentId;
    private final String outletName;
    private final int billId;
    private final double paymentAmount;
    private final String paymentMode;
    private final String paymentDate;

    public CreditPaymentView(int paymentId, String outletName, int billId, double paymentAmount, String paymentMode, String paymentDate) {
        this.paymentId = paymentId;
        this.outletName = outletName;
        this.billId = billId;
        this.paymentAmount = paymentAmount;
        this.paymentMode = paymentMode;
        this.paymentDate = paymentDate;
    }

    // Getters
    public int getPaymentId() { return paymentId; }
    public String getOutletName() { return outletName; }
    public int getBillId() { return billId; }
    public double getPaymentAmount() { return paymentAmount; }
    public String getPaymentMode() { return paymentMode; }
    public String getPaymentDate() { return paymentDate; }
} 