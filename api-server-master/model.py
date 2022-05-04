from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

# Float = Real = Double

class Building(db.Model):
    __tablename__ = 'buildings'
    bldn_uid = db.Column(db.BigInteger, primary_key=True)
    bldn_name = db.Column(db.String(20), nullable=False)
    bldn_idnt = db.Column(db.String(2))
    bldn_x = db.Column(db.Float, nullable=False)
    bldn_y = db.Column(db.Float, nullable=False)

    @property
    def serialized(self):
        return {
            'id': self.bldn_uid,
            'name': self.bldn_name,
            'identifier': "" if self.bldn_idnt == None else self.bldn_idnt,
            'lat': self.bldn_x,
            'long': self.bldn_y
        }

class Facility(db.Model):
    fac_uid = db.Column(db.BigInteger, primary_key=True)
    fac_name = db.Column(db.String(255), nullable=False)
    fac_bldnid = db.Column(db.BigInteger, db.ForeignKey('buildings.bldn_uid'), nullable=False)
    fac_bldn = db.relationship('Building', foreign_keys=fac_bldnid)
    fac_x = db.Column(db.Float, nullable=False)
    fac_y = db.Column(db.Float, nullable=False)
    fac_floor = db.Column(db.Integer)
    fac_dept = db.Column(db.String(30))
    fac_idname = db.Column(db.String(6))

    @property
    def serialized(self):
        return {
            'id': self.fac_uid,
            'name': self.fac_name,
            'building': self.fac_bldnid,
            'building_name': self.fac_bldn.bldn_name,
            'lat': self.fac_x,
            'long': self.fac_y,
            'floor': self.fac_floor,
            'department': self.fac_dept,
            'identifiername' : self.fac_idname
        }

class Phone(db.Model):
    phone_uid = db.Column(db.BigInteger, primary_key=True)
    phone_num = db.Column(db.String(12), nullable=False)
    phone_facid = db.Column(db.BigInteger, db.ForeignKey('facility.fac_uid'), nullable=False)
    phone_fac = db.relationship('Facility', foreign_keys=phone_facid)

    @property
    def serialized(self):
        return {
            'id': self.phone_uid,
            'facid': self.phone_facid,
            'name': self.phone_fac.fac_name,
            'number': self.phone_num
        }

class Accesspoint(db.Model):
    ap_uid = db.Column(db.BigInteger, primary_key=True)
    ap_name = db.Column(db.String(255), nullable=False)
    ap_bssid = db.Column(db.String(20), nullable=False)
    ap_facid = db.Column(db.BigInteger, db.ForeignKey('facility.fac_uid'), nullable=False)
    ap_fac = db.relationship('Facility', foreign_keys=ap_facid)
    ap_floor = db.Column(db.Integer, nullable=False) # Facility와 같지 않나? 필요한가?
    ap_x = db.Column(db.Float, nullable=False)
    ap_y = db.Column(db.Float, nullable=False)

    @property
    def serialized(self):
        return {
            'id': self.ap_uid,
            'name': self.ap_name,
            'bssid': self.ap_bssid,
            'facility': self.ap_facid,
            'facility_name': self.ap_fac.fac_name,
            'lat': self.ap_x,
            'long': self.ap_y,
            'floor': self.ap_floor
        }

class Account(db.Model):
    acc_uid = db.Column(db.BigInteger, primary_key=True)
    acc_name = db.Column(db.String(20), nullable=False)
    acc_pwd = db.Column(db.String(30), nullable=False)