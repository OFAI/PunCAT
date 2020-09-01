package at.ofai.punderstanding.puncat.gui.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CandidateModel {
    private final StringProperty pun = new SimpleStringProperty();
    private final StringProperty target = new SimpleStringProperty();
    private final StringProperty sem = new SimpleStringProperty();
    private final StringProperty phon = new SimpleStringProperty();
    private final boolean realTime;

    public CandidateModel(String pun, String target, String sem, String phon) {
        this.setPun(pun);
        this.setTarget(target);
        this.setSem(sem);
        this.setPhon(phon);
        this.realTime = false;
    }

    public CandidateModel(StringProperty punCandidate, StringProperty targetCandidate,
                          StringProperty semanticScore, StringProperty phoneticScore) {
        this.pun.bind(punCandidate);
        this.target.bind(targetCandidate);
        this.sem.bind(semanticScore);
        this.phon.bind(phoneticScore);
        this.realTime = true;
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

    public String getSem() {
        return sem.get();
    }

    public StringProperty semProperty() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem.set(sem);
    }

    public String getPhon() {
        return phon.get();
    }

    public StringProperty phonProperty() {
        return phon;
    }

    public void setPhon(String phon) {
        this.phon.set(phon);
    }

    public boolean isRealTime() {
        return realTime;
    }
}
