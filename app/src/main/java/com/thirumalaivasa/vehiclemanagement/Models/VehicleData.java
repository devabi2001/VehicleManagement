package com.thirumalaivasa.vehiclemanagement.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class VehicleData implements Parcelable {
    private String chassisNumber;
    private String engineNumber;
    private String manufacturer;
    private String manufacturerModel;
    private String registrationDate;
    private String vehicleClass;
    private String fuelType;
    private String colour;

    private String permitValidity;
    private String mvTaxValidity;
    private String fitnessValidity;
    private String insuranceValidity;
    private String pucValidity;

    private String registeredPlace;
    private String ownerName, fatherName;
    private String registrationNumber;
    private int fuelCapacity;

    private String vehiclePic;


    public VehicleData() {
    }

    public VehicleData(String chassisNumber, String engineNumber, String manufacturer, String manufacturerModel, String registrationDate, String vehicleClass, String fuelType, String colour, String permitValidity, String mvTaxValidity, String fitnessValidity, String insuranceValidity, String pucValidity, String registeredPlace, String ownerName, String registrationNumber, int fuelCapacity, String fatherName,String vehiclePic) {
        this.chassisNumber = chassisNumber;
        this.engineNumber = engineNumber;
        this.manufacturer = manufacturer;
        this.manufacturerModel = manufacturerModel;
        this.registrationDate = registrationDate;
        this.vehicleClass = vehicleClass;
        this.fuelType = fuelType;
        this.colour = colour;
        this.permitValidity = permitValidity;
        this.mvTaxValidity = mvTaxValidity;
        this.fitnessValidity = fitnessValidity;
        this.insuranceValidity = insuranceValidity;
        this.pucValidity = pucValidity;
        this.registeredPlace = registeredPlace;
        this.ownerName = ownerName;
        this.registrationNumber = registrationNumber;
        this.fuelCapacity = fuelCapacity;
        this.fatherName = fatherName;
        this.vehiclePic = vehiclePic;
    }

    protected VehicleData(Parcel in) {
        chassisNumber = in.readString();
        engineNumber = in.readString();
        manufacturer = in.readString();
        manufacturerModel = in.readString();
        registrationDate = in.readString();
        vehicleClass = in.readString();
        fuelType = in.readString();
        colour = in.readString();
        permitValidity = in.readString();
        mvTaxValidity = in.readString();
        fitnessValidity = in.readString();
        insuranceValidity = in.readString();
        pucValidity = in.readString();
        registeredPlace = in.readString();
        ownerName = in.readString();
        registrationNumber = in.readString();
        fuelCapacity = in.readInt();
        fatherName = in.readString();
        vehiclePic = in.readString();
    }

    public static final Creator<VehicleData> CREATOR = new Creator<VehicleData>() {
        @Override
        public VehicleData createFromParcel(Parcel in) {
            return new VehicleData(in);
        }

        @Override
        public VehicleData[] newArray(int size) {
            return new VehicleData[size];
        }
    };

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerModel() {
        return manufacturerModel;
    }

    public void setManufacturerModel(String manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getPermitValidity() {
        return permitValidity;
    }

    public void setPermitValidity(String permitValidity) {
        this.permitValidity = permitValidity;
    }

    public String getMvTaxValidity() {
        return mvTaxValidity;
    }

    public void setMvTaxValidity(String mvTaxValidity) {
        this.mvTaxValidity = mvTaxValidity;
    }

    public String getFitnessValidity() {
        return fitnessValidity;
    }

    public void setFitnessValidity(String fitnessValidity) {
        this.fitnessValidity = fitnessValidity;
    }

    public String getInsuranceValidity() {
        return insuranceValidity;
    }

    public void setInsuranceValidity(String insuranceValidity) {
        this.insuranceValidity = insuranceValidity;
    }

    public String getPucValidity() {
        return pucValidity;
    }

    public void setPucValidity(String pucValidity) {
        this.pucValidity = pucValidity;
    }

    public String getRegisteredPlace() {
        return registeredPlace;
    }

    public void setRegisteredPlace(String registeredPlace) {
        this.registeredPlace = registeredPlace;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }


    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public String getVehiclePic() {
        return vehiclePic;
    }

    public void setVehiclePic(String vehiclePic) {
        this.vehiclePic = vehiclePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chassisNumber);
        dest.writeString(engineNumber);
        dest.writeString(manufacturer);
        dest.writeString(manufacturerModel);
        dest.writeString(registrationDate);
        dest.writeString(vehicleClass);
        dest.writeString(fuelType);
        dest.writeString(colour);
        dest.writeString(permitValidity);
        dest.writeString(mvTaxValidity);
        dest.writeString(fitnessValidity);
        dest.writeString(insuranceValidity);
        dest.writeString(pucValidity);
        dest.writeString(registeredPlace);
        dest.writeString(ownerName);
        dest.writeString(registrationNumber);
        dest.writeInt(fuelCapacity);
        dest.writeString(fatherName);
        dest.writeString(vehiclePic);
    }
}
