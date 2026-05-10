package com.cloudcompare.ai.service;

import com.cloudcompare.ai.dto.Region;
import com.cloudcompare.ai.dto.ServiceType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages metadata for cloud services and regions.
 * Decouples hardcoded configuration from the Controller.
 */
@Service
public class MetaDataService {

    private final Map<String, List<ServiceType>> serviceTypes = new LinkedHashMap<>();
    private final List<Region> regions = new ArrayList<>();

    public MetaDataService() {
        initServiceTypes();
        initRegions();
    }

    private void initServiceTypes() {
        serviceTypes.put("compute", List.of(
                new ServiceType("Virtual Machines", "Virtual Machines"),
                new ServiceType("Serverless Compute", "Serverless Compute"),
                new ServiceType("Container Services", "Container Services"),
                new ServiceType("Kubernetes", "Kubernetes"),
                new ServiceType("Batch Processing", "Batch Processing"),
                new ServiceType("High Performance Computing", "High Performance Computing (HPC)"),
                new ServiceType("GPU Instances", "GPU Instances"),
                new ServiceType("Bare Metal Servers", "Bare Metal Servers"),
                new ServiceType("Auto Scaling", "Auto Scaling"),
                new ServiceType("Edge Computing", "Edge Computing"),
                new ServiceType("Spot/Preemptible Instances", "Spot / Preemptible Instances"),
                new ServiceType("App Hosting / PaaS", "App Hosting / PaaS")
        ));

        serviceTypes.put("storage", List.of(
                new ServiceType("Object Storage", "Object Storage"),
                new ServiceType("Block Storage", "Block Storage"),
                new ServiceType("File Storage", "File Storage"),
                new ServiceType("Archive Storage", "Archive / Cold Storage"),
                new ServiceType("Content Delivery Network", "CDN / Content Delivery"),
                new ServiceType("Backup and Disaster Recovery", "Backup & Disaster Recovery"),
                new ServiceType("Data Transfer and Migration", "Data Transfer & Migration"),
                new ServiceType("Hybrid Storage", "Hybrid / Gateway Storage"),
                new ServiceType("Storage for Big Data", "Data Lake Storage")
        ));

        serviceTypes.put("database", List.of(
                new ServiceType("Relational Database", "Relational Database (SQL)"),
                new ServiceType("NoSQL Database", "NoSQL Database"),
                new ServiceType("In-Memory Cache", "In-Memory Cache / Redis"),
                new ServiceType("Data Warehouse", "Data Warehouse"),
                new ServiceType("Graph Database", "Graph Database"),
                new ServiceType("Time Series Database", "Time Series Database"),
                new ServiceType("Document Database", "Document Database"),
                new ServiceType("Database Migration", "Database Migration"),
                new ServiceType("Managed MySQL", "Managed MySQL"),
                new ServiceType("Managed PostgreSQL", "Managed PostgreSQL"),
                new ServiceType("Managed MongoDB", "Managed MongoDB"),
                new ServiceType("Serverless Database", "Serverless Database"),
                new ServiceType("Ledger / Blockchain Database", "Ledger / Blockchain Database")
        ));

        serviceTypes.put("ai", List.of(
                new ServiceType("Large Language Models (LLMs)", "Large Language Models (LLMs)"),
                new ServiceType("Vision & Image AI", "Vision & Image AI"),
                new ServiceType("Speech & Audio", "Speech & Audio"),
                new ServiceType("NLP & Text Analytics", "NLP & Text Analytics"),
                new ServiceType("Translation AI", "Translation AI"),
                new ServiceType("Document & OCR AI", "Document & OCR AI"),
                new ServiceType("Machine Learning Platforms", "Machine Learning Platforms"),
                new ServiceType("Recommendation AI", "Recommendation AI"),
                new ServiceType("Conversational AI", "Conversational AI / Chatbots"),
                new ServiceType("Video AI & Analysis", "Video AI & Analysis"),
                new ServiceType("Forecasting", "Forecasting"),
                new ServiceType("Anomaly Detection", "Anomaly Detection"),
                new ServiceType("AI Search", "AI Search"),
                new ServiceType("Generative AI", "Generative AI"),
                new ServiceType("AI Code Assistants", "AI Code Assistants"),
                new ServiceType("Text to Image", "Text-to-Image Generation"),
                new ServiceType("AI Model Hosting", "AI Model Hosting / Inference"),
                new ServiceType("AutoML", "AutoML"),
                new ServiceType("AI Data Labeling", "AI Data Labeling"),
                new ServiceType("Fraud Detection", "Fraud Detection AI")
        ));
    }

    private void initRegions() {
        regions.add(new Region("all", "All Regions"));
        regions.add(new Region("us-east-1", "US East (N. Virginia)"));
        regions.add(new Region("us-central1", "US Central"));
        regions.add(new Region("eastus", "US East (Azure)"));
        regions.add(new Region("us-ashburn-1", "US Ashburn (OCI)"));
        regions.add(new Region("ap-southeast-1", "Asia Pacific (Singapore)"));
        regions.add(new Region("eu-central-1", "Europe (Frankfurt)"));
    }

    public List<ServiceType> getServiceTypes(String category) {
        return serviceTypes.getOrDefault(category, Collections.emptyList());
    }

    public List<Region> getRegions() {
        return regions;
    }

    public String getDefaultServiceType(String category) {
        List<ServiceType> types = getServiceTypes(category);
        return types.isEmpty() ? category : types.get(0).getValue();
    }
}
