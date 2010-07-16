/*
 * Copyright (C) 2008 - Huber Steffen
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"),
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied.
 * 
 * See the License for the specific language governing permissions 
 * and limitations under the License.
 * 
 * 
 * 
 * Contact: 
 * s4990348@mail.inf.tu-dresden.de
 */
package de.proteinms.omxparser.util;

import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

/**
 * Which ions to use no proline rule.
 * <br><br>
 * Please see "OMSSA.mod.xsd" for further information:
 * <br><br>
 * See <a href="http://www.ncbi.nlm.nih.gov/data_specs/schema/OMSSA.mod.xsd">http://www.ncbi.nlm.nih.gov/data_specs/schema/OMSSA.mod.xsd</a>
 *
 * @author Steffen Huber
 * @author Harald Barsnes
 */
public class MSSearchSettings_noprolineions implements Serializable {

    /**
     * List of ions to use no proline rule.
     * <br><br>
     * Hint: To get the ion type as text use the OmssaEnumerators class.
     */
    public List<Integer> MSIonType = new LinkedList<Integer>();

    /**
     * Adds an element to the MSIonType list. NB: The element has to be
     * an integer.
     *
     * @param s the element to add as a String
     */
    public void setMSIonType(String s) {
        MSIonType.add(Integer.valueOf(s));
    }
}
