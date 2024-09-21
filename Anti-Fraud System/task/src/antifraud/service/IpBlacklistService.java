package antifraud.service;

import antifraud.controller.dto.suspicious_ip.SuspiciousIpResponse;
import antifraud.model.IpBlacklist;
import antifraud.repository.IpBlacklistRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IpBlacklistService {

    @Autowired
    private IpBlacklistRepository ipBlacklistRepository;

    public SuspiciousIpResponse addIp(String ip) {
        IpBlacklist ipBlacklist = ipBlacklistRepository.findByIp(ip).orElse(null);
        if (ipBlacklist != null) {
            throw new EntityExistsException("Ip already exists");
        }
        IpBlacklist newIp = new IpBlacklist(ip);
        IpBlacklist createdIp = ipBlacklistRepository.save(newIp);
        return mapToSuspiciousIpResponse(createdIp);
    }

    public void removeIp(String ip) {
        IpBlacklist ipBlacklist = ipBlacklistRepository.findByIp(ip)
                .orElseThrow(() -> new EntityNotFoundException("Ip not found"));
        ipBlacklistRepository.delete(ipBlacklist);
    }

    public List<SuspiciousIpResponse> getAll() {
        return ipBlacklistRepository.findAllByOrderByIdAsc()
                .stream().map(this::mapToSuspiciousIpResponse).toList();
    }

    private SuspiciousIpResponse mapToSuspiciousIpResponse(IpBlacklist ip) {
        return new SuspiciousIpResponse(ip.getId(), ip.getIp());
    }
}
