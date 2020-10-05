/*
  Copyright 2020 Máté Lajkó

  This file is part of PunCAT.

  PunCAT is free software: you can redistribute it and/or modify it
  under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PunCAT is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with PunCAT.  If not, see <https://www.gnu.org/licenses/>.
 */

package at.ofai.punderstanding.puncat.model.corpus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
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
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(Corpus.class);
        } catch (JAXBException e) {
            e.printStackTrace();
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
        Corpus corpus = null;
        try {
            corpus = (Corpus) unmarshaller.unmarshal(source);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        assert corpus != null;
        updateImagePaths(xml, corpus);

        return corpus;
    }

    private static void updateImagePaths(File xml, Corpus corpus) {
        for (var ci : corpus.getInstances()) {
            var fileName = ci.getImg().src;
            fileName = Path.of(xml.getParent(), fileName).toUri().toString();
            ci.getImg().setSrc(fileName);
        }
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

}
