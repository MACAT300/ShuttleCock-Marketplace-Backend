package com.example.demo.dto;

public class PaymentIntentResponse {

    private String url;
    private int paymentId;

    public PaymentIntentResponse() {
    }

    public PaymentIntentResponse(String url, int paymentId) {
        this.url = url;
        this.paymentId = paymentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
}

//package com.example.demo.dto;
//
//public class PaymentIntentResponse {
//    private String checkoutUrl;
//    private int paymentId;
//
//    public PaymentIntentResponse(String checkoutUrl, int paymentId) {
//        this.checkoutUrl = checkoutUrl;
//        this.paymentId = paymentId;
//    }
//
//    public String getCheckoutUrl() { return checkoutUrl; }
//    public int getPaymentId() { return paymentId; }
//}