package antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"limit\"")
public class Limit {
    @Id
    @GeneratedValue
    private Long id;
    private Integer allowedAmount;
    private Integer manualProcessingAmount;

    public Limit() {}

    public Limit(Integer allowedAmount, Integer manualProcessingAmount) {
        this.allowedAmount = allowedAmount;
        this.manualProcessingAmount = manualProcessingAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAllowedAmount() {
        return allowedAmount;
    }

    public void setAllowedAmount(Integer allowedAmount) {
        this.allowedAmount = allowedAmount;
    }

    public Integer getManualProcessingAmount() {
        return manualProcessingAmount;
    }

    public void setManualProcessingAmount(Integer manualProcessingAmount) {
        this.manualProcessingAmount = manualProcessingAmount;
    }
}
