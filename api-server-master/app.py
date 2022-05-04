from gevent.pywsgi import WSGIServer
from flask import Flask
from kummap import api
import config
import model

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False
app.config['JSONIFY_PRETTYPRINT_REGULAR'] = True
app.register_blueprint(api)

# DB 설정
app.config.from_object(config)

model.db.init_app(app)

# 추후에 추가할 일이 있을 경우:
# app.register_blueprint()

# 임시: 포트 5000번
if __name__ == "__main__":
    server = WSGIServer(('0.0.0.0', 5000), app)
    server.serve_forever()