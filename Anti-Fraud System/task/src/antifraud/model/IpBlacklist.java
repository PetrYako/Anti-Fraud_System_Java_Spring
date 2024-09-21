package antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ip_blacklist")
public class IpBlacklist {
    @Id
    @GeneratedValue
    private Long id;
    private String ip;

    public IpBlacklist() {}

    public IpBlacklist(String ip) {
        this.ip = ip;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
