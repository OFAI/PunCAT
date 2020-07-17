package logic.semnet;

import java.util.List;

public interface SemnetController<SYNSET> {
    List<SYNSET> getSynsets(String word);
}
