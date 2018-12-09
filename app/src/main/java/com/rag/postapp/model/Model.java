package com.rag.postapp.model;

/**
 * Created by Raghavendra Kallubandi on 09/12/18.
 */
public class Model {

    private String text;
    private boolean isSelected = false;

    public Model(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
