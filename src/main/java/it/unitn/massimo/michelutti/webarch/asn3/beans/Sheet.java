package it.unitn.massimo.michelutti.webarch.asn3.beans;

import it.unitn.ronchet.Spreadsheet.Cell;

import java.io.Serializable;
import java.util.LinkedList;

public class Sheet implements Serializable {
    private Integer[][] values;
    private String[][] formulas;
    private static int NUMROWS=4;
    private static int NUMCOLUMNS=4;

    public Sheet(){
        values=new Integer[NUMROWS][NUMCOLUMNS];
        formulas=new String[NUMROWS][NUMCOLUMNS];
        for(int i=0;i<NUMROWS;i++){
            for(int j=0;j<NUMCOLUMNS;j++){
                values[i][j]=null;
                formulas[i][j]=null;
            }
        }
    }

    @Override
    public String toString() {
        String res="<table id='spreadsheet'>";
        res+="<tr id='letters_row'><td id='top_corner'>Michelutti Massimo<br>Web arch.<br>Assignment 3</td>";
        for(int j=0;j<NUMCOLUMNS;j++) {
            char column= (char)('A'+j);
            res+="<td id='column_"+column+"'>"+column+"</td>";
        }
        res+="</tr>";
        for(int i=1;i<NUMROWS+1;i++){
            res+="<tr id='row"+i+"'>";
            res+="<td id='row_"+i+"'>"+i+"</td>";
            for(int j=0;j<NUMCOLUMNS;j++){
                char column= (char)('A'+j);
                res+="<td id='"+column+ i +
                        "' onclick='selectCell(\""+column+ i +"\")'>"+
                        "<input type='hidden' id='number"+column+ i+"' value='";
                if(values[i-1][j]!=null)
                    res+=values[i-1][j];
                res+= "'><input type='hidden' id='formula"+column+ i+"' value='";
                if(formulas[i-1][j]!=null)
                    res+=formulas[i-1][j];
                res+="'><div id='content"+column+ i+"'>";
                if(values[i-1][j]!=null)
                    res+=values[i-1][j];
                res+="</div></td>";

            }
            res+="</tr>";
        }
        res+="</table>";
        return res;
    }

    public void setValue(int x,int y, int value){
        values[x][y]=value;
    }

    public void setFormula(int x,int y, String formula){
        formulas[x][y]=formula;
    }

    public String toJSON(){
        String res="{\"cells\":[";
        for(int i=0;i<NUMROWS;i++){
            for(int j=0;j<NUMCOLUMNS;j++){
                String id=(char)('A'+j)+""+(i+1);
                res+="{\"cell\":\""+id+"\"";
                if(values[i][j]!=null)
                    res+=","+"\"value\":"+values[i][j];
                if(formulas[i][j]!=null)
                    res+= ","+"\"formula\":\""+formulas[i][j]+"\"";
                res+="}";
                if(!(i==NUMROWS-1&&j==NUMCOLUMNS-1)){
                    res+=",";
                }
            }
        }
        return res+"]}";
    }
}
