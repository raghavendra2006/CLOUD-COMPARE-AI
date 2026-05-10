package com.cloudcompare.ai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cloud_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudServiceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String platform; // AWS, GCP, etc.
    private String serviceName;
    private String category; // compute, storage, etc.
    private String serviceType; // Virtual Machines, etc.
    
    private Integer cpu;
    private Integer ram;
    private Integer storage;
    
    private Double pricePerHour;
    private Double pricePerGb;
    
    private Double performanceScore;
    private Double popularityScore;
    
    private String region;
    
    @Column(length = 1000)
    private String description;
}
