package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class PatientProfileResponse {
    @SerializedName("userId")
    private int userId;
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
    @SerializedName("profileId")
    private int profileId;
    @SerializedName("user")
    private UserResponse user;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
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
    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }
} 