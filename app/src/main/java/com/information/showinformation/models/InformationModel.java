package com.information.showinformation.models;

public class InformationModel {
    private String text = "";
    private String startString = "";
    private String endString = "";

    public InformationModel(String text, String startString, String endString){
        this.text = text;
        this.startString = startString;
        this.endString = endString;
    }

    public InformationModel(String text, String endString){
        this.text = text;
        this.endString = endString;
    }

    public InformationModel(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getEndString() {
        return endString;
    }

    public String getStartString() {
        return startString;
    }


}
