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

// Created: Jun 7, 2004

package jade.core.migration;

import jade.core.PlatformID;
import jade.core.Service;
import jade.core.AID;
import jade.core.IMTPException;

/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public interface InterPlatformMobilitySlice extends Service.Slice {

  // Constants for the names of horizontal commands associated to methods
  static final String H_GETAGENTCODE = "1";
  static final String H_TRANSFERINSTANCE = "2";
  static final String H_INFORMMIGRATIONRESULT = "3";
  static final String H_DELETEAGENTREFERENCES = "4";

  byte[] getAgentCode(AID aid, String className) throws IMTPException;
  void transferInstance(
    byte[] instance,
    AID name,
    String className,
    String codesrc,
	PlatformID where)
    throws IMTPException;
  void informMigrationResult(AID aid, String res) throws IMTPException;
  void deleteAgentReferences(AID aid) throws IMTPException;
}
