<%--
  Created by IntelliJ IDEA.
  User: Omen
  Date: 28/10/2022
  Time: 17:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>My Spreadsheet</title>
    <style>
        table {
            table-layout: fixed;
            border-spacing: 0;
            border-collapse: collapse;
        }

        td {
            border: 1px solid lightgrey;
            height: 60px;
            width: 180px;
            text-align: center;
            vertical-align: middle;
        }

        td.active {
            outline: 3px solid green;
        }

    </style>
</head>
<body>
    <span id="selected_cell_name"></span>
    <input type="text" id="cell_editor" size="100" onclick="startWriting()"
         autocomplete='off' onchange="changeCell()">
    <jsp:useBean class="it.unitn.massimo.michelutti.webarch.asn3.beans.Sheet" id="Sheet" scope="application" />
    <p><%=Sheet.toString()%></p>
<script>
  var selected=null;
  var writing =false;
  var editor=document.getElementById('cell_editor');
  var url=window.location
  var lastupdate=Date.now()
  /**/

  function changeCell(){
      if(selected!==null){
          var cell_value = document.getElementById("content"+selected);
          cell_value.innerText=editor.value;
      }
  }

  document.getElementById('cell_editor').addEventListener("input", function(){
      if(selected!==null) {
          var cell_content = document.getElementById("content" + selected);
          //if (this.value.length && this.value !=cell_content.innerText) {
          if (this.value !=cell_content.innerText) {
          changeCell()
          }
      }
  }, false);

  function selectCell(cell_id){
    event.stopPropagation();
    if(selected==cell_id){
        editor.focus()
        return
    }else{
        if(selected!==null) {
            /*if(writing==true){
                updateCell();
                writing=false;
            }*/
            deselectCell();
        }
    }
    selected=cell_id
    var selected_cell_name=document.getElementById("selected_cell_name");
    selected_cell_name.innerText=selected+":";
    var sheet_cell=document.getElementById(cell_id)
    sheet_cell.className='active';
    console.log("selected "+cell_id);
    var cell_content=document.getElementById("content"+selected);
    var hidden_formula=document.getElementById("formula"+selected);
    var hidden_number=document.getElementById("number"+selected);
    if(hidden_formula.value!=""){
        editor.value=hidden_formula.value;
        cell_content.innerText=hidden_formula.value;
    }else{
        editor.value=hidden_number.value;
        cell_content.innerText=hidden_number.value;
    }
    editor.focus()
  }

  function deselectCell(){
      if(selected!=null) {
          updateCell()
          console.log("deselecting cell " + selected);
          var sheet_cell = document.getElementById(selected);
          sheet_cell.className = 'inactive';
          var selected_cell_name=document.getElementById("selected_cell_name");
          selected_cell_name.innerText="";
          var cell_content = document.getElementById("content" + selected);
          var divtext = cell_content.innerText;
          var hidden_number = document.getElementById("number" + selected);
          if (divtext.charAt(0) === '=') {
              cell_content.innerText = hidden_number.value;
          }
      }
  }

  function startWriting(){
      writing=true;
      event.stopPropagation();
  }

  function updateCell(){
      if(selected!=null) {
          var content=editor.value.trim();
          var hidden_formula=document.getElementById("formula"+selected);
          var hidden_number=document.getElementById("number"+selected);
          if(content===hidden_number.value || content===hidden_formula.value)
              return
          if(content===null||content==="")
              content="0"
          if(!(content.charAt(0)=='=')){
              console.log("Does not start with =")
              if(isNaN(content)){
                  content="0"
              }
          }
          fetch(url+'edit',
              {
                  method: 'POST',
                  headers: {
                      'Accept': 'application/json',
                      'Content-Type': 'application/json'
                  },
                  body: JSON.stringify({"content": content, "cell": selected})
              })
              .then((response) => response.json())
              .then((data) => {
                  var cells=data.cells;
                  for(let i = 0; i < cells.length; i++) {
                      let obj = cells[i];
                      var cell_to_edit=document.getElementById(obj.cell);
                      if(cell_to_edit!=null) {
                          if(obj.value!=undefined) {
                              var value_to_edit = document.getElementById("content" + obj.cell);
                              value_to_edit.innerText = obj.value;
                              var number_to_edit = document.getElementById("number" + obj.cell);
                              number_to_edit.value = obj.value;
                          }
                          if(obj.formula!=undefined) {
                              var formula_to_edit = document.getElementById("formula" + obj.cell);
                              formula_to_edit.value = obj.formula;
                          }
                      }
                  }
              }).catch(error => {
                  console.log(error)
              });
      }
  }

  document.onclick = function(){
      if(selected!==null) {
          if(writing==true){
              updateCell();
              writing=false;
          }
          deselectCell();
      }
      selected=null;
  }

  setInterval(updateSheet,1000);

  function updateSheet(){
      //console.log(Date.now())
      fetch(url+"update",{
          method: 'POST',
              headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json'
          },
          body: JSON.stringify({"time":lastupdate})
      })
          .then((response) => {
              if(response.status>=200&&response.status<300)
                return response.json()
              else
                return {cells:[]}
          })
          .then((data) => {
              var cells=data.cells;
              if(cells.length!=0)
                  lastupdate=Date.now();
              for(let i = 0; i < cells.length; i++) {
                  let obj = cells[i];
                  // do not update the cell the user is interacting with:
                  // updating may cause some annoyance in the user while
                  // he is editing the cell.
                  // The cell will be updated when the user stops the interaction
                  // by selecting another cell, or deselecting the cell in some way.
                  if(!(selected==obj.cell)){
                      var cell_to_edit = document.getElementById(obj.cell);
                      if (cell_to_edit != null) {
                          if(obj.hasOwnProperty('value')){
                              var value_to_edit = document.getElementById("content" + obj.cell);
                              value_to_edit.innerText = obj.value;
                              var number_to_edit = document.getElementById("number" + obj.cell);
                              number_to_edit.value = obj.value;
                          }
                          if(obj.hasOwnProperty('formula')) {
                              var formula_to_edit = document.getElementById("formula" + obj.cell);
                              formula_to_edit.value = obj.formula;
                          }
                      }
                  }
              }
          })
  }
</script>

</body>
</html>
