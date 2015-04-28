/*
   Copyright 2015 Stefan Fischer

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

package io.github.sfischer13.openthesaurusonline.model;

import java.io.Serializable;

public class Term implements Serializable {
    private String term = null;

    public Term(String term) {
        this.term = term;
    }

    // TODO; getter

    @Override
    public String toString() {
        return term;
    }
}
