package edu.rutgers.MOST.data;

import java.io.*;

/* from http://www.javaworld.com/javaworld/javatips/jw-javatip76.html?page=2
 * 
 * need to make deep copies of metaboliteUsedMap and invalidReactions list
 * so when original loaded file is changed, the optimizations are still referencing
 * these objects based on the conditions when they were created. So if a metabolite
 * becomes used for example in the original file, it will still be unused in the
 * optimization file as it should be since the optimization files are not editable
 * 
 */
public class ObjectCloner
{
   // so that nobody can accidentally create an ObjectCloner object
   private ObjectCloner(){}
   // returns a deep copy of an object
   static public Object deepCopy(Object oldObj) throws Exception
   {
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      try
      {
         ByteArrayOutputStream bos = 
               new ByteArrayOutputStream(); 
         oos = new ObjectOutputStream(bos); 
         // serialize and pass the object
         oos.writeObject(oldObj);   
         oos.flush();               
         ByteArrayInputStream bin = 
               new ByteArrayInputStream(bos.toByteArray()); 
         ois = new ObjectInputStream(bin);                  
         // return the new object
         return ois.readObject(); 
      }
      catch(Exception e)
      {
         //System.out.println("Exception in ObjectCloner = " + e);
         throw(e);
      }
      finally
      {
         oos.close();
         ois.close();
      }
   }
   
}
