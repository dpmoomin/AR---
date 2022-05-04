import os

BASE_DIR = os.path.dirname(__file__)

SQLALCHEMY_DATABASE_URI = 'mariadb+mariadbconnector://root:cfusionprj@tae.0xffff.host:12808/kummap'
SQLALCHEMY_TRACK_MODIFICATIONS = False