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

import jade.core.AID;
import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.PlatformID;
import jade.core.SliceProxy;
import jade.core.ServiceException;
import jade.core.IMTPException;
/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 * 
 */
public class InterPlatformMobilityProxy extends SliceProxy 
  implements InterPlatformMobilitySlice{

    public void transferInstance(byte[] instance, AID aid, String className, String codesrc, PlatformID where) 
      throws IMTPException {
        try {
          GenericCommand cmd = new GenericCommand(H_TRANSFERINSTANCE,
            InterPlatformMobilityHelper.NAME, null);
          cmd.addParam(instance);
          cmd.addParam(aid);
          cmd.addParam(className);
          cmd.addParam(codesrc);
          cmd.addParam(where);

          Node n = getNode();
          Object result = n.accept(cmd);
          if(result instanceof Throwable) 
            throw new IMTPException("Unable to transfer instance",(Throwable)result);

        }
        catch(ServiceException se) {
          throw new IMTPException("Unable to transfer instance",(Throwable)se);
        }
      }

  public byte[] getAgentCode(AID aid, String className) throws IMTPException {
    try {
      GenericCommand cmd = new GenericCommand(H_GETAGENTCODE,
        InterPlatformMobilityHelper.NAME, null);
      cmd.addParam(aid);
      cmd.addParam(className);
      Node n = getNode();
      return (byte[])n.accept(cmd);
    }
    catch(ServiceException se) {
      return null;
    }
  }

  public void informMigrationResult(AID aid, String res) throws IMTPException {
    try {
      GenericCommand cmd = new GenericCommand(H_INFORMMIGRATIONRESULT,
        InterPlatformMobilityHelper.NAME, null);
      cmd.addParam(aid);
      cmd.addParam(res);

      Node n = getNode();
      n.accept(cmd);
    }
    catch(ServiceException se) {
      throw new IMTPException("Unable to access remote node", se);
    }
  }

  public void deleteAgentReferences(AID aid) throws IMTPException {
    try {
      GenericCommand cmd = new GenericCommand(H_DELETEAGENTREFERENCES,
        InterPlatformMobilityHelper.NAME, null);
      cmd.addParam(aid);

      Node n = getNode();
      n.accept(cmd);
    }
    catch(ServiceException se) {
      throw new IMTPException("Unable to access remote node", se);
    }
  }
  
}
