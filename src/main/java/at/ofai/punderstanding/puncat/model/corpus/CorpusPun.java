package at.ofai.punderstanding.puncat.model.corpus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement
public class CorpusPun {
    @XmlValue
    String pun;
    @XmlAttribute(name = "first_lemma")
    String firstLemma;
    @XmlAttribute(name = "first_sense")
    String firstSense;
    @XmlAttribute(name = "second_lemma")
    String secondLemma;
    @XmlAttribute(name = "second_sense")
    String secondSense;

    public void printAll() {
        System.out.println("pun: " + pun);
        System.out.println("firstLemma: " + firstLemma + ", firstSense: " + firstSense);
        System.out.println("secondLemma: " + secondLemma + ", secondSense: " + secondSense);
    }

    public String getPun() {
        return pun;
    }

    public String getFirstLemma() {
        return firstLemma;
    }

    public String getFirstSense() {
        return firstSense;
    }

    public String getSecondLemma() {
        return secondLemma;
    }

    public String getSecondSense() {
        return secondSense;
    }
}
