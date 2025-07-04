package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class UpdatePatientProfileRequest {
    @SerializedName("insurance")
    private String insurance;
    @SerializedName("allergies")
    private String allergies;
    @SerializedName("chronicDiseases")
    private String chronicDiseases;
    @SerializedName("obstetricHistory")
    private String obstetricHistory;
    @SerializedName("surgicalHistory")
    private String surgicalHistory;
    @SerializedName("familyHistory")
    private String familyHistory;
    @SerializedName("socialHistory")
    private String socialHistory;
    @SerializedName("medicationHistory")
    private String medicationHistory;

    // Getters and setters
    public String getInsurance() { return insurance; }
    public void setInsurance(String insurance) { this.insurance = insurance; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
    public String getObstetricHistory() { return obstetricHistory; }
    public void setObstetricHistory(String obstetricHistory) { this.obstetricHistory = obstetricHistory; }
    public String getSurgicalHistory() { return surgicalHistory; }
    public void setSurgicalHistory(String surgicalHistory) { this.surgicalHistory = surgicalHistory; }
    public String getFamilyHistory() { return familyHistory; }
    public void setFamilyHistory(String familyHistory) { this.familyHistory = familyHistory; }
    public String getSocialHistory() { return socialHistory; }
    public void setSocialHistory(String socialHistory) { this.socialHistory = socialHistory; }
    public String getMedicationHistory() { return medicationHistory; }
    public void setMedicationHistory(String medicationHistory) { this.medicationHistory = medicationHistory; }
} 