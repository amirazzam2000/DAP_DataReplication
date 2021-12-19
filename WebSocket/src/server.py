import asyncio
import websockets

async def echo(websocket, path):
    message = await websocket.recv()
    print(message)



async def main():
    async with websockets.serve(echo, "localhost", 8080):
        await asyncio.Future()  # run forever

asyncio.run(main())