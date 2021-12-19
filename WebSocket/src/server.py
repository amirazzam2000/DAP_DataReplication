import asyncio
import websockets

async def echo(websocket, path):
    message = await websocket.recv()
    print(message)
    dic = json.loads(message.decode("utf-8"))
    df = pd.read_csv("array_values.csv", header=None)
    print(df)
    if dic["Layer"] != '4':
        for i in range(10):
            row = mapper(dic["Layer"],dic["NodeId"])
            col = int(dic["Array"][i]["id"])
            value =  int(dic["Array"][i]["value"])
            print("row:: " + str(row) + " col:: " + str(col) + " value:: " + str
            (value))
            print("old value: " + str(df.at[row,col]))
            df.at[row,col] = value
            print("new value: " + str(df.at[row,col]))
        df.to_csv('array_values.csv', index=False, encoding='utf-8', header=None)
        print(df)

    else:
        print("receiving information from client!")

        if dic["Operation"] == "Request":
            df.at[7,0] = int(dic["Operating Layer"]) #Request layer
            df.at[7,1] = int(dic["NodeId"]) #Request node
            df.at[7,2] = int(dic["Position"]) #Request position
            df.at[7,3] = 0 #Answer node
            df.at[7,4] = 0 #Answer position
            df.at[7,5] = 0 #Answer value
            df.at[7,6] = 0 #Request layer

        else: #Answer
            df.at[7,0] = 0 #Request layer
            df.at[7,1] = 0 #Request node
            df.at[7,2] = 0 #Request position
            df.at[7,3] = int(dic["NodeId"]) #Answer node
            df.at[7,4] = int(dic["Position"]) #Answer position
            df.at[7,5] = int(dic["Value"]) #Answer value
            df.at[7,6] = int(dic["Operating Layer"]) #Answer layer
        df.at[7,7] = 0
        df.at[7,8] = 0
        df.at[7,9] = 0
        df.to_csv('array_values.csv', index=False, encoding='utf-8', header=None)
        print(df)


async def main():
    async with websockets.serve(echo, "localhost", 8080):
        await asyncio.Future()  # run forever

def mapper(layer, node):
    offset = 0
    if layer == '1':
        offset = 3
    elif layer == '2':
        offset = 5
    index = offset+int(node)
    return index


asyncio.run(main())
