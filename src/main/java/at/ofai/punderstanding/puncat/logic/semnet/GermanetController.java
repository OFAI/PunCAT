package at.ofai.punderstanding.puncat.logic.semnet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.xml.stream.XMLStreamException;

import de.tuebingen.uni.sfs.germanet.api.ConRel;
import de.tuebingen.uni.sfs.germanet.api.EwnRel;
import de.tuebingen.uni.sfs.germanet.api.FilterConfig;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.IliRecord;
import de.tuebingen.uni.sfs.germanet.api.RelDirection;
import de.tuebingen.uni.sfs.germanet.api.Synset;

import at.ofai.punderstanding.puncat.logic.ResourcePaths;


public class GermanetController implements SemnetController<Synset> {
    private final GermaNet germanet;
    private final GermanetFrequencies frequencies;

    public GermanetController() throws IOException, XMLStreamException {
        this.germanet = new GermaNet(
                getClass().getResource(ResourcePaths.germaNetLocation).getPath(),
                getClass().getResource(ResourcePaths.nounFreq).getPath(),
                getClass().getResource(ResourcePaths.verbFreq).getPath(),
                getClass().getResource(ResourcePaths.adjFreq).getPath());
        this.frequencies = GermanetFrequencies.loadFrequencies(
                getClass().getResource(ResourcePaths.nounFreq).getPath(),
                getClass().getResource(ResourcePaths.verbFreq).getPath(),
                getClass().getResource(ResourcePaths.adjFreq).getPath());
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
        return null;
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

    public Long getSynsetCumulativeFrequency(Synset synset) {
        return this.frequencies.getSynsetCumulativeFrequency(synset);
    }

    public String getMostFrequentOrthForm(Synset synset) {
        return this.frequencies.getMostFrequentOrthForm(synset);
    }

    public List<Synset> getHypernyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hypernym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;

    }

    public List<Synset> getHyponyms(int synsetId) {
        Synset synset = this.germanet.getSynsetByID(synsetId);
        List<Synset> relations = synset.getRelatedSynsets(ConRel.has_hyponym, RelDirection.incoming);
        relations.sort(Comparator.comparing(frequencies::getSynsetCumulativeFrequency));
        return relations;
    }

    public Synset getSynsetById(int id) {
        return this.germanet.getSynsetByID(id);
    }

    public String getLexUnitById(int lexUnitId) {
        return this.germanet.getLexUnitByID(lexUnitId).getOrthForm();
    }
}