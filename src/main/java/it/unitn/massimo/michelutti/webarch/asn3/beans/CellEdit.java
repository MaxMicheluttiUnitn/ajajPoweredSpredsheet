package it.unitn.massimo.michelutti.webarch.asn3.beans;

import it.unitn.ronchet.Spreadsheet.Cell;

public class CellEdit {
    String content;
    String cell;

    public CellEdit(){
        cell=null;
        content=null;
    }

    public CellEdit(String cella, String contenuto){
        cell=cella;
        content=contenuto;
    }

    @Override
    public String toString(){
       String ce="null";
       String co="null";
       if(cell!=null)
           ce=cell;
       if(content!=null)
           co=content;
       return "{cell is "+ce+" and content is "+co+"}";
    }
}
