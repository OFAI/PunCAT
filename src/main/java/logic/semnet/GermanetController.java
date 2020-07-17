package logic.semnet;

import de.tuebingen.uni.sfs.germanet.api.EwnRel;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.IliRecord;
import de.tuebingen.uni.sfs.germanet.api.Synset;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

public class GermanetController implements SemnetController<Synset> {
    private static final String GermaNetLocation = "C:/git/puncat/GN_V150_XML";
    private final GermaNet germanet;

    public GermanetController() throws IOException, XMLStreamException {
        this.germanet = new GermaNet(GermaNetLocation, true);
    }

    @Override
    public List<Synset> getSynsets(String word) {
        return this.germanet.getSynsets(word);
    }

    public Synset equivalentByWordnetOffset(long offset) {
        String offsetString = Long.toString(offset);
        for (IliRecord ir : this.germanet.getIliRecords()) {
            if (ir.getPwn30Id().contains(offsetString) && ir.getEwnRelation() == EwnRel.synonym) {
                return germanet.getLexUnitByID(ir.getLexUnitId()).getSynset();
            }
        }
        for (IliRecord ir : this.germanet.getIliRecords()) {
            if (ir.getPwn30Id().contains(offsetString) && ir.getEwnRelation() == EwnRel.near_synonym) {
                return germanet.getLexUnitByID(ir.getLexUnitId()).getSynset();
            }
        }
        for (IliRecord ir : this.germanet.getIliRecords()) {
            if (ir.getPwn30Id().contains(offsetString)) {
                return germanet.getLexUnitByID(ir.getLexUnitId()).getSynset();
            }
        }
        return null; // TODO: better handling of no result
    }
}
