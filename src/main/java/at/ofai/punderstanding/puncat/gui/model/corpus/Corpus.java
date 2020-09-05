package at.ofai.punderstanding.puncat.gui.model.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;


@XmlRootElement(name = "corpus")
public class Corpus {
    @XmlElement(name = "instance")
    ArrayList<CorpusInstance> instances;

    public static Corpus parseCorpus(File xml) {
        //String xml = "C:/git/ref/puncat.xml";

        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(Corpus.class);
        } catch (JAXBException e) {
            throw new RuntimeException();
        }

        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://xml.org/sax/features/validation", false);
        } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException();
        }

        XMLReader xmlReader;
        try {
            xmlReader = spf.newSAXParser().getXMLReader();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        InputSource inputSource;
        try {
            inputSource = new InputSource(new FileReader(xml));
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        }
        SAXSource source = new SAXSource(xmlReader, inputSource);

        Unmarshaller unmarshaller;
        try {
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException();
        }
        Corpus corpus;
        try {
            corpus = (Corpus) unmarshaller.unmarshal(source);
        } catch (JAXBException e) {
            throw new RuntimeException();
        }

        return corpus;
    }

    public int size() {
        return this.instances.size();
    }

    public ArrayList<CorpusInstance> getInstances() {
        return instances;
    }

    public CorpusInstance getModel(int i) {
        return instances.get(0);
    }

    public void printAll() {
        for (CorpusInstance ci : this.instances) {
            ci.printAll();
        }
    }
}
