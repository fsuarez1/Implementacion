package simpledb.record;

import static simpledb.file.Page.*;
import simpledb.file.Block;
import simpledb.tx.Transaction;

/**
 * Manages the placement and access of records in a block.
 * @author Edward Sciore
 */
public class RecordPage {
   public static final int EMPTY = 0, INUSE = 1;
   
   private Block blk;
   private TableInfo ti;
   private Transaction tx;
   private int slotsize;
   private int currentslot = -1; //olvidarme del header

   
   
   /** Creates the record manager for the specified block.
     * The current record is set to be prior to the first one.
     * @param blk a reference to the disk block
     * @param ti the table's metadata
     * @param tx the transaction performing the operations
     */
   public RecordPage(Block blk, TableInfo ti, Transaction tx) {
      this.blk = blk;
      this.ti = ti;
      this.tx = tx;
    
     // slotsize = ti.recordLength() + INT_SIZE;
      slotsize = ti.recordLength() ;///YA NO TIENE EL INT DEL PRINCIPIO
      tx.pin(blk);
  }
   
   /**
    * Closes the manager, by unpinning the block.
    */
   public void close() {
      if (blk != null) {
    	  tx.unpin(blk);
    	  blk = null;
      }
   }
   
   /**
    * Moves to the next record in the block.
    * @return false if there is no next record.
    */
   public boolean next() { //cursor sobre cada segmento acutalmente


	   /////OBTENER NUMERO DE CAMPOS
	 int nFields=this.ti.schema().fields().size();
	   
	   ////OBTENER NUMERO DE RECORDS
	 int nRecords=tx.getInt(blk, 0);
	   
	  /////OBTENER POSICION DEL ULTIMO INT
	 int lastInt=INT_SIZE*2+nRecords*nFields*INT_SIZE;
	   
		  /// return searchFor(INUSE);
 if(currentpos()<lastInt){ ////SI NO HAY ULTIMO DEVUELVO FALSE
		   
		  currentslot++;  ///VOY AL SIGUIENTE SLOT ????
		   //int nRecords=tx.getInt(blk, 0);
		  // tx.setInt(blk, 0, nRecords+1);
		   return true;
		   
	   }
	   
	   return false; ///SI NO
   }
   
   /**
    * Returns the integer value stored for the
    * specified field of the current record.
    * @param fldname the name of the field.
    * @return the integer stored in that field
    */
   public int getInt(String fldname) { ///antiguo getint
      int position = fieldpos(fldname);
      return tx.getInt(blk, position);
   }
  
   /**
    * Returns the string value stored for the
    * specified field of the current record.
    * @param fldname the name of the field.
    * @return the string stored in that field
    */
   public String getString(String fldname) {
      int position = fieldpos(fldname);   ///RETORNA LA POSICION DEL PUNTERO
    ///SACO EL VALOR DEL PUNTERO AL STRING
      int punteroVal=tx.getInt(blk, position); 
      
      
      /////RETORNO EL STRING EN EL PUNTERO
      return tx.getString(blk, punteroVal);
   }
   
   /**
    * Stores an integer at the specified field
    * of the current record.
    * @param fldname the name of the field
    * @param val the integer value stored in that field
    */
   public void setInt(String fldname, int val) {
      int position = fieldpos(fldname); ///RETORNA POSICION TOTAL
      tx.setInt(blk, position, val);
   }
   
   /**
    * Stores a string at the specified field
    * of the current record.
    * @param fldname the name of the field
    * @param val the string value stored in that field
    */
   public void setString(String fldname, String val) {
      int position = fieldpos(fldname); ///RETORNA POSICION TOTAL PERO PARA EL PUNTERO
  
      ////OBTENER POSICION DEL ULTIMO STRING
  	  int lastString=tx.getInt(blk, INT_SIZE);
      
  	  ////OBTENER TAMAÑO DEL STRING A INSERTAR
  	  int tamString=val.getBytes().length+4; //le agrego el int del principio?
  	  
  	  /////OBTENER POSICION DEL NUEVO STRING(A.K.A PUNTERO)
      int punteroVal=lastString-tamString;
  	  
  	  ////SETEAMOS EL STRING EN LA NUEVA POSICION
      tx.setString(blk, punteroVal, val);
     /// tx.setString(blk, position, val);
      
      ////SETEAMOS EL VALOR DEL PUNTERO EN EL FIELD
      tx.setInt(blk, position, punteroVal);
      
      //////ACTUALIZAMOS LA POSICION DEL ULTIMO STRING
    
	   tx.setInt(blk, INT_SIZE, punteroVal);
   }
   
   /**
    * Deletes the current record.
    * Deletion is performed by just marking the record
    * as "deleted"; the current record does not change. 
    * To get to the next record, call next().
    */
   public void delete() {
      int position = currentpos();
      tx.setInt(blk, position, EMPTY);
   }
   
   /**
    * Inserts a new, blank record somewhere in the page.
    * Return false if there were no available slots.
    * @return false if the insertion was not possible
    */
   public boolean insert() {
    
	   /*
	  currentslot = -1;
      boolean found = searchFor(EMPTY);
      if (found) {
         int position = currentpos(); ////posicion es casillero y slot es todo el espacio de la tupla
         tx.setInt(blk, position, INUSE); ////INUSE ES EL 1 DEL PRINCIPIO
      }
      return found;
	   */
	   if(emptySpace()>ti.maxLen){ ////SI PUEDO INSERTAR
		   
		 //currentslot++;  ///VOY AL SIGUIENTE SLOT ????
		   int nRecords=tx.getInt(blk, 0);
		  currentslot=nRecords; 
		   tx.setInt(blk, 0, nRecords+1);
		   return true;
		   
	   }
	   
	   return false; ///SI NO
   }
   
   /**
    * Sets the current record to be the record having the
    * specified ID.
    * @param id the ID of the record within the page.
    */
   public void moveToId(int id) {
      currentslot = id;
   }
   
   /**
    * Returns the ID of the current record.
    * @return the ID of the current record
    */
   public int currentId() {
      return currentslot;
   }
   
   private int currentpos() {
      return (INT_SIZE*2)+(currentslot * slotsize);
   }
   
   private int fieldpos(String fldname) {
     // int offset = INT_SIZE + ti.offset(fldname);
	   int offset = ti.offset(fldname); ///
      return currentpos() + offset;
   }
   
   private boolean isValidSlot() { ////ACA CALCULAR SI TENGO ESPACIO Y WEAS
      return currentpos() + slotsize <= BLOCK_SIZE;
   }
   
   private boolean searchFor(int flag) { ///que hago con esto???????
      currentslot++;
      while (isValidSlot()) {
         int position = currentpos();
         if (tx.getInt(blk, position) == flag)
            return true;
         currentslot++;
      }
      return false;
   }
   
   public int emptySpace(){
	   ////VARIABLES
	   int nRecords=0;
	   int lastString=0;
	   int nFields=0;
	   int lastInt=0;
	   int freeSpace=0;
	   
	   
	   /////OBTENER NUMERO DE CAMPOS
	   nFields=this.ti.schema().fields().size();
	   
	   ////OBTENER NUMERO DE RECORDS
	  nRecords=tx.getInt(blk, 0);
	  
	  ////OBTENER POSICION DEL ULTIMO STRING
	  lastString=tx.getInt(blk, INT_SIZE);
	   
	  /////OBTENER POSICION DEL ULTIMO INT
	  lastInt=INT_SIZE*2+nRecords*nFields*INT_SIZE;
	  
	  
	  ///////OBTENER ESPACIO LIBRE
	  
	  freeSpace=lastString-lastInt;
	   
	 
	   
	   
	   return freeSpace;
   }
   
}
