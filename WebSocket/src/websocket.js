let Req_Layer = document.getElementById("Client_R_Layer_Id");
let Req_Node = document.getElementById("Client_R_Node_Id");
let Req_Pos = document.getElementById("Client_R_pos_Id");

let Ans_Node = document.getElementById("Client_A_Node_Id");
let Ans_Pos = document.getElementById("Client_A_pos");
let Ans_Val = document.getElementById("Client_A_value");

console.log("starting server...")

const socket = new WebSocket('ws://localhost:8080');
socket.onopen = function (event) {
    console.log("Connected");
};


socket.onerror = function(error) {
    alert(`[error] ${error.message}`);
};
socket.onclose = function(event) {
    if (event.wasClean) {
        alert(`[close] Connection closed cleanly, code=${event.code} reason=${event.reason}`);
    } else {
        // e.g. server process killed or network down
        // event.code is usually 1006 in this case
        alert('[close] Connection died');
    }
};

socket.onmessage = function (event) {
    if(event.data instanceof []){
        let data = event.data;
        let NodeId = "";
        switch (data[0]){
            case '0':
                NodeId += "A";
                NodeId += data[1] + "_";
                break;

            case '1':
                NodeId += "B";
                NodeId += data[1] + "_";
                break;

            case '2':
                NodeId += "C";
                NodeId += data[1] + "_";
                break;

            default:
                NodeId += "Client";
                if (data[1] === 0){
                    NodeId += "_R_";
                }
                else{
                    NodeId += "_A_";
                }
                break;
        }
        if(data[0] < 3){
            for( let i= 0; i < 10; i++)
            {
                document.getElementById(NodeId + i).innerText = data[2 + i];
            }
        }
    }

};
