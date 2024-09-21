package antifraud.controller.dto.transaction;

public class TransactionFeedbackRequest {
    private Long transactionId;
    private String feedback;

    public TransactionFeedbackRequest() {}

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
