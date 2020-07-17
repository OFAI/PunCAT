package logic;

import logic.semnet.GermanetController;
import logic.semnet.WordnetController;
import net.sf.extjwnl.JWNLException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class PunCat {
    static String pun = "hoarse";
    static String target = "horse";

    public static void main(String[] args) throws IOException, XMLStreamException, JWNLException {
        GermanetController gnc = new GermanetController();
        WordnetController wnc = new WordnetController();


    }

}
