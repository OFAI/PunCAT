package at.ofai.punderstanding.puncat.gui.model.CorpusInstance;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "corpus")
public class Corpus {
    @XmlElement(name = "instance")
    ArrayList<CorpusInstance> instances;

    public CorpusInstance getModel(int i) {
        return instances.get(0);
    }

    public void printAll() {
        for (CorpusInstance ci : this.instances) {
            ci.printAll();
        }
    }
}
