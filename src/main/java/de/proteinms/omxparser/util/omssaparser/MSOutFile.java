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
package de.proteinms.omxparser.util.omssaparser;

/**
 * This Class stores MSOutFile_outfiletype specific information.
 * <br>Please read "OMSSA.mod.dtd" and "OMSSA.xsd" for further information.
 * @author Steffen Huber
 */
public class MSOutFile {
		public String MSOutFile_outfile;
		public MSOutFile_outfiletype MSOutFile_outfiletype=new MSOutFile_outfiletype();
		public Boolean MSOutFile_includerequest;
		
		public void setMSOutFile_includerequest(String s){
			this.MSOutFile_includerequest=Boolean.valueOf(s);
		}
		public void setMSOutFile_outfiletype(MSOutFile_outfiletype s){
			this.MSOutFile_outfiletype=s;
		}
		public void setMSOutFile_outfile(String s){
			this.MSOutFile_outfile=s;
		}
		
}
