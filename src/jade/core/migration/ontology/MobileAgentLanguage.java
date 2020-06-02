/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

// Created: Jun 8, 2004

package jade.core.migration.ontology;

import jade.content.Concept;

/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public class MobileAgentLanguage implements Concept {
  private String name;
  private String majorVersion;
  private String minorVersion;
  private String format;
  private String filter;
  private String dependencies;
  

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public void setMajorVersion(String v) {
    majorVersion = v;
  }

  public String getMajorVersion() {
    return majorVersion;
  }

  public void setMinorVersion(String v) {
    minorVersion = v;
  }

  public String getMinorVersion() {
    return minorVersion;
  }
  
  public void setFormat(String f) {
    format = f;
  }

  public String getFormat() {
    return format;
  }
  
  public void setFilter(String f) {
    filter = f;
  }

  public String getFilter() {
    return filter;
  }
    
  public void setDependencies(String d) {
    dependencies = d;
  }

  public String getDependencies() {
    return dependencies;
  }

}
