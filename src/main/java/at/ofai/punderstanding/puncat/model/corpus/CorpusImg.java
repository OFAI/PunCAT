package at.ofai.punderstanding.puncat.model.corpus;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class CorpusImg {
    @XmlAttribute
    public String src;
    @XmlElement(name = "keyword")
    public ArrayList<String> keywords;

    public void printAll() {
        System.out.println("src: " + this.src);
        System.out.println("keywords: " + this.keywords);
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }
}
