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
import jade.core.ServiceHelper;
import jade.core.AID;
import jade.core.ServiceException;

/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public interface InterPlatformMobilityHelper extends ServiceHelper {

  static final String NAME = "jade.core.migration.InterPlatformMobility";

  static final String INFORM_MIGRATION_RESULT = "Inform-Migration-Result";

  static final String INFORM_MIGRATED = "Inform-Migrated";
  
  static final String LAUNCH_AGENT = "Launch-Incomming-Agent";
  
  static final String REMOVE_PREPOWERUP_AGENT = "Remove-PrePowerUp-Agent";
  
  static final String DELETE_AGENT_REFERENCES = "Delete-Agent-References";

  void powerUpAgent(AID name) throws ServiceException;
  void informMigrationResult (AID aid, String result) throws ServiceException;
  void informMigrated (PlatformID where, AID name)
    throws ServiceException;
  String launchIncommingAgent(byte[] jar, byte[] instance, AID name)
    throws ServiceException;
  void removePrePowerUpAgent(AID name) throws ServiceException;
}
