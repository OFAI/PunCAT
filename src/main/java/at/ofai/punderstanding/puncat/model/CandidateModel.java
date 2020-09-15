package at.ofai.punderstanding.puncat.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class CandidateModel {
    private final StringProperty pun = new SimpleStringProperty();
    private final StringProperty target = new SimpleStringProperty();
    private final StringProperty sem = new SimpleStringProperty();
    private final StringProperty phon = new SimpleStringProperty();
    private final BooleanProperty hasEmptyValues = new SimpleBooleanProperty();
    private final boolean currentCandidate;

    public CandidateModel(String pun, String target, String sem, String phon) {
        this.setPun(pun);
        this.setTarget(target);
        this.setSem(sem);
        this.setPhon(phon);
        this.currentCandidate = false;
    }

    public CandidateModel(StringProperty punCandidate, StringProperty targetCandidate,
                          StringProperty semanticScore, StringProperty phoneticScore) {
        this.pun.bind(punCandidate);
        this.target.bind(targetCandidate);
        this.sem.bind(semanticScore);
        this.phon.bind(phoneticScore);
        this.currentCandidate = true;
        this.hasEmptyValues.bind(this.pun.isEmpty()
                .or(this.target.isEmpty())
                .or(this.sem.isEmpty())
                .or(this.phon.isEmpty()));
    }

    public String getPun() {
        return pun.get();
    }

    public void setPun(String pun) {
        this.pun.set(pun);
    }

    public StringProperty punProperty() {
        return pun;
    }

    public String getTarget() {
        return target.get();
    }

    public void setTarget(String target) {
        this.target.set(target);
    }

    public StringProperty targetProperty() {
        return target;
    }

    public String getSem() {
        return sem.get();
    }

    public void setSem(String sem) {
        this.sem.set(sem);
    }

    public StringProperty semProperty() {
        return sem;
    }

    public String getPhon() {
        return phon.get();
    }

    public void setPhon(String phon) {
        this.phon.set(phon);
    }

    public StringProperty phonProperty() {
        return phon;
    }

    public boolean isHasEmptyValues() {
        return hasEmptyValues.get();
    }

    public BooleanProperty hasEmptyValuesProperty() {
        return hasEmptyValues;
    }

    public boolean isCurrentCandidate() {
        return currentCandidate;
    }
}