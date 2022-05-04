from re import X
from flask import Blueprint, jsonify, request, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
import model

# http://URL:PORT/api/의 하위 API들
api = Blueprint('api', __name__, url_prefix='/api')

db = SQLAlchemy()

@api.route("/")
def index():
    return jsonify({"result":"OK", "version":"1"})

# 여기서부터 추가

# 건물
@api.route("/building")
def get_all_building():
    all_building = model.Building.query.all()
    return jsonify({"building":[building.serialized for building in all_building]})

@api.route("/building/create", methods = ['POST'])
def create_building():
    try:
        uid = request.form['uid']
        name = request.form['name']
        idnt = request.form['idnt']
        x = request.form['x']       
        y = request.form['y']

        building = model.Building(bldn_uid = uid, bldn_name = name, bldn_idnt = idnt, bldn_x = x, bldn_y = y)

        db.session.add(building)
        db.session.commit()
    except :
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

@api.route("/building/delete", methods = ['POST'])
def delete_building():
    try:
        uid = request.form['uid']
        building = db.session.query(model.Building).filter(model.Building.bldn_uid == uid).first()
        db.session.delete(building)
        db.session.commit()
    except:
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})


@api.route("/building/modify", methods = ['POST'])
def modify_building():
    try:
        uid = request.form['uid']
        bldn_name = request.form['name']
        bldn_idnt = request.form['idnt']
        bldn_x = request.form['x']       
        bldn_y = request.form['y']

        db.session.query(model.Building).filter(model.Building.bldn_uid == uid).update(
        {'bldn_name':bldn_name,
         'bldn_idnt':bldn_idnt,
         'bldn_x':bldn_x,
         'bldn_y':bldn_y,})

        db.session.commit()
    except:
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

# 전화번호
@api.route("/phone")
def get_all_phone():
    all_phone = model.Phone.query.all()
    return jsonify({"phone": [phone.serialized for phone in all_phone]})

@api.route("/phone/create", methods = ['POST'])
def create_phone():
    try:
        uid = request.form['uid']
        num = request.form['num']
        facid = request.form['facid']

        phone = model.Phone(phone_uid = uid, phone_num = num, phone_facid = facid)

        db.session.add(phone)
        db.session.commit()
    except :
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

@api.route("/phone/delete", methods = ['POST'])
def delete_phone():
    try:
        uid = request.form['uid']
        phone = db.session.query(model.Phone).filter(model.Phone.phone_uid == uid).first()
        db.session.delete(phone)
        db.session.commit()
    except:
        return jsonify({"status":"NG"})
        
    return jsonify({"status":"OK"})


@api.route("/phone/modify", methods = ['POST'])
def modify_phone():
    try:
        uid = request.form['uid']
        phone_num = request.form['num']
        phone_facid = request.form['facid']

        db.session.query(model.Phone).filter(model.Phone.phone_uid == uid).update(
        {'phone_num':phone_num,
         'phone_facid':phone_facid,})

        db.session.commit()
    except :
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

# 시설물
@api.route("/facility")
def get_all_facility():
    all_facility = model.Facility.query.all()
    return jsonify({"facility":[facility.serialized for facility in all_facility]})

@api.route("/facility/create", methods = ['POST'])
def create_facility():
    try:
        uid = request.form['uid']
        name = request.form['name']
        bldnid = request.form['bldnid']
        x = request.form['x']
        y = request.form['y']
        floor = request.form['floor']
        dept = request.form['dept']
        idname = request.form['idname']
    
        facility = model.Facility(fac_uid = uid, fac_name = name, fac_bldnid = bldnid, fac_x = x, fac_y = y, fac_floor = floor, fac_dept = dept, fac_idname = idname)

        db.session.add(facility)
        db.session.commit()
    except:
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

@api.route("/facility/delete", methods = ['POST'])
def delete_facility():
    try:
        uid = request.form['uid']
        facility = db.session.query(model.Facility).filter(model.Facility.fac_uid == uid).first()
        db.session.delete(facility)
        db.session.commit()
    except:
        return jsonify({"status":"NG"})
        
    return jsonify({"status":"OK"})

@api.route("/facility/modify", methods = ['POST'])
def modify_facility():
    try:
        uid = request.form['uid']
        fac_name = request.form['name']
        fac_bldnid = request.form['bldnid']
        fac_x = request.form['x']
        fac_y = request.form['y']
        fac_floor = request.form['floor']
        fac_dept = request.form['dept']
        fac_idname = request.form['idname']

        db.session.query(model.Facility).filter(model.Facility.fac_uid == uid).update(
        {'fac_name':fac_name,
         'fac_bldnid':fac_bldnid,
         'fac_x':fac_x,
         'fac_y':fac_y,
         'fac_floor':fac_floor,
         'fac_dept':fac_dept,
         'fac_idname' : fac_idname})

        db.session.commit()
    except:
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

# 공유기
@api.route("/ap")
def get_all_ap():
    all_ap = model.Accesspoint.query.all()
    return jsonify({"ap": [ap.serialized for ap in all_ap]})

@api.route("/ap/create", methods = ['POST'])
def create_ap():
    try:
        uid = request.form['uid']
        name = request.form['name']
        bssid = request.form['bssid']
        facid = request.form['facid']
        floor = request.form['floor']
        x = request.form['x']
        y = request.form['y']
        accesspoint = model.Accesspoint(ap_uid = uid, ap_name = name, ap_bssid = bssid, ap_facid = facid, ap_floor = floor, ap_x = x, ap_y = y)

        db.session.add(accesspoint)
        db.session.commit()
    except :
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

@api.route("/ap/delete", methods = ['POST'])
def delete_ap():
    try:
        uid = request.form['uid']
        accesspoint = db.session.query(model.Accesspoint).filter(model.Accesspoint.ap_uid == uid).first()
        db.session.delete(accesspoint)
        db.session.commit()
    except:
        return jsonify({"status":"NG"})
        
    return jsonify({"status":"OK"})

@api.route("/ap/modify", methods = ['POST'])
def modify_ap():
    try:
        uid = request.form['uid']
        ap_name = request.form['name']
        ap_bssid = request.form['bssid']
        ap_facid = request.form['facid']
        ap_floor = request.form['floor']
        ap_x = request.form['x']
        ap_y = request.form['y']

        db.session.query(model.Accesspoint).filter(model.Accesspoint.ap_uid == uid).update(
        {'ap_name':ap_name,
         'ap_bssid':ap_bssid,
         'ap_facid':ap_facid,
         'ap_floor':ap_floor,
         'ap_x':ap_x,
         'ap_y':ap_y})

        db.session.commit()
    except :
        return jsonify({"status":"NG"})

    return jsonify({"status":"OK"})

# 로그인
@api.route("/account/login", methods = ['POST'])
def login_auth():
    id = request.form['id']
    pwd = request.form['password']

    # 성공시 result = OK, 실패시 result=NG
    if(db.session.query(model.Account).filter(model.Account.acc_name == id, model.Account.acc_pwd == pwd).first() != None):
        return jsonify({"result":"OK"})
    else:
        return jsonify({"result":"NG"})

# 검색
@api.route("/search", methods=['POST'])
def search_from_all():
    pass

# 검색 - 특정 빌딩에 있는 시설물만
@api.route("/search/building", methods=['POST'])
def search_from_building():
    pass

# 이하 API 정의