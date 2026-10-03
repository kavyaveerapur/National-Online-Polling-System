package com.polling.system.model;

import jakarta.persistence.*;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String party;
    private String constituency;
    
    @Column(length = 2000)
    private String manifesto;
    
    private String photoPath;
    private String status; // PENDING_APPROVAL, APPROVED, REJECTED
    
    private int onlineVotes;
    private int manualVotes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }

    public String getConstituency() { return constituency; }
    public void setConstituency(String constituency) { this.constituency = constituency; }

    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getOnlineVotes() { return onlineVotes; }
    public void setOnlineVotes(int onlineVotes) { this.onlineVotes = onlineVotes; }

    public int getManualVotes() { return manualVotes; }
    public void setManualVotes(int manualVotes) { this.manualVotes = manualVotes; }

    public int getTotalVotes() { return onlineVotes + manualVotes; }

    // Legacy getter/setter compatibility for votes
    public int getVotes() { return getTotalVotes(); }
    public void setVotes(int votes) { this.onlineVotes = votes; }
}
