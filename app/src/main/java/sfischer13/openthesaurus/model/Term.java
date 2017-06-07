/*
   Copyright 2015-2017 Stefan Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package sfischer13.openthesaurus.model;

import java.io.Serializable;

public class Term implements Serializable {
    private String term = null;
    private String level = null;

    public Term(String term) {
        this.term = term;
    }

    public Term(String term, String level) {
        this.term = term;
        this.level = level;
    }


    public String getTerm() {
        return term;
    }

    public String getLevel() {
        return level;
    }

    public String getLevelAbbreviation() {
        if (level == null) {
            return null;
        }
        switch (level) {
            case "umgangssprachlich":
                return "ugs.";
            case "derb":
                return "derb";
            case "vulgär":
                return "vulg.";
            case "fachsprachlich":
                return "fachspr.";
            case "gehoben":
                return "geh.";
            default:
                return level;
        }
    }

    @Override
    public String toString() {
        if (level == null) {
            return term;
        } else {
            return String.format("%s (%s)", term, level);
        }
    }
}
