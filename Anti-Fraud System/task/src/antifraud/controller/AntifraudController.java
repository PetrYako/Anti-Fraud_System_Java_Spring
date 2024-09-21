package antifraud.controller;

import antifraud.controller.dto.stolen_card.StolenCardRequest;
import antifraud.controller.dto.stolen_card.StolenCardResponse;
import antifraud.controller.dto.stolen_card.StolenCardStatusResponse;
import antifraud.controller.dto.suspicious_ip.SuspiciousIpRequest;
import antifraud.controller.dto.suspicious_ip.SuspiciousIpResponse;
import antifraud.controller.dto.suspicious_ip.SuspiciousIpStatusResponse;
import antifraud.controller.dto.transaction.TransactionFeedbackRequest;
import antifraud.controller.dto.transaction.TransactionHistoryResponse;
import antifraud.controller.dto.transaction.TransactionRequest;
import antifraud.controller.dto.transaction.TransactionResponse;
import antifraud.model.TransactionStatus;
import antifraud.service.IpBlacklistService;
import antifraud.service.StolenCardService;
import antifraud.service.TransactionService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.validator.routines.DateValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeParseException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping("/api/antifraud")
@ControllerAdvice
public class AntifraudController {

    private final InetAddressValidator validator = InetAddressValidator.getInstance();

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private IpBlacklistService ipBlacklistService;

    @Autowired
    private StolenCardService stolenCardService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> processTransaction(@RequestBody TransactionRequest request) {
        try {
            if (request.getAmount() <= 0 || request.getIp() == null || request.getIp().isBlank() || !validator.isValidInet4Address(request.getIp()) ||
                    request.getNumber() == null || request.getNumber().isBlank() || !LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(request.getNumber())) {
                throw new IllegalArgumentException("Validation error");
            }
            TransactionResponse response = transactionService.process(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIpResponse> addSuspiciousIp(@RequestBody SuspiciousIpRequest request) {
        try {
            if (request.getIp() == null || request.getIp().isBlank() || !validator.isValidInet4Address(request.getIp())) {
                throw new IllegalArgumentException("Ip can't be empty");
            }
            return ResponseEntity.ok(ipBlacklistService.addIp(request.getIp()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<SuspiciousIpStatusResponse> deleteSuspiciousIp(@PathVariable String ip) {
        try {
            if (!validator.isValidInet4Address(ip)) {
                throw new IllegalArgumentException("Ip can't be empty");
            }
            ipBlacklistService.removeIp(ip);
            return ResponseEntity.ok(new SuspiciousIpStatusResponse(
                    "IP " + ip + " successfully removed!"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<SuspiciousIpResponse>> getAllSuspiciousIps() {
        return ResponseEntity.ok(ipBlacklistService.getAll());
    }

    @PostMapping("/stolencard")
    public ResponseEntity<StolenCardResponse> processStolenCard(@RequestBody StolenCardRequest request) {
        try {
            String card = request.getNumber();
            if (card == null || card.isBlank() || !LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(request.getNumber())) {
                throw new IllegalArgumentException("Card can't be empty");
            }
            return ResponseEntity.ok(stolenCardService.addCard(card));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<StolenCardStatusResponse> deleteCard(@PathVariable String number) {
        try {
            if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(number)) {
                throw new IllegalArgumentException("Card can't be empty");
            }
            stolenCardService.removeCard(number);
            return ResponseEntity.ok(new StolenCardStatusResponse(
                    "Card " + number + " successfully removed!"
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<StolenCardResponse>> getAllStolenCards() {
        return ResponseEntity.ok(stolenCardService.getAll());
    }

    @PutMapping("/transaction")
    public ResponseEntity<TransactionHistoryResponse> updateTransaction(@RequestBody TransactionFeedbackRequest request) {
        try {
            return ResponseEntity.ok(transactionService.addFeedback(request.getTransactionId(), TransactionStatus.valueOf(request.getFeedback())));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(422).build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryResponse>> getHistory() {
        return ResponseEntity.ok(transactionService.getHistory());
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<TransactionHistoryResponse>> getHistoryByCard(@PathVariable String number) {
        try {
            if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(number)) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(transactionService.getHistory(number));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
        return ResponseEntity.badRequest().build();
    }
}