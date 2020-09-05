package at.ofai.punderstanding.puncat.gui.model.corpus;

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

    public String getSrc() {
        return src;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }
}
