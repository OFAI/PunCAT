package logic.search;

import logic.semnet.GermanetController;
import logic.semnet.WordnetController;
import net.sf.extjwnl.data.Synset;

import java.util.List;

public class Search {
    private GermanetController germaNet = null;
    private WordnetController wordNet = null;

    public Search() {
        try {
            this.germaNet = new GermanetController();
            this.wordNet = new WordnetController();
        } catch (Exception e) {
            e.printStackTrace();  // TODO
        }
    }

    public List<Synset> getSourceSenses(String word) {
        return wordNet.getSynsets(word);
    }

    public List<de.tuebingen.uni.sfs.germanet.api.Synset> getTargetSenses(String word) {
        return germaNet.getSynsets(word);
    }

    public de.tuebingen.uni.sfs.germanet.api.Synset mapToGermanet(long offset) {
        return germaNet.equivalentByWordnetOffset(offset);
    }

}
