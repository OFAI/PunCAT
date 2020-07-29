package gui.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CandidateModel {
    private final StringProperty pun = new SimpleStringProperty();
    private final StringProperty target = new SimpleStringProperty();
    private final DoubleProperty sem = new SimpleDoubleProperty();
    private final DoubleProperty phon = new SimpleDoubleProperty();

    public CandidateModel(String pun, String target, Double sem, Double phon) {
        this.setPun(pun);
        this.setTarget(target);
        this.setSem(sem);
        this.setPhon(phon);
    }

    public String getPun() {
        return pun.get();
    }

    public StringProperty punProperty() {
        return pun;
    }

    public void setPun(String pun) {
        this.pun.set(pun);
    }

    public String getTarget() {
        return target.get();
    }

    public StringProperty targetProperty() {
        return target;
    }

    public void setTarget(String target) {
        this.target.set(target);
    }

    public double getSem() {
        return sem.get();
    }

    public DoubleProperty semProperty() {
        return sem;
    }

    public void setSem(double sem) {
        this.sem.set(sem);
    }

    public double getPhon() {
        return phon.get();
    }

    public DoubleProperty phonProperty() {
        return phon;
    }

    public void setPhon(double phon) {
        this.phon.set(phon);
    }
}
