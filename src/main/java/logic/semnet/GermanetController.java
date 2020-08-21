package logic.semnet;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.EwnRel;
import de.tuebingen.uni.sfs.germanet.api.FilterConfig;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.IliRecord;
import de.tuebingen.uni.sfs.germanet.api.RelDirection;
import de.tuebingen.uni.sfs.germanet.api.Synset;
import logic.util.Consts;
import logic.util.GermanetFrequencies;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GermanetController implements SemnetController<Synset> {
    private final GermaNet germanet;
    private final GermanetFrequencies frequencies;

    public GermanetController() throws IOException, XMLStreamException {
        this.germanet = new GermaNet(Consts.germaNetLocation, Consts.nounFreq, Consts.verbFreq, Consts.adjFreq);
        this.frequencies = GermanetFrequencies.loadFrequencies(Consts.nounFreq, Consts.verbFreq, Consts.adjFreq);
    }

    @Override
    public List<Synset> getSynsets(String word) {
        FilterConfig filterConfig = new FilterConfig(word);
        filterConfig.setIgnoreCase(true);
        return this.germanet.getSynsets(filterConfig);
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

    public List<Long> getOffsetFromID(int id) {
        List<Long> offsets = new ArrayList<>();
        for (IliRecord ir : this.germanet.getSynsetByID(id).getIliRecords()) {
            offsets.add(Long.parseLong(ir.getPwn30Id()));
        }
        return offsets;
    }

    public GermaNet getObject() {
        return this.germanet;
    }

    public List<Synset> getHypernyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hypernym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getFrequency));
        return relations;

    }

    public List<Synset> getHyponyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hyponym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getFrequency));
        return relations;
    }

    public Synset getSynsetById(int id) {
        return this.germanet.getSynsetByID(id);
    }
}