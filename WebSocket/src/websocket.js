let Req_Layer = document.getElementById("Client_R_Layer_Id");
let Req_Node = document.getElementById("Client_R_Node_Id");
let Req_Pos = document.getElementById("Client_R_pos_Id");

let Ans_Node = document.getElementById("Client_A_Node_Id");
let Ans_Pos = document.getElementById("Client_A_pos");
let Ans_Val = document.getElementById("Client_A_value");


window.table;

function updateValues()
{
    console.log("start");

    var reader = new FileReader();

    // As with JSON, use the Fetch API & ES6
    fetch('../array_values.csv')
        .then(response => response.text())
        .then(data => {
            // Do something with your data
            console.log(data);
            var allRows = data.split(/\r?\n|\r/);
            var id = "";

            for (var singleRow = 0; singleRow < allRows.length - 1; singleRow++) {

                var rowCells = allRows[singleRow].split(',');

                switch (singleRow)
                {
                    case 0:
                        id = "A1_";
                        break;
                    case 1:
                        id = "A2_";
                        break;
                    case 2:
                        id = "A3_";
                        break;
                    case 3:
                        id = "B1_";
                        break;
                    case 4:
                        id = "B2_";
                        break;
                    case 5:
                        id = "C1_";
                        break;
                    case 6:
                        id = "C2_";
                        break;

                }
                for (var rowCell = 0; rowCell < rowCells.length; rowCell++) {
                    console.log(id + rowCell + " : " + rowCells[rowCell]);

                    document.getElementById(id + rowCell).innerText = rowCells[rowCell];
                }
            }
            console.log("done!");
        });
}