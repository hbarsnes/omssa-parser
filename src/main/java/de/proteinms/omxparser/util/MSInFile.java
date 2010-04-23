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
 * Contact: 
 * s4990348@mail.inf.tu-dresden.de
 */
package de.proteinms.omxparser.util;

import java.io.Serializable;

/**
 * This Class stores MSInFile specific information.
 * <br>Please read "OMSSA.mod.dtd" and "OMSSA.xsd" for further information.
 *
 * @author Steffen Huber
 * Modified by: Harald Barsnes (adding Javadoc)
 */
public class MSInFile implements Serializable {

    public String MSInFile_infile;
    public MSInFile_infiletype MSInFile_infiletype = new MSInFile_infiletype();

    /**
     * Sets the MSInFile_infiletype value.
     * 
     * @param s the MSInFile_infiletype
     */
    public void setMSInFile_infiletype(MSInFile_infiletype s) {
        this.MSInFile_infiletype = s;
    }

    /**
     * Sets the MSInFile_infile value
     *
     * @param s the MSInFile_infile value as a String
     */
    public void setMSInFile_infile(String s) {
        this.MSInFile_infile = s;
    }
}
