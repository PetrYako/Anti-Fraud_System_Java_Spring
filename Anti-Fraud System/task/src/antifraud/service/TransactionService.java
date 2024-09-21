package antifraud.service;

import antifraud.controller.dto.transaction.TransactionHistoryResponse;
import antifraud.controller.dto.transaction.TransactionRequest;
import antifraud.controller.dto.transaction.TransactionResponse;
import antifraud.model.*;
import antifraud.repository.IpBlacklistRepository;
import antifraud.repository.LimitRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.TransactionHistoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private StolenCardRepository stolenCardRepository;

    @Autowired
    private IpBlacklistRepository ipBlacklistRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private LimitRepository limitRepository;

    private int allowedAmount = 200;
    private int manualProcessingAmount = 1500;

    public TransactionResponse process(TransactionRequest request) {
        Limit limit = limitRepository.findByIdOrderById(1L).orElseGet(() -> limitRepository.save(new Limit(allowedAmount, manualProcessingAmount)));

        Double amount = request.getAmount();
        List<String> errors = new ArrayList<>();

        String number = request.getNumber();
        String ip = request.getIp();
        String region = request.getRegion();
        LocalDateTime date = request.getDate();

        boolean isCardStolen = isCardStolen(number);
        boolean isIpBlacklisted = isIpInBlacklist(ip);

        List<TransactionHistory> history = transactionHistoryRepository.findByNumberAndCreatedAtAfterAndCreatedAtBefore(
                number, date.minusHours(1), date
        );
        long regionCorrelation = countRegionCorrelation(history, region);
        long ipCorrelation = countIpCorrelation(history, ip);

        TransactionStatus status = null;

        if (isCardStolen) {
            status = TransactionStatus.PROHIBITED;
            errors.add("card-number");
        }
        if (isIpBlacklisted) {
            status = TransactionStatus.PROHIBITED;
            errors.add("ip");
        }
        if (regionCorrelation > 1) {
            errors.add("region-correlation");
            if (regionCorrelation > 2) {
                status = TransactionStatus.PROHIBITED;
            } else {
                status = TransactionStatus.MANUAL_PROCESSING;
            }
        }
        if (ipCorrelation > 1) {
            errors.add("ip-correlation");
            if (ipCorrelation > 2) {
                status = TransactionStatus.PROHIBITED;
            } else {
                status = TransactionStatus.MANUAL_PROCESSING;
            }
        }

        if (amount <= limit.getAllowedAmount()) {
            if (status != TransactionStatus.PROHIBITED && status != TransactionStatus.MANUAL_PROCESSING) {
                status = TransactionStatus.ALLOWED;
            }
        } else if (amount <= limit.getManualProcessingAmount()) {
            if (status != TransactionStatus.PROHIBITED) {
                status = TransactionStatus.MANUAL_PROCESSING;
                errors.add("amount");
            }
        } else {
            status = TransactionStatus.PROHIBITED;
            errors.add("amount");
        }

        Collections.sort(errors);
        String info;
        if (errors.isEmpty()) {
            info = "none";
        } else {
            info = String.join(", ", errors);
        }
        TransactionHistory trHistory = new TransactionHistory(number, ip, status.name(), region, date, amount.longValue());
        transactionHistoryRepository.save(trHistory);
        return new TransactionResponse(status.name(), info);
    }

    public TransactionHistoryResponse addFeedback(Long transactionId, TransactionStatus feedback) throws Exception {
        TransactionHistory history = transactionHistoryRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));
        if (!history.getFeedback().isBlank()) {
            throw new EntityExistsException("Transaction already has feedback");
        }
        history.setFeedback(feedback.name());
        recalculateLimits(TransactionStatus.valueOf(history.getResult()), feedback, history.getAmount());
        TransactionHistory saved = transactionHistoryRepository.save(history);
        return mapToTransactionHistoryResponse(saved);
    }

    public List<TransactionHistoryResponse> getHistory() {
        return transactionHistoryRepository.findAllByOrderByIdAsc().stream().map(this::mapToTransactionHistoryResponse).toList();
    }

    public List<TransactionHistoryResponse> getHistory(String number) {
        List<TransactionHistory> history = transactionHistoryRepository.findAllByNumberOrderByIdAsc(number);
        if (history.isEmpty()) {
            throw new EntityNotFoundException("Transaction not found");
        }
        return history.stream().map(this::mapToTransactionHistoryResponse).toList();
    }

    private void recalculateLimits(TransactionStatus current, TransactionStatus feedback, Long amount) throws Exception {
        if (current == feedback) {
            throw new Exception("Transaction status can't equal to feedback");
        }

        switch (current) {
            case ALLOWED -> {
                switch (feedback) {
                    case MANUAL_PROCESSING -> downAllowedLimit(amount);
                    case PROHIBITED -> {
                        downAllowedLimit(amount);
                        downManualProcessingLimit(amount);
                    }
                }
            }

            case MANUAL_PROCESSING -> {
                switch (feedback) {
                    case ALLOWED -> upAllowedLimit(amount);
                    case PROHIBITED -> downManualProcessingLimit(amount);
                }
            }

            case PROHIBITED -> {
                switch (feedback) {
                    case ALLOWED -> {
                        upAllowedLimit(amount);
                        upManualProcessingLimit(amount);
                    }
                    case MANUAL_PROCESSING -> upManualProcessingLimit(amount);
                }
            }
        }

        Limit limit = limitRepository.findByIdOrderById(1L).orElseThrow();
        limit.setAllowedAmount(allowedAmount);
        limit.setManualProcessingAmount(manualProcessingAmount);
        limitRepository.save(limit);
    }

    private void upAllowedLimit(Long amount) {
        allowedAmount = (int) Math.ceil(0.8 * allowedAmount + 0.2 * amount);
    }

    private void downAllowedLimit(Long amount) {
        allowedAmount = (int) Math.ceil(0.8 * allowedAmount - 0.2 * amount);
    }

    private void upManualProcessingLimit(Long amount) {
        manualProcessingAmount = (int) Math.ceil(0.8 * manualProcessingAmount + 0.2 * amount);
    }

    private void downManualProcessingLimit(Long amount) {
        manualProcessingAmount = (int) Math.ceil(0.8 * manualProcessingAmount - 0.2 * amount);
    }

    private boolean isCardStolen(String number) {
        StolenCard stolenCard = stolenCardRepository.findByNumber(number).orElse(null);
        return stolenCard != null;
    }

    private boolean isIpInBlacklist(String ip) {
        IpBlacklist ipBlacklist = ipBlacklistRepository.findByIp(ip).orElse(null);
        return ipBlacklist != null;
    }

    private long countRegionCorrelation(List<TransactionHistory> history, String region) {
        return history.stream()
                .map(TransactionHistory::getRegion)
                .filter(tr -> !tr.equals(region))
                .distinct()
                .count();
    }

    private long countIpCorrelation(List<TransactionHistory> history, String ip) {
        return history.stream()
                .map(TransactionHistory::getIp)
                .filter(tr -> !tr.equals(ip))
                .distinct().count();
    }

    private TransactionHistoryResponse mapToTransactionHistoryResponse(TransactionHistory history) {
        return new TransactionHistoryResponse(
                history.getId(),
                history.getAmount(),
                history.getIp(),
                history.getNumber(),
                history.getRegion(),
                history.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                history.getResult(),
                history.getFeedback()
        );
    }
}
