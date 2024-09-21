package antifraud.service;

import antifraud.controller.dto.stolen_card.StolenCardResponse;
import antifraud.model.StolenCard;
import antifraud.repository.StolenCardRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StolenCardService {

    @Autowired
    private StolenCardRepository stolenCardRepository;

    public StolenCardResponse addCard(String card) {
        StolenCard existingCard = stolenCardRepository.findByNumber(card).orElse(null);
        if (existingCard != null) {
            throw new EntityExistsException("Card already exists");
        }
        StolenCard newCard = new StolenCard(card);
        StolenCard createdCard = stolenCardRepository.save(newCard);
        return mapToStolenCardResponse(createdCard);
    }

    public void removeCard(String number) {
        StolenCard existingCard = stolenCardRepository.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Card not found"));
        stolenCardRepository.delete(existingCard);
    }

    public List<StolenCardResponse> getAll() {
        return stolenCardRepository.findAllByOrderByIdAsc()
                .stream().map(this::mapToStolenCardResponse).toList();
    }

    public StolenCardResponse mapToStolenCardResponse(StolenCard card) {
        return new StolenCardResponse(card.getId(), card.getNumber());
    }
}
