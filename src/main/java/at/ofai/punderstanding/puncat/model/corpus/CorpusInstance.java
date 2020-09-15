package at.ofai.punderstanding.puncat.model.corpus;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


public class CorpusInstance {
    @XmlAttribute
    String id;
    @XmlElement
    CorpusImg img;
    @XmlElement
    CorpusText text;

    public void printAll() {
        System.out.println("id: " + this.id);
        this.img.printAll();
        this.text.printAll();
    }

    public String getId() {
        return id;
    }

    public CorpusImg getImg() {
        return img;
    }

    public CorpusText getText() {
        return text;
    }
}
