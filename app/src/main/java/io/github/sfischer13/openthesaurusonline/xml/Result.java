package io.github.sfischer13.openthesaurusonline.xml;

import java.util.List;

import io.github.sfischer13.openthesaurusonline.model.Synset;

public class Result {
    private final List<Synset> synsets;

    public Result(List<Synset> synsets) {
        this.synsets = synsets;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Synset aSynset : synsets) {
            sb.append(sep);
            sb.append(aSynset.toString());
            sep = "\n\n";
        }
        return sb.toString();
    }

    public List<Synset> getSynsets() {
        return synsets;
    }
}
