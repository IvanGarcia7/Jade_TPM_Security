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
 
package jade.core.migration.analysis;

import java.math.BigInteger;

/*
 * Created on 29-jul-2005
 *
 */

/**
 * @author Joan Ametller, Jordi Cucurull
 *
 */
public class ConstantMethod implements ConstantElement {
	public ConstantMethod(byte[] classIndex, byte[] nameIndex){
		_classindex = new BigInteger(1, classIndex).intValue();
		_nameindex = new BigInteger(1, nameIndex).intValue();
	}
	
	public int getClassIndex(){
		return _classindex;
	}
	
	public int getNameIndex(){
		return _nameindex;
	}
	
	private int _classindex;
	private int _nameindex;
}
