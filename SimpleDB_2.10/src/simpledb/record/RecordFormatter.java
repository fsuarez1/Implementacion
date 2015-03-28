package simpledb.record;

import static java.sql.Types.INTEGER;
import static simpledb.file.Page.*;
import static simpledb.record.RecordPage.EMPTY;
import simpledb.file.Page;
import simpledb.buffer.PageFormatter;

/**
 * An object that can format a page to look like a block of 
 * empty records.
 * @author Edward Sciore
 */
class RecordFormatter implements PageFormatter {
   private TableInfo ti;
   
   /**
    * Creates a formatter for a new page of a table.
    * @param ti the table's metadata
    */
   public RecordFormatter(TableInfo ti) { //le llega el table info que es la info de la tabla y se el tamaño del record, y divide todo.
      this.ti = ti;
   }
   
   /** 
    * Formats the page by allocating as many record slots
    * as possible, given the record length.
    * Each record slot is assigned a flag of EMPTY.
    * Each integer field is given a value of 0, and
    * each string field is given a value of "".
    * @see simpledb.buffer.PageFormatter#format(simpledb.file.Page)
    */
  /* public void format(Page page) { ////primero al ultimo poner todos 0 FORMAT ANTIGUOOOOOOO
      int recsize = ti.recordLength() + INT_SIZE;//no pescar int size, es 0 si no hay tupla
      for (int pos=0; pos+recsize<=BLOCK_SIZE; pos += recsize) {
         page.setInt(pos, EMPTY);
         makeDefaultRecord(page, pos);
      }
   }
   */
   
   public void format(Page page){ ///TENGO QUE INICIAR TODO EN 0, BORRAR Y PROBAR EL DE ARRIBA SI NO FUNCIONA
	   
	   
	   
	   int recsize = INT_SIZE;
	   for(int i=0;i+recsize<BLOCK_SIZE;i+=recsize){
		   page.setInt(i, 0);
	   }
	   
	   
		   page.setInt(INT_SIZE, BLOCK_SIZE-1);   ////el ultimo string tiene que haber sido "insertado" alfinal
	       
	   
	   
   }
   
   private void makeDefaultRecord(Page page, int pos) { ///YA NO VA A SERVIR
      for (String fldname : ti.schema().fields()) {
         int offset = ti.offset(fldname);
         if (ti.schema().type(fldname) == INTEGER)
            page.setInt(pos + INT_SIZE + offset, 0);
         else
            page.setString(pos + INT_SIZE + offset, ""); //// sera por el bit de inicio?
      }
   }
}
