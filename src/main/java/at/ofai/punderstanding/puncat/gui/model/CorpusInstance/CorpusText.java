package at.ofai.punderstanding.puncat.gui.model.CorpusInstance;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class CorpusText {
    @XmlMixed
    @XmlJavaTypeAdapter(CorpusTextAdapter.class)
    ArrayList<String> text;
    @XmlElement
    CorpusPun pun;

    public void printAll() {
        System.out.println("text: " + text);
        pun.printAll();
    }

    public ArrayList<String> getText() {
        return text;
    }

    public CorpusPun getPun() {
        return pun;
    }

    static class CorpusTextAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String s) {
            return s.strip();
        }

        @Override
        public String marshal(String strings) {
            return null;
        }
    }
}
