from flask import Flask, render_template
from flask_socketio import SocketIO, emit
import json


app = Flask(__name__,  template_folder="./webapp")
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

@app.route('/')
def index():
    print("connecting")
    return render_template('webSocketPage.html')

@app.route('/update/<string>')
def update(string):
    dic = json.loads(string.replace('\'', '"'))
    print(dic)



    return render_template('webSocketPage.html')

@socketio.on('connect')
def test_connect():
    emit('my response', {'data': 'Connected'})

@socketio.on('disconnect')
def test_disconnect():
    print('Client disconnected')

if __name__ == '__main__':
    socketio.run(app)