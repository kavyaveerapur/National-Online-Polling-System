package com.polling.system.model;

import jakarta.persistence.*;

@Entity
public class VoterApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String aadhaar;
    private String constituency;
    private String photoPath;
    
    @Column(length = 1000)
    private String fieldOfficerReport;
    
    private String generatedVoterId;
    private String status; // PENDING, APPROVED, REJECTED

    @ManyToOne
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAadhaar() { return aadhaar; }
    public void setAadhaar(String aadhaar) { this.aadhaar = aadhaar; }

    public String getConstituency() { return constituency; }
    public void setConstituency(String constituency) { this.constituency = constituency; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getFieldOfficerReport() { return fieldOfficerReport; }
    public void setFieldOfficerReport(String fieldOfficerReport) { this.fieldOfficerReport = fieldOfficerReport; }

    public String getGeneratedVoterId() { return generatedVoterId; }
    public void setGeneratedVoterId(String generatedVoterId) { this.generatedVoterId = generatedVoterId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
