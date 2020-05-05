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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/*
 * Created on 29-jul-2005
 *
 */

/**
 * @author Joan Ametller, Jordi Cucurull
 *
 */
public class ConstantPool {
	public ConstantPool(byte[] bytecode) throws IOException{
		parseCP(new ByteArrayInputStream(bytecode));
	}
	
	public ConstantPool(String className) throws IOException{
		this(className, ConstantPool.class.getClassLoader());
	}
	
	public ConstantPool(String className, ClassLoader cl) throws IOException{
		parseCP(cl.getResourceAsStream(className.replace('.','/') + ".class"));
	}
	
	private void parseCP(InputStream is) throws IOException{
		
		DataInputStream dis = new DataInputStream(is);
		byte[] magic_number = new byte[4];
		byte[] minormajorv = new byte[4];
		byte[] cp_elements = new byte[2];
		
		dis.readFully(magic_number);
		dis.readFully(minormajorv);
		dis.readFully(cp_elements);
		
		int cpsize = new BigInteger(1, cp_elements).intValue();
		_cpool = new ConstantElement[cpsize + 1];
		// position zero will be left null to mantain consistence
		// with original constant pool indexs (1-n)
		
		int count=0;
		int id;
		
		byte[] b2 = new byte[2];
		byte[] b2_2 = new byte[2];
		byte[] b4 = new byte[4];
		byte[] b4_2 = new byte[4];
		
		while(count<cpsize-1)
		  {
		     count++;
		     id = dis.read();
		     switch(id)
		       {
			case 0x01: // utf8 string
			  dis.readFully(b2);
			  int len = new BigInteger(1, b2).intValue();  //costos
			  byte[] string = new byte[len];
			  dis.readFully(string);
			  _cpool[count] = new ConstantUtf8String(string);
			  break;
			case 0x03: // integer value
				dis.readFully(b4);
				_cpool[count] = new ConstantInteger(b4);
				break;
			case 0x04: // float value
			  dis.readFully(b4);
			  _cpool[count] = new ConstantFloat(b4);
			  break;
			case 0x05: // long value
				dis.readFully(b4);
				dis.readFully(b4_2);
				_cpool[count] = new ConstantLong(b4, b4_2);
				count++;
				break;
			case 0x06: // double value
			  dis.readFully(b4);
			  dis.readFully(b4_2);
			  _cpool[count] = new ConstantDouble(b4,b4_2);
			  count++;
			  break;
			case 0x07: // Class
			  dis.readFully(b2);
			  _cpool[count] = new ConstantClass(b2);
			  break;
			case 0x08: // Constant String
			  dis.readFully(b2);
			  _cpool[count] = new ConstantString(b2);
			  break;
			case 0x09: // Field ref
				dis.readFully(b2);
				dis.readFully(b2_2);
				_cpool[count] = new ConstantField(b2,b2_2);
				break;
			case 0x0A: // Method ref
				dis.readFully(b2);
				dis.readFully(b2_2);
				_cpool[count] = new ConstantMethod(b2,b2_2);
				break;
			case 0x0B: // Interface Method ref
				dis.readFully(b2);
				dis.readFully(b2_2);
				_cpool[count] = new ConstantIfaceMethod(b2,b2_2);
			  break;
			case 0x0C: // Name and Type
			  dis.readFully(b2);
			  dis.readFully(b2_2);
			  _cpool[count] = new ConstantNameAndType(b2,b2_2);
			  break;
			default:
			  System.out.println(count+ " Unknown id "+ id);
		       } 
		  }
	}
	
	public ConstantElement getElement(int index){
		return _cpool[index];
	}
	
	public int length(){
		return _cpool.length;
	}
	
	private ConstantElement[] _cpool;
}
