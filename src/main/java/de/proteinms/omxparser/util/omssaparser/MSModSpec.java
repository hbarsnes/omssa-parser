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
 * This Class stores MSModSpec specific information.
 * <br>Please read "OMSSA.mod.dtd" and "OMSSA.xsd" for further information.
 * @author Steffen Huber
 */
public class MSModSpec {
	public MSModSpec_mod MSModSpec_mod=new MSModSpec_mod();
	public MSModSpec_type MSModSpec_type=new MSModSpec_type();
	public String MSModSpec_name;
	public double MSModSpec_monomass;
	public double MSModSpec_averagemass;
	public double MSModSpec_n15mass;
	public MSModSpec_residues MSModSpec_residues=new MSModSpec_residues();
	public MSModSpec_neutralloss MSModSpec_neutralloss=new MSModSpec_neutralloss();
	
	public void setMSModSpec_neutralloss(MSModSpec_neutralloss s){
		this.MSModSpec_neutralloss=s;
	}
	public void setMSModSpec_residues(MSModSpec_residues s){
		this.MSModSpec_residues=s;
	}
	public void setMSModSpec_n15mass(String s){
		this.MSModSpec_n15mass=Double.valueOf(s);
	}
	public void setMSModSpec_averagemass(String s){
		this.MSModSpec_averagemass=Double.valueOf(s);
	}
	public void setMSModSpec_monomass(String s){
		this.MSModSpec_monomass=Double.valueOf(s);
	}
	public void setMSModSpec_name(String s){
		this.MSModSpec_name=s;
	}
	public void setMSModSpec_type(MSModSpec_type s){
		this.MSModSpec_type=s;
	}
	public void setMSModSpec_mod(MSModSpec_mod s){
		this.MSModSpec_mod=s;
	}
	
}
